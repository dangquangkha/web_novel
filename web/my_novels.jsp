<%-- 
    Document   : my_novels
    Created on : Aug 12, 2025, 5:51:15 PM
    Author     : LAPTOP
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>My Novels</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">

        <div class="container py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="mb-0">Novels by <span class="text-primary">${sessionScope.user.username}</span></h2>
                <a href="add_novel.jsp" class="btn btn-success">
                    <i class="bi bi-plus-circle"></i> Add New Novel
                </a>
            </div>

            <div class="table-responsive shadow-sm">
                <table class="table table-striped table-hover align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Genre</th>
                            <th>Status</th>
                            <th>Cover</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="novel" items="${novels}">
                        <tr>
                            <td>${novel.id}</td>
                            <td>${novel.title}</td>
                            <td>${novel.genre}</td>
                            <td>${novel.status}</td>
                            <td>
                        <c:choose>
                            <c:when test="${not empty novel.coverPath}">
                                <img src="${novel.coverPath}" alt="cover" class="img-thumbnail" style="max-width: 80px;">
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted fst-italic">No cover</span>
                            </c:otherwise>
                        </c:choose>
                        </td>
                        <td>
                            <a href="edit_novel.jsp?id=${novel.id}" class="btn btn-sm btn-primary">
                                <i class="bi bi-pencil"></i> Edit
                            </a>
                            <a href="delete_novel?id=${novel.id}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Are you sure you want to delete this novel?');">
                                <i class="bi bi-trash"></i> Delete
                            </a>
                        </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Bootstrap Icons -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

