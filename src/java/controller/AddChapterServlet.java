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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        HttpSession session = request.getSession(true); // ensure session exists
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Nếu chưa login -> redirect
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String novelIdParam = request.getParameter("novelId");
        List<Volume> volumes;

        if (novelIdParam != null && !novelIdParam.isEmpty()) {
            int novelId = Integer.parseInt(novelIdParam);

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
            // Lấy tất cả volumes của các novel thuộc author
            List<Novel> myNovels = novelDAO.listNovelsByAuthor(user.getId());
            volumes = new java.util.ArrayList<>();
            for (Novel n : myNovels) {
                volumes.addAll(volumeDAO.listVolumesByNovel(n.getId()));
            }
        }

        // --- CSRF token management: sử dụng Set để cho phép nhiều token cùng tồn tại (multi-tab) ---
        @SuppressWarnings("unchecked")
        Set<String> csrfSet = (Set<String>) session.getAttribute("csrfAddChapterTokens");
        if (csrfSet == null) {
            csrfSet = new HashSet<>();
        }
        String csrfToken = UUID.randomUUID().toString();
        csrfSet.add(csrfToken);
        session.setAttribute("csrfAddChapterTokens", csrfSet);

        request.setAttribute("csrfToken", csrfToken);

        System.out.println("[doGet] sessionId=" + session.getId() + ", added csrfToken=" + csrfToken + ", totalTokens=" + csrfSet.size());

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
        String sessionId = (session != null) ? session.getId() : "null";
        String formToken = request.getParameter("chapterCsrf");

        @SuppressWarnings("unchecked")
        Set<String> csrfSet = (session != null) ? (Set<String>) session.getAttribute("csrfAddChapterTokens") : null;
        String sessionTokensInfo = (csrfSet == null) ? "null" : ("size=" + csrfSet.size());

        System.out.println("[doPost] sessionId=" + sessionId + ", csrfSet=" + sessionTokensInfo + ", formToken=" + formToken);

        // Check login
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // CSRF validation
        if (formToken == null || csrfSet == null || !csrfSet.contains(formToken)) {
            System.out.println("[doPost] CSRF MISMATCH. formToken=" + formToken);
            request.setAttribute("error", "Invalid request (CSRF). Please try again.");
            // giữ lại token cũ để form render được
            request.setAttribute("csrfToken", formToken);
            // nạp lại volumes của author
            List<Novel> myNovels = novelDAO.listNovelsByAuthor(user.getId());
            List<Volume> volumes = new java.util.ArrayList<>();
            for (Novel n : myNovels) {
                volumes.addAll(volumeDAO.listVolumesByNovel(n.getId()));
            }
            request.setAttribute("volumes", volumes);
            request.getRequestDispatcher("addChapter.jsp").forward(request, response);
            return;
        }

        try {
            int volumeId = Integer.parseInt(request.getParameter("volume_id"));
            int chapterNumber = Integer.parseInt(request.getParameter("chapter_number"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            // validate
            if (chapterNumber <= 0) {
                request.setAttribute("error", "Chapter number must be greater than 0.");
            } else if (title == null || title.trim().isEmpty()) {
                request.setAttribute("error", "Chapter title cannot be empty.");
            } else if (content == null || content.trim().isEmpty()) {
                request.setAttribute("error", "Chapter content cannot be empty.");
            }

            if (request.getAttribute("error") != null) {
                // nếu có lỗi -> giữ token cũ
                request.setAttribute("csrfToken", formToken);
                List<Novel> myNovels = novelDAO.listNovelsByAuthor(user.getId());
                List<Volume> volumes = new java.util.ArrayList<>();
                for (Novel n : myNovels) {
                    volumes.addAll(volumeDAO.listVolumesByNovel(n.getId()));
                }
                request.setAttribute("volumes", volumes);
                request.getRequestDispatcher("addChapter.jsp").forward(request, response);
                return;
            }

            // Permission check
            Volume vol = volumeDAO.getVolumeById(volumeId);
            Novel novel = (vol != null) ? novelDAO.getNovelById(vol.getNovelId()) : null;
            if (vol == null || novel == null || novel.getAuthorId() != user.getId()) {
                request.setAttribute("error", "You do not have permission to add a chapter to this volume.");
                request.setAttribute("csrfToken", formToken);
                request.setAttribute("volumes", volumeDAO.listVolumesByNovel(novel != null ? novel.getId() : -1));
                request.getRequestDispatcher("addChapter.jsp").forward(request, response);
                return;
            }

            // Duplicate check
            if (chapterDAO.existsChapter(volumeId, chapterNumber)) {
                request.setAttribute("error", "Chapter " + chapterNumber + " already exists in this volume.");
                request.setAttribute("csrfToken", formToken);
                request.setAttribute("volumes", volumeDAO.listVolumesByNovel(novel.getId()));
                request.getRequestDispatcher("addChapter.jsp").forward(request, response);
                return;
            }

            // Word count
            int wc = content.trim().isEmpty() ? 0 : content.trim().split("\\s+").length;

            Chapter c = new Chapter();
            c.setVolumeId(volumeId);
            c.setChapterNumber(chapterNumber);
            c.setTitle(title.trim());
            c.setContent(content);
            c.setWordCount(wc);

            int newId = chapterDAO.createChapter(c);
            if (newId > 0) {
                // ✅ chỉ khi thành công mới remove token
                csrfSet.remove(formToken);
                if (csrfSet.isEmpty()) {
                    session.removeAttribute("csrfAddChapterTokens");
                } else {
                    session.setAttribute("csrfAddChapterTokens", csrfSet);
                }
                response.sendRedirect("/MyNovelsServlet");
            } else {
                request.setAttribute("error", "Failed to add chapter. Please try again.");
                request.setAttribute("csrfToken", formToken);
                request.setAttribute("volumes", volumeDAO.listVolumesByNovel(novel.getId()));
                request.getRequestDispatcher("addChapter.jsp").forward(request, response);
            }

        } catch (NumberFormatException nfe) {
            request.setAttribute("error", "Invalid numeric input.");
            request.setAttribute("csrfToken", formToken);
            List<Novel> myNovels = novelDAO.listNovelsByAuthor(user.getId());
            List<Volume> volumes = new java.util.ArrayList<>();
            for (Novel n : myNovels) {
                volumes.addAll(volumeDAO.listVolumesByNovel(n.getId()));
            }
            request.setAttribute("volumes", volumes);
            request.getRequestDispatcher("addChapter.jsp").forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "System error: " + ex.getMessage());
            request.setAttribute("csrfToken", formToken);
            List<Novel> myNovels = novelDAO.listNovelsByAuthor(user.getId());
            List<Volume> volumes = new java.util.ArrayList<>();
            for (Novel n : myNovels) {
                volumes.addAll(volumeDAO.listVolumesByNovel(n.getId()));
            }
            request.setAttribute("volumes", volumes);
            request.getRequestDispatcher("addChapter.jsp").forward(request, response);
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
