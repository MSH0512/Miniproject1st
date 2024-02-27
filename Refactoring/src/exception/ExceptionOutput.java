package exception;

public class ExceptionOutput extends RuntimeException{
    private ErrorCodeList errorCodeList;

    public ExceptionOutput(ErrorCodeList errorCode) {
        this.errorCodeList = errorCode;
        System.out.println("\n*************** error 발생 *************** ");
        System.out.println(errorCodeList.getCode()+" / "+errorCodeList.getMessage()+" / "+errorCodeList.getStatus());
        System.out.println("*****************************************");
    }

}
