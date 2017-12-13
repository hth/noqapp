<%@ include file="../../../jsp/include.jsp" %>
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
            <sec:authorize access="hasRole('ROLE_M_ADMIN')">
                <div class="admin-main">
                    <form:form modelAttribute="registerBusiness">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                        <div class="admin-title">
                            <h2>Add Store details</h2>
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
                                            <form:label path="displayName" cssErrorClass="lb_error">Queue Name</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="displayName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="storeBusinessType" cssErrorClass="lb_error">Queue for</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:select path="storeBusinessType" cssClass="form-field-select" cssErrorClass="form-field-select error-field" multiple="false">
                                                <form:options items="${registerBusiness.businessTypes}" itemValue="name" itemLabel="description"/>
                                            </form:select>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="addressStore" cssErrorClass="lb_error">Store Address</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:textarea path="addressStore" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <c:if test="${!empty registerBusiness.foundAddressStores}">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="foundAddressStorePlaceId" cssErrorClass="lb_error">Best Matching Store Addresses</form:label>
                                        </div>
                                        <div class="col-fields pT10 pB10">
                                            <c:forEach items="${registerBusiness.foundAddressStores}" var="mapElement">
                                                <form:radiobutton path="foundAddressStorePlaceId" value="${mapElement.key}" label="${mapElement.value.formattedAddress}"
                                                                  onclick="handleFoundAddressStoreClick();"/> <br />
                                            </c:forEach>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="selectFoundAddressStore" cssErrorClass="lb_error">I choose Best Matching Store Address</form:label>
                                        </div>
                                        <div id="addressStoreCheckBox" class="col-fields">
                                            <form:checkbox path="selectFoundAddressStore" cssClass="form-check-box" cssErrorClass="form-field-admin error-field" disabled="true"
                                                           onclick="handleFoundAddressStoreCheckboxUncheck()" />
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    </c:if>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="phoneStore" cssErrorClass="lb_error">Store Phone</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="phoneStore" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                </ul>

                                <div class="col-lable3"></div>
                                <div class="col-fields">
                                    <div class="left-btn">
                                        <input name="_eventId_submit" class="next-btn" value="NEXT" type="submit">
                                    </div>
                                    <div class="right-btn">
                                        <input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">
                                    </div>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/services.js"></script>
</html>
