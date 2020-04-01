<%@ page import="com.noqapp.domain.types.BusinessUserRegistrationStatusEnum" %>
<%@ include file="../../../../jsp/include.jsp" %>
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
            <!-- Add New Supervisor -->
            <sec:authorize access="hasRole('ROLE_CLIENT')">
            <div class="admin-main">
                <form:form modelAttribute="register">
                    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                    <div class="admin-title">
                        <h2>Review your personal details</h2>
                    </div>
                    <div class="admin-content">
                        <div class="add-new">
                            <ul class="list-form">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerUser.firstName" cssErrorClass="lb_error">First name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="registerUser.firstName" cssClass="form-field-admin" readonly="true"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>

                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerUser.lastName" cssErrorClass="lb_error">Last name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="registerUser.lastName" cssClass="form-field-admin" readonly="true"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>

                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerUser.address" cssErrorClass="lb_error">Your Address</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:textarea path="registerUser.address" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>

                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerUser.phone" cssErrorClass="lb_error">Your Phone</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="registerUser.phone" cssClass="form-field-admin" readonly="true"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>

                            <div id="storeDetail">
                                <div class="admin-title pT30">
                                    <h2>Review Business details</h2>
                                </div>
                                <ul class="list-form">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerBusiness.name" cssErrorClass="lb_error">Business Name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="registerBusiness.name" cssClass="form-field-admin" readonly="true"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerBusiness.businessType" cssErrorClass="lb_error">Business Type</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="registerBusiness.businessType" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:options items="${register.registerBusiness.availableBusinessTypes}" itemValue="name" itemLabel="description" disabled="true"/>
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerBusiness.address" cssErrorClass="lb_error">Business Address</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:textarea path="registerBusiness.address" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerBusiness.area" cssErrorClass="lb_error">Business Town</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="registerBusiness.area" cssClass="form-field-admin" readonly="true"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerBusiness.town" cssErrorClass="lb_error">Business City</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="registerBusiness.town" cssClass="form-field-admin" readonly="true"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerBusiness.phone" cssErrorClass="lb_error">Business Phone</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="registerBusiness.phone" cssClass="form-field-admin" readonly="true"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <c:if test="${register.registerBusiness.businessUserRegistrationStatus == BusinessUserRegistrationStatusEnum.V}">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerBusiness.dayClosed" cssErrorClass="lb_error" style="color: #9f1313;">Business Closed</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:checkbox path="registerBusiness.dayClosed" cssClass="form-check-box" cssErrorClass="form-check-box error-field" disabled="true"/>
                                        <span style="display:block; font-size:14px; color: #9f1313;">(Example: Closed for national holiday. This will remain closed unless unchecked. No Queues are open.)</span>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                </c:if>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerBusiness.claimed" cssErrorClass="lb_error" style="color: #9f1313;">Business Claimed</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:checkbox path="registerBusiness.claimed" cssClass="form-check-box" cssErrorClass="form-check-box error-field" disabled="true"/>
                                        <span style="display:block; font-size:14px; color: #9f1313;">(Check if business is claimed)</span>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="registerBusiness.inviteeCode" cssErrorClass="lb_error">Have Invitee Code?</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="registerBusiness.inviteeCode" cssClass="form-field-admin" readonly="true"/>
                                        <span style="display:block; font-size:14px;">Optional. Enter if you have been provided with one.</span>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>
                            </div>

                            <div class="full">
                                <div class="admin-title pT30">
                                    <h2>Business Amenities</h2>
                                </div>
                                <ul class="col3-grid">
                                    <c:choose>
                                        <c:when test="${!empty register.registerBusiness.amenities}">
                                            <form:checkboxes element="li" path="registerBusiness.amenities" items="${register.registerBusiness.amenities}" disabled="true"/>
                                            <div class="clearFix"></div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="alert-info">
                                                <p>
                                                    No amenity has been selected
                                                </p>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </ul>

                                <div class="admin-title pT30">
                                    <h2>Business Facilities</h2>
                                </div>
                                <ul class="col3-grid">
                                    <c:choose>
                                        <c:when test="${!empty register.registerBusiness.facilities}">
                                            <form:checkboxes element="li" path="registerBusiness.facilities" items="${register.registerBusiness.facilities}" disabled="true"/>
                                            <div class="clearFix"></div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="alert-info">
                                                <p>
                                                    No facility has been selected
                                                </p>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </ul>
                            </div>

                            <div class="btn-hours">
                                <c:choose>
                                    <c:when test="${register.registerUser.emailValidated}">
                                        <div class="button-btn">
                                            <button name="_eventId_confirm" class="ladda-button next-btn" style="width:32%; float: left">Confirm</button>
                                            <button name="_eventId_revise" class="ladda-button cancel-btn" style="width:32%; float: left; margin-left:2%">Revise</button>
                                            <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:32%; float: right">Cancel</button>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="button-btn">
                                            <button name="_eventId_revise" class="ladda-button cancel-btn" style="width:48%; float: left">Revise</button>
                                            <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <%--<c:if test="${register.registerUser.emailValidated}">--%>
                                    <%--<div class="first-btn">--%>
                                        <%--<input name="_eventId_confirm" class="next-btn" value="CONFIRM" type="submit">--%>
                                    <%--</div>--%>
                                <%--</c:if>--%>
                                <%--<div class="center-btn">--%>
                                    <%--<input name="_eventId_revise" class="cancel-btn" value="REVISE" type="submit">--%>
                                <%--</div>--%>
                                <%--<div class="last-btn">--%>
                                    <%--<input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">--%>
                                <%--</div>--%>
                                <div class="clearFix"></div>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
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
