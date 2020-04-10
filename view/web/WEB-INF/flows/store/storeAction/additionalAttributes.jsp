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
            <sec:authorize access="hasAnyRole('ROLE_M_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')">
                <div class="admin-main">
                    <form:form modelAttribute="registerBusiness">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                        <div class="admin-title">
                            <c:choose>
                            <c:when test="${!empty registerBusiness.bizStoreId}">
                                <h2>Edit ${registerBusiness.businessType.classifierTitle} Amenities & Facilities</h2>
                            </c:when>
                            <c:otherwise>
                                <h2>${registerBusiness.businessType.classifierTitle} Amenities & Facilities</h2>
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
                                <ul class="col3-grid">
                                    <h4><strong>Amenities</strong></h4>
                                    <c:choose>
                                    <c:when test="${!empty registerBusiness.amenitiesAvailable}">
                                        <form:checkboxes element="li" path="amenitiesStore" items="${registerBusiness.amenitiesAvailable}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>
                                                No individual amenity selection allowed
                                            </p>
                                        </div>
                                    </c:otherwise>
                                    </c:choose>
                                    <div class="clearFix"></div>
                                </ul>
                                <ul class="col3-grid">
                                    <h4><strong>Facilities</strong></h4>
                                    <c:choose>
                                    <c:when test="${!empty registerBusiness.facilitiesAvailable}">
                                        <form:checkboxes element="li" path="facilitiesStore" items="${registerBusiness.facilitiesAvailable}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>
                                                No individual facility selection allowed
                                            </p>
                                        </div>
                                    </c:otherwise>
                                    </c:choose>
                                    <div class="clearFix"></div>
                                </ul>

                                <div class="alert-info">
                                    <p>
                                        Select all amenities and facilities provided at your location. Please let us know if we have missed any at contact@noqapp.com
                                    </p>
                                </div>

                                <div class="btn-hours">
                                    <c:choose>
                                    <c:when test="${!empty registerBusiness.bizStoreId}">
                                    <div class="button-btn">
                                        <button name="_eventId_continue" class="ladda-button next-btn" style="width:48%; float: left">Edit</button>
                                        <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                    </div>
                                    </c:when>
                                    <c:otherwise>
                                    <div class="button-btn">
                                        <button name="_eventId_continue" class="ladda-button next-btn" style="width:48%; float: left">Next</button>
                                        <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                    </div>
                                    </c:otherwise>
                                    </c:choose>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/services.js"></script>
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
