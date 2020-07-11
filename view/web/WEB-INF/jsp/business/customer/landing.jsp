<%@ page import="com.noqapp.domain.types.OnOffEnum" %>
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

    <link rel="stylesheet" href="//cdn.jsdelivr.net/gh/StephanWagner/jBox@v1.0.9/dist/jBox.all.min.css" type='text/css'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>

    <!-- reference your copy Font Awesome here (from our CDN or by hosting yourself) -->
    <link href="${pageContext.request.contextPath}/static2/external/fontawesome/css/fontawesome.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static2/external/fontawesome/css/brands.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static2/external/fontawesome/css/solid.css" rel="stylesheet">

    <!-- custom styling for all icons -->
    i.fas,
    i.fab {
    border: 1px solid red;
    }
</head>

<body>

<!-- header -->
<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left">
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/>
                    </div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/business/external/access.htm">Permissions</a>
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
                        <div class="add-store">
                            <div class="alert-info">
                                <p>Priority helps give more privileges and better customer service to your customer.</p>
                            </div>
                            <div class="addbtn-store">
                                <form:form method="post" action="${pageContext.request.contextPath}/business/customer/priority.htm" modelAttribute="businessCustomerPriorityForm">
                                <div class="admin-content">
                                    <div class="add-new">
                                        <ul class="list-form">
                                            <li>
                                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                <div class="col-lable3">
                                                    <form:label path="priorityAccess" cssErrorClass="lb_error" style="color: #9f1313;">Priority Access</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:select path="priorityAccess" cssClass="form-field-select single-dropdown"
                                                            cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                        <form:option value="" label="--- Select ---"/>
                                                        <form:options items="${onOffTypes}" itemValue="name" itemLabel="description" />
                                                    </form:select>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                        </ul>
                                        <div class="col-lable3"></div>
                                        <div class="col-fields">
                                            <div class="button-btn">
                                                <button name="edit" class="ladda-button next-btn" style="width:48%; float: left">Edit</button>
                                                <button name="cancel-edit" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                            </div>
                                            <div class="clearFix"></div>
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                </div>
                                </form:form>
                            </div>
                            <div class="addbtn-store">
                            <c:if test="${businessCustomerPriorityForm.priorityAccess == OnOffEnum.O}">
                                <form:form method="post" action="${pageContext.request.contextPath}/business/customer/priority/add.htm" modelAttribute="businessCustomerPriorityForm">
                                <div class="admin-content">
                                    <div class="add-new">
                                        <ul class="list-form">
                                            <li>
                                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                <div class="col-lable3">
                                                    <form:label path="priorityName" cssErrorClass="lb_error" style="color: #9f1313;">Priority Name</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="priorityName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" />
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                            <li>
                                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                <div class="col-lable3">
                                                    <form:label path="priorityLevel" cssErrorClass="lb_error" style="color: #9f1313;">Priority Level</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:select path="priorityLevel" cssClass="form-field-select single-dropdown"
                                                            cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                        <form:option value="" label="--- Select ---"/>
                                                        <form:options items="${businessCustomerPriorityForm.customerPriorityLevels}" />
                                                    </form:select>
                                                    <span class="info-txt">(Low to High Priority. 1 is low and 10 is high. Default priority is 0.)</span>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                        </ul>
                                        <div class="col-lable3"></div>
                                        <div class="col-fields">
                                            <div class="button-btn">
                                                <button name="add" class="ladda-button next-btn" style="width:48%; float: left">Add</button>
                                                <button name="cancel-add" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                            </div>
                                            <div class="clearFix"></div>
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                </div>
                                </form:form>
                            </c:if>
                            </div>

                            <div class="add-store">
                                <div class="store-table">
                                    <c:choose>
                                        <c:when test="${!empty businessCustomerPriorityForm.businessCustomerPriorities}">
                                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <th>&nbsp;</th>
                                                    <th>&nbsp;Priority Name</th>
                                                    <th>&nbsp;Priority Level</th>
                                                </tr>
                                                <c:forEach items="${businessCustomerPriorityForm.businessCustomerPriorities}" var="businessCustomerPriority" varStatus="status">
                                                <tr>
                                                    <td>${status.count}&nbsp;</td>
                                                    <td>${businessCustomerPriority.priorityName}</td>
                                                    <td>${businessCustomerPriority.customerPriorityLevel.description}</td>
                                                </tr>
                                                </c:forEach>
                                            </table>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="alert-info">
                                                <c:choose>
                                                    <c:when test="${businessCustomerPriorityForm.priorityAccess == OnOffEnum.O}">
                                                        <p>Business has not set priority. Priority status ${businessCustomerPriorityForm.priorityAccess.description}</p>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <p>Business has not set priority.</p>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Add New Supervisor -->

        </div>
    </div>
    <!-- content end -->


    <!-- Foote -->
    <div class="footer">
        <div class="warp-inner ">
            <img src="${pageContext.request.contextPath}/static2/internal/img/footer-img.jpg" class="img100"/>
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2020 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> |
                        <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="//cdn.jsdelivr.net/gh/StephanWagner/jBox@v1.0.9/dist/jBox.all.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/ladda.min.js"></script>
<script type="text/javascript">
    // Bind normal buttons
    Ladda.bind('.button-btn button', {timeout: 6000});
    Ladda.bind('.button-btn button[name=reset]', {timeout: 10});

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
<script>
    new jBox('Tooltip', {
        attach: '.tooltip',
        adjustDistance: {
            top: 105,
            bottom: 150,
            left: 15,
            right: 50
        }
    });
</script>
<script>
    (function (w, u, d) {
        var i = function () {
            i.c(arguments)
        };
        i.q = [];
        i.c = function (args) {
            i.q.push(args)
        };
        var l = function () {
            var s = d.createElement('script');
            s.type = 'text/javascript';
            s.async = true;
            s.src = 'https://code.upscope.io/F3TE6jAMct.js';
            var x = d.getElementsByTagName('script')[0];
            x.parentNode.insertBefore(s, x);
        };
        if (typeof u !== "function") {
            w.Upscope = i;
            l();
        }
    })(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
