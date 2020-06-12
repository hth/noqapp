<%@ include file="../../include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

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
        <div class="logo-left"><a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a></div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName" /></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png" /></div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/emp/landing/account/access.htm">Permissions</a>
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
                        <form:form method="POST" action="${pageContext.request.contextPath}/business/message/bulk.htm" modelAttribute="sendNotificationForm">
                            <spring:hasBindErrors name="sendNotificationForm">
                                <div class="error-box">
                                    <div class="error-txt">
                                        <ul>
                                            <c:forEach items="${errors.allErrors}" var="message">
                                            <li><spring:message message="${message}" /></li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </div>
                                <div class="space10"></div>
                            </spring:hasBindErrors>
                            <ul class="list-form">
                                <li>
                                    <div class="alert-info">
                                        <p>
                                            Sends message to all users of: <b>${sendNotificationForm.businessName}</b>. </br>
                                            Users should have at least visited once in last 60 days.
                                        </p>
                                    </div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="title" cssErrorClass="lb_error">Title</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="title" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" cssStyle="text-transform: none;" readonly="${sendNotificationForm.success}" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="body" cssErrorClass="lb_error">Body</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:textarea path="body" rows ="7" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" cssStyle="text-transform: none;" readonly="${sendNotificationForm.success}" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <c:if test="${!sendNotificationForm.success}">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="ignoreSentiments" cssErrorClass="lb_error">Ignore Negative Sentiments</form:label>
                                    </div>
                                    <div id="addressStoreCheckBox" class="col-fields">
                                        <form:checkbox path="ignoreSentiments" cssClass="form-check-box" cssErrorClass="form-check-box error-field" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                </c:if>

                                <li class="mB0">
                                    <div class="col-lable3"></div>
                                    <div class="col-fields">
                                        <c:choose>
                                            <c:when test="${!sendNotificationForm.success}">
                                            <div class="left-btn">
                                                <input type="submit" value="SEND" class="next-btn" name="send-notification">
                                            </div>
                                            <div class="right-btn">
                                                <input type="submit" value="CANCEL" class="cancel-btn" name="cancel-send-notification">
                                            </div>
                                            </c:when>
                                            <c:otherwise>
                                            <div class="left-btn">
                                                <input type="submit" value="OK" class="cancel-btn" name="cancel-send-notification">
                                            </div>
                                            </c:otherwise>
                                        </c:choose>
                                        <div class="clearFix"></div>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>

                                <div class="alert-info">
                                    <p>
                                    <c:choose>
                                        <c:when test="${sendNotificationForm.success}">
                                            Message delivered successfully to ${sendNotificationForm.sentCount} people
                                        </c:when>
                                        <c:otherwise>
                                            Your message will reach to ${sendNotificationForm.sentCount} people
                                        </c:otherwise>
                                    </c:choose>
                                    </p>
                                </div>
                            </ul>
                        </form:form>
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
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>
</html>