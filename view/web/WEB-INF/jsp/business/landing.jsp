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
                        <h3><span>${businessLandingForm.bizName}</span></h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <a href="/business/category.htm" class="add-btn">Show Business Category</a>
                                <a href="/business/addStore.htm" class="add-btn">Add New Store</a>
                            </div>
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty businessLandingForm.bizStores}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th>Store Location</th>
                                                <th nowrap>Queue Name</th>
                                                <th>Pending</th>
                                                <th>Assigned</th>
                                                <th nowrap>Create Date</th>
                                            </tr>
                                            <c:forEach items="${businessLandingForm.bizStores}" var="store" varStatus="status">
                                                <tr>
                                                    <td>${status.count}&nbsp;</td>
                                                    <td>
                                                        <a href="/business/store/detail/${store.id}.htm">${store.address}</a>
                                                    </td>
                                                    <td nowrap>
                                                        <a href="/${store.codeQR}/q.htm" target="_blank">${store.displayName}</a>
                                                    </td>
                                                    <td>
                                                        <a href="/business/${store.id}/listQueueSupervisor.htm">${businessLandingForm.queueDetails.get(store.id).pendingApprovalToQueue}</a>
                                                    </td>
                                                    <td>
                                                        <a href="/business/${store.id}/listQueueSupervisor.htm">${businessLandingForm.queueDetails.get(store.id).assignedToQueue}</a>
                                                    </td>
                                                    <td nowrap>
                                                        <fmt:formatDate pattern="MMM dd, yyyy" value="${store.created}"/>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        There are no stores associated with business.
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


    <!-- Foote -->
    <div class="footer">
        <div class="warp-inner ">
            <img src="${pageContext.request.contextPath}/static2/internal/img/footer-img.jpg" class="img100"/>
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2017 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>


</body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>

</html>
