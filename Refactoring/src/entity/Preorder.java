package entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Preorder {
    private String preoCode;
    private String takeCode;
    private Date preoDate;
    private String preoState;
    private int preoconfirm;
    private int preoQuantity;
    private int preoUnitPrice;
    private int preoPrice;

    private String prodName;
    private int stockQuantity;
    private String customer;
    private String warehaouserName;
    private int prodNo;
}
