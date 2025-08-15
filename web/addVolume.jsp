<%-- 
    Document   : addVolume
    Created on : Aug 12, 2025, 9:22:54 PM
    Author     : LAPTOP
--%>

<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Add Volume</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">
        <div class="container mt-5">
            <h2 class="mb-4">Add New Volume</h2>

            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>

            <form action="AddVolumeServlet" method="post" class="bg-white p-4 shadow-sm rounded">

                <div class="mb-3">
                    <label for="novel_id" class="form-label">Select Novel *</label>
                    <select id="novel_id" name="novel_id" class="form-select" required>
                        <option value="">-- Select novel --</option>
                        <c:forEach var="novel" items="${listNovel}">
                            <option value="${novel.id}">${novel.title}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="volume_number" class="form-label">Volume Number *</label>
                    <input type="number" id="volume_number" name="volume_number" class="form-control" min="1" required>
                </div>

                <div class="mb-3">
                    <label for="title" class="form-label">Volume Title</label>
                    <input type="text" id="title" name="title" class="form-control">
                </div>

                <div class="mb-3">
                    <label for="description" class="form-label">Description</label>
                    <textarea id="description" name="description" rows="4" class="form-control"></textarea>
                    <small class="text-muted">Use the editor to format your content (bold, italic, links, lists, etc.).</small>
                </div>

                <button type="submit" class="btn btn-primary">Add Volume</button>
                <a href="my_novels.jsp" class="btn btn-secondary ms-2">Back</a>
            </form>
        </div>

        <!-- CKEditor 5 -->
        <script src="https://cdn.ckeditor.com/ckeditor5/41.3.1/classic/ckeditor.js"></script>
        <script>
            ClassicEditor
                    .create(document.querySelector('#description'), {
                        toolbar: [
                            'undo', 'redo', '|',
                            'bold', 'italic', 'underline', '|',
                            'link', 'bulletedList', 'numberedList', '|',
                            'blockQuote', 'insertTable', 'mediaEmbed', '|',
                            'imageUpload', 'imageInsert'
                        ],
                        placeholder: 'Start typing your chapter content here...',
                        ckfinder: {
                            uploadUrl: '<%=request.getContextPath()%>/UploadImageServlet'
                        },
                        image: {
                            toolbar: [
                                'imageTextAlternative',
                                'imageStyle:inline',
                                'imageStyle:block',
                                'imageStyle:side'
                            ]
                        }
                    })
                    .catch(error => {
                        console.error(error);
                    });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>


