package controller;

import service.PreorderServiceImpl;

import java.io.IOException;
import java.sql.SQLException;

public class PreorderController {
    public static void main(String[] args) throws IOException, SQLException {
        PreorderServiceImpl psImpl = new PreorderServiceImpl();
        psImpl.menuList();
    }
}
