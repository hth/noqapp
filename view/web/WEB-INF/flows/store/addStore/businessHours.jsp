<%@ include file="../../../jsp/include.jsp" %>
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
            <sec:authorize access="hasRole('ROLE_M_ADMIN')">
                <div class="admin-main">
                    <form:form modelAttribute="registerBusiness">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                        <div class="admin-title">
                            <h2>Add Store Hours For Each Day</h2>
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
                            <div class="full">

                                <ul class="col2-grid">
                                    <c:forEach items="${registerBusiness.businessHours}" var="businessHour" varStatus="status">
                                        <li>
                                            <h4><strong><c:out value="${businessHour.dayOfWeek}"/></strong></h4>
                                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td width="39%" class="lable-td">
                                                        <form:label path="businessHours[${status.index}].tokenAvailableFrom" cssErrorClass="lb_error">Token Available Time</form:label>
                                                    </td>
                                                    <td width="61%">
                                                        <form:input path="businessHours[${status.index}].tokenAvailableFrom" cssClass="form-field-hours" cssErrorClass="form-field-hours error-field"/>
                                                        <span>(As 1800 for 6:00 PM)</span>
                                                    </td>
                                                </tr>

                                                <tr>
                                                    <td class="lable-td">
                                                        <form:label path="businessHours[${status.index}].startHourStore" cssErrorClass="lb_error">Store Start Time</form:label>
                                                    </td>
                                                    <td>
                                                        <form:input path="businessHours[${status.index}].startHourStore" cssClass="form-field-hours" cssErrorClass="form-field-hours error-field"/>
                                                        <span>(As 1000 for 10:00 AM)</span>
                                                    </td>
                                                </tr>

                                                <tr>
                                                    <td class="lable-td">
                                                        <form:label path="businessHours[${status.index}].tokenNotAvailableFrom" cssErrorClass="lb_error">Token Not Available After</form:label>
                                                    </td>
                                                    <td>
                                                        <form:input path="businessHours[${status.index}].tokenNotAvailableFrom" cssClass="form-field-hours" cssErrorClass="form-field-hours error-field"/>
                                                        <span>(As 1800 for 6:00 PM)</span>
                                                    </td>
                                                </tr>

                                                <tr>
                                                    <td class="lable-td">
                                                        <form:label path="businessHours[${status.index}].endHourStore" cssErrorClass="lb_error">Store Close Time</form:label>
                                                    </td>
                                                    <td>
                                                        <form:input path="businessHours[${status.index}].endHourStore" cssClass="form-field-hours" cssErrorClass="form-field-hours error-field"/>
                                                        <span>(As 1800 for 6:00 PM)</span>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="lable-td pT0">
                                                        <form:label path="businessHours[${status.index}].dayClosed" cssErrorClass="lb_error">Store Closed</form:label>
                                                    </td>
                                                    <td>
                                                        <form:checkbox path="businessHours[${status.index}].dayClosed" cssErrorClass="error-field"/>
                                                    </td>
                                                </tr>
                                            </table>
                                        </li>
                                    </c:forEach>

                                    <div class="clearFix"></div>
                                </ul>

                                <div class="btn-hours">
                                    <div class="left-btn">
                                        <input name="_eventId_submit" class="next-btn" value="NEXT" type="submit">
                                    </div>
                                    <div class="right-btn">
                                        <input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">
                                    </div>
                                    <div class="clearFix"></div>
                                </div>
                                <div class="clearFix"></div>
                            </div>
                        </div>
                    </form:form>
                </div>
                <!-- Add New Supervisor -->
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
                    <div class="f-left">&copy; 2017 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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

</html>
