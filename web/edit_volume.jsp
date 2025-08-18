<%-- 
    Document   : edit_volume
    Created on : Aug 18, 2025, 8:37:00 PM
    Author     : LAPTOP
--%>

<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Edit Volume</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">
        <div class="container mt-5">
            <h2 class="mb-4">Edit Volume</h2>

            <form action="${pageContext.request.contextPath}/EditVolumeServlet" method="post" class="bg-white p-4 shadow-sm rounded">
                <input type="hidden" name="id" value="${volume.id}">
                <input type="hidden" name="novel_id" value="${volume.novelId}">

                <div class="mb-3">
                    <label for="volume_number" class="form-label">Volume Number *</label>
                    <input type="number" id="volume_number" name="volume_number" class="form-control" min="1" value="${volume.volumeNumber}" required>
                </div>

                <div class="mb-3">
                    <label for="title" class="form-label">Volume Title</label>
                    <input type="text" id="title" name="title" class="form-control" value="${volume.title}">
                </div>

                <div class="mb-3">
                    <label for="description" class="form-label">Description</label>
                    <textarea id="description" name="description" rows="4" class="form-control">${volume.description}</textarea>
                    <small class="text-muted">Use the editor to format your content (bold, italic, links, lists, etc.).</small>
                </div>

                <button type="submit" class="btn btn-primary">Save</button>
                <a href="${pageContext.request.contextPath}/VolumeListServlet?novelId=${volume.novelId}" class="btn btn-secondary ms-2">Cancel</a>
            </form>
        </div>
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
    </body>
</html>

