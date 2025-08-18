/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;


/**
 *
 * @author LAPTOP
 */
@WebServlet(name = "UploadImageServlet", urlPatterns = {"/UploadImageServlet"})

// Trên class: (tăng maxFileSize nếu bạn muốn cho phép file lớn hơn)
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 10L * 1024 * 1024, // 10 MB - tăng lên nếu cần
        maxRequestSize = 30L * 1024 * 1024
)

public class UploadImageServlet extends HttpServlet {

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
    private static final Set<String> ALLOWED_TYPES = new HashSet<>(Arrays.asList(
            "image/png", "image/jpeg", "image/jpg", "image/gif", "image/webp"
    ));

    // thư mục upload (Path) - khởi tạo trong init()
    private Path uploadsDir;

    @Override
    public void init() throws ServletException {
        // Read from web.xml context-param
        String cfg = getServletContext().getInitParameter("uploads.path");

        if (cfg == null || cfg.trim().isEmpty()) {
            // Fallback: use user.home if not configured
            cfg = System.getProperty("user.home")
                    + File.separator + "myapp-uploads"
                    + File.separator + "chapters";
        }

        uploadsDir = Paths.get(cfg);

        try {
            Files.createDirectories(uploadsDir);
        } catch (IOException e) {
            throw new ServletException("Unable to create uploads directory: " + uploadsDir, e);
        }

        // --- LOG LINE: print the actual path to the servlet container's log ---
        String abs = uploadsDir.toAbsolutePath().toString();
        // Write to the servlet container's log (appears in catalina.out or logs)
        getServletContext().log("Upload directory = " + abs);
        // Also print to the console for easy visibility when running in IDE
        System.out.println("Upload directory = " + abs);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            // Auth
            HttpSession session = request.getSession(false);
            model.User user = (session == null) ? null : (model.User) session.getAttribute("user");
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\":{\"message\":\"Unauthorized\"}}");
                return;
            }

            // Log all parts for debugging
            try {
                for (Part p : request.getParts()) {
                    getServletContext().log("[Upload DEBUG] part name=" + p.getName()
                            + " size=" + p.getSize() + " ct=" + p.getContentType());
                }
            } catch (Exception e) {
                getServletContext().log("[Upload DEBUG] getParts() failed", e);
            }

            // Find file part robustly (CKEditor uses "upload")
            Part filePart = null;
            try {
                filePart = request.getPart("upload");
            } catch (Exception ignore) {
            }
            if (filePart == null) {
                // fallback: pick first non-empty part that has image contentType
                for (Part p : request.getParts()) {
                    String ct = p.getContentType();
                    if (ct != null && ct.toLowerCase().startsWith("image/") && p.getSize() > 0) {
                        filePart = p;
                        break;
                    }
                }
            }

            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":{\"message\":\"No file uploaded (no suitable part found)\"}}");
                return;
            }

            long maxSize = 10L * 1024L * 1024L; // must match @MultipartConfig maxFileSize
            if (filePart.getSize() > maxSize) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":{\"message\":\"File too large (max " + (maxSize / 1024 / 1024) + "MB)\"}}");
                return;
            }

            String contentType = filePart.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":{\"message\":\"Invalid file type: " + contentType + "\"}}");
                return;
            }

            // Read bytes
            byte[] data;
            try (InputStream in = filePart.getInputStream()) {
                data = toByteArray(in);
            } catch (IOException ioe) {
                getServletContext().log("[Upload] read failed", ioe);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":{\"message\":\"Failed to read uploaded file\"}}");
                return;
            }

            // Verify image
            try (ByteArrayInputStream bin = new ByteArrayInputStream(data)) {
                BufferedImage img = ImageIO.read(bin);
                if (img == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":{\"message\":\"Uploaded file is not a valid image\"}}");
                    return;
                }
            } catch (Exception e) {
                getServletContext().log("[Upload] image verify failed", e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":{\"message\":\"Uploaded file is not a valid image\"}}");
                return;
            }

            // Build filename & write
            String submitted = filePart.getSubmittedFileName();
            String ext = "";
            if (submitted != null && submitted.lastIndexOf('.') >= 0) {
                ext = submitted.substring(submitted.lastIndexOf('.')).toLowerCase();
            }
            if (ext.isEmpty()) {
                if ("image/png".equals(contentType)) {
                    ext = ".png";
                } else if ("image/gif".equals(contentType)) {
                    ext = ".gif";
                } else {
                    ext = ".jpg";
                }
            }

            String filename = "ch_" + user.getId() + "_" + UUID.randomUUID().toString().replace("-", "") + ext;
            Path target = uploadsDir.resolve(filename).normalize();

            if (!target.startsWith(uploadsDir)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":{\"message\":\"Invalid upload path\"}}");
                return;
            }

            try {
                Files.write(target, data, StandardOpenOption.CREATE_NEW);
                getServletContext().log("[Upload] Saved file to: " + target.toString());
            } catch (Exception e) {
                getServletContext().log("[Upload] write failed", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":{\"message\":\"Cannot save file: " + e.getMessage().replace("\"", "'") + "\"}}");
                return;
            }

// build absolute URL (optional but recommended)
            String publicPath = request.getContextPath() + "/uploads/chapters/" + filename;
            String hostPort = (request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort();
            String fullUrl = request.getScheme() + "://" + request.getServerName() + hostPort + publicPath;

// set JSON content-type (you already do) and status
            response.setStatus(HttpServletResponse.SC_CREATED); // 201

// Return both fields to be safe for CKFinder and SimpleUploadAdapter
// Example: {"url":"...","uploaded":1,"fileName":"..."}
            out.print("{\"url\":\"" + fullUrl + "\",\"uploaded\":1,\"fileName\":\"" + filename + "\"}");

        } catch (Exception ex) {
            getServletContext().log("[Upload] Unexpected", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":{\"message\":\"Upload failed: " + (ex.getMessage() == null ? "error" : ex.getMessage().replace("\"", "'")) + "\"}}");
            }
        }
    }

    // nhỏ: đọc InputStream thành byte[]
    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int r;
        while ((r = in.read(buf)) != -1) {
            baos.write(buf, 0, r);
        }
        return baos.toByteArray();
    }

    // nhỏ: escape " trong JSON message
    private static String escapeForJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\"", "'").replace("\n", " ").replace("\r", " ");
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
