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
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;
import model.Novel;
import model.User;

/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "AddNovelServlet", urlPatterns = {"/AddNovelServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB: file lớn hơn sẽ được ghi tạm ra disk
        maxFileSize = 5L * 1024 * 1024, // 5 MB: tối đa 1 file
        maxRequestSize = 20L * 1024 * 1024, // 20 MB: tổng kích thước request (files + fields)
        location = "" // optional: thư mục tạm; "" => container temp
)

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
        // Ensure session + user
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        String sessionId = (session != null) ? session.getId() : "null";
        String userInfo = (user != null) ? ("id=" + user.getId()) : "null";

        getServletContext().log("[AddVolume] Enter doGet - sessionId=" + sessionId + ", user=" + userInfo);

        if (session == null || user == null) {
            getServletContext().log("[AddVolume] No session/user -> redirect to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Important: set encoding before reading parameters
        request.setCharacterEncoding("UTF-8");

        // --- Log start ---
        getServletContext().log("[AddNovel] Start processing AddNovel for userId=" + user.getId());

        // Read parameters
        String title = trimToNull(request.getParameter("title"));
        String otherNames = trimToEmpty(request.getParameter("other_names"));
        String genre = trimToEmpty(request.getParameter("genre"));
        String status = trimToEmpty(request.getParameter("status"));
        String summary = trimToEmpty(request.getParameter("summary"));
        String notes = trimToEmpty(request.getParameter("notes"));
        boolean isSensitive = "on".equals(request.getParameter("is_sensitive"))
                || "true".equals(request.getParameter("is_sensitive"));
        boolean isPublic = !"false".equals(request.getParameter("is_public")); // default true

        // Keep form values to re-populate in case of error
        request.setAttribute("form_title", title);
        request.setAttribute("form_other_names", otherNames);
        request.setAttribute("form_genre", genre);
        request.setAttribute("form_status", status);
        request.setAttribute("form_summary", summary);
        request.setAttribute("form_notes", notes);
        request.setAttribute("form_isSensitive", isSensitive);
        request.setAttribute("form_isPublic", isPublic);

        // Basic validation
        if (title == null || title.length() < 2) {
            String msg = "Invalid title. Please enter a title (at least 2 characters).";
            getServletContext().log("[AddNovel][Validation] " + msg + " - title=" + title);
            request.setAttribute("error", msg);
            request.getRequestDispatcher("add_novel.jsp").forward(request, response);
            return;
        }

        // Log parameter summary (truncate long fields)
        getServletContext().log(String.format("[AddNovel] params: title='%s', otherNames='%s', genre='%s', status='%s', summaryLen=%d, notesLen=%d, isSensitive=%b, isPublic=%b",
                safeForLog(title), safeForLog(otherNames), safeForLog(genre), safeForLog(status),
                safeLength(summary), safeLength(notes), isSensitive, isPublic));

        // Handle cover upload
        String coverPath = null;
        Part coverPart = null;
        try {
            coverPart = request.getPart("cover");
        } catch (IllegalStateException ex) {
            // file too large or multipart not configured
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            getServletContext().log("[AddNovel][Upload] request.getPart threw IllegalStateException: " + sw.toString());
            request.setAttribute("error", "Upload error: file too large or server not configured for multipart.");
            request.getRequestDispatcher("add_novel.jsp").forward(request, response);
            return;
        } catch (ServletException | IOException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            getServletContext().log("[AddNovel][Upload] request.getPart exception: " + sw.toString());
            request.setAttribute("error", "Error reading uploaded file. Please try again.");
            request.getRequestDispatcher("add_novel.jsp").forward(request, response);
            return;
        }

        if (coverPart != null && coverPart.getSize() > 0) {
            getServletContext().log("[AddNovel][Upload] Received file: name=" + coverPart.getSubmittedFileName() + ", size=" + coverPart.getSize());

            // Validate file size (e.g., max 5MB)
            final long MAX_SIZE = 5L * 1024L * 1024L; // 5 MB
            if (coverPart.getSize() > MAX_SIZE) {
                String msg = "Cover image is too large. Please choose a file smaller than 5MB.";
                getServletContext().log("[AddNovel][Upload] " + msg + " size=" + coverPart.getSize());
                request.setAttribute("error", msg);
                request.getRequestDispatcher("add_novel.jsp").forward(request, response);
                return;
            }

            // Validate content type (simple check)
            String contentType = coverPart.getContentType();
            if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
                String msg = "Uploaded file is not an image. Please select an image file.";
                getServletContext().log("[AddNovel][Upload] Invalid contentType=" + contentType);
                request.setAttribute("error", msg);
                request.getRequestDispatcher("add_novel.jsp").forward(request, response);
                return;
            }

            // Prepare uploads directory
            String uploadsDir = getServletContext().getRealPath("/") + File.separator + "uploads";
            File uploads = new File(uploadsDir);
            try {
                if (!uploads.exists()) {
                    boolean ok = uploads.mkdirs();
                    getServletContext().log("[AddNovel][Upload] Created uploads dir: " + uploadsDir + " result=" + ok);
                }
            } catch (SecurityException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                getServletContext().log("[AddNovel][Upload] Cannot create uploads dir: " + sw.toString());
                request.setAttribute("error", "Cannot create directory for storing images on the server. Check write permissions.");
                request.getRequestDispatcher("add_novel.jsp").forward(request, response);
                return;
            }

            // Build filename
            String submittedFileName = coverPart.getSubmittedFileName();
            String extension = "";
            if (submittedFileName != null && submittedFileName.contains(".")) {
                extension = submittedFileName.substring(submittedFileName.lastIndexOf("."));
            }
            String filename = "cover_" + user.getId() + "_" + Instant.now().toEpochMilli() + "_"
                    + UUID.randomUUID().toString().substring(0, 6) + extension;
            File file = new File(uploads, filename);

            // Copy file
            try (InputStream is = coverPart.getInputStream()) {
                Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                coverPath = "uploads/" + filename; // relative path to store in DB
                getServletContext().log("[AddNovel][Upload] Saved cover to: " + file.getAbsolutePath());
            } catch (IOException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                getServletContext().log("[AddNovel][Upload] Failed to save file: " + sw.toString());
                request.setAttribute("error", "Error while saving the image file. Please try again.");
                request.getRequestDispatcher("add_novel.jsp").forward(request, response);
                return;
            }
        } else {
            getServletContext().log("[AddNovel][Upload] No cover file uploaded (part is null or size == 0)");
        }

        // Build Novel object
        Novel novel = new Novel();
        novel.setAuthorId(user.getId());
        novel.setTitle(title);
        novel.setOtherNames(otherNames);
        novel.setGenre(genre);
        novel.setStatus(status == null || status.isEmpty() ? "ONGOING" : status);
        novel.setSummary(summary);
        novel.setNotes(notes);
        novel.setSensitive(isSensitive);
        novel.setIsPublic(isPublic);
        novel.setCoverPath(coverPath);

        // Save to DB with detailed error handling
        NovelDAO novelDAO = new NovelDAO();
        try {
            int newId = novelDAO.addNovel(novel);
            if (newId > 0) {
                getServletContext().log("[AddNovel] Successfully added novel id=" + newId + " by userId=" + user.getId());
                response.sendRedirect(request.getContextPath() + "/AddVolumeServlet?novelId=" + newId);
            } else {
                // DAO returned non-positive id -> treat as failure
                String msg = "Unable to add novel. Please try again later.";
                getServletContext().log("[AddNovel][DB] DAO returned id=" + newId + " for userId=" + user.getId());
                request.setAttribute("error", msg);
                request.getRequestDispatcher("add_novel.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            // Log full stacktrace to server log
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            getServletContext().log("[AddNovel][DB] Exception while saving novel: " + sw.toString());

            // Provide the user with a friendly message and a short hint
            request.setAttribute("error", "An error occurred while saving the novel to the database. Please check the input fields and try again.");
            // Optionally provide a short developer-facing hint (not full stack) for debugging in UI:
            request.setAttribute("errorDetail", ex.getClass().getSimpleName() + ": " + safeForLog(ex.getMessage()));

            request.getRequestDispatcher("add_novel.jsp").forward(request, response);
            return;
        }
    }

    /* -------------------------
 * Helper utility methods
 * ------------------------- */
    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private static String trimToEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private static int safeLength(String s) {
        return s == null ? 0 : s.length();
    }

    private static String safeForLog(String s) {
        if (s == null) {
            return "";
        }
        String t = s.replaceAll("\\s+", " ");
        if (t.length() > 200) {
            return t.substring(0, 200) + "...(truncated)";
        }
        return t;
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
