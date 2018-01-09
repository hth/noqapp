<%@ page import="com.noqapp.domain.types.UserLevelEnum" %>
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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue Inc"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn">
                    <sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow">
                        <img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
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
                        <h3><span>${queueSupervisorForm.queueName}</span></h3>

                        <div class="add-store">
                            <div class="store-table">
                                <spring:hasBindErrors name="errorMessage">
                                    <div class="error-box">
                                        <div class="error-txt">
                                            <ul>
                                                <li><form:errors path="errorMessage"/></li>
                                            </ul>
                                        </div>
                                    </div>
                                    <div class="space10"></div>
                                </spring:hasBindErrors>

                                <c:choose>
                                    <c:when test="${!empty queueSupervisorForm.queueSupervisors}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th>Name</th>
                                                <th>Address</th>
                                                <th>Email</th>
                                                <th>Role</th>
                                                <th>Since</th>
                                                <th>&nbsp;</th>
                                            </tr>
                                            <c:set var="userLevelEnumValues" value="<%=UserLevelEnum.queueManagers()%>"/>
                                            <c:forEach items="${queueSupervisorForm.queueSupervisors}" var="queueSupervisor" varStatus="status">
                                                <tr>
                                                    <td>${status.count}&nbsp;</td>
                                                    <td nowrap>${queueSupervisor.name}</td>
                                                    <td>${queueSupervisor.address}
                                                        <p>Phone: ${queueSupervisor.phone}</p>
                                                    </td>
                                                    <td>${queueSupervisor.email}</td>
                                                    <td nowrap>
                                                    <c:choose>
                                                        <c:when test="${queueSupervisor.businessUserRegistrationStatus eq 'V'}">
                                                            <select path="userLevel" class="form-field-select single-dropdown">
                                                                <c:forEach var="item" items="${userLevelEnumValues}">
                                                                    <%--//TODO Add ajax call to change user role--%>
                                                                    <option value="${item}" ${item == queueSupervisor.userLevel ? 'selected="selected"' : ''}>${item.description}</option>
                                                                </c:forEach>
                                                            </select>
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${queueSupervisor.userLevel.description}
                                                        </c:otherwise>
                                                    </c:choose>
                                                    </td>
                                                    <td nowrap>
                                                        <fmt:formatDate value="${queueSupervisor.created}" pattern="yyyy-MM-dd"/>
                                                    </td>
                                                    <td class="Tleft" nowrap>
                                                    <c:if test="${queueSupervisor.businessUserRegistrationStatus eq 'V'}">
                                                        <form:form action="${pageContext.request.contextPath}/business/actionQueueSupervisor.htm" modelAttribute="queueSupervisorActionForm" method="post">
                                                            <form:hidden path="action" value="DELETE" />
                                                            <form:hidden path="businessUserId" value="${queueSupervisor.businessUserId}" />
                                                            <input class="cancel-btn" value="Delete" type="submit">
                                                        </form:form>
                                                    </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>There is no supervisor added to manage queues. Highly recommended to added queue supervisor. Select a queue to add supervisor.</p>
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
                    <div class="f-left">&copy; 2018 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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
