<%-- 
    Document   : verifyEmail
    Created on : Aug 11, 2025, 4:51:39 PM
    Author     : LAPTOP
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Verify Email</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">

        <div class="container mt-5">
            <div class="row justify-content-center">
                <div class="col-md-6">

                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h2 class="text-center mb-4">Verify Your Email</h2>

                            <!-- Display error message -->
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger" role="alert">
                                    ${error}
                                </div>
                            </c:if>

                            <!-- Display success message -->
                            <c:if test="${not empty msg}">
                                <div class="alert alert-success" role="alert">
                                    ${msg}
                                </div>
                            </c:if>

                            <form action="/VerifyEmailServlet" method="post">
                                <div class="mb-3">
                                    <label for="otp" class="form-label">OTP Code</label>
                                    <input id="otp" name="otp" type="text" class="form-control" placeholder="Enter OTP" required>
                                </div>

                                <button type="submit" class="btn btn-primary w-100">Verify</button>
                            </form>

                            <div class="mt-3 text-center text-muted" style="font-size: 0.9rem;">
                                Check your inbox (including Spam). The code is valid for <strong>5 minutes</strong>.
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

