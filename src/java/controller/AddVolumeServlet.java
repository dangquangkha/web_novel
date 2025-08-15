/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
import model.Novel;
import model.User;
import model.Volume;

/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "AddVolumeServlet", urlPatterns = {"/AddVolumeServlet"})
public class AddVolumeServlet extends HttpServlet {

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
            out.println("<title>Servlet AddVolumeServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddVolumeServlet at " + request.getContextPath() + "</h1>");
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
        // Lấy author_id từ session
        HttpSession session = request.getSession(false);
        Integer authorId = null;
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                authorId = user.getId();
            } else {
                // fallback nếu bạn vẫn dùng verifyUserId ở chỗ khác
                Integer vid = (Integer) session.getAttribute("verifyUserId");
                if (vid != null) {
                    authorId = vid;
                }
            }
        }

        if (authorId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }


        // Lấy danh sách novel theo author_id
        NovelDAO dao = new NovelDAO();
        List<Novel> listNovel = dao.listNovelsByAuthor(authorId);

        // Gửi dữ liệu sang JSP
        request.setAttribute("listNovel", listNovel);
        request.getRequestDispatcher("addVolume.jsp").forward(request, response);
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
        request.setCharacterEncoding("UTF-8");

        NovelDAO dao = new NovelDAO();

        HttpSession session = request.getSession(false);
        if (session == null) {
            request.setAttribute("error", "You must be logged in to perform this action.");
            doGet(request, response);
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            request.setAttribute("error", "You must be logged in to perform this action.");
            doGet(request, response);
            return;
        }

        try {
            int novelId = Integer.parseInt(request.getParameter("novel_id"));
            int volumeNumber = Integer.parseInt(request.getParameter("volume_number"));
            String title = request.getParameter("title");
            String description = request.getParameter("description");

            // validate
            if (volumeNumber <= 0) {
                request.setAttribute("error", "Volume number must be greater than 0.");
                doGet(request, response);
                return;
            }
            if (title == null) {
                title = "";
            }

            // permission: check whether the current user is the author of this novel (if required)
            Novel novel = dao.getNovelById(novelId);
            if (novel == null) {
                request.setAttribute("error", "Novel does not exist.");
                doGet(request, response);
                return;
            }
            // If you want only the author to be able to add volumes -> uncomment:
            if (novel.getAuthorId() != user.getId()) {
                request.setAttribute("error", "You do not have permission to add a volume to this novel.");
                doGet(request, response);
                return;
            }

            VolumeDAO vdao = new VolumeDAO();

            // check duplicate
            if (vdao.existsVolume(novelId, volumeNumber)) {
                request.setAttribute("error", "Volume " + volumeNumber + " for this novel already exists.");
                doGet(request, response);
                return;
            }

            // Create Volume and save
            Volume v = new Volume();
            v.setNovelId(novelId);
            v.setVolumeNumber(volumeNumber);
            v.setTitle(title.trim());
            v.setDescription(description);

            int newId = vdao.createVolume(v);
            if (newId > 0) {
                // Successfully added -> redirect to this novel's volume list
                response.sendRedirect(request.getContextPath() + "/AddChapterServlet?novelId=" + novelId);
            } else {
                request.setAttribute("error", "Failed to add volume. Please try again.");
                doGet(request, response);
            }

        } catch (NumberFormatException nfe) {
            request.setAttribute("error", "Invalid numeric input.");
            doGet(request, response);
        } catch (ServletException | IOException ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Server error: " + ex.getMessage());
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
