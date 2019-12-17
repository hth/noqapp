<%@ page import="com.noqapp.domain.types.UserLevelEnum, com.noqapp.domain.types.BusinessTypeEnum" %>
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
                        <h3>Queue Name: <span>${queueSupervisorForm.queueName}</span></h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <a href="/business/${queueSupervisorForm.bizStoreId}/addNewAgent.htm" class="add-btn">Add New Agent</a>
                                <a href="/business/${queueSupervisorForm.bizStoreId}/addQueueSupervisor.htm" class="add-btn">Add new queue supervisor</a>
                            </div>
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
                                            <th nowrap>
                                                Name
                                                &nbsp;
                                                <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                                     alt="Sort" height="16px;"/>
                                            </th>
                                            <th>Address</th>
                                            <th>Email</th>
                                            <th>Role</th>
                                            <th>Since</th>
                                            <th>&nbsp;</th>
                                        </tr>
                                        <c:set var="userLevelEnumValues" value="<%=UserLevelEnum.allowedBusinessUserLevel()%>"/>
                                        <c:forEach items="${queueSupervisorForm.queueSupervisors}" var="queueSupervisor" varStatus="status">
                                        <tr>
                                            <td style="font-size:13px;">${status.count}&nbsp;</td>
                                            <td nowrap>
                                                <span style="display:block; font-size:13px;">${queueSupervisor.name}</span>
                                                <c:if test="${queueSupervisor.userLevel eq UserLevelEnum.S_MANAGER && queueSupervisor.businessType eq BusinessTypeEnum.DO}">
                                                    <c:choose>
                                                        <c:when test="${!empty queueSupervisor.educations}">
                                                            <span style="display:block; font-size:11px;">Educations:
                                                            <c:forEach items="${queueSupervisor.educations}" var="item" varStatus="status">
                                                                ${item.name},&nbsp;
                                                            </c:forEach>
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="display:block; font-size:11px;">Educations: N/A</span>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:choose>
                                                        <c:when test="${!empty queueSupervisor.licenses}">
                                                            <span style="display:block; font-size:11px;">Licenses:
                                                            <c:forEach items="${ queueSupervisor.licenses}" var="item" varStatus="status">
                                                                ${item.name},&nbsp;
                                                            </c:forEach>
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="display:block; font-size:11px;">Licenses: N/A</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                            </td>
                                            <td>
                                                <span style="display:block; font-size:13px;">${queueSupervisor.address}</span>
                                                <c:choose>
                                                    <c:when test="${queueSupervisor.phoneValidated}">
                                                        <span style="display:block; font-size:13px;"><p>Phone: ${queueSupervisor.phone}</p></span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="display:block; font-size:13px;"><p>Phone: N/A</p></span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                <c:when test="${fn:endsWith(queueSupervisor.email, '@mail.noqapp.com')}">
                                                --
                                                </c:when>
                                                <c:otherwise>
                                                <span style="display:block; font-size:13px;">${queueSupervisor.email}</span>
                                                </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td nowrap>
                                                <span style="display:block; font-size:13px;">${queueSupervisor.userLevel.description}</span>
                                            </td>
                                            <td nowrap>
                                                <span style="display:block; font-size:13px;"><fmt:formatDate value="${queueSupervisor.created}" pattern="yyyy-MM-dd"/></span>
                                            </td>
                                            <td class="Tleft" nowrap>
                                                <c:choose>
                                                <c:when test="${queueSupervisor.businessUserRegistrationStatus eq 'C'}">
                                                <div>
                                                    <form:form action="${pageContext.request.contextPath}/business/actionQueueSupervisor.htm" modelAttribute="queueSupervisorActionForm" method="post">
                                                        <form:hidden path="action" value="APPROVE" />
                                                        <form:hidden path="businessUserId" value="${queueSupervisor.businessUserId}" />
                                                        <form:hidden path="bizStoreId" value="${queueSupervisorForm.bizStoreId}" />
                                                        <input class="cancel-btn" value="Approve" type="submit">
                                                    </form:form>
                                                    <br />
                                                    <form:form action="${pageContext.request.contextPath}/business/actionQueueSupervisor.htm" modelAttribute="queueSupervisorActionForm" method="post">
                                                        <form:hidden path="action" value="REJECT" />
                                                        <form:hidden path="businessUserId" value="${queueSupervisor.businessUserId}" />
                                                        <form:hidden path="bizStoreId" value="${queueSupervisorForm.bizStoreId}" />
                                                        <input class="cancel-btn" value="Reject" type="submit">
                                                    </form:form>
                                                </div>
                                                </c:when>
                                                <c:when test="${queueSupervisor.businessUserRegistrationStatus eq 'I'}">
                                                    In progress
                                                </c:when>
                                                <c:when test="${queueSupervisor.businessUserRegistrationStatus eq 'V'}">
                                                    <form:form action="${pageContext.request.contextPath}/business/actionQueueSupervisor.htm" modelAttribute="queueSupervisorActionForm" method="post">
                                                        <form:hidden path="action" value="REMOVE" />
                                                        <form:hidden path="businessUserId" value="${queueSupervisor.businessUserId}" />
                                                        <form:hidden path="bizStoreId" value="${queueSupervisorForm.bizStoreId}" />
                                                        <input class="cancel-btn" value="Remove" type="submit">
                                                    </form:form>
                                                </c:when>
                                                <c:otherwise>
                                                    Pending
                                                </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        </c:forEach>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert-info">
                                        <p>There is no supervisor assigned to manage this queue.</p>
                                    </div>
                                </c:otherwise>
                                </c:choose>
                            </div>

                            <h2>&nbsp;</h2>
                            <h3>Available Supervisors: <span>${queueSupervisorForm.availableQueueSupervisor.size()}</span></h3>
                            <div class="store-table">
                                <c:choose>
                                <c:when test="${!empty queueSupervisorForm.availableQueueSupervisor}">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th>
                                            Name
                                            &nbsp;
                                            <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                                 alt="Sort" height="16px;"/>
                                        </th>
                                        <th>Address</th>
                                        <th>Email</th>
                                        <th>Role</th>
                                        <th>Since</th>
                                        <th>&nbsp;</th>
                                    </tr>
                                    <c:set var="userLevelEnumValues" value="<%=UserLevelEnum.allowedBusinessUserLevel()%>"/>
                                    <c:forEach items="${queueSupervisorForm.availableQueueSupervisor}" var="queueSupervisor" varStatus="status">
                                    <tr>
                                        <td style="font-size:13px;">${status.count}&nbsp;</td>
                                        <td nowrap>
                                            <span style="display:block; font-size:13px;">${queueSupervisor.name}</span>
                                            <c:if test="${queueSupervisor.userLevel eq UserLevelEnum.S_MANAGER && queueSupervisor.businessType eq BusinessTypeEnum.DO}">
                                                <c:choose>
                                                    <c:when test="${!empty queueSupervisor.educations}">
                                                        <span style="display:block; font-size:11px;">Educations:
                                                        <c:forEach items="${queueSupervisor.educations}" var="item" varStatus="status">
                                                            ${item.name},&nbsp;
                                                        </c:forEach>
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="display:block; font-size:11px;">Educations: N/A</span>
                                                    </c:otherwise>
                                                </c:choose>

                                                <c:choose>
                                                    <c:when test="${!empty queueSupervisor.licenses}">
                                                        <span style="display:block; font-size:11px;">Licenses:
                                                        <c:forEach items="${ queueSupervisor.licenses}" var="item" varStatus="status">
                                                            ${item.name},&nbsp;
                                                        </c:forEach>
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="display:block; font-size:11px;">Licenses: N/A</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:if>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${queueSupervisor.address}</span>
                                            <span style="display:block; font-size:13px;"><p>Phone: ${queueSupervisor.phone}</p></span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${fn:endsWith(queueSupervisor.email, '@mail.noqapp.com')}">
                                                    --
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="display:block; font-size:13px;">${queueSupervisor.email}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><span style="display:block; font-size:13px;">${queueSupervisor.userLevel.description}</span></td>
                                        <td nowrap>
                                            <span style="display:block; font-size:13px;"><fmt:formatDate value="${queueSupervisor.created}" pattern="yyyy-MM-dd"/></span>
                                        </td>
                                        <td class="Tleft" nowrap>
                                            <form:form action="${pageContext.request.contextPath}/business/actionQueueSupervisor.htm" modelAttribute="queueSupervisorActionForm" method="post">
                                                <form:hidden path="action" value="ADD" />
                                                <form:hidden path="businessUserId" value="${queueSupervisor.businessUserId}" />
                                                <form:hidden path="bizStoreId" value="${queueSupervisorForm.bizStoreId}" />
                                                <input class="cancel-btn" value="Add" type="submit">
                                            </form:form>
                                        </td>
                                    </tr>
                                    </c:forEach>
                                </table>
                                </c:when>
                                <c:otherwise>
                                <div class="alert-info">
                                    <p>There is no additional supervisor to be assigned to this queue.</p>
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
