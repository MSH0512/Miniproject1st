package service;

import entity.Preorder;
import view.PreorderMenu;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface PreorderService {
    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    Preorder order = new Preorder();
    PreorderMenu pm = new PreorderMenu();

    List<String> requestlist = new ArrayList<String>();
        public void preorderList();
        public abstract void request ();
        public void confirm();
        public void update ();
        public void cancle ();
        public void authorize ();
        public void exit();

}
