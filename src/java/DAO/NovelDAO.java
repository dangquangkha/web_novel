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
import java.sql.ResultSetMetaData;


/**
 *
 * @author LAPTOP
 */
public class NovelDAO extends BaseDao {

    public List<Novel> listAllNovels() {
        List<Novel> list = new ArrayList<>();
        String sql = "SELECT * FROM novels ORDER BY created_at DESC";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs != null && rs.next()) {
                Novel n = mapRowToNovel(rs);
                if (n != null) {
                    list.add(n);
                }
            }
        } catch (SQLException ex) {
            System.err.println("listAllNovels error: " + ex.getMessage());
        } finally {
            try {
                closeResources();
            } catch (Exception e) {
                // ignore
            }
        }
        return list;
    }

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
     *
     * @param authorId
     * @return
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
     *
     * @param id
     * @return
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
     *
     * @param id
     * @return
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

        /**
         * Chuyển 1 hàng ResultSet -> Novel Ghi chú: caller phải đảm bảo rs đang
         * trỏ tới 1 row hợp lệ (đã gọi rs.next()).
         */
        private Novel mapRowToNovel(ResultSet r) throws SQLException {
            if (r == null) {
                return null;
            }
            Novel n = new Novel();
            try {
                n.setId(r.getInt("id"));
                n.setAuthorId(r.getInt("author_id"));
                n.setTitle(r.getString("title"));
                n.setOtherNames(r.getString("other_names"));
                n.setSensitive(r.getBoolean("is_sensitive"));
                n.setCoverPath(r.getString("cover_path"));
                n.setGenre(r.getString("genre"));
                n.setStatus(r.getString("status"));
                n.setIsPublic(r.getBoolean("is_public"));
                n.setSummary(r.getString("summary"));
                n.setNotes(r.getString("notes"));
                n.setCreatedAt(r.getTimestamp("created_at"));
            } catch (SQLException ex) {
                // debug: in ra danh sách cột trả về để dễ tìm lỗi tên cột
                try {
                    ResultSetMetaData md = r.getMetaData();
                    int cols = md.getColumnCount();
                    StringBuilder sb = new StringBuilder("ResultSet columns: ");
                    for (int i = 1; i <= cols; i++) {
                        sb.append(md.getColumnLabel(i)).append("(").append(md.getColumnTypeName(i)).append(")");
                        if (i < cols) {
                            sb.append(", ");
                        }
                    }
                    System.err.println(sb.toString());
                } catch (Exception ignore) {
                }
                throw ex;
            }
            return n;
        }

}
