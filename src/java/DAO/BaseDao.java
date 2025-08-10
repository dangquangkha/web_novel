/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.DBContext1;

/**
 *
 * @author ADMIN
 */
public class BaseDao {

    public DBContext1 dbc = new DBContext1();
    public Connection connection = null;
    public PreparedStatement ps = null;
    public ResultSet rs = null;

    public BaseDao() {
        this.connection = dbc.getConnection();
    }

    protected boolean closeResources() throws SQLException, Exception {
        if (rs != null) {
            rs.close();
        }
        if (ps != null) {
            ps.close();
        }
        if (connection != null) {
            connection.close();
        }
        return true;
    }
}
