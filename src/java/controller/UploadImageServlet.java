/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import model.User;

/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "UploadImageServlet", urlPatterns = {"/UploadImageServlet"})
public class UploadImageServlet extends HttpServlet {

    private static final Set<String> ALLOWED_TYPES = new HashSet<>(Arrays.asList(
            "image/png", "image/jpeg", "image/jpg", "image/gif", "image/webp"
    ));

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
            out.println("<title>Servlet UploadImageServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UploadImageServlet at " + request.getContextPath() + "</h1>");
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 1. Auth: chỉ cho user đã đăng nhập upload
        HttpSession session = request.getSession(false);
        User user = null;
        if (session == null || (user = (User) session.getAttribute("user")) == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":{\"message\":\"Unauthorized\"}}");
            return;
        }

        try {
            Part filePart = request.getPart("upload"); // CKEditor 5 uses 'upload'
            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":{\"message\":\"No file uploaded\"}}");
                return;
            }

            // 2. Check size
            long maxSize = 5L * 1024L * 1024L; // 5MB
            if (filePart.getSize() > maxSize) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":{\"message\":\"File too large (max 5MB)\"}}");
                return;
            }

            // 3. Check content type
            String contentType = filePart.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":{\"message\":\"Invalid file type\"}}");
                return;
            }

            // 4. Save file to uploads/chapters
            String uploadsDir = getServletContext().getRealPath("/uploads/chapters");
            File uploadDirFile = new File(uploadsDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // keep extension from original filename
            String submitted = filePart.getSubmittedFileName();
            String ext = "";
            if (submitted != null && submitted.lastIndexOf('.') >= 0) {
                ext = submitted.substring(submitted.lastIndexOf('.'));
            }
            String filename = "ch_" + user.getId() + "_" + UUID.randomUUID().toString().replace("-", "") + ext;
            File target = new File(uploadDirFile, filename);

            try (InputStream in = filePart.getInputStream()) {
                Files.copy(in, target.toPath());
            }

            // 5. Build public URL
            String fileUrl = request.getContextPath() + "/uploads/chapters/" + filename;

            // 6. Return JSON for CKEditor 5: { "url": "..." }
            out.print("{\"url\":\"" + fileUrl + "\"}");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":{\"message\":\"Upload failed: " + ex.getMessage().replace("\"", "'") + "\"}}");
        } finally {
            out.flush();
            out.close();
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
