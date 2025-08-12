/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.EmailVerificationDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "VerifyEmailServlet", urlPatterns = {"/VerifyEmailServlet"})
public class VerifyEmailServlet extends HttpServlet {

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
            out.println("<title>Servlet VerifyEmailServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet VerifyEmailServlet at " + request.getContextPath() + "</h1>");
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
        String code = request.getParameter("otp");
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("verifyUserId");

        // Check if there is no verification process in progress
        if (userId == null) {
            request.setAttribute("error", "No verification process found. Please register again.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Check if the OTP code is empty
        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("error", "Please enter the OTP code.");
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
            return;
        }

        EmailVerificationDAO evDao = new EmailVerificationDAO();
        if (evDao.verify(userId, code.trim())) {
            // OTP verified successfully
            session.removeAttribute("verifyUserId");
            request.setAttribute("msg", "Verification successful. You can now log in.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            // OTP is invalid or expired
            request.setAttribute("error", "The OTP is invalid or has expired.");
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
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
