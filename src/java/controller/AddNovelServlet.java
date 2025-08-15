/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.NovelDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Instant;
import java.util.UUID;
import model.Novel;
import model.User;

/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "AddNovelServlet", urlPatterns = {"/AddNovelServlet"})
public class AddNovelServlet extends HttpServlet {

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
            out.println("<title>Servlet AddNovelServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddNovelServlet at " + request.getContextPath() + "</h1>");
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

        request.getRequestDispatcher("add_novel.jsp").forward(request, response);
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
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Retrieve the logged-in user from session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        // Get form parameters
        String title = request.getParameter("title");
        String otherNames = request.getParameter("other_names");
        String genre = request.getParameter("genre");
        String status = request.getParameter("status");
        String summary = request.getParameter("summary");
        String notes = request.getParameter("notes");
        boolean isSensitive = "on".equals(request.getParameter("is_sensitive"))
                || "true".equals(request.getParameter("is_sensitive"));
        boolean isPublic = !"false".equals(request.getParameter("is_public")); // default: true

        // Handle cover image upload
        String coverPath = null;
        Part coverPart = request.getPart("cover");
        if (coverPart != null && coverPart.getSize() > 0) {
            // Create uploads directory if it doesn't exist
            String uploadsDir = getServletContext().getRealPath("/") + File.separator + "uploads";
            File uploads = new File(uploadsDir);
            if (!uploads.exists()) {
                uploads.mkdirs();
            }

            // Generate a unique filename
            String submittedFileName = coverPart.getSubmittedFileName();
            String extension = "";
            if (submittedFileName != null && submittedFileName.contains(".")) {
                extension = submittedFileName.substring(submittedFileName.lastIndexOf("."));
            }
            String filename = "cover_" + user.getId() + "_" + Instant.now().toEpochMilli() + "_"
                    + UUID.randomUUID().toString().substring(0, 6) + extension;
            File file = new File(uploads, filename);

            try (InputStream is = coverPart.getInputStream()) {
                Files.copy(is, file.toPath());
                // Store the relative path for later use
                coverPath = "uploads/" + filename;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Build the Novel object
        Novel novel = new Novel();
        novel.setAuthorId(user.getId());
        novel.setTitle(title);
        novel.setOtherNames(otherNames);
        novel.setGenre(genre);
        novel.setStatus(status == null ? "ONGOING" : status);
        novel.setSummary(summary);
        novel.setNotes(notes);
        novel.setSensitive(isSensitive);
        novel.setIsPublic(isPublic);
        novel.setCoverPath(coverPath);

        // Save novel to database
        NovelDAO novelDAO = new NovelDAO();
        int newId = novelDAO.addNovel(novel);
        if (newId > 0) {
            // Success: redirect to the author's novels list or the novel detail page
            response.sendRedirect("/AddVolumeServlet");
        } else {
            // Failure: show error message
            request.setAttribute("error", "Failed to add the novel. Please try again.");
            request.getRequestDispatcher("add_novel.jsp").forward(request, response);
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
