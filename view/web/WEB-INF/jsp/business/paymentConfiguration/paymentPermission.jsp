<%@ include file="../../include.jsp"%>
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
                        <form:form method="POST" action="./landing.htm" modelAttribute="paymentPermissionForm">
                            <ul class="list-form">
                                <li>
                                    <div class="alert-info">
                                        <p>
                                            Data visible based on Roles
                                        </p>
                                    </div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="paymentPermissionForSupervisor" cssErrorClass="lb_error">Supervisor</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="paymentPermissionForSupervisor" cssClass="form-field-select single-dropdown"
                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:option value="" label="--- Select ---"/>
                                            <form:options items="${paymentPermissionForm.paymentPermissions}" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="paymentPermissionForManager" cssErrorClass="lb_error">Manager/Doctor</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="paymentPermissionForManager" cssClass="form-field-select single-dropdown"
                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:option value="" label="--- Select ---"/>
                                            <form:options items="${paymentPermissionForm.paymentPermissions}" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li class="mB0">
                                    <div class="col-lable3"></div>
                                    <div class="col-fields">
                                        <div class="left-btn">
                                            <input type="submit" value="UPDATE" class="next-btn" name="update-paymentPermission">
                                        </div>
                                        <div class="right-btn">
                                            <input type="submit" value="CANCEL" class="cancel-btn" name="cancel-paymentPermission">
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>
                        </form:form>
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
