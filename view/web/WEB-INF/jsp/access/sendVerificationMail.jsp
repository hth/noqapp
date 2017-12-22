<%@ include file="../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="utf-8">
    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
    <meta content='width=device-width, initial-scale=1' name='viewport'/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>
</head>

<body>

<!-- header -->
<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left">
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn">
                    <sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow">
                        <img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/">Home</a>
                        <a href="${pageContext.request.contextPath}/access/rewards.htm">Rewards</a>
                        <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                            <input type="submit" value="Logout" class="button-txt"/>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div class="clearFix"></div>
    </div>
</div>
<!-- header end -->
<!-- header end -->
<div class="main-warp">
    <!-- content -->
    <div class="content">
        <div class="warp-inner">
            <!-- Add New Supervisor -->
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <h3>Re-Send Email Verification</h3>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${profile.accountValidated}">
                        <div class="admin-content">
                            <div class="register-c">
                                <p>Your account has already been validated. If you see this message, please contact your
                                    administrator. Or contact at contact@noqapp.com and describe the issue in detail.
                                </p>
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${!profile.submitState && !profile.accountValidated}">
                        <form:form method="post" modelAttribute="profile"  action="${pageContext.request.contextPath}/access/sendVerificationMail.htm">
                            <div class="admin-content">
                                <div class="add-new">
                                    <ul class="list-form">
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="mail" cssErrorClass="lb_error">Send Mail To</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="mail" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                    </ul>

                                    <div class="col-lable3"></div>
                                    <div class="col-fields">
                                        <div class="left-btn">
                                            <input name="send" class="next-btn" value="SEND" type="submit">
                                        </div>
                                        <div class="right-btn">
                                            <input name="cancel_Send" class="cancel-btn" value="CANCEL" type="submit">
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                    <div class="clearFix"></div>
                                </div>
                            </div>
                        </form:form>
                    </c:when>
                    <c:otherwise>
                        <div class="admin-content">
                            <div class="register-c">
                                <p>You should receive an email in few minutes.</p>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="alert-info">
                    <p>
                        You should receive an email in 5 minutes. Click on the link in email to validate your email address.
                    </p>
                </div>
            </div>
            <!-- Add New Supervisor -->

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
                    <div class="f-left">&copy; 2017 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>

</html>
