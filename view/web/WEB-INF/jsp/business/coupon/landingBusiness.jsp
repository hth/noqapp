<%@ page import="com.noqapp.domain.types.DiscountTypeEnum,com.noqapp.domain.types.ActionTypeEnum" %>
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
                        <sec:authorize access="hasRole('ROLE_M_ADMIN')">
                        <a href="${pageContext.request.contextPath}/business/discount/landing">Discount</a>
                        </sec:authorize>
                        <a href="${pageContext.request.contextPath}/business/coupon/landing">Client Coupon</a>
                        <a href="${pageContext.request.contextPath}/business/coupon/businessLanding">Business Coupon</a>
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
                        <h3>Active Business Coupons</h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <a href="/business/coupon/businessUpcoming" class="add-btn">Upcoming Business Coupon</a>
                                <a href="/business/coupon/newBusinessCoupon" class="add-btn">Add New Business Coupon</a>
                            </div>
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty couponForm.coupons}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th nowrap>Name & Description</th>
                                                <th nowrap>Dates</th>
                                                <th>Issued By</th>
                                                <th>Multi Use</th>
                                                <th>Discount</th>
                                                <th></th>
                                            </tr>
                                            <c:forEach items="${couponForm.coupons}" var="coupon" varStatus="status">
                                                <tr>
                                                    <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                                    <td nowrap>
                                                        <span style="display:block; font-size:13px;"><b>${coupon.discountName}</b></span>
                                                        <br/>
                                                        <span style="display:block; font-size:13px;">${coupon.discountDescription}</span>
                                                    </td>
                                                    <td nowrap>
                                                        <span style="display:block; font-size:13px;"><fmt:formatDate pattern="MMMM dd, yyyy" value="${coupon.couponStartDate}"/></span>
                                                        <span style="display:block; font-size:13px;">-</span>
                                                        <span style="display:block; font-size:13px;"><fmt:formatDate pattern="MMMM dd, yyyy" value="${coupon.couponEndDate}"/></span>
                                                    </td>
                                                    <td nowrap><span style="display:block; font-size:13px;">${coupon.issuedBy}</span></td>
                                                    <td nowrap>
                                                        <c:choose>
                                                            <c:when test="${coupon.multiUse}">
                                                                <span style="display:block; font-size:13px;">Yes</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="display:block; font-size:13px;">No</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td nowrap align="left">
                                                        <c:choose>
                                                            <c:when test="${coupon.discountType eq DiscountTypeEnum.F}">
                                                                <span style="display:block; font-size:13px;">Rs ${coupon.discountAmountAsString}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="display:block; font-size:13px;">${coupon.discountAmount}<span style="font-size: large; font-weight: bold">%</span></span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${coupon.active}">
                                                                <form:form action="${pageContext.request.contextPath}/business/coupon/action" modelAttribute="couponForm" method="post">
                                                                    <form:hidden path="actionType" value="${ActionTypeEnum.INACTIVE}" />
                                                                    <form:hidden path="couponId" value="${coupon.id}" />
                                                                    <input class="cancel-btn" style="margin: 0;" value="In-Active" type="submit">
                                                                </form:form>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form:form action="${pageContext.request.contextPath}/business/coupon/action" modelAttribute="couponForm" method="post">
                                                                    <form:hidden path="actionType" value="${ActionTypeEnum.REMOVE}" />
                                                                    <form:hidden path="couponId" value="${coupon.id}" />
                                                                    <input class="cancel-btn" style="margin: 0;" value="Delete" type="submit">
                                                                </form:form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>There are no coupon listed. Coupons listed here are available for use.</p>
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
