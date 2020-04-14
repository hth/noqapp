<%@ include file="../../../../jsp/include.jsp" %>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/jquery/css/jquery-ui.css" />
</head>

<body>

<!-- header -->
<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left">
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue Inc"/></a>
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
            <!-- Complete profile -->
            <sec:authorize access="hasRole('ROLE_Q_SUPERVISOR')">
            <div class="admin-main">
                <form:form modelAttribute="registerUser">
                    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                    <div class="admin-title">
                        <h2>Please complete your profile</h2>
                    </div>
                    <div class="error-box">
                        <div class="error-txt">
                            <c:if test="${!empty flowRequestContext.messageContext.allMessages}">
                            <ul>
                                <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
                                <li>${message.text}</li>
                                </c:forEach>
                            </ul>
                            </c:if>
                        </div>
                    </div>
                    <div class="admin-content">
                        <div class="add-new">
                            <ul class="list-form">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="firstName" cssErrorClass="lb_error">First name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="firstName" cssClass="form-field-admin"
                                                    cssErrorClass="form-field-admin error-field"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>

                                <li>
                                    <div class="col-lable3">
                                        <form:label path="lastName" cssErrorClass="lb_error">Last name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="lastName" cssClass="form-field-admin"
                                                    cssErorrClass="form-field-admin error-field"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>

                                <li>
                                    <div class="col-lable3">
                                        <form:label path="birthday" cssErrorClass="lb_error">Date of Birth</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="birthday" cssClass="datepicker form-field-admin"
                                                    cssErrorClass="datepicker form-field-admin error-field"
                                                    placeholder="Date of Birth YYYY-MM-DD"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>

                                <li>
                                    <div class="col-lable3">
                                        <form:label path="gender" cssErrorClass="lb_error">Gender</form:label>
                                    </div>
                                    <div class="col-fields pT10 pB10">
                                        <form:radiobutton path="gender" value="M" label="Male"/> &nbsp; &nbsp;
                                        <form:radiobutton path="gender" value="F" label="Female"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="address" cssErrorClass="lb_error">Your Address</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:textarea path="address" cols="" rows="3" cssClass="form-field-admin"
                                                       cssErrorClass="form-field-admin error-field"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <c:if test="${!empty registerUser.foundAddresses}">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="foundAddressPlaceId" cssErrorClass="lb_error">Best Matching Addresses</form:label>
                                    </div>
                                    <div class="col-fields pT10 pB10">
                                        <c:forEach items="${registerUser.foundAddresses}" var="mapElement">
                                            <form:radiobutton path="foundAddressPlaceId" value="${mapElement.key}" label="${mapElement.value.formattedAddress}"
                                                    onclick="handleFoundAddressClick();"/> <br />
                                        </c:forEach>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="selectFoundAddress" cssErrorClass="lb_error">I choose Best Matching Address</form:label>
                                    </div>
                                    <div id="addressCheckBox" class="col-fields">
                                        <form:checkbox path="selectFoundAddress" cssClass="form-check-box"
                                                       cssErrorClass="form-field-admin error-field"
                                                       disabled="true"
                                                onclick="handleFoundAddressCheckboxUncheck()" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                </c:if>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="phone" cssErrorClass="lb_error">Your Phone</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="phone" cssClass="form-field-admin"
                                                    cssErrorClass="form-field-admin error-field"
                                                    readonly="${register.registerUser.phoneValidated}"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="email" cssErrorClass="lb_error">Email Address</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="email" cssClass="form-field-admin"
                                                    cssErrorClass="form-field-admin error-field"
                                                    readonly="${register.registerUser.phoneValidated}"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <c:if test="${!registerUser.emailValidated}">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="password" cssErrorClass="lb_error">Password</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:password path="password" cssClass="form-field-admin"
                                                       cssErrorClass="form-field-admin error-field"
                                                       readonly="${register.registerUser.phoneValidated}"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                </c:if>

                                <c:if test="${!registerUser.emailValidated}">
                                <li>
                                    <div class="alert-info">
                                        <p>
                                            Your email address
                                            <span class="txt-red">${registerUser.email}</span>
                                            has not been validated. Please validated email address to continue merchant account registration.
                                        </p>
                                        <p>To resend account validation email, <a href="${pageContext.request.contextPath}/access/sendVerificationMail.htm">click here.</a>
                                        </p>
                                    </div>
                                </li>
                                </c:if>

                                <c:if test="${!registerUser.phoneValidated}">
                                <li>
                                    <div class="alert-info">
                                        <p>Your phone number has not been validated. For phone validation, login using phone number.</p>
                                    </div>
                                </li>
                                </c:if>

                                <c:if test="${registerUser.emailValidated}">
                                <li class="mB0">
                                    <div class="col-lable3"></div>
                                    <div class="col-fields">
                                        <input type="submit" value="NEXT" class="next-btn" name="_eventId_submit">
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                </c:if>

                                <%--<div class="btn-hours">--%>
                                    <%--<c:if test="${registerUser.emailValidated}">--%>
                                        <%--<div class="first-btn">--%>
                                            <%--<input name="_eventId_confirm" class="next-btn" value="NEXT" type="submit">--%>
                                        <%--</div>--%>
                                    <%--</c:if>--%>
                                    <%--<div class="clearFix"></div>--%>
                                <%--</div>--%>
                            </ul>
                        </div>
                    </div>
                </form:form>
            </div>
            <!-- Complete profile -->
            </sec:authorize>
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
                    <div class="f-left">&copy; 2020 NoQueue Inc. | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/jquery/js/jquery-ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/services.js"></script>
<script>
    $(function () {
        $(".datepicker").datepicker({
            dateFormat: 'yy-mm-dd'
        });
    });
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
