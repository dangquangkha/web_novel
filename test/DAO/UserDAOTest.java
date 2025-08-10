/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import model.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author LAPTOP
 */
public class UserDAOTest extends BaseDao {

    public UserDAOTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        // nothing global
    }

    @AfterClass
    public static void tearDownClass() {
        // nothing global
    }

    @Before
    public void setUp() {
        // nothing per-test
    }

    @After
    public void tearDown() {
        // nothing per-test
    }

    /**
     * Test of createUser method, of class UserDAO.
     */
    @Test
    public void testCreateUser() {
        System.out.println("createUser");
        String uniq = UUID.randomUUID().toString().substring(0, 8);
        String username = "tuser_" + uniq;
        String email = "t_" + uniq + "@example.com";

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash("hash_test");
        u.setRole("READER");

        UserDAO instance = new UserDAO();
        int generatedId = -1;

        try {
            // gọi DAO để tạo user
            generatedId = instance.createUser(u);
            assertTrue("Generated id should be > 0", generatedId > 0);

            // kiểm tra bằng findByUsername
            User found = instance.findByUsername(username);
            assertNotNull("User should be found by username", found);
            assertEquals(username, found.getUsername());
            assertEquals(email, found.getEmail());
            assertEquals("READER", found.getRole());

            // kiểm tra findByEmail
            User foundByEmail = instance.findByEmail(email);
            assertNotNull("User should be found by email", foundByEmail);
            assertEquals(username, foundByEmail.getUsername());
            assertEquals(email, foundByEmail.getEmail());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("testCreateUser failed: " + ex.getMessage());
        } finally {
            // cleanup: xóa bản ghi vừa tạo
            if (generatedId > 0) {
                deleteUserById(instance, generatedId);
            } else {
                deleteUserByUsername(instance, username);
            }
        }
    }

    /**
     * Test of findByUsername method, of class UserDAO.
     */
    @Test
    public void testFindByUsername() {
        System.out.println("findByUsername");
        String uniq = UUID.randomUUID().toString().substring(0, 8);
        String username = "fuser_" + uniq;
        String email = "f_" + uniq + "@example.com";

        UserDAO instance = new UserDAO();
        int insertedId = -1;

        try {
            insertedId = insertUserDirect(instance, username, email, "pwdhash", "AUTHOR");

            User result = instance.findByUsername(username);
            assertNotNull("findByUsername should return a user", result);
            assertEquals(username, result.getUsername());
            assertEquals(email, result.getEmail());
            assertEquals("AUTHOR", result.getRole());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("testFindByUsername failed: " + ex.getMessage());
        } finally {
            if (insertedId > 0) {
                deleteUserById(instance, insertedId);
            }
        }
    }

    /**
     * Test of findByEmail method, of class UserDAO.
     */
    @Test
    public void testFindByEmail() {
        System.out.println("findByEmail");
        String uniq = UUID.randomUUID().toString().substring(0, 8);
        String username = "euser_" + uniq;
        String email = "e_" + uniq + "@example.com";

        UserDAO instance = new UserDAO();
        int insertedId = -1;

        try {
            insertedId = insertUserDirect(instance, username, email, "pwdhash", "READER");

            User result = instance.findByEmail(email);
            assertNotNull("findByEmail should return a user", result);
            assertEquals(username, result.getUsername());
            assertEquals(email, result.getEmail());
            assertEquals("READER", result.getRole());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("testFindByEmail failed: " + ex.getMessage());
        } finally {
            if (insertedId > 0) {
                deleteUserById(instance, insertedId);
            }
        }
    }

    // ---------- Helper methods sử dụng DBContext1 từ BaseDao (qua instance.dbc) ----------
    private int insertUserDirect(UserDAO instance, String username, String email, String pwdHash, String role) {
        String sql = "INSERT INTO users(username,email,password_hash,role) VALUES(?,?,?,?)";
        ResultSet localRs = null;
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, pwdHash);
            ps.setString(4, role);
            ps.executeUpdate();

            localRs = ps.getGeneratedKeys();
            if (localRs != null && localRs.next()) {
                return localRs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            fail("insertUserDirect failed: " + ex.getMessage());
        } finally {
            // đóng resources (dùng các biến trong BaseDao)
            try {
                if (localRs != null) {
                    localRs.close();
                    localRs = null;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                    connection = null;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return -1;
    }

    private void deleteUserById(UserDAO instance, int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                    connection = null;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteUserByUsername(UserDAO instance, String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try {
            connection = dbc.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                    connection = null;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}
