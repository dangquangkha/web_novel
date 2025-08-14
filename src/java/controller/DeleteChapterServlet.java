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
import model.Chapter;
import model.Novel;
import model.User;
import model.Volume;

/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "DeleteChapterServlet", urlPatterns = {"/DeleteChapterServlet"})
public class DeleteChapterServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private ChapterDAO chapterDAO = new ChapterDAO();
    private VolumeDAO volumeDAO = new VolumeDAO();
    private NovelDAO novelDAO = new NovelDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DeleteChapterServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DeleteChapterServlet at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int chapterId = Integer.parseInt(request.getParameter("chapterId"));

            Chapter c = chapterDAO.getChapterById(chapterId);
            if (c == null) {
                request.setAttribute("error", "Chương không tồn tại.");
                request.getRequestDispatcher("viewChapters.jsp").forward(request, response);
                return;
            }

            Volume vol = volumeDAO.getVolumeById(c.getVolumeId());
            if (vol == null) {
                request.setAttribute("error", "Tập không tồn tại.");
                request.getRequestDispatcher("viewChapters.jsp").forward(request, response);
                return;
            }

            Novel novel = novelDAO.getNovelById(vol.getNovelId());
            // chỉ tác giả hoặc ADMIN được xóa
            if (novel.getAuthorId() != user.getId() && !"ADMIN".equalsIgnoreCase(user.getRole())) {
                request.setAttribute("error", "Bạn không có quyền xóa chương này.");
                request.getRequestDispatcher("viewChapters.jsp").forward(request, response);
                return;
            }

            boolean ok = chapterDAO.deleteChapter(chapterId);
            if (ok) {
                response.sendRedirect("viewChapters.jsp?volumeId=" + vol.getId());
            } else {
                request.setAttribute("error", "Xóa thất bại.");
                request.getRequestDispatcher("viewChapters.jsp?volumeId=" + vol.getId()).forward(request, response);
            }
        } catch (NumberFormatException nfe) {
            response.sendRedirect("my_novels.jsp");
        } catch (ServletException | IOException ex) {
            response.sendRedirect("my_novels.jsp");
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
