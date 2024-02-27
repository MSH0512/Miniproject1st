package controller;

import service.WarehousingServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class WarehousingController {
    public static void main(String[] args) throws SQLException, IOException, ParseException {
        WarehousingServiceImpl wsImpl = new WarehousingServiceImpl();
        wsImpl.menuList();
    }
}