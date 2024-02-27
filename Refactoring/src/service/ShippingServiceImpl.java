package service;

import config.DBConnection;
import entity.Shipping;
import view.ShippingMenu;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShippingServiceImpl {
    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    Shipping sh = new Shipping();
    ShippingMenu sm = new ShippingMenu();
    List<String> requestlist = new ArrayList<String>();
    private DataSource dataSource;
    public ShippingServiceImpl() {
        this.dataSource = DBConnection.getDataSource();
    }
    public void menuList() {
        try {
            sm.menuList();
            int menuNo = Integer.parseInt(bf.readLine());
            switch (menuNo) {
                case 1:
                    request();
                    break;
                case 2:
                    ShippingDocument();
                    break;
                case 3:
                    ShippingList();
                    break;
                case 4:
                    exit();
                    break;
                case 5:
                    authMenuList();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("입출력 문제");
        }
    }

    public void authMenuList() {
        try {
            sm.authMenuList();
            int menuNo = Integer.parseInt(bf.readLine());
            switch (menuNo) {
                case 1:
                    authorize();
                    break;
                case 2:
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

    public void request() {
        try {
            List<String> request = new ArrayList<String>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("출고일자를 입력해주세요. yyyy-MM-dd");
            String input = bf.readLine();
            Date date = dateFormat.parse(input);
            sh.setShippingDate(date);
            System.out.println("출고 담당자를 입력해주세요.");
            sh.setShippingPersonnel(bf.readLine());
            request.add(auto_increment());
            request.add(input);
            request.add(sh.getShippingPersonnel());
            requestlist.add(request.toString());
            for (String s : requestlist) {
                System.out.println(s);
            }
            System.out.println("출고 요청이 저장되었습니다. 승인을 받아야 요청을 확정할 수 있습니다.");
            menuList();
        }catch ( IOException e){
            e.printStackTrace();
            System.out.println("입출력 문제");
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("parse문제");
        }
    }

    public void ShippingDocument() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            System.out.println("출고 지시서를 출력합니다.");
            System.out.println("조회할 출고 일자를 작성해 주십시오.(yyyy-MM-dd)");
            String date = bf.readLine();
            System.out.printf("%-12s%-10s%-10s%-10s%-10s%-10s%-10s%n", "출고일", "출고담당자", "창고명", "상품명", "수량", "단가", "금액");
            String sql = "select i.takeoutDate, i.takeoutPersonnel, d.whName, s.prodName, i.takeoutQuantity, i.takeoutUnitPrice, i.takeoutPrice from order_takeout i left join stock s on i.stockNo = s.stockNo left join warehouse d on d.whCode = i.takeoutconfirm where i.takeoutDate Like ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sh.setShippingDate(rs.getDate("takeoutDate"));
                sh.setShippingPersonnel(rs.getString("takeoutPersonnel"));
                sh.setWhName(rs.getString("whName"));
                sh.setProdName(rs.getString("prodName"));
                sh.setShippingQuaintity(rs.getInt("takeoutQuantity"));
                sh.setShippingUnitPrice(rs.getInt("takeoutUnitPrice"));
                sh.setShippingPrice(rs.getInt("takeoutPrice"));
                System.out.printf("%-14s%-14s%-10s%-14s%-10d%-10d%-10d%n", sh.getShippingDate(), sh.getShippingPersonnel(), sh.getWhName(), sh.getProdName(), sh.getShippingQuaintity(), sh.getShippingUnitPrice(), sh.getShippingPrice());
            }
            menuList();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void ShippingList() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            System.out.println("출고 현황을 출력합니다.");
            System.out.printf("%-6s%-10s%-6s%-6s%-6s%-6s%-6s%-6s%n", "출고번호", "출고날짜", "출고담당자", "출고수량", "출고단가", "출고금액", "출고상태", "출고확정");
            String sql = "select * from takeout";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sh.setShippingCode(rs.getString("takeoutCode"));
                sh.setShippingDate(rs.getDate("takeoutDate"));
                sh.setShippingPersonnel(rs.getString("takeoutPersonnel"));
                sh.setShippingQuaintity(rs.getInt("takeoutQuantity"));
                sh.setShippingUnitPrice(rs.getInt("takeoutUnitPrice"));
                sh.setShippingPrice(rs.getInt("takeoutPrice"));
                sh.setShippingState(rs.getString("takeoutState"));
                sh.setShippingconfirm(rs.getInt("takeoutconfirm"));
                System.out.printf("%-8s%-12s%-10s%-10d%-10d%-10d%-10s%-10d%n", sh.getShippingCode(), sh.getShippingDate(), sh.getShippingPersonnel(), sh.getShippingQuaintity(), sh.getShippingUnitPrice(), sh.getShippingPrice(), sh.getShippingState(), sh.getShippingconfirm());
            }
            menuList();
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e)
        {e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");}

    }

    public void confirm() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            System.out.println("확정할 출고번호를 입력해주세요");
            String confirmNo = bf.readLine();
            String sql = "select * from order_takeout where takeoutCode = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, confirmNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String tf = rs.getString("insertCode");
                if (confirmNo.equals(tf)) {
                    System.out.println("출고를 확정하시겠습니까? (Y/N)");
                    String select = bf.readLine();
                    if (select.equalsIgnoreCase("Y")) {
                        String updateSql = "UPDATE order_takeout SET takeoutState = 'Confirmed' WHERE takeoutCode = ?";
                        PreparedStatement updatepstmt = conn.prepareStatement(updateSql);
                        updatepstmt.setString(1, confirmNo);
                        updatepstmt.executeUpdate();
                        System.out.println("출고가 확정되었습니다.");
                        menuList();
                    } else if (select.equalsIgnoreCase("N")) {
                        System.out.println("확정을 취소합니다.");
                        authMenuList();
                    }
                } else {
                    System.out.println("입력한 출고 번호가 존재하지 않습니다.");
                    menuList();
                }
            }
        }catch (IOException | SQLException e){
            e.printStackTrace();
        }catch (Exception e)
        {e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");}
    }

    public void serch() {  // 나중에

    }

    public void authorize() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            for (String request : requestlist) {
                String[] requestData = request.split(",");
                String insertCode = requestData[0].substring(1);
                String insertDate = requestData[1];
                String insertPersonnel = requestData[2].substring(0,requestData.length - 1);

                String sql = "insert into order_takeout(takeoutCode, takeoutDate, takeoutPersonnel, takeoutState, takeoutconfirm) values(?, ?, ?, ?, 1)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, insertCode);
                pstmt.setString(2, insertDate);
                pstmt.setString(3, insertPersonnel);
                pstmt.setString(4, "pending");
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
    public void myDaoMethod() {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM order_takeout");
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
        }catch (Exception e)
        {e.printStackTrace();
            System.out.println("뭐가 오류인지 몰라");}
    }
    private static int i=0;
    public String auto_increment() {
        i++;
        String increment = "T1000" + i;
        return increment;
    }
}
