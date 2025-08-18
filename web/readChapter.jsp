<%-- 
    Document   : readChapter
    Created on : Aug 15, 2025, 4:28:48 PM
    Author     : LAPTOP
--%>

<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title><c:out value="${chapter.title}" /></title>

        <!-- Bootstrap 5 CSS (CDN) -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

        <style>
            /* Make images inside chapter content responsive */
            .chapter-content img {
                max-width: 100%;
                height: auto;
            }

            /* Comfortable line-height for reading */
            .chapter-card {
                line-height: 1.8;
            }
        </style>
    </head>
    <body class="bg-light">
        <div class="container py-4">
            <div class="row justify-content-center">
                <div class="col-lg-9 col-md-10">
                    <div class="card shadow-sm chapter-card">
                        <div class="card-body">
                            <!-- Chapter title -->
                            <h1 class="card-title text-center mb-3"><c:out value="${chapter.title}" /></h1>

                            <!-- Optional small metadata -->
                            <div class="d-flex justify-content-center mb-3">
                                <small class="text-muted">Volume ID: <c:out value="${chapter.volumeId}" /></small>
                            </div>

                            <hr />

                            <!-- Chapter content (assumed sanitized) -->
                            <div class="chapter-content">
                                <c:out value="${contentHtml}" escapeXml="false" />
                            </div>

                            <!-- Back button -->
                            <div class="mt-4">
                                <c:url var="backUrl" value="/ViewChaptersServlet">
                                    <c:param name="volumeId" value="${chapter.volumeId}" />
                                </c:url>

                                <a href="${backUrl}" class="btn btn-outline-primary">
                                    &larr; Back to chapter list
                                </a>
                            </div>
                                    
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bootstrap 5 JS bundle -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

