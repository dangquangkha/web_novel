/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.SQLException;
import java.util.Random;

/**
 *
 * @author LAPTOP
 */
public class EmailVerificationDAO extends BaseDao {

    // Create OTP and save it (valid for 5 minutes)
    private void deleteExpiredOTPs() {
        String sql = "DELETE FROM email_verifications WHERE expires_at <= NOW()";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String generateAndSave(int userId) {
        
        deleteExpiredOTPs();
        
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        String sql = "INSERT INTO email_verifications(user_id, code, expires_at, used, created_at) VALUES(?, ?, DATE_ADD(NOW(), INTERVAL 5 MINUTE), false, NOW())";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, otp);
            ps.executeUpdate();
            return otp;
        } catch (SQLException ex) {
        }
        return null;
    }

    // Verify OTP (mark used=true if valid)
    public boolean verify(int userId, String code) {
        String sql = "SELECT id FROM email_verifications WHERE user_id=? AND code=? AND used=false AND expires_at > NOW() ORDER BY id DESC LIMIT 1";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, code);
            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                ps2 = connection.prepareStatement("UPDATE email_verifications SET used=true WHERE id=?");
                ps2.setInt(1, id);
                ps2.executeUpdate();
                ps2.close();
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    // (Optional) Remove old OTPs or retrieve status — not required in this version
}
