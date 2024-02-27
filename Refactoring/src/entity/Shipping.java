package entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Shipping {
    private String shippingCode;
    private int stockNo;
    private Date shippingDate;
    private String shippingState;
    private int shippingconfirm;
    private String shippingPersonnel;
    private int shippingQuaintity;
    private int shippingUnitPrice;
    private int shippingPrice;
    private String whName;
    private String prodName;
}
