package exception;

/**
 * 에러를 생성하기 위해서 사용 되는 ENUM 클래스 입니다.
 * 에러는 에러상태, 에러문구, 에러코드를 포함하고 있습니다.
 */
public enum ErrorCodeList implements ErrorModel{
    INVALID_INPUT_EMAIL(400, "유효하지 않는 이메일 형식입니다.", "E001"),
    INVALID_INPUT_PHONENUMBER(400, "유효하지 않는 전화번호 형식입니다.", "P001"),
    ID_ALREADY_EXISTS(400, "이미 존재하는 아이디입니다.", "I001"),
    ID_MAXIMUM_LENGTH_EXCCEDED(400, "아이디는 8자 이하만 생성이 가능합니다", "I002"),
    ID_CONTAINS_SPACE(400, "아이디는 공백을 포함 할 수 없습니다", "I003"),
    ID_CONTAINS_NON_ALPHABET_NUMBER(400, "아이디는 영어와 숫자만 입력이 가능합니다.", "I004"),
    SQL_SELECT_FAIL(400, "DB 에서 값을 가져오는대에 실패하였습니다.", "S001"),
    NULL_POINTER_EXCEPTION(404, " Null 값입니다! 값을 확인해주세요.", "N001"),
    USER_INPUT_ERROR(400, "유저의 값을 입력 받는데 문제가 있습니다.", "U001");
    private int status;
    private String code;
    private String message;

    ErrorCodeList(int status, String message, String code) {
        this.status = status;
        this.message = message;
        this.code = code;

    }


    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public int getStatus() {
        return this.status;
    }
}
