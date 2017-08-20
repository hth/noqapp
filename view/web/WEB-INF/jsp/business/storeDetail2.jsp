<%@ include file="../include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="utf-8">
    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
    <meta content='width=device-width, initial-scale=1' name='viewport' />

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'  />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen" />
</head>

<body>

<!-- header -->
<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left"><a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png"/></a></div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName" /></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png" /></div>
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
            <!-- Add New Store -->
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <div class="store-details-row">
                            <div class="qr-store-box">
                                <img src="/business/store/detail/i/${storeLandingForm.qrFileName}.htm" />
                            </div>
                            <div class="details-box">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <td width="100">Address:</td>
                                        <td>${storeLandingForm.address}</td>
                                    </tr>
                                    <tr>
                                        <td>Phone:</td>
                                        <td>${storeLandingForm.phone}</td>
                                    </tr>
                                    <tr>
                                        <td>Queue Name:</td>
                                        <td>${storeLandingForm.displayName}</td>
                                    </tr>
                                    <tr>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                    </tr>
                                </table>

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
            <img src="${pageContext.request.contextPath}/static2/internal/img/footer-img.jpg" class="img100" />
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2017  NoQueue Inc.   |  <a href="#">Privacy</a>    |    <a href="#">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>



</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>

</html>
