<%@ include file="../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <c:if test="${!empty migrateBusinessTypeForm.migrationMessage}">
    <meta http-equiv="Refresh" content="5;url=/">
    </c:if>

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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/">Home</a>
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
                    <div class="add-new">
                        <div class="admin-title">
                            <h2>Migrate Business Type</h2>
                        </div>
                        <c:choose>
                            <c:when test="${!empty migrateBusinessTypeForm.allowedMigrationBusinessType && !migrateBusinessTypeForm.migrationSuccess}">
                                <form:form method="post" action="${pageContext.request.contextPath}/business/migrateBusinessType.htm" modelAttribute="migrateBusinessTypeForm">
                                <form:hidden path="existingBusinessType" />
                                <form:hidden path="allowedMigrationBusinessType" />
                                <div class="alert-info">
                                    <p><h3>Migrate from ${migrateBusinessTypeForm.existingBusinessType.description} -- to -- ${migrateBusinessTypeForm.allowedMigrationBusinessType.description}</h3></p>
                                    <p>Note: <b>'Queue Only'</b> does not take orders but gives out just tokens. These tokens are used to manage queue just like order queue.</p>
                                </div>
                                    <c:choose>
                                        <c:when test="${!empty migrateBusinessTypeForm.migrationMessage}">
                                            <div>
                                                <span style="display:block; font-size:14px; color: #9f1313;">${migrateBusinessTypeForm.migrationMessage}</span>
                                                <span style="display:block; font-size:14px; color: #9f1313;"><p>Redirecting in next <span id="countdown">5</span> seconds</p></span>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="admin-content">
                                                <div class="add-new">
                                                    <ul class="list-form">
                                                        <li>
                                                            <div class="col-lable3">
                                                                <form:label path="migrate" cssErrorClass="lb_error">Confirm Business Migrate</form:label>
                                                            </div>
                                                            <div class="col-fields">
                                                                <form:checkbox path="migrate" cssClass="form-check-box" cssErrorClass="form-check-box error-field" />
                                                                <span style="display:block; font-size:14px; color: #9f1313;">Caution: Check to confirm migration of your business. <br/>Note: This is instantaneous.</span>
                                                            </div>

                                                            <div class="clearFix"></div>
                                                        </li>
                                                    </ul>

                                                    <div class="col-lable3"></div>
                                                    <div class="col-fields">
                                                        <div class="button-btn">
                                                            <button name="migrate" class="ladda-button next-btn" style="width:48%; float: left">Confirm Migration</button>
                                                            <button name="cancel_Migrate" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                                        </div>
                                                        <div class="clearFix"></div>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </form:form>
                            </c:when>
                            <c:when test="${!empty migrateBusinessTypeForm.allowedMigrationBusinessType && migrateBusinessTypeForm.migrationSuccess}">
                                <div class="alert-info">
                                    <p>
                                        Migration was successful. Business type changed to '${migrateBusinessTypeForm.existingBusinessType.description}'.
                                        Takes at least 5 minutes or more to show up on mobile.
                                    </p>
                                </div>
                            </c:when>
                            <c:otherwise>
                            <div class="alert-info">
                                <p>Migration not available</p>
                            </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script type="text/javascript" >
    let el = document.getElementById("countdown");
    let i = 5;

    function counter() {
        el.innerHTML = i--;
        if (i <= 5 && i >= 0) {
            setTimeout(counter, 1000);
        }
    }
    counter();
</script>
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
