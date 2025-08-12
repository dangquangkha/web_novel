/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Novel;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author LAPTOP
 */
public class NovelDAO extends BaseDao{

    public int addNovel(Novel novel) {
        String sql = "INSERT INTO novels (author_id, title, other_names, is_sensitive, cover_path, genre, status, is_public, summary, notes, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try {
            // ensure fresh connection (consistent with your UserDAO style)
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, novel.getAuthorId());
            ps.setString(2, novel.getTitle());
            ps.setString(3, novel.getOtherNames());
            ps.setBoolean(4, novel.isSensitive());
            ps.setString(5, novel.getCoverPath());
            ps.setString(6, novel.getGenre());
            ps.setString(7, novel.getStatus());
            ps.setBoolean(8, novel.isIsPublic());
            ps.setString(9, novel.getSummary());
            ps.setString(10, novel.getNotes());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            System.out.println("addNovel error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
                // ignore
            }
        }
        return -1;
    }

    /**
     * Get list of novels by author id
     */
    public List<Novel> getNovelsByAuthor(int authorId) {
        List<Novel> list = new ArrayList<>();
        String sql = "SELECT * FROM novels WHERE author_id = ? ORDER BY created_at DESC";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, authorId);
            rs = ps.executeQuery();
            while (rs != null && rs.next()) {
                Novel n = mapRowToNovel(rs);
                list.add(n);
            }
        } catch (SQLException ex) {
            System.out.println("getNovelsByAuthor error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return list;
    }

    /**
     * Get novel detail by id
     */
    public Novel getNovelById(int id) {
        String sql = "SELECT * FROM novels WHERE id = ?";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs != null && rs.next()) {
                return mapRowToNovel(rs);
            }
        } catch (SQLException ex) {
            System.out.println("getNovelById error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * Delete novel by id (returns true if deleted)
     */
    public boolean deleteNovel(int id) {
        String sql = "DELETE FROM novels WHERE id = ?";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            System.out.println("deleteNovel error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
            }
        }
        return false;
    }

    private Novel mapRowToNovel(ResultSet rs) throws SQLException {
        Novel n = new Novel();
        n.setId(rs.getInt("id"));
        n.setAuthorId(rs.getInt("author_id"));
        n.setTitle(rs.getString("title"));
        n.setOtherNames(rs.getString("other_names"));
        n.setSensitive(rs.getBoolean("is_sensitive"));
        n.setCoverPath(rs.getString("cover_path"));
        n.setGenre(rs.getString("genre"));
        n.setStatus(rs.getString("status"));
        n.setIsPublic(rs.getBoolean("is_public"));
        n.setSummary(rs.getString("summary"));
        n.setNotes(rs.getString("notes"));
        n.setCreatedAt(rs.getTimestamp("created_at"));
        return n;
    }
}
