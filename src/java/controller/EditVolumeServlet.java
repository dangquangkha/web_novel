/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.VolumeDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Volume;

/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "EditVolumeServlet", urlPatterns = {"/EditVolumeServlet"})
public class EditVolumeServlet extends HttpServlet {

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
            out.println("<title>Servlet EditVolumeServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditVolumeServlet at " + request.getContextPath() + "</h1>");
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
    private VolumeDAO volumeDAO = new VolumeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int volumeId = Integer.parseInt(request.getParameter("id"));
        Volume volume = volumeDAO.getVolumeById(volumeId);

        if (volume == null) {
            response.sendRedirect("MyNovelsServlet");
            return;
        }
        request.setAttribute("volume", volume);
        request.getRequestDispatcher("edit_volume.jsp").forward(request, response);
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
        int id = Integer.parseInt(request.getParameter("id"));
        int novelId = Integer.parseInt(request.getParameter("novel_id"));
        int volumeNumber = Integer.parseInt(request.getParameter("volume_number"));
        String title = request.getParameter("title");
        String description = request.getParameter("description");

        Volume v = new Volume();
        v.setId(id);
        v.setNovelId(novelId);
        v.setVolumeNumber(volumeNumber);
        v.setTitle(title);
        v.setDescription(description);

        volumeDAO.updateVolume(v);
        response.sendRedirect("VolumeListServlet?novelId=" + novelId);
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
