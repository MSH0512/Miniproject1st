package entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Getter
@Setter
@ToString
public class Warehousing {
    private String insertCode;
    private Date insertDate;
    private String insertPersonnel;
    private int insertQuantity;
    private int insertUnitPrice;
    private int insertPrice;
    private String insertState;
    private int insertconfirm;
    private String whName;
    private String prodName;
}
