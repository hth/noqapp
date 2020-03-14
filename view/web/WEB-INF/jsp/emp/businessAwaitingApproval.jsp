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
            <sec:authorize access="hasRole('ROLE_SUPERVISOR')">
            <!-- Add New Supervisor -->
            <div class="admin-main">
                <div class="admin-title">
                    <h2>Confirm personal and business details</h2>
                </div>
                <div class="admin-content">
                    <div class="add-new">
                        <form:form method="POST" action="./approval.htm" modelAttribute="businessAwaitingApprovalForm">
                            <form:hidden path="businessUser.id"/>
                            <ul class="list-form">
                                <li>
                                    <div class="alert-info">
                                        <p>
                                            Registered Account Information
                                        </p>
                                    </div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="userProfile.firstName" cssErrorClass="lb_error">First name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="userProfile.firstName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="userProfile.lastName" cssErrorClass="lb_error">Last name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="userProfile.lastName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="userProfile.address" cssErrorClass="lb_error">Your Address</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:textarea path="userProfile.address" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="userProfile.phoneFormatted" cssErrorClass="lb_error">Your Phone</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="userProfile.phoneFormatted" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="alert-info">
                                        <p>
                                            Business Information
                                        </p>
                                    </div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="businessUser.bizName.businessName" cssErrorClass="lb_error">Business Name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="businessUser.bizName.businessName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="businessUser.bizName.businessType" cssErrorClass="lb_error">Business Type</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="businessUser.bizName.businessType" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:options items="${businessAwaitingApprovalForm.availableBusinessTypes}" itemValue="name" itemLabel="description" disabled="true" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="businessUser.bizName.address" cssErrorClass="lb_error">Business Address</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:textarea path="businessUser.bizName.address" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="businessUser.bizName.phoneFormatted" cssErrorClass="lb_error">Business Phone</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="businessUser.bizName.phoneFormatted" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>

                                <c:choose>
                                <c:when test="${!empty businessAwaitingApprovalForm.inviteeUserProfile}">
                                <li>
                                    <div class="alert-info">
                                        <p>
                                            Invitee Information below
                                        </p>
                                    </div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="inviteeUserProfile.firstName" cssErrorClass="lb_error">Invitee First name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="inviteeUserProfile.firstName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="inviteeUserProfile.lastName" cssErrorClass="lb_error">Invitee Last name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="inviteeUserProfile.lastName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="inviteeUserProfile.address" cssErrorClass="lb_error">Invitee Address</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:textarea path="inviteeUserProfile.address" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="inviteeUserProfile.phoneFormatted" cssErrorClass="lb_error">Invitee Phone</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="inviteeUserProfile.phoneFormatted" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                </c:when>
                                <c:otherwise>
                                <li>
                                    <div class="alert-info">
                                        <p>
                                            Direct sign up. Please call to validate account. And, do ask how did they hear about us?
                                        </p>
                                    </div>
                                </li>
                                </c:otherwise>
                                </c:choose>

                                <li class="mB0">
                                    <div class="col-lable3"></div>
                                    <c:choose>
                                    <c:when test="${businessAwaitingApprovalForm.businessUser.businessUserRegistrationStatus eq 'C'}">
                                    <div class="col-fields">
                                        <div class="left-btn">
                                            <input type="submit" value="APPROVE" class="next-btn" name="business-user-approve">
                                        </div>
                                        <div class="right-btn">
                                            <input type="submit" value="DECLINE" class="cancel-btn" name="business-user-decline">
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                    </c:when>
                                    <c:otherwise>
                                    <div class="col-fields">
                                        <div class="right-btn">
                                        <input type="submit" value="CANCEL" class="cancel-btn" name="_eventId_cancel"
                                                style="background: #2c97de; margin: 77px 10px 0 0;">
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                    </c:otherwise>
                                    </c:choose>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>
                        </form:form>
                    </div>
                </div>
            </div>
            <!-- Add New Supervisor -->
            </sec:authorize>

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
                    <div class="f-left">&copy; 2020 NoQueue Inc. | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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
