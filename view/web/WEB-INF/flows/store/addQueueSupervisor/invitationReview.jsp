<%@ include file="../../../jsp/include.jsp" %>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/intl-tel-input/css/intlTelInput.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>
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
                <sec:authorize access="hasAnyRole('ROLE_M_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')">

                    <div class="admin-title">
                        <h2>Add New Supervisor</h2>
                    </div>

                    <form:form modelAttribute="inviteQueueSupervisor">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

                        <c:if test="${!empty flowRequestContext.messageContext.allMessages}">
                            <div class="error-box">
                                <div class="error-txt">
                                    <ul>
                                        <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
                                        <li>${message.text}</li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>
                        </c:if>

                        <div class="admin-content">
                            <div class="add-new">
                                <ul class="list-form">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="phoneNumber" cssErrorClass="lb_error">Phone Number</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="phoneNumber" cssClass="form-fe" cssErrorClass="form-fe lb_error" readonly="true"/>
                                            <span class="info-txt">(Must be a registered user)</span>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>

                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="inviteeCode" cssErrorClass="lb_error">Invitee Code</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="inviteeCode" cssClass="form-field-admin" cssErrorClass="form-field-admin lb_error" readonly="true"/>
                                            <span class="info-txt">(Owner of the Phone Number has to share invite code with you)</span>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>

                                    <li class="mB0">
                                        <div class="col-lable3"></div>
                                        <div class="col-fields">
                                            <%--<div class="first-btn">--%>
                                                <%--<input name="_eventId_confirm" class="next-btn" value="CONFIRM" type="submit">--%>
                                            <%--</div>--%>
                                            <%--<div class="center-btn">--%>
                                                <%--<input name="_eventId_revise" class="cancel-btn" value="REVISE" type="submit">--%>
                                            <%--</div>--%>
                                            <%--<div class="last-btn">--%>
                                                <%--<input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">--%>
                                            <%--</div>--%>

                                            <div class="button-btn">
                                                <button name="_eventId_confirm" class="ladda-button next-btn" style="width:32%; float: left">Confirm</button>
                                                <button name="_eventId_revise" class="ladda-button cancel-btn" style="width:32%; float: left; margin-left:2%">Revise</button>
                                                <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:32%; float: right">Cancel</button>
                                            </div>
                                            <div class="clearFix"></div>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </form:form>
                </sec:authorize>
            </div>
            <!-- Add New Supervisor -->

        </div>
    </div>
    <!-- content end -->


    <!-- Foote -->
    <div class="footer">
        <div class="warp-inner ">
            <img src="${pageContext.request.contextPath}/static2/internal/img/footer-img.jpg" class="img100"/>
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2018 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/intl-tel-input/js/intlTelInput.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>

<script>
    $("#phoneNumber").intlTelInput({
        // allowDropdown: false,
        // autoHideDialCode: false,
        // autoPlaceholder: "off",
        // dropdownContainer: "body",
        // excludeCountries: ["us"],
        // formatOnDisplay: false,
        // geoIpLookup: function(callback) {
        //   $.get("http://ipinfo.io", function() {}, "jsonp").always(function(resp) {
        //     var countryCode = (resp && resp.country) ? resp.country : "";
        //     callback(countryCode);
        //   });
        // },
        // initialCountry: "auto",
        // nationalMode: false,
        // onlyCountries: ['us', 'gb', 'ch', 'ca', 'do'],
        // placeholderNumberType: "MOBILE",
        preferredCountries: ['in'],
        // separateDialCode: true,
        utilsScript: "${pageContext.request.contextPath}/static2/external/intl-tel-input/js/utils.js"
    });
</script>
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
