<%@ page import="com.noqapp.domain.types.ActionTypeEnum,com.noqapp.domain.types.DiscountTypeEnum" %>
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
                        <a href="${pageContext.request.contextPath}/business/discount/landing.htm">Discount</a>
                        <a href="${pageContext.request.contextPath}/business/coupon/landing.htm">Client Coupon</a>
                        <a href="${pageContext.request.contextPath}/business/coupon/businessLanding.htm">Business Coupon</a>
                        <a href="${pageContext.request.contextPath}/access/userProfile.htm">Profile</a>
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
                    <div class="store">
                        <h3>Discounts</h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <a href="/business/discount/add.htm" class="add-btn">Add New Discount</a>
                            </div>
                            <div class="store-table">
                                <c:choose>
                                <c:when test="${!empty discountForm.discounts}">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th nowrap>Name & Description</th>
                                        <th>Discount</th>
                                        <th>Discount Type</th>
                                        <th>Coupon Type</th>
                                        <th>Usage</th>
                                        <th></th>
                                    </tr>
                                    <c:forEach items="${discountForm.discounts}" var="discount" varStatus="status">
                                    <tr>
                                        <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                        <td nowrap>
                                            <span style="display:block; font-size:13px;">${discount.discountName}</span>
                                            <br/>
                                            <span style="display:block; font-size:13px;">${discount.discountDescription}</span>
                                        </td>
                                        <td nowrap align="left">
                                            <c:choose>
                                                <c:when test="${discount.discountType eq DiscountTypeEnum.F}">
                                                    <span style="display:block; font-size:13px;">Rs ${discount.discountAmountAsString}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="display:block; font-size:13px;">${discount.discountAmount}<span style="font-size: large; font-weight: bold">%</span></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><span style="display:block; font-size:13px;">${discount.discountType.description}</span></td>
                                        <td><span style="display:block; font-size:13px;">${discount.couponType.description}</span></td>
                                        <td><span style="display:block; font-size:13px;">${discount.usageCount}</span></td>
                                        <td>
                                            <c:choose>
                                            <c:when test="${discount.active}">
                                                <form:form action="${pageContext.request.contextPath}/business/discount/action.htm" modelAttribute="discountForm" method="post">
                                                    <form:hidden path="actionType" value="${ActionTypeEnum.INACTIVE}" />
                                                    <form:hidden path="discountId" value="${discount.id}" />
                                                    <input class="cancel-btn" style="margin: 0;" value="In-Active" type="submit">
                                                </form:form>
                                            </c:when>
                                            <c:when test="${discount.canDeletedAfterDays > 0}">
                                                <span style="display:block; font-size:13px;">Available to delete in ${discount.canDeletedAfterDays} days</span>
                                            </c:when>
                                            <c:otherwise>
                                                <form:form action="${pageContext.request.contextPath}/business/discount/action.htm" modelAttribute="discountForm" method="post">
                                                    <form:hidden path="actionType" value="${ActionTypeEnum.REMOVE}" />
                                                    <form:hidden path="discountId" value="${discount.id}" />
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
                                    <p>There are no discount listed. Discount helps reward your loyal customers</p>
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

</html>
