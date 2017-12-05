<%@ include file="../../../../jsp/include.jsp" %>
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
            <sec:authorize access="hasRole('ROLE_CLIENT')">
                <div class="admin-main">
                    <form:form modelAttribute="register">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                        <div class="admin-title">
                            <h2>Review your personal and business details</h2>
                        </div>
                        <div class="admin-content">
                            <div class="add-new">
                                <ul class="list-form">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="registerUser.firstName" cssErrorClass="lb_error">First name</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="registerUser.firstName" cssClass="form-field-admin" readonly="true"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>

                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="registerUser.lastName" cssErrorClass="lb_error">Last name</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="registerUser.lastName" cssClass="form-field-admin" readonly="true"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>

                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="registerUser.address" cssErrorClass="lb_error">Your Address</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:textarea path="registerUser.address" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>

                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="registerUser.phone" cssErrorClass="lb_error">Your Phone</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="registerUser.phone" cssClass="form-field-admin" readonly="true"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="registerBusiness.name" cssErrorClass="lb_error">Business Name</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="registerBusiness.name" cssClass="form-field-admin" readonly="true"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="registerBusiness.businessTypes" cssErrorClass="lb_error">Business Type</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:select path="registerBusiness.businessTypes" cssClass="form-field-select" cssErrorClass="form-field-select error-field" multiple="true">
                                                <form:options items="${register.registerBusiness.availableBusinessTypes}" itemValue="name" itemLabel="description" disabled="true"/>
                                            </form:select>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="registerBusiness.address" cssErrorClass="lb_error">Business Address</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:textarea path="registerBusiness.address" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="registerBusiness.phone" cssErrorClass="lb_error">Business Phone</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="registerBusiness.phone" cssClass="form-field-admin" readonly="true"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="registerBusiness.multiStore" cssErrorClass="lb_error">More than 1 store?</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:checkbox path="registerBusiness.multiStore" cssClass="form-check-box" cssErrorClass="form-field-admin error-field" disabled="true"/>
                                            <c:choose>
                                                <c:when test="${register.registerBusiness.multiStore eq false}">
                                                    <span style="display:block; font-size:14px;">You have answered 'NO'. You can always change this later.</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="display:block; font-size:14px;">You have answered 'YES'</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="registerBusiness.inviteeCode" cssErrorClass="lb_error">Have Invitee Code?</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="registerBusiness.inviteeCode" cssClass="form-field-admin" readonly="true"/>
                                            <span style="display:block; font-size:14px;">Optional. Enter if you have been provided with one.</span>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                </ul>

                                <c:if test="${!register.registerBusiness.multiStore}">
                                <div id="storeDetail">
                                    <div class="admin-title pT30">
                                        <h2>Review Store details</h2>
                                    </div>
                                    <ul class="list-form">
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="registerBusiness.displayName" cssErrorClass="lb_error">Queue Name</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="registerBusiness.displayName" cssClass="form-field-admin" readonly="true"/>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="registerBusiness.addressStore" cssErrorClass="lb_error">Store Address</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:textarea path="registerBusiness.addressStore" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="registerBusiness.phoneStore" cssErrorClass="lb_error">Store Phone</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="registerBusiness.phoneStore" cssClass="form-field-admin" readonly="true"/>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="registerBusiness.remoteJoin" cssErrorClass="lb_error">Allow Remote Join</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:checkbox path="registerBusiness.remoteJoin" cssClass="form-field-admin" disabled="true"/>
                                                <span style="display:block; font-size:14px;">(Allow user to join queue from Home, or far of places)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="registerBusiness.allowLoggedInUser" cssErrorClass="lb_error">Allow Registered User</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:checkbox path="registerBusiness.allowLoggedInUser" cssClass="form-field-admin" disabled="true"/>
                                                <span style="display:block; font-size:14px;">(Will limit registered users joining this queue)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                        <li>
                                            <div class="col-lable3">
                                                <form:label path="registerBusiness.availableTokenCount" cssErrorClass="lb_error">Issue Limited Tokens</form:label>
                                            </div>
                                            <div class="col-fields">
                                                <form:input path="registerBusiness.availableTokenCount" cssClass="form-field-admin" disabled="true"/>
                                                <span style="display:block; font-size:14px;">(Customers in the queue will be limited to allowed number. 0 is Unlimited Token. Greater than 0 is limited token)</span>
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                    </ul>
                                </div>

                                <div>
                                    <ul class="col2-grid">
                                        <c:forEach items="${register.registerBusiness.businessHours}" var="businessHour" varStatus="status">
                                            <li>
                                                <h4><strong><c:out value="${businessHour.dayOfWeek}"/></strong></h4>
                                                <c:choose>
                                                    <c:when test="${businessHour.dayClosed}">
                                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <td>Closed for the day</td>
                                                            </tr>
                                                        </table>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <td>Token Available Time</td>
                                                                <td>
                                                                    <c:out value="${businessHour.tokenAvailableFromAsString}"/></td>
                                                            </tr>
                                                            <tr>
                                                                <td>Store Start Time</td>
                                                                <td><c:out value="${businessHour.startHourStoreAsString}"/></td>
                                                            </tr>
                                                            <tr>
                                                                <td>Token Not Available After</td>
                                                                <td>
                                                                    <c:out value="${businessHour.tokenNotAvailableFromAsString}"/></td>
                                                            </tr>
                                                            <tr>
                                                                <td>Store Close Time</td>
                                                                <td><c:out value="${businessHour.endHourStoreAsString}"/></td>
                                                            </tr>
                                                        </table>
                                                    </c:otherwise>
                                                </c:choose>
                                            </li>
                                        </c:forEach>

                                        <div class="clearFix"></div>
                                    </ul>

                                </div>
                                </c:if>

                                <div class="btn-hours">
                                    <c:if test="${register.registerUser.emailValidated}">
                                        <div class="first-btn">
                                            <input name="_eventId_confirm" class="next-btn" value="CONFIRM" type="submit">
                                        </div>
                                    </c:if>
                                    <div class="center-btn">
                                        <input name="_eventId_revise" class="cancel-btn" value="REVISE" type="submit">
                                    </div>
                                    <div class="last-btn">
                                        <input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">
                                    </div>
                                    <div class="clearFix"></div>
                                </div>
                            </div>
                        </div>
                    </form:form>
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
                    <div class="f-left">&copy; 2017 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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
<script type="text/javascript">
    $(document).ready(function () {
        if ($('[name="registerBusiness.multiStore"]').is(':checked')) {
            $('#storeDetail').hide();
        } else {
            $('#storeDetail').show();
        }
    });
</script>
</html>
