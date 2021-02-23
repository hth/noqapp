<%@ include file="../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
    <meta content='width=device-width, initial-scale=1' name='viewport'/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>
</head>

<body>

<!-- header -->
<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left">
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static/internal/img/logo.png" alt="NoQueue"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static/internal/img/menu-top-arrow.png"/></div>
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
            <sec:authorize access="hasAnyRole('ROLE_M_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')">
                <div class="admin-main">
                    <div class="admin-content">
                        <div class="store">
                            <h3>Preferred business count: <span>${preferredBusinessForm.preferredBusinesses.size()}</span></h3>
                        </div>
                    </div>

                    <form:form method="post" action="${pageContext.request.contextPath}/business/preferredBusiness.htm" modelAttribute="preferredBusinessForm">
                        <spring:hasBindErrors name="preferredBusinessForm">
                        <div class="error-box">
                            <div class="error-txt">
                                <ul>
                                    <c:if test="${errors.hasFieldErrors('categoryName')}">
                                        <li><form:errors path="categoryName"/></li>
                                    </c:if>
                                </ul>
                            </div>
                        </div>
                        </spring:hasBindErrors>

                        <div class="admin-content">
                            <div class="add-new">
                                <ul class="list-form">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="businessNameToAdd" cssErrorClass="lb_error">Name of Preferred Business</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="businessNameToAdd" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                    placeholder="Name of Business"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                </ul>

                                <div class="col-lable3"></div>
                                <div class="col-fields">
                                    <div class="button-btn">
                                        <button name="add" class="ladda-button next-btn" style="width:48%; float: left">Add</button>
                                        <button name="cancel_Add" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                    </div>
                                    <div class="clearFix"></div>
                                </div>
                                <div class="clearFix"></div>
                            </div>
                        </div>
                    </form:form>

                    <div class="store-table">
                        <c:choose>
                            <c:when test="${!empty preferredBusinessForm.preferredBusinesses}">
                                <div class="alert-info">
                                    Preferred businesses is way of promoting other businesses.
                                </div>
                                <h2>Total preferred businesses: <span>${preferredBusinessForm.preferredBusinesses.size()}</span></h2>

                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th nowrap>
                                            Business Name
                                            &nbsp;
                                            <img src="${pageContext.request.contextPath}/static/internal/img/sortAZ.png"
                                                    alt="Sort" height="16px;"/>
                                        </th>
                                        <th>
                                            Business Type
                                        </th>
                                        <th nowrap></th>
                                    </tr>
                                    <c:forEach items="${preferredBusinessForm.preferredBusinesses}" var="preferredBusiness" varStatus="status">
                                        <tr>
                                            <td>${status.count}&nbsp;</td>
                                            <td nowrap><span style="display:block; font-size:13px;">${preferredBusiness.preferredBusinessName}</span></td>
                                            <td nowrap><span style="display:block; font-size:13px;">${preferredBusiness.businessType.description}</span></td>
                                            <td nowrap>
                                                <form:form method="post" action="${pageContext.request.contextPath}/business/preferredBusiness.htm" modelAttribute="preferredBusinessForm">
                                                    <form:hidden path="recordId" value="${preferredBusiness.id}" />
                                                    <button name="delete" class="add-btn">Delete</button>
                                                </form:form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="alert-info">
                                    <p>No preferred business added.</p>
                                    <p>
                                        What's Preferred Business?
                                        Preferred business will show up in case history form for taking order for
                                        medicine or pathology or radiology to directly show up on their ordered list.
                                    </p>
                                </div>
                            </c:otherwise>
                        </c:choose>
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
            <img src="${pageContext.request.contextPath}/static/internal/img/footer-img.jpg" class="img100"/>
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2021 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/ladda/js/ladda.min.js"></script>
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
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>

