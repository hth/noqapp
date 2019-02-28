<%@ page import="com.noqapp.domain.types.BusinessTypeEnum,com.noqapp.domain.types.QueueUserStateEnum" %>
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
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <h3>${inQueueForm.businessType.description} Queue: <span>${inQueueForm.queueName}</span></h3>

                        <div class="add-store">
                            <div class="details-box" style="padding: 10px 0 10px 0;">
                                Total: <span>${inQueueForm.purchaseOrders.size()}</span>
                            </div>
                            <div class="store-table">
                                <c:choose>
                                <c:when test="${!empty inQueueForm.purchaseOrders}">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th nowrap>Name</th>
                                        <th nowrap>Token</th>
                                        <th nowrap>Cost</th>
                                        <th>Payment Type</th>
                                        <th>Order State</th>
                                        <th>Reports</th>
                                    </tr>
                                    <c:forEach items="${inQueueForm.purchaseOrders}" var="purchaseOrder" varStatus="status">
                                    <tr>
                                        <td>${status.count}&nbsp;</td>
                                        <td nowrap>${purchaseOrder.customerName}</td>
                                        <td nowrap>${purchaseOrder.tokenNumber}</td>
                                        <td nowrap align="right">${purchaseOrder.orderPrice}</td>
                                        <td nowrap align="center">
                                            ${purchaseOrder.paymentMode.description}
                                        </td>
                                        <td nowrap align="left">
                                            ${purchaseOrder.presentOrderState.description}
                                            <span style="display:block; font-size:13px;">Order Date: <fmt:formatDate pattern="MMMM dd, yyyy hh:mm a" value="${purchaseOrder.created}"/></span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${purchaseOrder.businessType eq BusinessTypeEnum.HS}">
                                                <span style="display:block; font-size:13px;"><a
                                                        href="${pageContext.request.contextPath}/business/store/sup/order/medicalReport/${purchaseOrder.bizStoreId}/${purchaseOrder.transactionId}.htm"
                                                        target="_blank">Add Medical Report</a></span>
                                                </c:when>
                                                <c:otherwise>
                                                    N/A
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                    </c:forEach>
                                </table>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert-info">
                                        <p>Could not find any orders.</p>
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
