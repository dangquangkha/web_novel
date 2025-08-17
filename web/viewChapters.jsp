<%-- 
    Document   : viewChapters
    Created on : Aug 14, 2025, 5:10:10 PM
    Author     : LAPTOP
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="user" value="${sessionScope.user}" />
<c:set var="volumeId" value="${param.volumeId}" />

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Chapters of Volume</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">
        <div class="container mt-4">
            <h3 class="mb-3">Chapters</h3>

            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger" role="alert"><c:out value="${requestScope.error}"/></div>
            </c:if>

            <c:if test="${empty volumeId}">
                <div class="alert alert-warning" role="alert">
                    No volumeId specified. <a href="<c:url value='my_novels.jsp'/>" class="alert-link">Back to your novels</a>.
                </div>
            </c:if>

            <c:if test="${not empty volumeId}">
                <c:choose>
                    <c:when test="${empty chapters}">
                        <div class="alert alert-info" role="alert">No chapters found in this volume.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-striped align-middle">
                                <thead>
                                    <tr>
                                        <th scope="col">#</th>
                                        <th scope="col">Title</th>
                                        <th scope="col">Word count</th>
                                        <th scope="col">Created</th>
                                        <th scope="col">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="ch" items="${chapters}">
                                    <tr>
                                        <td><c:out value="${ch.chapterNumber}"/></td>
                                    <td>
                                        <a href="<c:url value='ReadChapterServlet'><c:param name='chapterId' value='${ch.id}'/></c:url>">
                                            <c:out value="${ch.title}"/>
                                        </a>
                                    </td>
                                    <td><c:out value="${ch.wordCount}"/></td>
                                    <td><fmt:formatDate value="${ch.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                                    <td>
                                    <c:choose>
                                        <c:when test="${user != null and (user.id == selectedNovel.authorId or user.role == 'ADMIN')}">
                                            <a href="<c:url value='editChapter.jsp'><c:param name='chapterId' value='${ch.id}'/></c:url>" class="btn btn-sm btn-outline-primary me-1">Edit</a>

                                            <form action="<c:url value='DeleteChapterServlet'/>" method="post" class="d-inline" onsubmit="return confirm('Are you sure you want to delete this chapter?');">
                                                <input type="hidden" name="chapterId" value="${ch.id}" />
                                                <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">—</span>
                                        </c:otherwise>
                                    </c:choose>
                                    </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="mt-3">
                    <a href="<c:url value='addChapter.jsp'><c:param name='novelId' value='${selectedNovel.id}'/></c:url>" class="btn btn-success me-2">Add Chapter</a>
                    <a href="<c:url value='my_novels.jsp'/>" class="btn btn-secondary">Back to novels</a>
                </div>
            </c:if>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

