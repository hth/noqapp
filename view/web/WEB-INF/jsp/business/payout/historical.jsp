<%@ page import="com.noqapp.domain.types.ValidateStatusEnum" %>
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
                        <h3>Earnings: <span>${historicalTransactionForm.historicalTransaction.size()}</span></h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <a href="/business/payout/couponUsed.htm" class="add-btn">Coupon Used</a>
                            </div>
                            <div class="store-table">
                            <c:choose>
                                <c:when test="${fn:length(historicalTransactionForm.historicalTransaction) gt 0}">
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <th>&nbsp;</th>
                                            <th nowrap>Date</th>
                                            <th nowrap>Delivery Mode</th>
                                            <th nowrap>Payment Status</th>
                                            <th nowrap>Through NoQueue</th>
                                            <th>At Counter</th>
                                            <th>Unknown</th>
                                        </tr>
                                        <c:forEach items="${historicalTransactionForm.historicalTransaction}" var="item" varStatus="status">
                                        <tr>
                                            <td>
                                                <span style="display:block; font-size:13px;">${status.count}</span>
                                            </td>
                                            <td>
                                                <span style="display:block; font-size:13px;">${item.key}</span>
                                            </td>
                                            <td nowrap>
                                                <span style="display:block; font-size:13px;">${item.value.deliveryMode.description}</span>
                                            </td>
                                            <td nowrap>
                                                <span style="display:block; font-size:13px;">${item.value.paymentStatus.description}</span>
                                            </td>
                                            <td nowrap>
                                                <span style="display:block; font-size:13px;">${item.value.internalTransaction}</span>
                                            </td>
                                            <td nowrap>
                                                <span style="display:block; font-size:13px;">${item.value.externalTransaction}</span>
                                            </td>
                                            <td nowrap>
                                                <span style="display:block; font-size:13px;">${item.value.unknownTransaction}</span>
                                            </td>
                                        </tr>
                                        </c:forEach>
                                    </table>
                                    <br/>
                                    <h4 style="display:block; font-size:13px; float: right;">Records for last ${historicalTransactionForm.durationInDays} days</h4>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert-info">
                                        <p>No transactions in last ${historicalTransactionForm.durationInDays} day</p>
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
                    <div class="f-left">&copy; 2019 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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
