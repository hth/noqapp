<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
    <meta content='width=device-width, initial-scale=1' name='viewport'/>

    <link rel="stylesheet" href="/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="/static/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="/static/external/intl-tel-input/css/intlTelInput.css" type='text/css'>
</head>

<body>

<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left"><a href="/"><img src="/static/internal/img/logo.png" alt="NoQueue"/></a></div>
        <div class="top-menu-right">
            <span class="help-btn"><a href="/open/login">Sign In</a></span>
            <span class="become-btn"><a href="/open/register">Business Register</a></span>
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
                            <c:when test="${errorCode == 400}">
                                <h3>We're sorry...</h3>
                                <p>Bad request received. <sup>(400)</sup></p>
                            </c:when>
                            <c:when test="${errorCode == 401}">
                                <h3>We're sorry...</h3>
                                <p>The page you are looking for cannot be found. <sup>(404)</sup></p>
                            </c:when>
                            <c:when test="${errorCode == 403}">
                                <h3>We're sorry...</h3>
                                <p>Unauthorized access. <sup>(403)</sup></p>
                            </c:when>
                            <c:when test="${errorCode == 404}">
                                <h3>We're sorry...</h3>
                                <p>The page you are looking for cannot be found. <sup>(404)</sup></p>
                            </c:when>
                            <c:when test="${errorCode == 405}">
                                <h3>We're sorry...</h3>
                                <p>This request is not supported . <sup>(405)</sup></p>
                            </c:when>
                            <c:when test="${errorCode == 410}">
                                <h3>Expired Link</h3>
                                <p>
                                    This link has expired after successful validation.
                                    To re-validate, submit a new request. <sup>(410)</sup>
                                </p>
                            </c:when>
                            <c:when test="${errorCode == 414}">
                                <h3>We're sorry...</h3>
                                <p>Failed to understand longer URI. <sup>(414)</sup></p>
                            </c:when>
                            <c:when test="${errorCode == 500}">
                                <h3>We're sorry...</h3>
                                <p>Encountered error processing your request. <sup>(500)</sup></p>
                            </c:when>
                            <c:when test="${errorCode == 503}">
                                <h3>We're sorry...</h3>
                                <p>All of our servers are busy right now. <sup>(503)</sup></p>
                            </c:when>
                            <c:otherwise>
                                <h3>We're sorry...</h3>
                                <p>The page you are looking for cannot be found. <sup>(404)</sup></p>
                            </c:otherwise>
                        </c:choose>
                        <p>
                            Hit back button on the browser or
                            <a href="/open/login" class="add-btn">click to login</a>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- content end -->


    <!-- Footer -->
    <div class="footer">
        <div class="warp-inner ">
            <img src="/static/internal/img/footer-img.jpg" class="img100"/>
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
<script type="text/javascript" src="/static/external/intl-tel-input/js/intlTelInput.js"></script>
</html>
