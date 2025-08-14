/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.ChapterDAO;
import DAO.NovelDAO;
import DAO.VolumeDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import model.Chapter;
import model.Novel;
import model.User;
import model.Volume;

/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "AddChapterServlet", urlPatterns = {"/AddChapterServlet"})
public class AddChapterServlet extends HttpServlet {

    private VolumeDAO volumeDAO = new VolumeDAO();
    private ChapterDAO chapterDAO = new ChapterDAO();
    private NovelDAO novelDAO = new NovelDAO();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AddChapterServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddChapterServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }

        String novelIdParam = request.getParameter("novelId");
        List<Volume> volumes;

        if (novelIdParam != null && !novelIdParam.isEmpty()) {
            int novelId = Integer.parseInt(novelIdParam);
            // If needed, check permission: if user is not the author, redirect
            if (user == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            Novel novel = novelDAO.getNovelById(novelId);
            if (novel == null) {
                request.setAttribute("error", "Novel does not exist.");
                request.getRequestDispatcher("addChapter.jsp").forward(request, response);
                return;
            }
            if (novel.getAuthorId() != user.getId()) {
                request.setAttribute("error", "You do not have permission to add a chapter to this novel.");
                request.getRequestDispatcher("addChapter.jsp").forward(request, response);
                return;
            }

            volumes = volumeDAO.listVolumesByNovel(novelId);
            request.setAttribute("selectedNovel", novel);
        } else {
            // No novelId provided: get all volumes from all novels of the logged-in author
            if (user == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            // Get novels of the author, then get volumes for each novel
            List<Novel> myNovels = novelDAO.listNovelsByAuthor(user.getId());
            // Combine all volumes from each novel
            volumes = new java.util.ArrayList<>();
            for (Novel n : myNovels) {
                volumes.addAll(volumeDAO.listVolumesByNovel(n.getId()));
            }
        }

        String csrfToken = UUID.randomUUID().toString();
        session.setAttribute("csrfAddChapter", csrfToken);
        request.setAttribute("csrfToken", csrfToken);

        request.setAttribute("volumes", volumes);
        request.getRequestDispatcher("addChapter.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        if (user == null) {
            request.setAttribute("error", "You must be logged in.");
            doGet(request, response);
            return;
        }

        // CSRF token validation
        String sessionToken = (String) session.getAttribute("csrfAddChapter");
        String formToken = request.getParameter("_csrf");
        if (sessionToken == null || formToken == null || !sessionToken.equals(formToken)) {
            request.setAttribute("error", "Yêu cầu không hợp lệ (CSRF). Vui lòng thử lại.");
            doGet(request, response);
            return;
        }
        // One-time token: remove from session (prevent reuse)
        session.removeAttribute("csrfAddChapter");

        try {
            int volumeId = Integer.parseInt(request.getParameter("volume_id"));
            int chapterNumber = Integer.parseInt(request.getParameter("chapter_number"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            if (chapterNumber <= 0) {
                request.setAttribute("error", "Chapter number must be greater than 0.");
                doGet(request, response);
                return;
            }
            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("error", "Chapter title cannot be empty.");
                doGet(request, response);
                return;
            }
            if (content == null || content.trim().isEmpty()) {
                request.setAttribute("error", "Chapter content cannot be empty.");
                doGet(request, response);
                return;
            }

            // Check permission: volume -> novel -> author
            Volume vol = volumeDAO.getVolumeById(volumeId);
            if (vol == null) {
                request.setAttribute("error", "Volume does not exist.");
                doGet(request, response);
                return;
            }
            Novel novel = novelDAO.getNovelById(vol.getNovelId());
            if (novel == null || novel.getAuthorId() != user.getId()) {
                request.setAttribute("error", "You do not have permission to add a chapter to this volume.");
                doGet(request, response);
                return;
            }

            // Check for duplicate chapter number
            if (chapterDAO.existsChapter(volumeId, chapterNumber)) {
                request.setAttribute("error", "Chapter " + chapterNumber + " already exists in this volume.");
                doGet(request, response);
                return;
            }

            // Simple word count (split by whitespace)
            int wc = content.trim().isEmpty() ? 0 : content.trim().split("\\s+").length;

            Chapter c = new Chapter();
            c.setVolumeId(volumeId);
            c.setChapterNumber(chapterNumber);
            c.setTitle(title.trim());
            c.setContent(content);
            c.setWordCount(wc);

            int newId = chapterDAO.createChapter(c);
            if (newId > 0) {
                // Redirect to a page showing chapters of this volume
                response.sendRedirect("viewChapters.jsp?volumeId=" + volumeId);
            } else {
                request.setAttribute("error", "Failed to add chapter. Please try again.");
                doGet(request, response);
            }

        } catch (NumberFormatException nfe) {
            request.setAttribute("error", "Invalid numeric input.");
            doGet(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "System error: " + ex.getMessage());
            doGet(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
