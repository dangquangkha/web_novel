/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Chapter;
import java.sql.ResultSet;

/**
 *
 * @author LAPTOP
 */
public class ChapterDAO extends BaseDao {

    public boolean existsChapter(int volumeId, int chapterNumber) {
        String sql = "SELECT COUNT(*) FROM chapters WHERE volume_id = ? AND chapter_number = ?";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, volumeId);
            ps.setInt(2, chapterNumber);
            rs = ps.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.err.println("existsChapter error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public int createChapter(Chapter c) {
        String sql = "INSERT INTO chapters (volume_id, chapter_number, title, content, word_count, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, c.getVolumeId());
            ps.setInt(2, c.getChapterNumber());
            ps.setString(3, c.getTitle());
            ps.setString(4, c.getContent());
            if (c.getWordCount() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, c.getWordCount());
            }
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println("createChapter error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return -1;
    }

    public List<Chapter> listChaptersByVolume(int volumeId) {
        List<Chapter> list = new ArrayList<>();
        String sql = "SELECT * FROM chapters WHERE volume_id = ? ORDER BY chapter_number ASC";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, volumeId);
            rs = ps.executeQuery();
            while (rs != null && rs.next()) {
                Chapter c = mapRowToChapter(rs);
                if (c != null) {
                    list.add(c);
                }
            }
        } catch (SQLException ex) {
            System.err.println("listChaptersByVolume error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return list;
    }

    public Chapter getChapterById(int id) {
        String sql = "SELECT * FROM chapters WHERE id = ?";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs != null && rs.next()) {
                return mapRowToChapter(rs);
            }
        } catch (SQLException ex) {
            System.err.println("getChapterById error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public boolean deleteChapter(int id) {
        String sql = "DELETE FROM chapters WHERE id = ?";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            System.err.println("deleteChapter error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return false;
    }

    private Chapter mapRowToChapter(ResultSet r) throws SQLException {
        if (r == null) {
            return null;
        }
        Chapter c = new Chapter();
        c.setId(r.getInt("id"));
        c.setVolumeId(r.getInt("volume_id"));
        c.setChapterNumber(r.getInt("chapter_number"));
        c.setTitle(r.getString("title"));
        c.setContent(r.getString("content"));
        int wc = r.getInt("word_count");
        if (!r.wasNull()) {
            c.setWordCount(wc);
        }
        c.setCreatedAt(r.getTimestamp("created_at"));
        c.setUpdatedAt(r.getTimestamp("updated_at"));
        return c;
    }
}
