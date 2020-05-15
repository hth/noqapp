<%@ page import="com.noqapp.domain.types.MailTypeEnum" %>
<%@ include file="../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
    <meta content='width=device-width, initial-scale=1' name='viewport'/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/intl-tel-input/css/intlTelInput.css">
</head>

<body>

<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></div>
        <div class="top-menu-right">
            <span class="help-btn"><a href="${pageContext.request.contextPath}/open/login.htm">Sign In</a></span>
            <span class="become-btn"><a href="${pageContext.request.contextPath}/open/register.htm">Merchant Register</a></span>
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
                        <c:choose>
                            <c:when test="${success_email eq MailTypeEnum.SUCCESS}">
                                <h3>Success Password Mail</h3>
                                <p>An email has been sent with information regarding recovering your account password.</p>
                                <p>Click here for
                                    <a href="${pageContext.request.contextPath}/open/login.htm" class="add-btn">Sign In</a> page.
                                </p>
                            </c:when>
                            <c:when test="${success_email_admin eq MailTypeEnum.SUCCESS_SENT_TO_ADMIN}">
                                <h3>Success Password Mail</h3>
                                <p>An email has been sent with information regarding recovering your account password to the administrator.</p>
                                <p>Click here for
                                    <a href="${pageContext.request.contextPath}/open/login.htm" class="add-btn">Sign In</a> page.
                                </p>
                            </c:when>
                            <c:otherwise>
                                <h3>Success Validate Mail Address</h3>
                                <p>Since your email address has not being verified, we have sent verification email.</p>
                                <p>Follow directions in email to validated your account and then resubmit new password reset request.</p>
                                <p>Click here for
                                    <a href="${pageContext.request.contextPath}/open/login.htm" class="add-btn">Sign In</a> page.
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- content end -->


    <!-- Footer -->
    <div class="footer">
        <div class="warp-inner ">
            <img src="${pageContext.request.contextPath}/static2/internal/img/footer-img.jpg" class="img100"/>
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2020 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/intl-tel-input/js/intlTelInput.js"></script>

</html>
