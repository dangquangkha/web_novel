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

            <form id="chapterForm" action="AddChapterServlet" method="post" class="bg-white p-4 shadow-sm rounded">
                <!-- CSRF token -->
                <input type="hidden" name="chapterCsrf" value="${csrfToken}" />


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

                <div class="mb-3">
                    <label for="content" class="form-label">Content *</label>
                    <!-- removed `required` here to avoid browser trying to focus a hidden control -->
                    <textarea id="content" name="content" rows="12" class="form-control"></textarea>
                    <small class="text-muted">Use the editor to format your content (bold, italic, links, lists, etc.).</small>
                    <div id="contentError" class="invalid-feedback" style="display:none;">Content cannot be empty.</div>
                </div>

                <button type="submit" class="btn btn-success">Add Chapter</button>
                <a href="my_novels.jsp" class="btn btn-secondary ms-2">Back</a>
            </form>
        </div>

        <!-- CKEditor 5 -->
        <script src="https://cdn.ckeditor.com/ckeditor5/41.3.1/classic/ckeditor.js"></script>
        <script>
            let chapterEditor = null;

            ClassicEditor
                    .create(document.querySelector('#content'), {
                        // remove toolbar items that may not be included in this build.
                        toolbar: [
                            'undo', 'redo', '|',
                            'bold', 'italic', 'underline', '|',
                            'link', 'bulletedList', 'numberedList', '|',
                            'blockQuote', 'insertTable', 'mediaEmbed', '|',
                            'imageUpload' // keep only items likely included in Classic build
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
                    .then(editor => {
                        chapterEditor = editor;
                    })
                    .catch(error => {
                        console.error(error);
                    });

            // helper: strip HTML and check if there's any real text
            function isEditorContentEmpty(html) {
                // create temporary element to strip tags
                const div = document.createElement('div');
                div.innerHTML = html || '';
                const text = div.textContent || div.innerText || '';
                return text.trim().length === 0;
            }

            // Form submit handler: validate editor content and copy data to textarea
            document.getElementById('chapterForm').addEventListener('submit', function (e) {
                // if editor not ready, allow normal behavior (or block if you prefer)
                if (!chapterEditor)
                    return;

                const data = chapterEditor.getData();
                if (isEditorContentEmpty(data)) {
                    e.preventDefault();
                    // show bootstrap-style invalid feedback
                    document.getElementById('content').classList.add('is-invalid');
                    const err = document.getElementById('contentError');
                    if (err)
                        err.style.display = 'block';
                    // optionally scroll to editor
                    document.getElementById('content').scrollIntoView({behavior: 'smooth', block: 'center'});
                    return;
                }

                // set textarea value so it's sent to server
                document.getElementById('content').value = data;
                // allow submit to proceed
            });
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>


