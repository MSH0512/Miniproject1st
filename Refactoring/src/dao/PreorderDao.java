package dao;

import config.DBConnection;

import javax.sql.DataSource;

public class PreorderDao extends DBConnection {
    private DataSource dataSource;
    public PreorderDao(DataSource dataSource){
        super();
        this.dataSource = dataSource;
    }
}
