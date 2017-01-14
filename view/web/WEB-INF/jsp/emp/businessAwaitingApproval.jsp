<%@ include file="../include.jsp"%>
<!DOCTYPE html>
<html lang="en" ng-app="scroll" ng-controller="Main">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
    <link rel='stylesheet' href='${pageContext.request.contextPath}/static/external/css/fineuploader/fine-uploader.css'/>
    <link rel='stylesheet' href='//cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.0.1/fullcalendar.min.css'/>
    <link rel='stylesheet' href='//cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.0.1/fullcalendar.print.css' media='print'/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/5.0.0/highcharts.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/fineuploader/jquery.fine-uploader.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.0.1/fullcalendar.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/randomcolor/0.4.4/randomColor.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/classie.js"></script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><a href="/access/landing.htm"><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/>Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                    <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                        <input type="submit" value="LOG OUT" class="logout_btn"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                <sec:authentication var="validated" property="principal.accountValidated"/>
                <c:choose>
                    <c:when test="${!validated}">
                        <a class="top-account-bar-text user-email" href="/access/userprofilepreference/i.htm">
                            <sec:authentication property="principal.username" />
                            <span class="notification-counter">1</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text user-email" href="#">
                            <sec:authentication property="principal.username" />
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<header>
</header>
<div class="main clearfix">
    <div class="sidebar_no_use">
    </div>
    <div class="rightside-content">
    <sec:authorize access="hasRole('ROLE_SUPERVISOR')">
    <div class="business_reg">
        <div class="down_form" style="width: 90%">
            <form:form method="POST" action="./approval.htm" modelAttribute="businessAwaitingApprovalForm">
                <form:hidden path="businessUser.id"/>
                <h1 class="h1">Confirm personal and business details</h1>
                <hr>
                <div class="row_field">
                    <form:label path="userProfile.firstName" cssClass="profile_label" cssStyle="width: 145px;"
                            cssErrorClass="profile_label lb_error">First name</form:label>
                    <form:input path="userProfile.firstName" size="20" cssClass="name_txt" readonly="true" />
                </div>
                <div class="row_field">
                    <form:label path="userProfile.lastName" cssClass="profile_label" cssStyle="width: 145px;"
                            cssErrorClass="profile_label lb_error">Last name</form:label>
                    <form:input path="userProfile.lastName" size="20" cssClass="name_txt" readonly="true" />
                </div>
                <div class="row_field">
                    <form:label path="userProfile.address" cssClass="profile_label" cssStyle="width: 145px;"
                            cssErrorClass="profile_label lb_error">Your Address</form:label>
                    <form:input path="userProfile.address" size="200" cssClass="name_txt" readonly="true" style="width: 600px;" />
                </div>
                <div class="row_field">
                    <form:label path="userProfile.phone" cssClass="profile_label" cssStyle="width: 145px;"
                            cssErrorClass="profile_label lb_error">Your Phone</form:label>
                    <form:input path="userProfile.phone" size="20" cssClass="name_txt" readonly="true" />
                </div>
                <div class="row_field">
                    <form:label path="businessUser.bizName.businessName" cssClass="profile_label" cssStyle="width: 145px;"
                            cssErrorClass="profile_label lb_error">Business Name</form:label>
                    <form:input path="businessUser.bizName.businessName" size="20" cssClass="name_txt" readonly="true" />
                </div>
                <div class="row_field">
                    <form:label path="businessUser.bizName.businessTypes" cssClass="profile_label" cssStyle="width: 145px;"
                            cssErrorClass="profile_label lb_error">Business Type</form:label>

                    <form:select path="businessUser.bizName.businessTypes" cssClass="styled-select slate" multiple="true" style="height: 100px;">
                        <form:options items="${businessAwaitingApprovalForm.availableBusinessTypes}" itemValue="name" itemLabel="description" disabled="true" />
                    </form:select>
                </div>
                <div class="row_field">
                    <form:label path="businessUser.bizName.address" cssClass="profile_label" cssStyle="width: 145px;"
                            cssErrorClass="profile_label lb_error">Business Address</form:label>
                    <form:input path="businessUser.bizName.address" size="200" cssClass="name_txt" readonly="true" style="width: 600px;" />
                </div>
                <div class="row_field">
                    <form:label path="businessUser.bizName.phone" cssClass="profile_label" cssStyle="width: 145px;"
                            cssErrorClass="profile_label lb_error">Business Phone</form:label>
                    <form:input path="businessUser.bizName.phone" size="20" cssClass="name_txt" readonly="true" />
                </div>

                <c:choose>
                    <c:when test="${businessAwaitingApprovalForm.businessUser.businessUserRegistrationStatus eq 'C'}">
                        <input type="submit" value="APPROVE" class="read_btn" name="business-user-approve"
                                style="background: #2c97de; margin: 77px 10px 0 0;">
                        <input type="submit" value="DECLINE" class="read_btn" name="business-user-decline"
                                style="background: #FC462A; margin: 77px 10px 0 0;">
                    </c:when>
                    <c:otherwise>
                        <input type="submit" value="CANCEL" class="read_btn" name="_eventId_cancel"
                                style="background: #2c97de; margin: 77px 10px 0 0;">
                    </c:otherwise>
                </c:choose>
            </form:form>
        </div>
    </div>
    </sec:authorize>
    </div>
    <div class="footer-tooth clearfix">
        <div class="footer-tooth-middle"></div>
        <div class="footer-tooth-right"></div>
    </div>
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2017 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
</html>
