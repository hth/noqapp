<%@ page import="com.noqapp.domain.types.CouponTypeEnum,com.noqapp.domain.types.ValidateStatusEnum" %>
<%@ include file="../../include.jsp" %>
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
                        <a href="${pageContext.request.contextPath}/access/userProfile.htm">Profile</a>
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
                        <h3>Coupon</h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                            </div>
                            <div class="store-table">
                                <form:form modelAttribute="couponForm">
                                <c:choose>
                                    <c:when test="${fn:length(couponForm.couponId) gt 0}">
                                    <div class="admin-content">
                                        <div class="add-new">
                                            <ul class="list-form">
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="discountName" cssErrorClass="lb_error">Coupon Name</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="discountName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="discountDescription" cssErrorClass="lb_error">Coupon Description</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="discountDescription" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="discountAmount" cssErrorClass="lb_error">Coupon Amount</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="discountAmountAsString" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="discountType" cssErrorClass="lb_error">Discount Type</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="discountType.description" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="couponType" cssErrorClass="lb_error">Coupon Type</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="couponType.description" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="couponCode" cssErrorClass="lb_error">Coupon Code</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="couponCode" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="multiUse" cssErrorClass="lb_error">Coupon Multi-use</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:checkbox path="multiUse" cssClass="form-check-box" cssErrorClass="form-check-box error-field" disabled="true"/>
                                                        <span style="display:block; font-size:14px;">(When checked, can be used multiple times over)</span>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="couponStartDate" cssErrorClass="lb_error">Coupon Start Date</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="couponStartDate" cssClass="datepicker form-field-admin" cssErrorClass="datepicker form-field-admin error-field" placeholder="YYYY-MM-DD" readonly="true"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="couponEndDate" cssErrorClass="lb_error">Coupon End Date</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="couponEndDate" cssClass="datepicker form-field-admin" cssErrorClass="datepicker form-field-admin error-field" placeholder="YYYY-MM-DD" readonly="true"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <c:if test="${couponForm.couponType ne CouponTypeEnum.G}">
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="phoneRaw" cssErrorClass="lb_error">Customer Phone</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="phoneRaw" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                </c:if>
                                            </ul>

                                            <div class="col-lable3"></div>
                                            <div class="clearFix"></div>
                                        </div>
                                    </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>No coupon found. Please check again or let your Administrator know. Also contact NoQueue Support.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                </form:form>
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
                    <div class="f-left">&copy; 2019 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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
