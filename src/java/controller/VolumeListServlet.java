/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.ChapterDAO;
import DAO.VolumeDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Chapter;
import model.Volume;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "VolumeListServlet", urlPatterns = {"/VolumeListServlet"})
public class VolumeListServlet extends HttpServlet {

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
            out.println("<title>Servlet VolumeListServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet VolumeListServlet at " + request.getContextPath() + "</h1>");
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
    private VolumeDAO volumeDAO;
    private ChapterDAO chapterDAO;

    @Override
    public void init() throws ServletException {
        volumeDAO = new VolumeDAO();
        chapterDAO = new ChapterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String novelIdStr = request.getParameter("novelId");
        if (novelIdStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing novelId");
            return;
        }

        int novelId;
        try {
            novelId = Integer.parseInt(novelIdStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid novelId");
            return;
        }

        // Lấy list volumes
        List<Volume> volumes = volumeDAO.listVolumesByNovel(novelId);

        // Lấy chapters theo từng volume
        Map<Integer, List<Chapter>> chaptersMap = new HashMap<>();
        if (volumes != null) {
            for (Volume v : volumes) {
                List<Chapter> chapters = chapterDAO.listChaptersByVolume(v.getId());
                chaptersMap.put(v.getId(), chapters);
            }
        } else {
            volumes = Collections.emptyList();
        }

        // --- SANITIZE: tạo map chứa mô tả đã được làm sạch (safe HTML) ---
        // Sử dụng Safelist.basicWithImages() để cho phép thẻ cơ bản + <img>
        Safelist safelist = Safelist.basicWithImages();
        Map<Integer, String> safeDescriptions = new HashMap<>();
        for (Volume v : volumes) {
            String raw = v.getDescription();
            if (raw == null) {
                safeDescriptions.put(v.getId(), "");
            } else {
                // Jsoup.clean sẽ loại bỏ/escape các thẻ/attribute không an toàn
                String clean = Jsoup.clean(raw, safelist);
                safeDescriptions.put(v.getId(), clean);
            }
        }

        // Set attribute cho JSP
        request.setAttribute("volumes", volumes);
        request.setAttribute("chaptersMap", chaptersMap);
        // attribute mới: chứa HTML đã clean cho mỗi volume
        request.setAttribute("safeDescriptions", safeDescriptions);

        // Forward đến view
        request.getRequestDispatcher("volume_list.jsp").forward(request, response);
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
        processRequest(request, response);
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
