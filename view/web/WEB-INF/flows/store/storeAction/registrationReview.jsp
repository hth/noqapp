<%@ page import="com.noqapp.domain.types.AppointmentStateEnum, com.noqapp.domain.types.MessageOriginEnum, com.noqapp.domain.types.BusinessSupportEnum" %>
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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>
</head>

<body>
<!-- header -->
<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left">
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static/internal/img/logo.png" alt="NoQueue"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow">
                        <img src="${pageContext.request.contextPath}/static/internal/img/menu-top-arrow.png"/>
                    </div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/">Home</a>
                        <form action="${pageContext.request.contextPath}/access/signoff" method="post">
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
            <sec:authorize access="hasAnyRole('ROLE_M_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')">
                <div class="admin-main">
                    <form:form modelAttribute="registerBusiness">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                        <div class="admin-title">
                            <h2>Confirm your ${registerBusiness.businessType.classifierTitle.toLowerCase()} details</h2>
                            <h3>${registerBusiness.displayName}</h3>
                        </div>
                        <div class="admin-content">
                            <div class="add-new">
                                <div id="storeDetail">
                                    <div class="admin-title pT30">
                                        <h2>${registerBusiness.businessType.classifierTitle} profile</h2>
                                    </div>
                                    <ul class="list-form">
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="displayName" cssErrorClass="lb_error">Online ${registerBusiness.businessType.classifierTitle} Name</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="displayName" cssClass="form-field-admin" readonly="true"/>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <c:if test="${!empty registerBusiness.categories}">
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="bizCategoryId" cssErrorClass="lb_error">Category</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:select path="bizCategoryId" cssClass="form-field-select single-dropdown" multiple="false" disabled="true">
                                                    <form:option value="" label="--- Select ---"/>
                                                    <form:options items="${registerBusiness.categories}" />
                                                </form:select>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        </c:if>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="addressStore" cssErrorClass="lb_error">Store Address</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:textarea path="addressStore" cols="" rows="3" cssClass="form-field-admin" readonly="true"/>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="areaStore" cssErrorClass="lb_error">Store Town</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="areaStore" cssClass="form-field-admin" readonly="true"/>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="townStore" cssErrorClass="lb_error">Store City</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="townStore" cssClass="form-field-admin" readonly="true"/>
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
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="famousFor" cssErrorClass="lb_error">Famous For</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="famousFor" cssClass="form-field-admin" readonly="true"/>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <c:if test="${MessageOriginEnum.Q == registerBusiness.businessType.messageOrigin}">
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="walkInState" cssErrorClass="lb_error">Walk-in</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:select path="walkInState" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false" disabled="true">
                                                    <form:option value="" label="--- Select ---"/>
                                                    <form:options items="${registerBusiness.walkinStates}" />
                                                </form:select>
                                                <span style="display:block; font-size:14px;">(Allow user to take walk-in appointments)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        </c:if>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="remoteJoin" cssErrorClass="lb_error">Allow Remote Join</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:checkbox path="remoteJoin" cssClass="form-check-box" disabled="true"/>
                                                <span style="display:block; font-size:14px;">(Allow user to join queue from Home, place online orders)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="allowLoggedInUser" cssErrorClass="lb_error">Allow Registered User</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:checkbox path="allowLoggedInUser" cssClass="form-check-box" disabled="true"/>
                                                <span style="display:block; font-size:14px;">(Will limit registered users joining this queue)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <c:if test="${MessageOriginEnum.Q == registerBusiness.businessType.messageOrigin}">
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="availableTokenCount" cssErrorClass="lb_error">Issue Limited Tokens</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="availableTokenCount" cssClass="form-field-admin" disabled="true"/>
                                                <span style="display:block; font-size:14px;">(Customers in the queue will be limited to allowed number. 0 is Unlimited Token. Greater than 0 is limited token)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        </c:if>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="averageServiceTimeInMinutes" cssErrorClass="lb_error">Average Handling Time</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="averageServiceTimeInMinutes" cssClass="form-field-admin" disabled="true"/>
                                                <span style="display:block; font-size:14px;">(AHT: Expected average service time per customer in minutes)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="appendPrefixToToken" cssErrorClass="lb_error">Append Token Prefix</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:select path="appendPrefixToToken" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false" disabled="true">
                                                    <form:option value="" label="--- Select ---"/>
                                                    <form:options items="${registerBusiness.tokenPrefixes}" />
                                                </form:select>
                                                <span style="display:block; font-size:14px;">(Appends prefix to tokens. Example A101 or V101)</span>
                                            </div>
                                            <span class="tooltip" title="Helps separate different token based on prefix" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                            <div class="clearFix"></div>
                                        </li>
                                        <c:if test="${MessageOriginEnum.O == registerBusiness.businessType.messageOrigin}">
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="deliveryRange" cssErrorClass="lb_error">Delivery Radius</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="deliveryRange" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" disabled="true"/>
                                                <span style="display:block; font-size:14px;">(Delivery Range in km)</span>
                                            </div>
                                            <span class="tooltip" title="Accepts order from locations within range" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                            <div class="clearFix"></div>
                                        </li>
                                        </c:if>
                                    </ul>
                                </div>

                                <div class="full">
                                    <div class="admin-title pT30">
                                        <h2>${registerBusiness.businessType.classifierTitle} Amenities</h2>
                                    </div>
                                    <ul class="col3-grid">
                                        <c:choose>
                                            <c:when test="${!empty registerBusiness.amenitiesStore}">
                                                <form:checkboxes element="li" path="amenitiesStore" items="${registerBusiness.amenitiesStore}" disabled="true"/>
                                                <div class="clearFix"></div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="alert-info">
                                                    <p>
                                                        No ${registerBusiness.businessType.classifierTitle.toLowerCase()} amenity has been selected
                                                    </p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </ul>

                                    <div class="admin-title pT30">
                                        <h2>${registerBusiness.businessType.classifierTitle} Facilities</h2>
                                    </div>
                                    <ul class="col3-grid">
                                        <c:choose>
                                            <c:when test="${!empty registerBusiness.facilitiesStore}">
                                                <form:checkboxes element="li" path="facilitiesStore" items="${registerBusiness.facilitiesStore}" disabled="true"/>
                                                <div class="clearFix"></div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="alert-info">
                                                    <p>
                                                        No ${registerBusiness.businessType.classifierTitle.toLowerCase()} facility has been selected
                                                    </p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </ul>

                                    <div class="admin-title pT30">
                                        <h2>${registerBusiness.businessType.classifierTitle} Supports Delivery Types</h2>
                                    </div>
                                    <ul class="col3-grid">
                                        <c:choose>
                                        <c:when test="${!empty registerBusiness.acceptedDeliveries}">
                                            <form:checkboxes element="li" path="acceptedDeliveries" items="${registerBusiness.acceptedDeliveries}" disabled="true"/>
                                            <div class="clearFix"></div>
                                        </c:when>
                                            <c:otherwise>
                                                <div class="alert-info">
                                                    <p>Delivery options not supported</p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </ul>
                                </div>

                                <div class="admin-title pT30">
                                    <h2>${registerBusiness.businessType.classifierTitle} Accepts Payment</h2>
                                </div>
                                <ul class="col3-grid">
                                    <c:choose>
                                        <c:when test="${!empty registerBusiness.acceptedPayments}">
                                            <form:checkboxes element="li" path="acceptedPayments" items="${registerBusiness.acceptedPayments}" disabled="true"/>
                                            <div class="clearFix"></div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="alert-info">
                                                <p>Payment options not supported</p>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </ul>

                                <div>
                                    <div class="admin-title pT30">
                                        <h2>${registerBusiness.businessType.classifierTitle} Hours</h2>
                                    </div>
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
                                                            <td>${registerBusiness.labelForOrderOrToken} Available Time</td>
                                                            <td><c:out value="${businessHour.tokenAvailableFromAsString}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <td>${registerBusiness.businessType.classifierTitle} Start Time</td>
                                                            <td><c:out value="${businessHour.startHourStoreAsString}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <td>&nbsp;</td>
                                                            <td>&nbsp;</td>
                                                        </tr>
                                                        <tr>
                                                            <td>${registerBusiness.labelForOrderOrToken} Not Available After</td>
                                                            <td><c:out value="${businessHour.tokenNotAvailableFromAsString}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <td>${registerBusiness.businessType.classifierTitle} Close Time</td>
                                                            <td><c:out value="${businessHour.endHourStoreAsString}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <td>&nbsp;</td>
                                                            <td>&nbsp;</td>
                                                        </tr>
                                                        <tr>
                                                            <td>${registerBusiness.businessType.classifierTitle} Lunch Start Time</td>
                                                            <td><c:out value="${businessHour.lunchTimeStartAsString}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <td>${registerBusiness.businessType.classifierTitle} Lunch End Time</td>
                                                            <td><c:out value="${businessHour.lunchTimeEndAsString}"/></td>
                                                        </tr>
                                                    </table>
                                                    </c:otherwise>
                                                </c:choose>
                                            </li>
                                        </c:forEach>

                                        <div class="clearFix"></div>
                                    </ul>
                                </div>

                                <c:if test="${BusinessSupportEnum.QQ eq registerBusiness.businessType.businessSupport}">
                                <div class="full">
                                    <div class="admin-title pT30">
                                        <h2>Appointment Settings</h2>
                                    </div>
                                    <ul class="list-form">
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="appointmentState" cssErrorClass="lb_error">Allow Appointment</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:select path="appointmentState" cssClass="form-field-select single-dropdown" multiple="false" disabled="true">
                                                    <form:option value="" label="--- Select ---"/>
                                                    <form:options items="${registerBusiness.appointmentStates}" />
                                                </form:select>
                                                <span style="display:block; font-size:14px;">(Allow user to take appointment)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="appointmentDuration" cssErrorClass="lb_error">Duration of Appointment</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="appointmentDuration" cssClass="form-field-admin" disabled="true"/>
                                                <span style="display:block; font-size:14px;">(Duration of each appointment in minutes)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="appointmentOpenHowFar" cssErrorClass="lb_error">Booking Window</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="appointmentOpenHowFar" cssClass="form-field-admin" disabled="true"/>
                                                <span style="display:block; font-size:14px;">(Booking window lets user book weeks ahead of time. Max booking window is 52 weeks.)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                    </ul>
                                </div>

                                <c:if test="${registerBusiness.appointmentState ne AppointmentStateEnum.O}">
                                <div>
                                    <div class="admin-title pT30">
                                        <h2>Appointment Hours</h2>
                                    </div>
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
                                                        <td>${registerBusiness.businessType.classifierTitle} Start Time</td>
                                                        <td><c:out value="${businessHour.startHourStoreAsString}"/></td>
                                                    </tr>
                                                    <tr>
                                                        <td>Appointment Start Time</td>
                                                        <td><c:out value="${businessHour.appointmentStartHourStoreAsString}"/></td>
                                                    </tr>
                                                    <tr>
                                                        <td>&nbsp;</td>
                                                        <td>&nbsp;</td>
                                                    </tr>
                                                    <tr>
                                                        <td>${registerBusiness.businessType.classifierTitle} Close Time</td>
                                                        <td><c:out value="${businessHour.endHourStoreAsString}"/></td>
                                                    </tr>
                                                    <tr>
                                                        <td>Appointment End Time</td>
                                                        <td><c:out value="${businessHour.appointmentEndHourStoreAsString}"/></td>
                                                    </tr>
                                                </table>
                                                </c:otherwise>
                                            </c:choose>
                                        </li>
                                        </c:forEach>

                                        <div class="clearFix"></div>
                                    </ul>
                                </div>
                                </c:if>
                                </c:if>

                                <div>
                                    <div class="btn-hours">
                                        <div class="button-btn">
                                            <button name="_eventId_confirm" class="ladda-button next-btn" style="width:32%; float: left">Confirm</button>
                                            <button name="_eventId_revise" class="ladda-button cancel-btn" style="width:32%; float: left; margin-left:2%">Revise</button>
                                            <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:32%; float: right">Cancel</button>
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
            <img src="${pageContext.request.contextPath}/static/internal/img/footer-img.jpg" class="img100"/>
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2021 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/ladda/js/ladda.min.js"></script>
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
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
