/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Volume;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author LAPTOP
 */
public class VolumeDAO extends BaseDao{

    /**
     * Kiểm tra volume đã tồn tại (unique constraint: novel_id + volume_number)
     * @param novelId
     * @param volumeNumber
     * @return 
     */
    public boolean existsVolume(int novelId, int volumeNumber) {
        String sql = "SELECT COUNT(*) FROM volumes WHERE novel_id = ? AND volume_number = ?";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, novelId);
            ps.setInt(2, volumeNumber);
            rs = ps.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.err.println("existsVolume error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * Tạo volume mới, trả về id (generated key) hoặc -1 nếu thất bại.
     * @param v
     * @return 
     */
    public int createVolume(Volume v) {
        String sql = "INSERT INTO volumes (novel_id, volume_number, title, description, created_at) VALUES (?, ?, ?, ?, NOW())";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, v.getNovelId());
            ps.setInt(2, v.getVolumeNumber());
            ps.setString(3, v.getTitle());
            ps.setString(4, v.getDescription());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println("createVolume error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return -1;
    }

    /**
     * Lấy danh sách volumes của 1 novel (dùng cho hiển thị)
     * @param novelId
     * @return 
     */
    public List<Volume> listVolumesByNovel(int novelId) {
        List<Volume> list = new ArrayList<>();
        String sql = "SELECT * FROM volumes WHERE novel_id = ? ORDER BY volume_number ASC";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, novelId);
            rs = ps.executeQuery();
            while (rs != null && rs.next()) {
                Volume v = mapRowToVolume(rs);
                if (v != null) {
                    list.add(v);
                }
            }
        } catch (SQLException ex) {
            System.err.println("listVolumesByNovel error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return list;
    }

    /**
     * Lấy volume theo id
     * @param id
     * @return 
     */
    public Volume getVolumeById(int id) {
        String sql = "SELECT * FROM volumes WHERE id = ?";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs != null && rs.next()) {
                return mapRowToVolume(rs);
            }
        } catch (SQLException ex) {
            System.err.println("getVolumeById error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return null;
    }

    private Volume mapRowToVolume(ResultSet r) throws SQLException {
        if (r == null) {
            return null;
        }
        Volume v = new Volume();
        v.setId(r.getInt("id"));
        v.setNovelId(r.getInt("novel_id"));
        v.setVolumeNumber(r.getInt("volume_number"));
        v.setTitle(r.getString("title"));
        v.setDescription(r.getString("description"));
        v.setCreatedAt(r.getTimestamp("created_at"));
        return v;
    }
}
