package view;

public class ShippingMenu {
    public void menuList() {
        System.out.println("출고 관리 페이지에 입장하였습니다.");
        System.out.println("============================================================================================");
        System.out.println("1.출고요청 | 2.출고지시서 출력 | 3.출고 현황 출력 | 4.나가기 | 5. 관리자 권한");
        System.out.println("============================================================================================");
        System.out.println("번호 선택 : ");
    }

    public void authMenuList() {
        System.out.println("관리자 페이지에 입장하였습니다.");
        System.out.println("==================================================");
        System.out.println("1.출고요청승인 | 2.출고확정 | 3.이전화면");
        System.out.println("==================================================");
        System.out.println("번호 선택 : ");
    }
}
