<%@ include file="../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
    <meta content='width=device-width, initial-scale=1' name='viewport'/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/external/intl-tel-input/css/intlTelInput.css" type='text/css'>
</head>

<body>

<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left"><img src="${pageContext.request.contextPath}/static/internal/img/logo.png" alt="NoQueue"/></div>
        <div class="top-menu-right">
            <span class="help-btn"><a href="${pageContext.request.contextPath}/open/login">Sign In</a></span>
            <span class="become-btn"><a href="${pageContext.request.contextPath}/open/register">Business Register</a></span>
        </div>

        <div class="clearFix"></div>
    </div>
</div>
<!-- header end -->
<div class="main-warp">

    <!-- content -->
    <div class="content">
        <div class="warp-inner">
            <div class="admin-main">
                <div class="admin-content">
                    <div class="register-c">
                        <c:if test="${success eq false}">
                            <h3>Password Update Failed</h3>
                            <p>We apologize, but we were unable to update your account with new password.</p>
                            <p>Please
                                <a href="${pageContext.request.contextPath}/open/login" class="add-btn">click here</a> to return to the main page and start over.
                            </p>
                        </c:if>

                        <c:if test="${success eq true}">
                            <h3>Password Update Success</h3>
                            <p>Please log in with your new password.</p>
                            <p>Click here for
                                <a href="${pageContext.request.contextPath}/open/login" class="add-btn">Sign In</a> page.
                            </p>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- content end -->


    <!-- Footer -->
    <div class="footer">
        <div class="warp-inner ">
            <img src="${pageContext.request.contextPath}/static/internal/img/footer-img.jpg" class="img100"/>
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2021 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/intl-tel-input/js/intlTelInput.js"></script>

</html>
