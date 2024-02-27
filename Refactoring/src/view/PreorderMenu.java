package view;

public class PreorderMenu {
    public void menuList ()  {
        System.out.println("발주 관리 페이지에 입장하였습니다.");
        System.out.println("=======================================================");
        System.out.println("1.발주요청 | 2.발주수정 | 3.발주취소 | 4.나가기 | 5.관리자 권한");
        System.out.println("=======================================================");
        System.out.println("번호 선택 : ");
    }
    public void authMenuList () {
        System.out.println("관리자 페이지에 입장하였습니다.");
        System.out.println("=====================================================");
        System.out.println("1.발주승인 | 2.발주확정 | 3.이전화면");
        System.out.println("=====================================================");
        System.out.println("번호 선택 : ");
    }
}
