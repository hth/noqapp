<%@ page import="com.noqapp.domain.types.ActionTypeEnum" %>
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
                        <a href="${pageContext.request.contextPath}/emp/landing/account/access.htm">Permissions</a>
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
                    <div class="add-new">
                        <form:form method="POST" action="./landing.htm" modelAttribute="searchUserForm">
                            <ul class="list-form">
                                <li>
                                    <div class="alert-info">
                                        <p>
                                            Find user to modify account info
                                        </p>
                                    </div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="qid" cssErrorClass="lb_error">QID</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="qid" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="false" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li class="mB0">
                                    <div class="col-lable3"></div>
                                    <div class="col-fields">
                                        <div class="left-btn">
                                            <input type="submit" value="SEARCH" class="next-btn" name="search-user">
                                        </div>
                                        <div class="right-btn">
                                            <input type="submit" value="CANCEL" class="cancel-btn" name="cancel-search-user">
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>
                        </form:form>
                    </div>
                </div>
                <div class="admin-content">
                    <div class="add-store">
                        <div class="store-table">
                        <c:choose>
                        <c:when test="${!searchUserForm.noUserFound and !empty fn:trim(searchUserForm.qid)}">
                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <th width="4%">&nbsp;</th>
                                    <th width="30%">Name</th>
                                    <th width="25%">Status</th>
                                    <th width="25%">Account Inactive Reason</th>
                                    <th width="16%"></th>
                                </tr>
                                <tr>
                                    <form:form method="POST" action="./action.htm" modelAttribute="searchUserForm">
                                        <form:hidden path="qid" />
                                        <td style="font-size:13px;">${status.count}&nbsp;</td>
                                        <td style="font-size:13px;">
                                            ${searchUserForm.displayName}
                                            <c:choose>
                                                <c:when test="${searchUserForm.dependent}">
                                                    <span style="display:block; font-size:13px;">Guardian Phone: ${searchUserForm.guardianPhone}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="display:block; font-size:13px;">Phone: ${searchUserForm.phone}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="font-size:13px;">
                                            ${searchUserForm.status}
                                            <c:if test="${searchUserForm.status eq ActionTypeEnum.INACTIVE}">
                                                <span style="display:block; font-size:13px;">Reason: ${searchUserForm.accountInactiveReason.description}</span>
                                            </c:if>
                                        </td>
                                        <td style="font-size:13px;">
                                            <c:choose>
                                                <c:when test="${searchUserForm.status eq ActionTypeEnum.ACTIVE}">
                                                    <form:select path="accountInactiveReason" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                        <form:option value="" label="--- Select ---"/>
                                                        <form:options items="${searchUserForm.accountInactiveReasons}" />
                                                    </form:select>
                                                </c:when>
                                                <c:otherwise>
                                                    <form:select path="accountInactiveReason" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                        <form:option value="" label="--- Select ---"/>
                                                    </form:select>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="Tleft" width="90px" nowrap>
                                            <c:choose>
                                                <c:when test="${searchUserForm.status eq ActionTypeEnum.ACTIVE}">
                                                    <input type="submit" value="MAKE INACTIVE" class="add-btn" style="margin: 5px 0 0 0" name="update-user">
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="submit" value="ACTIVATE" class="add-btn" style="margin: 5px 0 0 0" name="update-user">
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </form:form>
                                </tr>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="alert-info">
                                <div class="no-approve">
                                    Submit to find user.
                                    <c:if test="${!empty fn:trim(searchUserForm.qid)}">
                                        No user found ${searchUserForm.qid}
                                    </c:if>
                                </div>
                            </div>
                        </c:otherwise>
                        </c:choose>
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
