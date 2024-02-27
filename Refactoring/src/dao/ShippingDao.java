package dao;

import javax.sql.DataSource;

public class ShippingDao {
    private DataSource dataSource;
    public ShippingDao(DataSource dataSource){
        super();
        this.dataSource = dataSource;
    }
}
