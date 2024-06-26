<%@ page import="com.noqapp.domain.types.BusinessTypeEnum, com.noqapp.domain.types.MessageOriginEnum" %>
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
                            <h2>${registerBusiness.businessType.classifierTitle} Setup</h2>
                            <h3>${registerBusiness.displayName}</h3>
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
                                    <c:if test="${MessageOriginEnum.Q == registerBusiness.businessType.messageOrigin}">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="walkInState" cssErrorClass="lb_error">Walk-in</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:select path="walkInState" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false" disabled="${!registerBusiness.claimed}">
                                                <form:option value="" label="--- Select ---"/>
                                                <form:options items="${registerBusiness.walkinStates}" />
                                            </form:select>
                                            <span style="display:block; font-size:14px;">(Allow user to take walk-in appointments)</span>
                                            <c:if test="${!registerBusiness.claimed}"><span style="font-size:14px; color: #9f1313">Since business is not claimed. Walk-in is disabled.</span></c:if>
                                        </div>
                                        <span class="tooltip" title="Allows customers to take walk-in appointments, join queue." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                        <div class="clearFix"></div>
                                    </li>
                                    </c:if>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="remoteJoin" cssErrorClass="lb_error">Allow Remote Join</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:checkbox path="remoteJoin" cssClass="form-check-box" cssErrorClass="form-check-box error-field"/>
                                            <span style="display:block; font-size:14px;">(Allow user to join queue from Home, place online orders)</span>
                                        </div>
                                        <span class="tooltip" title="Remote join allows customers to join queue from home." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="allowLoggedInUser" cssErrorClass="lb_error">Allow Registered User</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:checkbox path="allowLoggedInUser" cssClass="form-check-box" cssErrorClass="form-check-box error-field" disabled="true"/>
                                            <span style="display:block; font-size:14px;">NoQueue Registered Users</span>
                                        </div>
                                        <span class="tooltip" title="Limits to registered users." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                        <div class="clearFix"></div>
                                    </li>
                                    <c:if test="${MessageOriginEnum.Q == registerBusiness.businessType.messageOrigin}">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="availableTokenCount" cssErrorClass="lb_error">Issue Limited Tokens</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="availableTokenCount" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                            <span style="display:block; font-size:14px;">(Customers in the queue will be limited to allowed number. 0 is Unlimited Token. Greater than 0 is limited token)</span>
                                        </div>
                                        <span class="tooltip" title="Limits the number of tokens that can be issued everyday. Default set to limitless." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                        <div class="clearFix"></div>
                                    </li>
                                    </c:if>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="averageServiceTimeInMinutes" cssErrorClass="lb_error">Average Handling Time</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="averageServiceTimeInMinutes" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                            <span style="display:block; font-size:14px;">(AHT: Expected average service time per customer in minutes)</span>
                                        </div>
                                        <span class="tooltip" title="Expected service time per customer at store" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="appendPrefixToToken" cssErrorClass="lb_error">Append Token Prefix</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:select path="appendPrefixToToken" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
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
                                            <form:input path="deliveryRange" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                            <span style="display:block; font-size:14px;">(Delivery Range in km)</span>
                                        </div>
                                        <span class="tooltip" title="Accepts order from locations within range" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                        <div class="clearFix"></div>
                                    </li>
                                    </c:if>
                                </ul>

                                <div class="col-lable3"></div>
                                <div class="col-fields">
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/services.js"></script>
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
