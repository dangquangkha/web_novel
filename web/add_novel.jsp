<%-- 
    Document   : add_novel
    Created on : Aug 12, 2025, 5:45:56 PM
    Author     : LAPTOP
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="model.User" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="login.jsp" />
</c:if>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Add Novel</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">

        <div class="container mt-5">
            <h2 class="mb-4">Add New Novel</h2>

            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>

            <form action="AddNovelServlet" method="post" enctype="multipart/form-data" class="bg-white p-4 shadow-sm rounded">

                <div class="mb-3">
                    <label for="title" class="form-label">Title *</label>
                    <input type="text" id="title" name="title" class="form-control" required>
                </div>

                <div class="mb-3">
                    <label for="other_names" class="form-label">Other Names</label>
                    <input type="text" id="other_names" name="other_names" class="form-control">
                </div>

                <div class="mb-3">
                    <label for="genre" class="form-label">Genre</label>
                    <input type="text" id="genre" name="genre" class="form-control">
                </div>

                <div class="mb-3">
                    <label for="status" class="form-label">Status</label>
                    <select id="status" name="status" class="form-select">
                        <option value="ONGOING">Ongoing</option>
                        <option value="COMPLETED">Completed</option>
                    </select>
                </div>

                <div class="form-check mb-3">
                    <input type="checkbox" id="is_sensitive" name="is_sensitive" class="form-check-input">
                    <label for="is_sensitive" class="form-check-label">Sensitive Content</label>
                </div>

                <div class="mb-3">
                    <label for="is_public" class="form-label">Visibility</label>
                    <select id="is_public" name="is_public" class="form-select">
                        <option value="true" selected>Public</option>
                        <option value="false">Private</option>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="summary" class="form-label">Summary</label>
                    <textarea id="summary" name="summary" rows="5" class="form-control"></textarea>
                </div>

                <div class="mb-3">
                    <label for="notes" class="form-label">Notes</label>
                    <textarea id="notes" name="notes" rows="3" class="form-control"></textarea>
                </div>

                <div class="mb-3">
                    <label for="cover" class="form-label">Cover Image</label>
                    <input type="file" id="cover" name="cover" accept="image/*" class="form-control">
                </div>

                <button type="submit" class="btn btn-success">Add Novel</button>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>


