<%@ page import="com.noqapp.domain.types.ActionTypeEnum" %>
<%@ include file="../../../jsp/include.jsp" %>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/jquery/css/jquery-ui.css"/>
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
                        <a href="${pageContext.request.contextPath}/business/discount/landing.htm">Discount</a>
                        <a href="${pageContext.request.contextPath}/business/coupon/landing.htm">Client Coupon</a>
                        <a href="${pageContext.request.contextPath}/business/coupon/businessLanding.htm">Business Coupon</a>
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
            <!-- Complete profile -->
            <div class="admin-main">
                <!-- File Upload From -->

                <form:form action="${pageContext.request.contextPath}/business/discount/action.htm" modelAttribute="discountForm" method="post">
                    <form:hidden path="actionType" value="${ActionTypeEnum.ADD}" />
                    <div class="admin-title">
                        <h2>Add New Discount</h2>
                    </div>

                    <spring:hasBindErrors name="discountForm">
                    <div class="error-box">
                        <div class="error-txt">
                            <ul>
                                <c:forEach items="${errors.allErrors}" var="message">
                                <li><spring:message message="${message}" /></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                    </spring:hasBindErrors>

                    <div class="admin-content">
                        <div class="add-new">
                            <ul class="list-form">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="discountName" cssErrorClass="lb_error">Discount Name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="discountName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                placeholder="Name this discount"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="discountDescription" cssErrorClass="lb_error">Discount Description</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="discountDescription" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                placeholder="Describe discount in detail"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="couponType" cssErrorClass="lb_error">Coupon Type</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="couponType" cssClass="form-field-select single-dropdown"
                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:option value="" label="--- Select ---"/>
                                            <form:options items="${discountForm.couponTypes}" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="discountType" cssErrorClass="lb_error">Discount Type</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="discountType" cssClass="form-field-select single-dropdown"
                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:option value="" label="--- Select ---"/>
                                            <form:options items="${discountForm.discountTypes}" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="discountAmount" cssErrorClass="lb_error">Discount Amount</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="discountAmount" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                placeholder="Set amount to be discounted"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>

                            <div class="col-lable3"></div>
                            <div class="col-fields">
                                <div class="left-btn">
                                    <input name="add" class="next-btn" value="ADD" type="submit">
                                </div>
                                <div class="right-btn">
                                    <input name="cancel_Add" class="cancel-btn" value="CANCEL" type="submit">
                                </div>
                                <div class="clearFix"></div>
                            </div>
                            <div class="clearFix"></div>
                        </div>
                    </div>
                </form:form>
            </div>
            <!-- Complete profile -->
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
                    <div class="f-left">&copy; 2020 NoQueue Inc. | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> |
                        <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
