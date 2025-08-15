<%-- 
    Document   : index
    Created on : Aug 12, 2025, 9:45:39 AM
    Author     : LAPTOP
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Web Novel</title>

        <!-- Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Optional: Bootstrap Icons -->
        <link rel="stylesheet" 
              href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    </head>

    <body class="p-3">
        <!-- User Menu -->
        <div class="container-fluid">
            <div class="d-flex justify-content-end align-items-center py-2">
                <c:choose>
                    <c:when test="${empty sessionScope.user}">
                        <a href="login.jsp" class="btn btn-primary">Login</a>
                    </c:when>
                    <c:otherwise>
                        <div class="dropdown">
                            <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle"
                               id="userMenu" data-bs-toggle="dropdown" aria-expanded="false">
                                <img src="${pageContext.request.contextPath}/images/my.jpg" alt="User Avatar" width="50" height="50"
                                     class="rounded-circle me-2">
                                <span>${sessionScope.user.username}</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userMenu">
                                <li><a class="dropdown-item" href="#">Account</a></li>
                                <li><a class="dropdown-item" href="#">Order History</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/AddNovelServlet">Add new novel</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="LogoutServlet">Logout</a></li>
                            </ul>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Bootstrap 5 JS (no jQuery needed) -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>

</html>

