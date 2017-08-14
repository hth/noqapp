<%@ include file="../../../jsp/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="utf-8">
    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
    <meta content='width=device-width, initial-scale=1' name='viewport' />

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'  />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/intl-tel-input/css/intlTelInput.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen" />
</head>

<body>

<!-- header -->
<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" /></div>
        <div class="top-menu-right2">
            <div class="menu-wrap">
                <div class="txt-tight mobile-icon"><a class="toggleMenu" href="#"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-icon.png" /></a></div>
                <div class="clearFix"></div>

                <ul class="nav">
                    <li><a href="#"><sec:authentication property="principal.username" /></a></li>
                    <li><a href="login.html">Account</a></li>
                    <li><a href="login.html">Feedback</a></li>
                    <li>
                        <a href="#">
                            <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                                <input type="submit" value="LOG OUT" class="logout_btn"/>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            </form>
                        </a>
                    </li>

                    <div class="clearFix"></div>
                </ul>
                <div class="clearFix"></div>
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
                <sec:authorize access="hasRole('ROLE_M_ADMIN')">

                    <div class="admin-title">
                        <h2>Add New Supervisor</h2>
                    </div>

                    <form:form commandName="inviteQueueSupervisor">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

                        <c:if test="${!empty flowRequestContext.messageContext.allMessages}">
                            <div class="r-validation" style="width: 100%; margin: 0 0 0 0;">
                                <ul>
                                    <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
                                        <li>${message.text}</li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>

                        <div class="admin-content">
                            <div class="add-new">
                                <ul class="list-form">
                                    <li>
                                        <div class="col-lable3"><form:label path="phoneNumber" cssErrorClass="profile_label lb_error">Phone Number</form:label></div>
                                        <div class="col-fields">
                                            <form:input path="phoneNumber" cssClass="form-fe" readonly="true" />
                                            <span class="info-txt">(Must be a registered users)</span>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>

                                    <li>
                                        <div class="col-lable3"><form:label path="inviteeCode" cssErrorClass="profile_label lb_error">Invitee Code</form:label></div>
                                        <div class="col-fields">
                                            <form:input path="inviteeCode" cssClass="form-field-admin" readonly="true" />
                                            <span class="info-txt">(Supervisor's invitee code found on Invite screen)</span>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>

                                    <li class="mB0">
                                        <div class="col-lable3"> </div>
                                        <div class="col-fields">
                                            <div class="left-btn"><input name="_eventId_confirm" class="next-btn" value="CONFIRM" type="submit"></div>
                                            <div class="right-btn"><input name="_eventId_revise" class="cancel-btn" value="REVISE" type="submit"></div>
                                            <div class="right-btn"><input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit"></div>
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
            <img src="${pageContext.request.contextPath}/static2/internal/img/footer-img.jpg" class="img100" />
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2017  NoQueue.   |  <a href="#">Privacy</a>    |    <a href="#">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>



</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
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

</html>
