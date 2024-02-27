package view;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class WarehousingMenu {
    public void menuList ()  {
        System.out.println("입고 관리 페이지에 입장하였습니다.");
        System.out.println("============================================================================================");
        System.out.println("1.입고요청 | 2.요청수정 | 3.요청취소 | 4.입고지시서 출력 | 5.입고 현황 출력 | 6.나가기 | 7. 관리자 권한");
        System.out.println("============================================================================================");
        System.out.println("번호 선택 : ");
    }
    public void authMenuList() {
        System.out.println("관리자 페이지에 입장하였습니다.");
        System.out.println("==================================================");
        System.out.println("1.입고요청승인 | 2.입고확정 | 3.입고위치지정 | 4.이전화면 ");
        System.out.println("==================================================");
        System.out.println("번호 선택 : ");
    }
}
