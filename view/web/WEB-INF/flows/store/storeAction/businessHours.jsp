<%@ page import="com.noqapp.domain.types.MessageOriginEnum" %>
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

    <link rel="stylesheet" href="//cdn.jsdelivr.net/gh/StephanWagner/jBox@v1.0.9/dist/jBox.all.min.css" type='text/css'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>

    <!-- reference your copy Font Awesome here (from our CDN or by hosting yourself) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/external/fontawesome/css/fontawesome.css" type='text/css'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/external/fontawesome/css/brands.css" type='text/css'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/external/fontawesome/css/solid.css" type='text/css'>

    <!-- custom styling for all icons -->
    i.fas,
    i.fab {
        border: 1px solid red;
    }
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
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static/internal/img/menu-top-arrow.png"/></div>
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
                        <c:choose>
                            <c:when test="${!empty registerBusiness.bizStoreId}">
                                <h2>Edit ${registerBusiness.businessType.classifierTitle} Hours For Each Day</h2>
                                <h3>${registerBusiness.displayName}</h3>
                            </c:when>
                            <c:otherwise>
                                <h2>Add ${registerBusiness.businessType.classifierTitle} Hours For Each Day</h2>
                                <h3>${registerBusiness.displayName}</h3>
                            </c:otherwise>
                        </c:choose>
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
                                <li>
                                    <table width="325px" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td class="lable-td pT0">
                                                <label for="copyData">Copy Monday's Data For All Other Days</label>
                                            </td>
                                            <td>
                                                <input id="copyData" name="copyData" type="checkbox" />
                                            </td>
                                        </tr>
                                    </table>
                                </li>
                                <div class="clearFix"></div>
                            </ul>
                        </div>

                        <div class="full">
                            <ul class="col2-grid">
                                <c:forEach items="${registerBusiness.businessHours}" var="businessHour" varStatus="status">
                                    <li>
                                        <h4><strong><c:out value="${businessHour.dayOfWeek}"/></strong></h4>
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <td width="45%" class="lable-td">
                                                    <form:label path="businessHours[${status.index}].tokenAvailableFrom" cssErrorClass="lb_error">${registerBusiness.labelForOrderOrToken} Available Time</form:label>
                                                </td>
                                                <td width="45%">
                                                    <form:input path="businessHours[${status.index}].tokenAvailableFrom" cssClass="form-field-hours" cssErrorClass="form-field-hours error-field"/>
                                                    <span>(As 1800 for 6:00 PM)</span>
                                                </td>
                                                <td>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <c:choose>
                                                       <c:when test="${registerBusiness.businessType.messageOrigin == MessageOriginEnum.O}">
                                                           <span class="tooltip" title="Time when customers can place the order." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                       </c:when>
                                                        <c:otherwise>
                                                            <span class="tooltip" title="Time when customers can join the queue." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="lable-td">
                                                    <form:label path="businessHours[${status.index}].startHourStore" cssErrorClass="lb_error">${registerBusiness.businessType.classifierTitle} Start Time</form:label>
                                                </td>
                                                <td>
                                                    <form:input path="businessHours[${status.index}].startHourStore" cssClass="form-field-hours" cssErrorClass="form-field-hours error-field"/>
                                                    <span>(As 1000 for 10:00 AM)</span>
                                                </td>
                                                <td>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <c:choose>
                                                        <c:when test="${registerBusiness.businessType.messageOrigin == MessageOriginEnum.O}">
                                                            <span class="tooltip" title="Time when store opens up" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="tooltip" title="Time when queue opens up" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="lable-td">
                                                    <form:label path="businessHours[${status.index}].tokenNotAvailableFrom" cssErrorClass="lb_error">${registerBusiness.labelForOrderOrToken} Not Available After</form:label>
                                                </td>
                                                <td>
                                                    <form:input path="businessHours[${status.index}].tokenNotAvailableFrom" cssClass="form-field-hours" cssErrorClass="form-field-hours error-field"/>
                                                    <span>(As 1800 for 6:00 PM)</span>
                                                </td>
                                                <td>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <c:choose>
                                                        <c:when test="${registerBusiness.businessType.messageOrigin == MessageOriginEnum.O}">
                                                            <span class="tooltip" title="Time when store stops accepting order for the day" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="tooltip" title="Time when queue stops accepting new requests" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="lable-td">
                                                    <form:label path="businessHours[${status.index}].endHourStore" cssErrorClass="lb_error">${registerBusiness.businessType.classifierTitle} Close Time</form:label>
                                                </td>
                                                <td>
                                                    <form:input path="businessHours[${status.index}].endHourStore" cssClass="form-field-hours" cssErrorClass="form-field-hours error-field"/>
                                                    <span>(As 1800 for 6:00 PM)</span>
                                                </td>
                                                <td>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <c:choose>
                                                        <c:when test="${registerBusiness.businessType.messageOrigin == MessageOriginEnum.O}">
                                                            <span class="tooltip" title="Time when store closes for the day" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="tooltip" title="Time when queue closes for the day" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="lable-td">
                                                    <form:label path="businessHours[${status.index}].lunchTimeStart" cssErrorClass="lb_error">${registerBusiness.businessType.classifierTitle} Lunch Start Time</form:label>
                                                </td>
                                                <td>
                                                    <form:input path="businessHours[${status.index}].lunchTimeStart" cssClass="form-field-hours" cssErrorClass="form-field-hours error-field"/>
                                                    <span>(As 1800 for 6:00 PM)</span>
                                                </td>
                                                <td>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <c:choose>
                                                        <c:when test="${registerBusiness.businessType.messageOrigin == MessageOriginEnum.O}">
                                                            <span class="tooltip" title="Time when store closes for lunch" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="tooltip" title="Time when queue closes for lunch" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="lable-td">
                                                    <form:label path="businessHours[${status.index}].lunchTimeEnd" cssErrorClass="lb_error">${registerBusiness.businessType.classifierTitle} Lunch End Time</form:label>
                                                </td>
                                                <td>
                                                    <form:input path="businessHours[${status.index}].lunchTimeEnd" cssClass="form-field-hours" cssErrorClass="form-field-hours error-field"/>
                                                    <span>(As 1800 for 6:00 PM)</span>
                                                </td>
                                                <td>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <c:choose>
                                                        <c:when test="${registerBusiness.businessType.messageOrigin == MessageOriginEnum.O}">
                                                            <span class="tooltip" title="Time when store closes for lunch" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="tooltip" title="Time when queue closes for lunch" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="lable-td pT0">
                                                    <form:label path="businessHours[${status.index}].dayClosed" cssErrorClass="lb_error">${registerBusiness.businessType.classifierTitle} Closed</form:label>
                                                </td>
                                                <td>
                                                    <form:checkbox path="businessHours[${status.index}].dayClosed" cssErrorClass="error-field"/>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${registerBusiness.businessType.messageOrigin == MessageOriginEnum.O}">
                                                            <span class="tooltip" title="Day when store is closed" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="tooltip" title="Day when queue is closed" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </table>
                                    </li>
                                </c:forEach>

                                <div class="clearFix"></div>
                            </ul>

                            <div class="btn-hours">
                                <%--<div class="left-btn">--%>
                                    <%--<input name="_eventId_submit" class="next-btn" value="NEXT" type="submit">--%>
                                <%--</div>--%>
                                <%--<div class="right-btn">--%>
                                    <%--<input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">--%>
                                <%--</div>--%>

                                <div class="button-btn">
                                    <button name="_eventId_submit" class="ladda-button next-btn" style="width:48%; float: left">Next</button>
                                    <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
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
<script type="text/javascript" src="//cdn.jsdelivr.net/gh/StephanWagner/jBox@v1.0.9/dist/jBox.all.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/script.js"></script>
<script type="text/javascript">
    $('[name="copyData"]').click(function () {
        if (document.getElementById('copyData').checked) {
            document.getElementById('businessHours1.tokenAvailableFrom').value = document.getElementById('businessHours0.tokenAvailableFrom').value;
            document.getElementById('businessHours2.tokenAvailableFrom').value = document.getElementById('businessHours0.tokenAvailableFrom').value;
            document.getElementById('businessHours3.tokenAvailableFrom').value = document.getElementById('businessHours0.tokenAvailableFrom').value;
            document.getElementById('businessHours4.tokenAvailableFrom').value = document.getElementById('businessHours0.tokenAvailableFrom').value;
            document.getElementById('businessHours5.tokenAvailableFrom').value = document.getElementById('businessHours0.tokenAvailableFrom').value;
            document.getElementById('businessHours6.tokenAvailableFrom').value = document.getElementById('businessHours0.tokenAvailableFrom').value;

            document.getElementById('businessHours1.startHourStore').value = document.getElementById('businessHours0.startHourStore').value;
            document.getElementById('businessHours2.startHourStore').value = document.getElementById('businessHours0.startHourStore').value;
            document.getElementById('businessHours3.startHourStore').value = document.getElementById('businessHours0.startHourStore').value;
            document.getElementById('businessHours4.startHourStore').value = document.getElementById('businessHours0.startHourStore').value;
            document.getElementById('businessHours5.startHourStore').value = document.getElementById('businessHours0.startHourStore').value;
            document.getElementById('businessHours6.startHourStore').value = document.getElementById('businessHours0.startHourStore').value;

            document.getElementById('businessHours1.tokenNotAvailableFrom').value = document.getElementById('businessHours0.tokenNotAvailableFrom').value;
            document.getElementById('businessHours2.tokenNotAvailableFrom').value = document.getElementById('businessHours0.tokenNotAvailableFrom').value;
            document.getElementById('businessHours3.tokenNotAvailableFrom').value = document.getElementById('businessHours0.tokenNotAvailableFrom').value;
            document.getElementById('businessHours4.tokenNotAvailableFrom').value = document.getElementById('businessHours0.tokenNotAvailableFrom').value;
            document.getElementById('businessHours5.tokenNotAvailableFrom').value = document.getElementById('businessHours0.tokenNotAvailableFrom').value;
            document.getElementById('businessHours6.tokenNotAvailableFrom').value = document.getElementById('businessHours0.tokenNotAvailableFrom').value;

            document.getElementById('businessHours1.endHourStore').value = document.getElementById('businessHours0.endHourStore').value;
            document.getElementById('businessHours2.endHourStore').value = document.getElementById('businessHours0.endHourStore').value;
            document.getElementById('businessHours3.endHourStore').value = document.getElementById('businessHours0.endHourStore').value;
            document.getElementById('businessHours4.endHourStore').value = document.getElementById('businessHours0.endHourStore').value;
            document.getElementById('businessHours5.endHourStore').value = document.getElementById('businessHours0.endHourStore').value;
            document.getElementById('businessHours6.endHourStore').value = document.getElementById('businessHours0.endHourStore').value;

            document.getElementById('businessHours1.lunchTimeStart').value = document.getElementById('businessHours0.lunchTimeStart').value;
            document.getElementById('businessHours2.lunchTimeStart').value = document.getElementById('businessHours0.lunchTimeStart').value;
            document.getElementById('businessHours3.lunchTimeStart').value = document.getElementById('businessHours0.lunchTimeStart').value;
            document.getElementById('businessHours4.lunchTimeStart').value = document.getElementById('businessHours0.lunchTimeStart').value;
            document.getElementById('businessHours5.lunchTimeStart').value = document.getElementById('businessHours0.lunchTimeStart').value;
            document.getElementById('businessHours6.lunchTimeStart').value = document.getElementById('businessHours0.lunchTimeStart').value;

            document.getElementById('businessHours1.lunchTimeEnd').value = document.getElementById('businessHours0.lunchTimeEnd').value;
            document.getElementById('businessHours2.lunchTimeEnd').value = document.getElementById('businessHours0.lunchTimeEnd').value;
            document.getElementById('businessHours3.lunchTimeEnd').value = document.getElementById('businessHours0.lunchTimeEnd').value;
            document.getElementById('businessHours4.lunchTimeEnd').value = document.getElementById('businessHours0.lunchTimeEnd').value;
            document.getElementById('businessHours5.lunchTimeEnd').value = document.getElementById('businessHours0.lunchTimeEnd').value;
            document.getElementById('businessHours6.lunchTimeEnd').value = document.getElementById('businessHours0.lunchTimeEnd').value;
        }
    });
</script>
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
    new jBox('Tooltip', {
        attach: '.tooltip',
        adjustDistance : {
            top : 105,
            bottom : 150,
            left : 15,
            right : 50
        }
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
