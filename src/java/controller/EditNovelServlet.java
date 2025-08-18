/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.NovelDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Paths;
import model.Novel;

/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "EditNovelServlet", urlPatterns = {"/EditNovelServlet"})

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,  // 1MB: lưu file tạm trong bộ nhớ trước khi ghi ra disk
    maxFileSize = 1024 * 1024 * 10,       // 10MB: giới hạn file upload
    maxRequestSize = 1024 * 1024 * 50     // 50MB: giới hạn tổng request
)

public class EditNovelServlet extends HttpServlet {

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
            out.println("<title>Servlet EditNovelServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditNovelServlet at " + request.getContextPath() + "</h1>");
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));

        try {
            NovelDAO dao = new NovelDAO();
            Novel novel = dao.getNovelById(id);

            if (novel == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Novel not found");
                return;
            }
            System.out.println(">>> req.getParameter(\"id\") = " + request.getParameter("id"));

            request.setAttribute("novel", novel);
            request.getRequestDispatcher("edit_novel.jsp").forward(request, response);
        } catch (ServletException | IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing novel ID");
            return;
        }
        int id = Integer.parseInt(idStr);

        System.out.println(">>> req.getParameter(\"id\") = " + request.getParameter("id"));

        String title = request.getParameter("title");
        String otherNames = request.getParameter("otherNames");
        String genre = request.getParameter("genre");
        String status = request.getParameter("status");
        String summary = request.getParameter("summary");

        // Xử lý file upload
        Part filePart = request.getPart("cover");
        String coverPath;
        if (filePart != null && filePart.getSize() > 0) {
            // Lấy tên file gốc
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

            // Thư mục upload (trong webapp)
            String uploadDir = getServletContext().getRealPath("/uploads");
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // Lưu file
            File file = new File(uploadDirFile, fileName);
            filePart.write(file.getAbsolutePath());

            // Đường dẫn lưu trong DB (relative path)
            coverPath = request.getContextPath() + "/uploads/" + fileName;
        } else {
            // Không upload file mới => giữ ảnh cũ
            coverPath = request.getParameter("coverPathOld");
        }

        Novel novel = new Novel();
        novel.setId(id);
        novel.setTitle(title);
        novel.setOtherNames(otherNames);
        novel.setGenre(genre);
        novel.setStatus(status);
        novel.setCoverPath(coverPath);
        novel.setSummary(summary);

        try {
            NovelDAO dao = new NovelDAO();
            dao.updateNovel(novel);
            response.sendRedirect(request.getContextPath() + "/MyNovelsServlet");
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
