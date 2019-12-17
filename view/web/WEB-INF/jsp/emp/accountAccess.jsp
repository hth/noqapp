<%@ include file="../include.jsp"%>
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
        <div class="logo-left"><a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue Inc"/></a></div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName" /></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png" /></div>
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
                        <h3>Total support approvals: <span>${accountAccessForm.jsonBusinesses.size()}</span></h3>
                    </div>
                </div>

                <form:form method="post"
                           action="${pageContext.request.contextPath}/emp/landing/account/access/search.htm"
                           modelAttribute="searchForm">
                <spring:hasBindErrors name="searchForm">
                <div class="error-box">
                    <div class="error-txt">
                        <ul>
                            <c:if test="${errors.hasFieldErrors('search')}">
                            <li><form:errors path="search"/></li>
                            </c:if>
                        </ul>
                    </div>
                </div>
                </spring:hasBindErrors>

                <div class="admin-content">
                    <div class="add-new">
                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="search" cssErrorClass="lb_error">Search</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="search" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                placeholder="Bussiness Name"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                        </ul>

                        <div class="col-lable3"></div>
                        <div class="col-fields">
                                <%--<div class="left-btn">--%>
                                <%--<input name="add" class="next-btn" value="ADD" type="submit">--%>
                                <%--</div>--%>
                                <%--<div class="right-btn">--%>
                                <%--<input name="cancel_Add" class="cancel-btn" value="CANCEL" type="submit">--%>
                                <%--</div>--%>
                            <div class="button-btn">
                                <button name="search" class="ladda-button next-btn" style="width:48%; float: left">Search</button>
                                <button name="cancel_Search" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                            </div>
                            <div class="clearFix"></div>
                        </div>
                        <div class="clearFix"></div>

                        <c:if test="${empty accountAccessForm.jsonBusinessesMatchingSearch}">
                        <div class="alert-info">
                            <p>
                                Please send permission request only when a specific business has asked for it.
                            </p>
                        </div>
                        </c:if>
                    </div>
                </div>
                </form:form>

                <c:if test="${!empty accountAccessForm.jsonBusinessesMatchingSearch}">
                <div class="admin-content">
                    <div class="store">
                        <h3>Search Result</h3>
                    </div>
                    <div class="add-new">
                        <div class="store-table">
                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <th>&nbsp;</th>
                                    <th nowrap>
                                        Business Name
                                        &nbsp;
                                        <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                             alt="Sort" height="16px;"/>
                                    </th>
                                    <th nowrap>&nbsp;</th>
                                </tr>
                                <c:forEach items="${accountAccessForm.jsonBusinessesMatchingSearch}" var="jsonBusiness" varStatus="status">
                                <tr>
                                    <td>${status.count}&nbsp;</td>
                                    <td>${jsonBusiness.bizName}</td>
                                    <td nowrap>
                                        <form:form action="${pageContext.request.contextPath}/emp/landing/account/access/actionExternalAccess.htm"
                                                   modelAttribute="accountAccessForm"
                                                   method="post">
                                            <form:hidden path="action" value="SEND" />
                                            <form:hidden path="id" value="${jsonBusiness.bizId}" />
                                            <input class="cancel-btn" style="margin: 0;" value="Send" type="submit">
                                        </form:form>
                                    </td>
                                </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </div>
                </div>
                </c:if>

                <div class="store-table">
                    <c:choose>
                    <c:when test="${!empty accountAccessForm.jsonBusinesses}">
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <th width="4%">&nbsp;</th>
                            <th width="40%">Business Name</th>
                            <th width="36%">Permission</th>
                            <th width="20%">&nbsp;</th>
                        </tr>
                        <c:forEach items="${accountAccessForm.jsonBusinesses}" var="jsonBusiness" varStatus="status">
                        <tr>
                            <td>${status.count}&nbsp;</td>
                            <td>
                                <c:choose>
                                <c:when test="${!empty jsonBusiness.approverQID}">
                                <a href="/emp/landing/account/access/${jsonBusiness.externalAccessId}.htm">${jsonBusiness.bizName}</a>
                                <span style="display:block; font-size:13px;">Approved Access</span>
                                </c:when>
                                <c:otherwise>
                                ${jsonBusiness.bizName}
                                <span style="display:block; font-size:13px;">Pending Approval</span>
                                </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${jsonBusiness.externalPermission.customerFriendlyDescription}</td>
                            <td class="Tleft" nowrap>
                                <div>
                                    <form:form action="${pageContext.request.contextPath}/emp/landing/account/access/actionExternalAccess.htm"
                                               modelAttribute="accountAccessForm"
                                               method="post">
                                        <form:hidden path="action" value="REMOVE" />
                                        <form:hidden path="id" value="${jsonBusiness.externalAccessId}" />
                                        <input class="cancel-btn" style="margin: 0;" value="Remove" type="submit">
                                    </form:form>
                                </div>
                            </td>
                        </tr>
                        </c:forEach>
                    </table>
                    </c:when>
                    <c:otherwise>
                    <div class="alert-info">
                        <div class="no-approve">
                            You do not have any external permission.
                        </div>
                    </div>
                    </c:otherwise>
                    </c:choose>
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
                    <div class="f-left">&copy; 2020 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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
