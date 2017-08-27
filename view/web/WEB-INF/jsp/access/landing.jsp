<%@ include file="../include.jsp" %>
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
            <a href="${pageContext.request.contextPath}"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png"/></a>
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
            <div class="admin-main">
                <div class="admin-content">
                    <div class="register-c">
                        <h3>Welcome <sec:authentication property="principal.userShortName"/></h3>

                        <sec:authorize access="hasRole('ROLE_CLIENT')">
                            <c:choose>
                                <c:when test="${!empty landingForm.businessUserRegistrationStatus}">
                                    <c:if test="${landingForm.businessUserRegistrationStatus eq 'C'}">
                                        Awaiting approval for business account. <br/>
                                        Since: ${landingForm.businessAccountSignedUp}
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <a href="/access/landing/business/migrate.htm">Do you own a business which you would like to register?</a>
                                </c:otherwise>
                            </c:choose>
                        </sec:authorize>
                    </div>

                    <p>&nbsp;</p>

                    <div class="store">
                        <h3>Current Queue</h3>

                        <div class="add-store">
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty landingForm.currentQueues}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th>Queue Name</th>
                                                <th>Status</th>
                                                <th>Token Number</th>
                                            </tr>
                                            <c:forEach items="${landingForm.currentQueues}" var="store" varStatus="status">
                                                <tr>
                                                    <td>${status.count}&nbsp;</td>
                                                    <td>
                                                        <a href="/${store.codeQR}/q.htm" target="_blank">${store.displayName}</a>
                                                    </td>
                                                    <td>
                                                        ${store.queueUserState.description}
                                                    </td>
                                                    <td>
                                                        ${store.tokenNumber}
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        No queue joined for today. 
                                    </c:otherwise>
                                </c:choose>
                            </div>

                        </div>
                        <h3>Historical Queue</h3>

                        <div class="add-store">
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty landingForm.historicalQueues}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th>Queue Name</th>
                                                <th>Date Serviced</th>
                                                <th>Hour Saved</th>
                                                <th>Rating</th>
                                            </tr>
                                            <c:forEach items="${landingForm.historicalQueues}" var="store" varStatus="status">
                                                <tr>
                                                    <td>${status.count}&nbsp;</td>
                                                    <td>
                                                        <a href="/${store.codeQR}/q.htm" target="_blank">${store.displayName}</a>
                                                    </td>
                                                    <td>
                                                        <fmt:formatDate pattern="MMMM dd, yyyy hh:mm a" value="${store.serviceEndTime}"/>
                                                    </td>
                                                    <td>
                                                        ${store.hoursSaved}
                                                    </td>
                                                    <td>
                                                        ${store.ratingCount}
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        You don't have any recent history.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Add New Supervisor -->

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
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>

</html>
