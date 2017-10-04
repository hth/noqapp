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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png"/></a>
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
                        <h3>Queue Name: <span>${queueSupervisorForm.queueName}</span></h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <a href="/business/${queueSupervisorForm.bizStoreId}/addQueueSupervisor.htm" class="add-btn">Add new queue supervisor</a>
                            </div>
                            <div class="store-table">
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
                                            <c:set var="userLevelEnumValues" value="<%=UserLevelEnum.merchantLevels()%>"/>
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
                                                                <select path="userLevel" cssClass="form-field-select">
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
                                                        <fmt:formatDate value="${queueSupervisor.created}" pattern="yyyy-MM-dd"/></td>
                                                    <td class="Tleft" nowrap>
                                                        <c:choose>
                                                            <c:when test="${queueSupervisor.userLevel eq 'M_ADMIN'}">
                                                                <!-- Admin cannot delete self -->
                                                                --
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:choose>
                                                                    <c:when test="${queueSupervisor.businessUserRegistrationStatus eq 'C'}">
                                                                    <div>
                                                                        <form:form action="${pageContext.request.contextPath}/business/approveRejectQueueSupervisor.htm" modelAttribute="queueSupervisorApproveRejectForm" method="post">
                                                                            <form:hidden path="approveOrReject" value="approve" />
                                                                            <form:hidden path="referenceId" value="${queueSupervisor.businessUserId}" />
                                                                            <form:hidden path="storeId" value="${queueSupervisorForm.bizStoreId}" />
                                                                            <input class="cancel-btn" value="Approve" type="submit">
                                                                        </form:form>
                                                                        <br />
                                                                        <form:form action="${pageContext.request.contextPath}/business/approveRejectQueueSupervisor.htm" modelAttribute="queueSupervisorApproveRejectForm" method="post">
                                                                            <form:hidden path="approveOrReject" value="reject" />
                                                                            <form:hidden path="referenceId" value="${queueSupervisor.businessUserId}" />
                                                                            <form:hidden path="storeId" value="${queueSupervisorForm.bizStoreId}" />
                                                                            <input class="cancel-btn" value="Reject" type="submit">
                                                                        </form:form>
                                                                    </div>
                                                                    </c:when>
                                                                    <c:when test="${queueSupervisor.businessUserRegistrationStatus eq 'I'}">
                                                                        In progress
                                                                    </c:when>
                                                                    <c:when test="${queueSupervisor.businessUserRegistrationStatus eq 'V'}">
                                                                        <c:choose>
                                                                            <c:when test="${queueSupervisor.active}">
                                                                                Remove
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                Delete
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        Pending
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>There is no one assigned to this queue.</p>
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
                    <div class="f-left">&copy; 2017 NoQueue Inc. | <a href="#">Privacy</a> | <a href="#">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>

</html>
