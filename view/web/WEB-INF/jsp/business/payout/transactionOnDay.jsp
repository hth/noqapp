<%@ page import="com.noqapp.domain.types.PaymentStatusEnum, com.noqapp.domain.types.TransactionViaEnum" %>
<%@ include file="../../include.jsp" %>
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
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static/internal/img/menu-top-arrow.png"/></div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/">Home</a>
                        <a href="${pageContext.request.contextPath}/access/userProfile">Profile</a>
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
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <c:set var="currentPage" value="${requestScope['javax.servlet.forward.request_uri']}"/>
                        <c:set var="splitURI" value="${fn:split(currentPage, '/')}"/>
                        <c:set var="lastValue" value="${splitURI[fn:length(splitURI)-1]}"/>
                        <h3>Transaction on day: <c:out value="${lastValue}"></c:out></h3>

                        <div class="add-store">
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${fn:length(payoutLandingForm.purchaseOrders) gt 0}">
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <th>&nbsp;</th>
                                            <th nowrap>Name</th>
                                            <th nowrap>Store</th>
                                            <th>Grand Total</th>
                                            <th>Transaction Detail</th>
                                            <th>Transaction Message</th>
                                        </tr>
                                        <c:forEach items="${payoutLandingForm.purchaseOrders}" var="item" varStatus="status">
                                            <tr>
                                                <td>
                                                    <span style="display:block; font-size:13px;">${status.count}</span>
                                                </td>
                                                <td nowrap>
                                                    <span style="display:block; font-size:13px;">${item.customerName}</span>
                                                    <span style="display:block; font-size:13px;">${item.customerPhone}</span>
                                                    <span style="display:block; font-size:13px;">Token/Order: <b style="color: #1b1b1b;">${item.tokenNumber}</b></span>
                                                </td>
                                                <td nowrap>
                                                    <span style="display:block; font-size:13px;">${item.displayName}</span>
                                                </td>
                                                <td>
                                                    <span style="display:block; font-size:13px;"><b style="color: #1b1b1b;">${item.grandTotalForDisplay}</b></span>
                                                    <c:if test="${fn:length(item.partialPayment) gt 0}">
                                                    <span style="display:block; font-size:12px; color:red;">Partial: ${item.partialPaymentForDisplay}</span>
                                                    </c:if>
                                                    <c:if test="${fn:length(item.couponId) gt 0}">
                                                        <span style="display:block; font-size:12px;"><a href="/business/payout/coupon/${item.couponId}" target="_blank">Used Coupon</a></span>
                                                    </c:if>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                    <c:when test="${item.paymentStatus eq PaymentStatusEnum.PA}">
                                                        <span style="display:block; font-size:13px;"><b style="color: darkgreen;">${item.paymentStatus.description}</b> via <b style="color: #1b1b1b;">${item.paymentMode.description}</b></span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="display:block; font-size:13px;"><b style="color: darkred;">${item.paymentStatus.description}</b></span>
                                                    </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <span style="display:block; font-size:13px;">&nbsp;${item.transactionMessage}</span>
                                                    <span style="display:block; font-size:13px;">
                                                    <c:choose>
                                                        <c:when test="${item.transactionVia eq TransactionViaEnum.I}">
                                                            <span style="display:block; font-size:13px;"><b style="color: darkgreen;">&nbsp;Payment Through NoQueue</b></span>
                                                        </c:when>
                                                        <c:when test="${item.transactionVia eq TransactionViaEnum.E}">
                                                            <span style="display:block; font-size:13px;"><b style="color: darkgreen;">&nbsp;At Counter</b></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="display:block; font-size:13px;"><b style="color: darkred;">&nbsp;N/A</b></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    </span>
                                                    <span style="display:block; font-size:13px;">&nbsp;Date Time: <fmt:formatDate pattern="MMMM dd, yyyy hh:mm a" value="${item.created}"/></span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                    </c:when>
                                    <c:otherwise>
                                    <div class="alert-info">
                                        <p>No transaction for the day</p>
                                    </div>
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
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
