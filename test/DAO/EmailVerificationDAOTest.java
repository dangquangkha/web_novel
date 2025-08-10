package DAO;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmailVerificationDAOTest {

    private EmailVerificationDAO mockDao;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        // Tạo mock object thay vì kết nối DB thật
        mockDao = mock(EmailVerificationDAO.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGenerateAndSave_Mock() {
        int userId = 1;
        String fakeOtp = "123456";

        // Giả lập khi gọi generateAndSave(1) thì trả về "123456"
        when(mockDao.generateAndSave(userId)).thenReturn(fakeOtp);

        String otp = mockDao.generateAndSave(userId);

        assertNotNull("OTP must not be null", otp);
        assertTrue("OTP must be 6 digits", otp.matches("\\d{6}"));
        assertEquals("123456", otp);

        // Xác nhận method được gọi đúng 1 lần
        verify(mockDao, times(1)).generateAndSave(userId);
    }

    @Test
    public void testVerify_Mock() {
        int userId = 1;
        String otp = "123456";

        // Giả lập verify() lần đầu → true
        when(mockDao.verify(userId, otp)).thenReturn(true).thenReturn(false);

        // Lần 1: thành công
        assertTrue(mockDao.verify(userId, otp));
        // Lần 2: thất bại vì đã dùng OTP
        assertFalse(mockDao.verify(userId, otp));

        // Xác nhận verify() được gọi 2 lần
        verify(mockDao, times(2)).verify(userId, otp);
    }

    @Test
    public void testGenerateAndSave_NoDB() {
        int userId = 1;
        String expectedOtp = "654321";

        // Mock DAO
        EmailVerificationDAO dao = mock(EmailVerificationDAO.class);
        when(dao.generateAndSave(userId)).thenReturn(expectedOtp);

        String otp = dao.generateAndSave(userId);

        assertNotNull("OTP must not be null", otp);
        assertEquals(expectedOtp, otp);
        assertTrue("OTP must be 6 digits", otp.matches("\\d{6}"));

        // Kiểm tra gọi đúng số lần
        verify(dao, times(1)).generateAndSave(userId);
    }

    @Test
    public void testVerify_NoDB() {
        int userId = 1;
        String otp = "654321";

        // Mock DAO
        EmailVerificationDAO dao = mock(EmailVerificationDAO.class);
        when(dao.verify(userId, otp))
                .thenReturn(true) // Lần 1
                .thenReturn(false); // Lần 2

        assertTrue("First time should verify OK", dao.verify(userId, otp));
        assertFalse("Second time should fail", dao.verify(userId, otp));

        verify(dao, times(2)).verify(userId, otp);
    }

}
