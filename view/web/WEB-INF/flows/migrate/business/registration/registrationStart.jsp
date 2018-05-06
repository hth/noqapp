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
            <sec:authorize access="hasAnyRole('ROLE_M_ADMIN', 'ROLE_CLIENT')">
                <div class="admin-main">
                    <form:form modelAttribute="register.registerBusiness">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                        <div class="admin-title">
                            <c:choose>
                                <c:when test="${empty register.registerBusiness.businessUser}">
                                    <h2>Add Business Details</h2>
                                </c:when>
                                <c:otherwise>
                                    <h2>Edit Business Details</h2>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="error-box">
                            <div class="error-txt">
                                <c:if test="${!empty flowRequestContext.messageContext.allMessages}">
                                <ul>
                                <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
                                    <li>${message.text}</li>
                                </c:forEach>
                                </ul>
                                </c:if>
                            </div>
                        </div>
                        <div class="admin-content">
                            <div class="add-new">
                                <ul class="list-form">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="name" cssErrorClass="lb_error">Business Name</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="name" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="businessTypes" cssErrorClass="lb_error">Business Type</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <c:choose>
                                                <c:when test="${!empty register.registerBusiness.businessUser}">
                                                    <form:select path="businessTypes" cssClass="form-field-select" cssErrorClass="form-field-select error-field" multiple="true">
                                                        <form:options items="${register.registerBusiness.availableBusinessTypes}" itemValue="name" itemLabel="description" disabled="true"/>
                                                    </form:select>
                                                </c:when>
                                                <c:otherwise>
                                                    <form:select path="businessTypes" cssClass="form-field-select" cssErrorClass="form-field-select error-field" multiple="true">
                                                        <form:options items="${register.registerBusiness.availableBusinessTypes}" itemValue="name" itemLabel="description"/>
                                                    </form:select>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="address" cssErrorClass="lb_error">Business Address</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:textarea path="address" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>

                                    <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
                                    <c:if test="${message.source eq 'registerBusiness.area'}">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="area" cssErrorClass="lb_error">Business Town</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="area" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" placeholder="Santacruz" />
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    </c:if>
                                    <c:if test="${message.source eq 'registerBusiness.town'}">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="town" cssErrorClass="lb_error">Business City</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="town" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" placeholder="Mumbai" />
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    </c:if>
                                    </c:forEach>

                                    <c:if test="${!empty register.registerBusiness.foundAddresses}">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="foundAddressPlaceId" cssErrorClass="lb_error">Best Matching Business Addresses</form:label>
                                        </div>
                                        <div class="col-fields pT10 pB10">
                                            <c:forEach items="${register.registerBusiness.foundAddresses}" var="mapElement">
                                                <form:radiobutton path="foundAddressPlaceId" value="${mapElement.key}" label="${mapElement.value.formattedAddress}"
                                                        onclick="handleFoundAddressClick();"/> <br />
                                            </c:forEach>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="selectFoundAddress" cssErrorClass="lb_error">I choose Best Matching Business Address</form:label>
                                        </div>
                                        <div id="addressCheckBox" class="col-fields">
                                            <form:checkbox path="selectFoundAddress" cssClass="form-check-box" cssErrorClass="form-field-admin error-field" disabled="true"
                                                    onclick="handleFoundAddressCheckboxUncheck()" />
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    </c:if>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="phone" cssErrorClass="lb_error">Business Phone</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="phone" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="businessServiceImage" cssErrorClass="lb_error">Business Image</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="businessServiceImage" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                        placeholder="Show the best image of your business"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <c:if test="${empty register.registerBusiness.businessUser}">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="inviteeCode" cssErrorClass="lb_error">Have Invitee Code?</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <c:choose>
                                                <c:when test="${empty register.registerBusiness.inviteeCode}">
                                                    <form:input path="inviteeCode" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <form:input path="inviteeCode" cssClass="form-field-admin" readonly="true"/>
                                                </c:otherwise>
                                            </c:choose>
                                            <span style="display:block; font-size:14px;">Optional. Enter if you have been provided with one.</span>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    </c:if>

                                    <c:if test="${empty register.registerBusiness.businessUser}">
                                    <li>
                                        <div class="alert-info">
                                            <p>
                                                To approve your business, we may ask you to provide valid
                                                documentation for your registered business. Please keep business
                                                information and point of contact up to date.
                                            </p>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="alert-info">
                                            <p>
                                                Profile information added in previous screen would be primary point of
                                                contact for this business.
                                            </p>
                                        </div>
                                    </li>
                                    </c:if>
                                </ul>

                                <div class="col-lable3"></div>
                                <div class="col-fields">
                                    <c:choose>
                                        <c:when test="${register.registerUser.emailValidated}">
                                            <div class="button-btn">
                                                <button name="_eventId_submit" class="ladda-button next-btn" style="width:48%; float: left">Next</button>
                                                <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                            </div>
                                        </c:when>
                                        <c:when test="${!empty register.registerBusiness.businessUser}">
                                            <div class="button-btn">
                                                <button name="_eventId_edit" class="ladda-button next-btn" style="width:48%; float: left">Edit</button>
                                                <button name="_eventId_editCancel" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <button name="_eventId_cancel" class="ladda-button cancel-btn">Cancel</button>
                                        </c:otherwise>
                                    </c:choose>

                                    <%--<c:if test="${register.registerUser.emailValidated}">--%>
                                        <%--<div class="left-btn">--%>
                                            <%--<input name="_eventId_submit" class="next-btn" value="NEXT" type="submit">--%>
                                        <%--</div>--%>
                                    <%--</c:if>--%>
                                    <%--<div class="right-btn">--%>
                                        <%--<input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">--%>
                                    <%--</div>--%>
                                    <div class="clearFix"></div>
                                </div>
                                <div class="clearFix"></div>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/services.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/ladda.min.js"></script>
<script type="text/javascript">
    // Bind normal buttons
    Ladda.bind('.button-btn button', {timeout: 6000});

    // Bind progress buttons and simulate loading progress
    Ladda.bind('.progress-demo button', {
        callback: function (instance) {
            var progress = 0;
            var interval = setInterval(function () {
                progress = Math.min(progress + Math.random() * 0.1, 1);
                instance.setProgress(progress);

                if (progress === 1) {
                    instance.stop();
                    clearInterval(interval);
                }
            }, 200);
        }
    });

    // You can control loading explicitly using the JavaScript API
    // as outlined below:

    // var l = Ladda.create( document.querySelector( 'button' ) );
    // l.start();
    // l.stop();
    // l.toggle();
    // l.isLoading();
    // l.setProgress( 0-1 );
</script>
</html>
