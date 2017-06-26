<%@ include file="../../../jsp/include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/colpick.css"/>
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/access/landing.htm">Receiptofi</a></h1>
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
        <sec:authorize access="hasRole('ROLE_MER_ADMIN')">
        <div class="business_reg">
            <div class="down_form" style="width: 90%">
                <form:form commandName="registerBusiness">
                    <div id="storeDetail">
                    <h1 class="h1">&nbsp;</h1>
                    <h1 class="h1">Add Store details</h1>
                    <hr>

                    <div class="row_field">
                        <form:label path="displayName" cssClass="profile_label" cssStyle="width: 155px;"
                                cssErrorClass="profile_label lb_error">Queue Name</form:label>
                        <form:input path="displayName" size="20" cssClass="name_txt" readonly="true" style="width: 600px;" />
                    </div>
                    <div class="row_field">
                        <form:label path="addressStore" cssClass="profile_label" cssStyle="width: 155px;"
                                cssErrorClass="profile_label lb_error">Store Address</form:label>
                        <form:input path="addressStore" size="200" cssClass="name_txt" readonly="true" style="width: 600px;" />
                    </div>
                    <div class="row_field">
                        <form:label path="phoneStore" cssClass="profile_label" cssStyle="width: 155px;"
                                cssErrorClass="profile_label lb_error">Store Phone</form:label>
                        <form:input path="phoneStore" size="20" cssClass="name_txt" readonly="true" />
                    </div>
                    <c:forEach items="${registerBusiness.businessHours}" var="businessHour" varStatus="status">
                        <h2 class="h2"><c:out value="${businessHour.dayOfWeek}" /></h2>
                        <hr>

                        Token Available Time: <c:out value="${businessHour.tokenAvailableFrom}" /> <br>
                        Store Start Time: <c:out value="${businessHour.startHourStore}" /> <br>
                        Token Not Available After: <c:out value="${businessHour.tokenNotAvailableFrom}" /> <br>
                        Store Close Time: <c:out value="${businessHour.endHourStore}" /> <br>

                        <br>
                    </c:forEach>

                    </div>

                    <div class="full">
                        <input type="submit" value="CONFIRM" class="read_btn" name="_eventId_confirm"
                                style="background: #2c97de; margin: 77px 10px 0 0;">
                        <input type="submit" value="REVISE" class="read_btn" name="_eventId_revise"
                                style="background: #2c97de; margin: 77px 10px 0 0;">
                        <input type="submit" value="CANCEL" class="read_btn" name="_eventId_cancel"
                                style="background: #FC462A; margin: 77px 10px 0 0;" formnovalidate>
                    </div>
                </form:form>
            </div>
        </div>
        </sec:authorize>
    </div>
</div>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2017 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        if ($('[name="registerBusiness.multiStore"]').is(':checked')) {
            $('#storeDetail').hide();
        } else {
            $('#storeDetail').show();
        }
    });
</script>
</html>