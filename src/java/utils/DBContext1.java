package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author FPT University - PRJ30X
 */
public class DBContext1 {

    ResourceBundle bundle = ResourceBundle.getBundle("constant.db");

    public Connection getConnection() {
        try {
            Class.forName(bundle.getString("drivername"));
            String url = bundle.getString("url");
            String username = bundle.getString("username");
            String password = bundle.getString("password");
            Connection connection = DriverManager.getConnection(url, username, password);
            return connection;
        } catch (ClassNotFoundException e) {
            String msg = "ClassNotFoundException throw from method getConnection()";
            System.out.println(msg);
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            String msg = "SQLException throw from method getConnection()";
            System.out.println(msg);
            System.out.println(e.getMessage());
        } catch (Exception e) {
            String msg = "Unexpected Exception throw from method getConnection()";
            System.out.println(msg);
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Test out connection
    public static void main(String[] args) {
        DBContext1 dbContext = new DBContext1();
        Connection conn = dbContext.getConnection();

        if (conn != null) {
            System.out.println("Kết nối DB thành công!");
        } else {
            System.out.println("Kết nối DB thất bại!");
        }
    }

}
