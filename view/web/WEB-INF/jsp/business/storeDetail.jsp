<%@ include file="../include.jsp" %>
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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a>
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
            <!-- Add New Store -->
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <div class="store-details-row">
                            <div class="qr-store-box">
                                <img src="/i/${storeLandingForm.qrFileName}.htm"/>
                            </div>
                            <div class="details-box">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <td><h3><span>${storeLandingForm.businessName}</span></h3></td>
                                    </tr>
                                    <tr>
                                        <td style="font-weight: bold; color: #222;">${storeLandingForm.displayName}</td>
                                    </tr>
                                    <c:if test="${!empty storeLandingForm.categoryName}">
                                    <tr>
                                        <td><strong>${storeLandingForm.categoryName}</strong></td>
                                    </tr>
                                    </c:if>
                                    <tr>
                                        <td>&nbsp;</td>
                                    </tr>
                                </table>

                                <div class="store-hours">
                                    <p><strong>Location</strong></p>
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td>${storeLandingForm.address}</td>
                                        </tr>
                                        <tr>
                                            <td>${storeLandingForm.phone}</td>
                                        </tr>
                                        <tr>
                                            <td>&nbsp;</td>
                                        </tr>
                                    </table>
                                </div>

                                <div class="store-hours">
                                    <p><strong>Open & Closed Hours</strong></p>
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <c:forEach items="${storeLandingForm.storeHours}" var="storeHour">
                                    <c:choose>
                                    <c:when test="${storeHour.dayClosed}">
                                        <tr>
                                            <td width="100">${storeHour.dayOfTheWeekAsString}:</td>
                                            <td>Closed</td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td width="100">${storeHour.dayOfTheWeekAsString}:</td>
                                            <td>${storeHour.storeStartHourAsString} - ${storeHour.storeEndHourAsString}</td>
                                        </tr>
                                    </c:otherwise>
                                    </c:choose>
                                    </c:forEach>
                                    </table>
                                </div>
                            </div>
                            <div class="clearFix"></div>
                        </div>

                    </div>
                </div>
            </div>
            <!-- Add New Store -->

        </div>
    </div>
    <!-- content end -->


    <!-- Foote -->
    <div class="footer">
        <div class="warp-inner ">
            <img src="${pageContext.request.contextPath}/static2/internal/img/footer-img.jpg" class="img100"/>
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2020 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
