package dao;

import config.DBConnection;

import javax.sql.DataSource;

public class WarehousingDao extends DBConnection {
    private DataSource dataSource;
    public WarehousingDao(DataSource dataSource){
        super();
        this.dataSource = dataSource;
    }
}