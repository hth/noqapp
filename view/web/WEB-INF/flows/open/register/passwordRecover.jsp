<%@ include file="../../../jsp/include.jsp" %>
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

    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-101872684-1"></script>
    <script>
        window.dataLayer = window.dataLayer || [];
        function gtag(){dataLayer.push(arguments);}
        gtag('js', new Date());

        gtag('config', 'UA-101872684-1');
    </script>
</head>

<body>

<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></div>
        <div class="top-menu-right">
            <span class="help-btn"><a href="${pageContext.request.contextPath}/open/login.htm">Sign In</a></span>
            <span class="become-btn"><a href="${pageContext.request.contextPath}/open/register.htm">Business Register</a></span>
        </div>

        <div class="clearFix"></div>
    </div>
</div>
<!-- header end -->
<div class="main-warp">

    <!-- content -->
    <div class="content">
        <div class="warp-inner">
            <!-- login-box -->
            <div class="login-box">
                <h2><fmt:message key="account.recover.title"/></h2>
                <p class="mB20 Tcenter"><fmt:message key="account.recover.sub.title"/></p>
                <div class="form-style">
                    <form:form method="post" modelAttribute="merchantRegistration">
                        <form:hidden path="mail"/>
                        <spring:hasBindErrors name="forgotRecoverForm">
                            <div class="error-box">
                                <div class="error-txt">
                                    <ul>
                                        <c:if test="${errors.hasFieldErrors('mail')}">
                                            <li><form:errors path="mail"/></li>
                                        </c:if>
                                        <c:if test="${errors.hasFieldErrors('captcha')}">
                                            <li><form:errors path="captcha"/></li>
                                        </c:if>
                                    </ul>
                                </div>
                            </div>
                        </spring:hasBindErrors>

                        <div class="admin-content" style="background:white;">
                            <p style="display:none;visibility:hidden;">
                                <form:input path="captcha" cssClass="form-field" cssErrorClass="form-field error-field"/>
                            </p>
                            <form:input path="mail" cssClass="form-field" required="required" type="email" disabled="true" cssErrorClass="form-field error-field"/>
                            <%--<input name="_eventId_sendRecoveryMail" class="form-btn mT10" value="Send Recovery Email" type="submit">--%>
                            <div class="button-btn">
                                <button name="_eventId_sendRecoveryMail" class="ladda-button form-btn" style="width:100%;">Send Recovery Email</button>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>

            <!-- login-box -->

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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/ladda.min.js"></script>
<script type="text/javascript">
    // Bind normal buttons
    Ladda.bind('.button-btn button', {timeout: 6000});

    // Bind progress buttons and simulate loading progress
    Ladda.bind('.progress-demo button', {
        callback: function (instance) {
            var progress = 0;
            var interval = setInterval(function () {
                progress = Math.min(progress + Math.random() * 0.1, 1);
                instance.setProgress(progress);

                if (progress === 1) {
                    instance.stop();
                    clearInterval(interval);
                }
            }, 200);
        }
    });

    // You can control loading explicitly using the JavaScript API
    // as outlined below:

    // var l = Ladda.create( document.querySelector( 'button' ) );
    // l.start();
    // l.stop();
    // l.toggle();
    // l.isLoading();
    // l.setProgress( 0-1 );
</script>

</html>
