<%-- 
    Document   : volume_list
    Created on : Aug 17, 2025, 7:58:20 PM
    Author     : LAPTOP
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Volume List</title>
        <!-- Bootstrap 5 -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="container py-4">

        <h1 class="mb-4">Volume List</h1>

        <!-- If there are volumes -->
        <c:if test="${not empty volumes}">
            <c:forEach var="v" items="${volumes}">
                <div class="card mb-4 shadow-sm">
                    <div class="card-body">
                        <h4 class="card-title">
                            Volume ${v.volumeNumber}: ${v.title}
                        </h4>
                        <p class="card-text">
                            <c:out value="${safeDescriptions[v.id]}" escapeXml="false" default="No description available."/>
                        </p>

                        <h5 class="mt-3">Chapters:</h5>
                        <c:set var="chapters" value="${chaptersMap[v.id]}"/>

                        <c:if test="${not empty chapters}">
                            <ul class="list-group list-group-flush">
                                <c:forEach var="c" items="${chapters}">
                                    <li class="list-group-item">
                                        <a href="${pageContext.request.contextPath}/ReadChapterServlet?chapterId=${c.id}" class="text-decoration-none">
                                            Chapter ${c.chapterNumber}: ${c.title}
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:if>

                        <c:if test="${empty chapters}">
                            <p class="text-muted"><i>No chapters available.</i></p>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
        </c:if>

        <!-- If there are no volumes -->
        <c:if test="${empty volumes}">
            <div class="alert alert-info">
                No volumes available for this novel.
            </div>
        </c:if>

        <!-- Bootstrap JS (if needed) -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

