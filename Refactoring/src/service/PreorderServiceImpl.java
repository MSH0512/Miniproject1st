package service;

import config.DBConnection;
import entity.Preorder;
import view.PreorderMenu;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PreorderServiceImpl implements PreorderService {

    DataSource dataSource;
    public PreorderServiceImpl() {
        this.dataSource = DBConnection.getDataSource();
    }

    public void menuList() {
        try {
            pm.menuList();
            int menuNo = Integer.parseInt(bf.readLine());
            switch (menuNo) {
                case 1:
                    request();
                    break;
                case 2:
                    update();
                    break;
                case 3:
                    cancle();
                    break;
                case 4:
                    exit();
                    break;
                case 5:
                    authMenuList();
                    break;
                default:
                    System.out.println("올바른 번호를 입력해 주십시오.");
                    menuList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authMenuList() {
        try {
            pm.authMenuList();
            int menuNo = Integer.parseInt(bf.readLine());
            switch (menuNo) {
                case 1:
                    authorize();
                    break;
                case 2:
                    confirm();
                    break;
                case 3:
                    menuList();
                    break;
                default:
                    System.out.println("올바른 번호를 입력해 주십시오.");
                    authMenuList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void request() {  // 발주요청 : 발주일자,거래처,창고 데이터를 입력하고 추가시킨다(발주번호=자동생성, 발주상태-미확정, 상세목록 메소드 불러오기)
       try{
        List<String> request = new ArrayList<String>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("발주일자를 입력해주세요. yyyy-MM-dd");
        String input = bf.readLine();
        Date date = dateFormat.parse(input);
        order.setPreoDate(date);
        request.add(auto_increment());
        request.add(input);
        requestlist.add(request.toString());
        for (String s : requestlist) {
            System.out.println(s);
        }
        System.out.println("발주 요청이 저장되었습니다. 승인을 받아야 요청을 확정할 수 있습니다.");
        menuList();
    }catch (IOException e) {
        e.printStackTrace();
    } catch (ParseException e) {
        e.printStackTrace();
    }catch (Exception e)
    {e.printStackTrace();
        System.out.println("뭐가 오류인지 몰라");}
    }

    public void confirm() {  // 발주 번호를 입력받아 현재 확정 상태를 조회하고 확정 Y/N 메세지 띄워서 반영
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("요청을 확정할 발주 번호를 입력해주세요");
            int confirmNo = Integer.parseInt(bf.readLine());
            String sql = "select * from preorder where preoCode = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, confirmNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int tf = rs.getInt("preoCode");
                if (confirmNo == tf) {
                    System.out.println("발주를 확정하시겠습니까? (Y/N)");
                    String select = bf.readLine();
                    if (select.equals("Y")) {
                        order.setPreoState("Confirmed");
                        System.out.println("발주가 확정되었습니다.");
                        System.out.println(order.getPreoState());
                    } else if (select.equals("N")) {
                        System.out.println("확정을 취소합니다.");
                        authMenuList();
                    }
                } else {
                    System.out.println("입력한 발주 번호가 존재하지 않습니다.");
                    menuList();
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            list();
            System.out.println("요청 내용을 변경할 발주번호를 입력해주십시오.");
            System.out.println("발주 번호 : (ex. PXXXXX)");
            String insNo = bf.readLine();
            String date = null;
//            String personnel = null;
            int quantity = 0;
            int unitPrice = 0;
            System.out.println("발주 날짜를 변경하시겠습니까?(Y/N)");
            String select = bf.readLine();
            if (select.equals("Y")) {
                System.out.println("발주 날짜를 변경해주십시오.");
                date = bf.readLine();
            }
//            System.out.println("발주 담당자를 변경하시겠습니까?(Y/N)");
//            select = bf.readLine();
//            if (select.equals("Y")) {
//                System.out.println("발주 담당자를 변경해주십시오.");
//                personnel = bf.readLine();
            System.out.println("발주 수량을 변경하시겠습니까?(Y/N)");
            select = bf.readLine();
            if (select.equals("Y")) {
                System.out.println("발주 수량을 변경해주십시오.");
                quantity = Integer.parseInt(bf.readLine());
            }
            System.out.println("발주 단가를 변경하시겠습니까?(Y/N)");
            select = bf.readLine();
            if (select.equals("Y")) {
                System.out.println("발주 단가를 변경해주십시오.");
                unitPrice = Integer.parseInt(bf.readLine());
            }
            String sql = "select * from preorder where preoCode = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, insNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String updatesql = "update preorder set preoDate = ?, preoQuantity = ?, preoUnitPrice = ? where preoCode = ?";
                pstmt = conn.prepareStatement(updatesql);
                pstmt.setString(1, date);
                pstmt.setInt(2, quantity);
                pstmt.setInt(3, unitPrice);
                pstmt.setString(4, insNo);
                pstmt.executeUpdate();
                System.out.println("요청 내용이 변경되었습니다.");
            } else {
                System.out.println("입력한 발주번호가 존재하지 않습니다.");
            }
            menuList();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }catch (Exception e)
        {e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");}
    }

    public void cancle() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            list();
            System.out.println("요청 취소할 발주 번호를 입력해주십시오.");
            System.out.println("발주 번호 : (ex. PXXXXX)");
            String cancel = bf.readLine();
            String sql = "select * from preorder where preoCode = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cancel);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String deletesql = "delete from preorder where preoCode = ?";
                pstmt = conn.prepareStatement(deletesql);
                pstmt.setString(1, cancel);
                pstmt.executeUpdate();
                System.out.println("입력한 발주번호의 요청이 취소되었습니다.");
            } else {
                System.out.println("입력한 발주번호가 존재하지 않습니다.");
            }
            menuList();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }catch (Exception e)
        {e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");}
    }

    public void authorize() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            for (String request : requestlist) {
                String[] requestData = request.split(",");
                String insertCode = requestData[0].substring(1);
                String insertDate = requestData[1].substring(0,requestData.length - 1);

                String sql = "insert into preorder(preoCode, preoDate, preoState, preoconfirm) values(?, ?, 'pending', 1)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, insertCode);
                pstmt.setString(2, insertDate);
                pstmt.executeUpdate();
            }
            System.out.println("데이터가 정상 반영되었습니다.");
            menuList();
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e)
        {e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");}
    }

    public void exit() {
        System.out.println("시스템을 종료합니다.");
        System.exit(0);
    }


    public void preorderList() { // 상세목록 조회(상품코드, 상품명, 현 재고, 발주 수량, 발주 단가, 발주 금액)

    }

    public void addPreorderList() { // 상품목록 추가 : 상품리스트 출력 후 발주번호, 상품코드를 입력받아 상품명(조인) 받아오고, 수량, 단가, 금액 설정
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("해당 발주 건에 상품을 추가합니다.");
            System.out.println("발주 번호를 입력해주십시오");
            order.setPreoCode(bf.readLine());
            System.out.println("상품 번호를 입력해주십시오");
            order.setProdNo(Integer.parseInt(bf.readLine()));
            System.out.println("현 재고량을 입력해주십시오");
            order.setStockQuantity(Integer.parseInt(bf.readLine()));
            System.out.println("발주 수량을 입력해주십시오");
            order.setPreoQuantity(Integer.parseInt(bf.readLine()));
            System.out.println("발주 단가를 입력해주십시오");
            order.setPreoUnitPrice(Integer.parseInt(bf.readLine()));
            String sql = "update preorder set PreoQuantity = ?, PreoUnitPrice = ? , PreoPrice = ? where preoCode = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, order.getPreoQuantity());
            pstmt.setInt(2, order.getPreoUnitPrice());
            pstmt.setInt(3, order.getPreoUnitPrice() * order.getPreoQuantity());
            pstmt.setString(4, order.getPreoCode());
            pstmt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    private static int i = 0; // insertCode값 자동 증가를 위한 스태틱 변수

    public String auto_increment() {
        i++;
        String increment = "P1000" + i;
        return increment;
    }
    public void list() { // 데이터가 들어간 리스트를 출력(대부분 기능에서 나올 예정)
        try (Connection conn = dataSource.getConnection()) {
            String sql = "Select * From preorder;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            System.out.printf("%-8s%-12s%-16s%-20s%-24s%-28s%n", "발주번호", "발주상태", "상품명", "발주금액", "발주단가", "발주수량");
            while (rs.next()) {
                order.setPreoCode(rs.getString("preoCode"));
                order.setPreoState(rs.getString("preoState"));
                order.setPreoDate(rs.getDate("preoDate"));
                order.setPreoPrice(rs.getInt("preoPrice"));
                order.setPreoUnitPrice(rs.getInt("preoUnitPrice"));
                order.setPreoQuantity(rs.getInt("preoQuantity"));
                System.out.printf("%-8s%-12s%-16s%-20d%-24d%-28d%n", order.getPreoCode(), order.getPreoState(), order.getPreoDate(), order.getPreoPrice(), order.getPreoUnitPrice(), order.getPreoQuantity());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
