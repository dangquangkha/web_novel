<%-- 
    Document   : addChapter
    Created on : Aug 14, 2025, 9:20:54 AM
    Author     : LAPTOP
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Add Chapter</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">
        <div class="container mt-5">
            <h2 class="mb-4">Add New Chapter</h2>

            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>

            <form action="AddChapterServlet" method="post" class="bg-white p-4 shadow-sm rounded" enctype="multipart/form-data">

                <!-- CSRF token -->
                <input type="hidden" name="_csrf" value="${csrfToken}" />

                <div class="mb-3">
                    <label for="volume_id" class="form-label">Select Volume *</label>
                    <select id="volume_id" name="volume_id" class="form-select" required>
                        <option value="">-- Select volume --</option>
                        <c:forEach var="v" items="${volumes}">
                            <option value="${v.id}">
                                ${v.volumeNumber} - ${v.title} (Novel: <c:out value='${v.novelId}'/>)
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="chapter_number" class="form-label">Chapter Number *</label>
                    <input type="number" id="chapter_number" name="chapter_number" class="form-control" min="1" required>
                </div>

                <div class="mb-3">
                    <label for="title" class="form-label">Chapter Title *</label>
                    <input type="text" id="title" name="title" class="form-control" required>
                </div>

                <!-- Chapter Content with CKEditor -->
                <div class="mb-3">
                    <label for="content" class="form-label">Content *</label>
                    <textarea id="content" name="content" rows="12" class="form-control" required></textarea>
                    <small class="text-muted">Use the editor to format your content (bold, italic, links, lists, etc.).</small>
                </div>

                <button type="submit" class="btn btn-success">Add Chapter</button>
                <a href="my_novels.jsp" class="btn btn-secondary ms-2">Back</a>
            </form>
        </div>

        <!-- CKEditor 5 -->
        <script src="https://cdn.ckeditor.com/ckeditor5/41.3.1/classic/ckeditor.js"></script>
        <script>
            ClassicEditor
                    .create(document.querySelector('#content'), {
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


