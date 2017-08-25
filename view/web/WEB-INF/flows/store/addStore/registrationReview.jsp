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
                        <a href="#">Account</a>
                        <a href="#">Feedback</a>
                        <a href="#">Sign In</a>
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
                    <form:form commandName="registerBusiness">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                        <div class="admin-title">
                            <h2>Confirm your personal and business details</h2>
                        </div>
                        <div class="admin-content">
                            <div class="add-new">
                                <div id="storeDetail">
                                    <div class="admin-title pT30">
                                        <h2>Add Store details</h2>
                                    </div>
                                    <ul class="list-form">
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="displayName" cssErrorClass="lb_error">Queue Name</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="displayName" cssClass="form-field-admin" readonly="true"/>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="addressStore" cssErrorClass="lb_error">Store Address</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:textarea path="addressStore" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="phoneStore" cssErrorClass="lb_error">Store Phone</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="phoneStore" cssClass="form-field-admin" readonly="true"/>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>

                                    </ul>
                                </div>

                                <div>
                                    <ul class="col2-grid">
                                        <c:forEach items="${registerBusiness.businessHours}" var="businessHour" varStatus="status">
                                            <li>
                                                <h4><strong><c:out value="${businessHour.dayOfWeek}"/></strong></h4>
                                                <c:choose>
                                                    <c:when test="${businessHour.dayClosed}">
                                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <td>Closed for the day</td>
                                                            </tr>
                                                        </table>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <td>Token Available Time</td>
                                                                <td>
                                                                    <c:out value="${businessHour.tokenAvailableFrom}"/></td>
                                                            </tr>
                                                            <tr>
                                                                <td>Store Start Time</td>
                                                                <td><c:out value="${businessHour.startHourStore}"/></td>
                                                            </tr>
                                                            <tr>
                                                                <td>Token Not Available After</td>
                                                                <td>
                                                                    <c:out value="${businessHour.tokenNotAvailableFrom}"/></td>
                                                            </tr>
                                                            <tr>
                                                                <td>Store Close Time</td>
                                                                <td><c:out value="${businessHour.endHourStore}"/></td>
                                                            </tr>
                                                        </table>
                                                    </c:otherwise>
                                                </c:choose>
                                            </li>
                                        </c:forEach>

                                        <div class="clearFix"></div>
                                    </ul>

                                    <div class="btn-hours">
                                        <div class="first-btn">
                                            <input name="_eventId_confirm" class="next-btn" value="CONFIRM" type="submit">
                                        </div>
                                        <div class="center-btn">
                                            <input name="_eventId_revise" class="cancel-btn" value="REVISE" type="submit">
                                        </div>
                                        <div class="last-btn">
                                            <input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                </div>
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
                    <div class="f-left">&copy; 2017 NoQueue Inc. | <a href="#">Privacy</a> | <a href="#">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
</html>
