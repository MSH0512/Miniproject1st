package service;

import config.DBConnection;
import entity.Warehousing;
import view.WarehousingMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import javax.sql.DataSource;

public class WarehousingServiceImpl {
    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    Warehousing ws = new Warehousing();
    WarehousingMenu wm = new WarehousingMenu();
    private DataSource dataSource;
    List<String> requestlist = new ArrayList<String>();

    public WarehousingServiceImpl() {
        this.dataSource = DBConnection.getDataSource();
    }


    public void menuList() {
        try {
            wm.menuList();
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
                    orderInsertDocument();
                    break;
                case 5:
                    orderInsertList();
                    break;
                case 6:
                    exit();
                    break;
                case 7:
                    authMenuList();
                    break;
                default:
                    System.out.println("올바른 번호를 입력해 주십시오.");
                    menuList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }
    }


    public void authMenuList() {
        try {
            wm.authMenuList();
            int menuNo = Integer.parseInt(bf.readLine());
            switch (menuNo) {
                case 1:
                    authorize();
                    break;
                case 2:
                    confirm();
                    break;
                case 3:
                    assignPositon();
                    break;
                case 4:
                    menuList();
                    break;
                default:
                    System.out.println("올바른 번호를 입력해 주십시오.");
                    authMenuList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }
    }

    public void request() { // 입고요청 : 입고번호(자동생성), 입고일자,입고 담당자,입고상태, 입고확정(미확정) 데이터를 입력하고 추가시킨다(발주번호=자동생성, 발주상태-미확정, 상세목록 메소드 불러오기)
        try (Connection conn = dataSource.getConnection()) {

            List<String> request = new ArrayList<String>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("입고일자를 입력해주세요. yyyy-MM-dd");
            String input = bf.readLine();
            Date date = dateFormat.parse(input);
            ws.setInsertDate(date);
            System.out.println("입고 담당자를 입력해주세요.");
            ws.setInsertPersonnel(bf.readLine());

            System.out.println("[상품 목록]");
            String sql = "select prodNo, prodName, prodCategory_main, prodCategory_middle, prodCategory_sub, prodBrand from product";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int prodNo = rs.getInt("prodNo");
                String prodName = rs.getString("prodName");
                String categoryMain = rs.getString("prodCategory_main");
                String categoryMiddle = rs.getString("prodCategory_middle");
                String categorySub = rs.getString("prodCategory_sub");
                String prodBrand = rs.getString("prodBrand");
                System.out.println(prodNo + prodName + categoryMain + categoryMiddle + categorySub + prodBrand);
            }
            System.out.println("추가할 상품코드를 입력해주십시오");
            int prodCode = Integer.parseInt(bf.readLine());
            System.out.println("입고 수량을 입력해주십시오.");
            int quantity = Integer.parseInt(bf.readLine());
            System.out.println("입고 단가를 입력해주십시오.");
            int unitPrice = Integer.parseInt(bf.readLine());

            System.out.println("[창고 목록]");
            String whsql = "select whCode, whName, whAddr from warehouse";
            PreparedStatement wh = conn.prepareStatement(whsql);
            ResultSet whrs = wh.executeQuery(whsql);
            while (whrs.next()) {
                int whCode = whrs.getInt("whCode");
                String whName = whrs.getString("whName");
                String whAddr = whrs.getString("whAddr");
                System.out.println(whCode + whName + whAddr);
            }
            System.out.println("입고 받을 창고 코드를 지정해주세요.");
            int warecode = Integer.parseInt(bf.readLine());
            request.add(auto_increment());
            request.add(String.valueOf(prodCode));
            request.add(input);
            request.add(ws.getInsertPersonnel());
            request.add(String.valueOf(quantity));
            request.add(String.valueOf(unitPrice));
            request.add(String.valueOf(warecode));
            requestlist.add(request.toString());
            for (String s : requestlist) {
                System.out.println(s);
            }
            System.out.println("입고 요청이 저장되었습니다. 승인을 받아야 요청을 확정할 수 있습니다.");
            menuList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }
    }

    public void admincurrent() {

    }

    public void orderInsertDocument() { // 입고지시서출력 : 입고일, 입고 담당자, 창고명, 상품명, 수량, 단가, 금액 출력
        try (
                Connection conn = dataSource.getConnection();
        ) {
            System.out.println("입고 지시서를 출력합니다.");
            System.out.println("조회할 입고 일자를 작성해 주십시오.(yyyy-MM-dd)");
            String date = bf.readLine();
            System.out.printf("%-12s%-10s%-10s%-10s%-10s%-10s%-10s%n", "입고일", "입고담당자", "창고명", "상품명", "수량", "단가", "금액");
            String sql = "select i.insertDate, i.insertPersonnel, d.whName, p.prodName, i.insertQuantity, i.insertUnitPrice, i.insertPrice from order_insert i left join product p on i.prodNo = p.prodNo \n" +
                    "left join stock s on i.insertCode = s.insertCode\n" +
                    "left join warehouse d on i.insertconfirm = d.whCode where i.insertDate = ? ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ws.setInsertDate(rs.getDate("insertDate"));
                ws.setInsertPersonnel(rs.getString("insertPersonnel"));
                ws.setWhName(rs.getString("whName"));
                ws.setProdName(rs.getString("prodName"));
                ws.setInsertQuantity(rs.getInt("insertQuantity"));
                ws.setInsertUnitPrice(rs.getInt("insertUnitPrice"));
                ws.setInsertPrice(rs.getInt("insertPrice"));
                System.out.printf("%-14s%-14s%-10s%-14s%-10d%-10d%-10d%n", ws.getInsertDate(), ws.getInsertPersonnel(), ws.getWhName(), ws.getProdName(), ws.getInsertQuantity(), ws.getInsertUnitPrice(), ws.getInsertPrice());
            }
            menuList();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }
    }

    public void orderInsertList() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            System.out.println("입고 현황을 출력합니다.");
            System.out.printf("%-6s%-10s%-6s%-6s%-6s%-6s%-6s%n", "입고번호", "입고날짜", "입고담당자", "입고수량", "입고단가", "입고금액", "입고상태");
            String sql = "select * from order_insert";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ws.setInsertCode(rs.getString("insertCode"));
                ws.setInsertDate(rs.getDate("insertDate"));
                ws.setInsertPersonnel(rs.getString("insertPersonnel"));
                ws.setInsertQuantity(rs.getInt("insertQuantity"));
                ws.setInsertUnitPrice(rs.getInt("insertUnitPrice"));
                ws.setInsertPrice(rs.getInt("insertPrice"));
                ws.setInsertState(rs.getString("insertState"));
                ws.setInsertconfirm(rs.getInt("insertconfirm"));
                System.out.printf("%-8s%-12s%-10s%-10d%-10d%-10d%-10s%n", ws.getInsertCode(), ws.getInsertDate(), ws.getInsertPersonnel(), ws.getInsertQuantity(), ws.getInsertUnitPrice(), ws.getInsertPrice(), ws.getInsertState());
            }
            menuList();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }
    }

    public void confirm() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            System.out.println("확정할 입고번호를 입력해주세요");
            String confirmNo = bf.readLine();
            String sql = "select * from order_insert where insertCode = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, confirmNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String tf = rs.getString("insertCode");
                if (confirmNo.equals(tf)) {
                    System.out.println("입고를 확정하시겠습니까? (Y/N)");
                    String select = bf.readLine();
                    if (select.equalsIgnoreCase("Y")) {
                        String updateSql = "UPDATE order_insert SET insertState = 'Confirmed' WHERE insertCode = ?";
                        PreparedStatement updatepstmt = conn.prepareStatement(updateSql);
                        updatepstmt.setString(1, confirmNo);
                        updatepstmt.executeUpdate();
                        System.out.println("입고가 확정되었습니다.");
                        menuList();
                    } else if (select.equalsIgnoreCase("N")) {
                        System.out.println("확정을 취소합니다.");
                        authMenuList();
                    }
                } else {
                    System.out.println("입력한 입고 번호가 존재하지 않습니다.");
                    menuList();
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }

    }

    public void update() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            list();
            System.out.println("요청 내용을 변경할 입고번호를 입력해주십시오.");
            System.out.println("입고 번호 : (ex. IXXXXX)");
            String insNo = bf.readLine();
            String date = null;
            String personnel = null;
            int quantity = 0;
            int unitPrice = 0;
            System.out.println("입고 날짜를 변경하시겠습니까?(Y/N)");
            String select = bf.readLine();
            if (select.equals("Y")) {
                System.out.println("입고 날짜를 변경해주십시오.");
                date = bf.readLine();
            }
            System.out.println("입고 담당자를 변경하시겠습니까?(Y/N)");
            select = bf.readLine();
            if (select.equals("Y")) {
                System.out.println("입고 담당자를 변경해주십시오.");
                personnel = bf.readLine();
            }
            System.out.println("입고 수량을 변경하시겠습니까?(Y/N)");
            select = bf.readLine();
            if (select.equals("Y")) {
                System.out.println("입고 수량을 변경해주십시오.");
                quantity = Integer.parseInt(bf.readLine());
            }
            System.out.println("입고 단가를 변경하시겠습니까?(Y/N)");
            select = bf.readLine();
            if (select.equals("Y")) {
                System.out.println("입고 단가를 변경해주십시오.");
                unitPrice = Integer.parseInt(bf.readLine());
            }
            String sql = "select * from order_insert where insertCode = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, insNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String updatesql = "update order_insert set insertDate = ?, insertPersonnel = ?, insertQuantity = ?, insertUnitPrice = ? where insertCode = ?";
                pstmt = conn.prepareStatement(updatesql);
                pstmt.setString(1, date);
                pstmt.setString(2, personnel);
                pstmt.setInt(3, quantity);
                pstmt.setInt(4, unitPrice);
                pstmt.setString(5, insNo);
                pstmt.executeUpdate();
                System.out.println("요청 내용이 변경되었습니다.");
            } else {
                System.out.println("입력한 입고번호가 존재하지 않습니다.");
            }
            menuList();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }
    }

    public void authorize() {  // 요청 승인 : 리스트에서 불러온 요청목록을 조회하여 단 건 또는 복수 건 승인  / 현재는 일괄 승인
        try (
                Connection conn = dataSource.getConnection();
        ) {

            for (String request : requestlist) {
                String[] requestData = request.split(",");
                String insertCode = requestData[0].substring(1);
                String prodNo = requestData[1];
                String insertDate = requestData[2];
                String insertPersonnel = requestData[3];
                String unitPrice = requestData[4];
                String quantity = requestData[5];
                String whCode = requestData[6].substring(0, requestData[6].length() - 1);
                System.out.println(insertCode + prodNo + insertDate + insertPersonnel + quantity + unitPrice + whCode);
                int price = Integer.parseInt(quantity) * Integer.parseInt(unitPrice);
                String sql = "insert into order_insert values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, insertCode);
                pstmt.setString(2, prodNo);
                pstmt.setString(3, insertDate);
                pstmt.setString(4, insertPersonnel);
                pstmt.setString(5, quantity);
                pstmt.setString(6, unitPrice);
                pstmt.setString(7, String.valueOf(price));
                pstmt.setString(8, "pending");
                pstmt.setString(9, whCode);
                pstmt.executeUpdate();
            }
            System.out.println("데이터가 정상 반영되었습니다.");
            menuList();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }
    }

    public void assignPositon() {
        try (Connection conn = dataSource.getConnection();) {
            System.out.println("창고 위치를 지정(변경)할 입고 번호를 입력해주십시오.");
            String insertCode = bf.readLine();
            System.out.println("[창고 목록]");
            String whsql = "select whCode, whName, whAddr from warehouse";
            PreparedStatement wh = conn.prepareStatement(whsql);
            ResultSet whrs = wh.executeQuery(whsql);
            while (whrs.next()) {
                int whCode = whrs.getInt("whCode");
                String whName = whrs.getString("whName");
                String whAddr = whrs.getString("whAddr");
                System.out.println(whCode + whName + whAddr);
            }
            System.out.println("지정(변경)할 창고 번호를 입력해주십시오.");
            int select = Integer.parseInt(bf.readLine());

            String sql = "update order_insert set insertconfirm = ? where insertCode = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, select);
            pstmt.setString(2, insertCode);
            pstmt.executeUpdate();
            System.out.println("창고 위치가 지정(변경)되었습니다.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


//    public void productlist() { // 상세목록 : 상품코드를 선택하면 상품명 같이 입력됨(JOIN) 수량, 단가, 금액은 직접 입력 상품코드는 상품테이블에서 불러옴
    // 입고 요청에 합침 추후 메소드로 분리를 위해 남겨둠.
//        try (
//                Connection conn = dataSource.getConnection();
//        ) {
//            System.out.println("[상품 목록]");
//            String sql = "select prodNo, prodName, prodCategory_main, prodCategory_middle, prodCategory_sub, prodBrand from product";
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                int prodNo = rs.getInt("prodNo");
//                String prodName = rs.getString("prodName");
//                String categoryMain = rs.getString("prodCategory_main");
//                String categoryMiddle = rs.getString("prodCategory_middle");
//                String categorySub = rs.getString("prodCategory_sub");
//                String prodBrand = rs.getString("prodBrand");
//                System.out.println(prodNo + prodName + categoryMain + categoryMiddle + categorySub + prodBrand);
//            }
//            System.out.println("추가할 상품코드를 입력해주십시오");
//            int prodCode = Integer.parseInt(bf.readLine());
//            System.out.println("상품을 추가하시겠습니까?(Y/N)");
//            String select = bf.readLine();
//            if (select.equalsIgnoreCase("Y")) {
//                System.out.println("상품이 추가되었습니다.");
//            } else {
//                menuList();
//            }
//            System.out.println("[창고 목록]");
//            System.out.println("입고 받을 창고를 지정해주세요.");
//
//
//        } catch (SQLException | IOException e) {
//            e.printStackTrace();
//        }


    public void cancle() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            list();
            System.out.println("요청 취소할 입고 번호를 입력해주십시오.");
            System.out.println("입고 번호 : (ex. IXXXXX)");
            String cancel = bf.readLine();
            String sql = "select * from order_insert where insertCode = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cancel);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String deletesql = "delete from order_insert where insertCode = ?";
                pstmt = conn.prepareStatement(deletesql);
                pstmt.setString(1, cancel);
                pstmt.executeUpdate();
                System.out.println("입력한 입고번호의 요청이 취소되었습니다.");
            } else {
                System.out.println("입력한 입고번호가 존재하지 않습니다.");
            }
            menuList();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }
    }

    public void assignDate() {

    }

    public void exit() {
        System.out.println("시스템을 종료합니다.");
        System.exit(0);
    }

    private static int i = 0; // insertCode값 자동 증가를 위한 스태틱 변수

    public String auto_increment() {
        i++;
        String increment = "I1000" + i;
        return increment;
    }

    public void list() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            String sql = "select * from order_insert";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ws.setInsertCode(rs.getString("insertCode"));
                ws.setInsertDate(rs.getDate("insertDate"));
                ws.setInsertPersonnel(rs.getString("insertPersonnel"));
                ws.setInsertQuantity(rs.getInt("insertQuantity"));
                ws.setInsertUnitPrice(rs.getInt("insertUnitPrice"));
                ws.setInsertPrice(rs.getInt("insertPrice"));
                ws.setInsertState(rs.getString("insertState"));
                ws.setInsertconfirm(rs.getInt("insertconfirm"));
                System.out.printf("%s%s%s%d%d%d%s%d%n", ws.getInsertCode(), ws.getInsertDate(), ws.getInsertPersonnel(), ws.getInsertQuantity(), ws.getInsertUnitPrice(), ws.getInsertPrice(), ws.getInsertState(), ws.getInsertconfirm());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }
    }

    public void myDaoMethod() {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM order_insert");
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            // Process the result set
            while (resultSet.next()) {
                // Retrieve data from the result set
                int id = resultSet.getInt("root");
                String name = resultSet.getString("문상현");
                // Print or process the retrieved data
                System.out.println("ID: " + id + ", Name: " + name);
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");
        }
    }
}
