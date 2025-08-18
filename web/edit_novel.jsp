<%-- 
    Document   : edit_novel
    Created on : Aug 18, 2025, 3:21:28 PM
    Author     : LAPTOP
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Edit Novel</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">

        <div class="container py-4">
            <h2 class="mb-4">Edit Novel</h2>

            <form method="post" action="${pageContext.request.contextPath}/EditNovelServlet" enctype="multipart/form-data">
                <input type="hidden" name="id" value="${novel.id}"/>
                <!-- giữ coverPath cũ để servlet xử lý -->
                <input type="hidden" name="coverPathOld" value="${novel.coverPath}"/>

                <div class="mb-3">
                    <label class="form-label">Title</label>
                    <input type="text" class="form-control" name="title" value="${novel.title}" required>
                </div>

                <div class="mb-3">
                    <label class="form-label">Other Names</label>
                    <input type="text" class="form-control" name="otherNames" value="${novel.otherNames}">
                </div>

                <div class="mb-3">
                    <label class="form-label">Genre</label>
                    <input type="text" class="form-control" name="genre" value="${novel.genre}">
                </div>

                <div class="mb-3">
                    <label class="form-label">Status</label>
                    <select class="form-select" name="status">
                        <option value="ONGOING" ${novel.status == 'ONGOING' ? 'selected' : ''}>Ongoing</option>
                        <option value="COMPLETED" ${novel.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                    </select>
                </div>

                <!-- Cover hiển thị ảnh hiện tại -->
                <div class="mb-3">
                    <label class="form-label">Current Cover</label><br>
                    <c:if test="${not empty novel.coverPath}">
                        <img src="${novel.coverPath}" alt="Cover" class="img-thumbnail mb-2" style="max-height: 200px;">
                    </c:if>
                </div>

                <!-- Input chọn file ảnh mới -->
                <div class="mb-3">
                    <label for="cover" class="form-label">Change Cover Image</label>
                    <input type="file" id="cover" name="cover" accept="image/*" class="form-control">
                    <small class="text-muted">Leave empty to keep the current cover.</small>
                </div>

                <div class="mb-3">
                    <label class="form-label">Summary</label>
                    <textarea class="form-control" name="summary" rows="4">${novel.summary}</textarea>
                </div>

                <button type="submit" class="btn btn-primary">Save</button>
                <a href="${pageContext.request.contextPath}/MyNovelsServlet" class="btn btn-secondary">Cancel</a>
            </form>
        </div>

    </body>
</html>

