<%@ include file="../../jsp/include.jsp" %>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/jquery/css/jquery-ui.css" />
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
            <!-- Complete profile -->
            <div class="admin-main">
                <c:if test="${professionalProfileEditForm.professionalProfile}">
                    <div class="admin-title">
                        <h2>Edit Professional Profile</h2>
                    </div>
                    <div class="error-box">
                        <div class="error-txt">
                            <spring:hasBindErrors name="professionalProfileEditForm">
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
                        </div>
                    </div>
                    <div class="admin-content">
                        <div class="add-new">
                            <ul class="list-form">
                                <form:form action="${pageContext.request.contextPath}/access/userProfile/userProfessionalDetail/add.htm" method="post" modelAttribute="professionalProfileEditForm">
                                    <form:hidden path="action" value="${professionalProfileEditForm.action}" />
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="name" cssErrorClass="lb_error">Name</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="name" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" />
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="monthYear" cssErrorClass="lb_error">Date Achieved</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="monthYear" cssClass="datepicker form-field-admin" cssErrorClass="datepicker form-field-admin error-field" placeholder="YYYY-MM-DD"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <div class="col-lable3"></div>
                                    <div class="col-fields">
                                        <div class="left-btn">
                                            <input name="add" class="next-btn" value="ADD" type="submit">
                                        </div>
                                        <div class="right-btn">
                                            <input name="cancel" class="cancel-btn" value="CANCEL" type="submit">
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                    <div class="clearFix"></div>
                                </form:form>

                                <fieldset>
                                    <legend>${professionalProfileEditForm.action.toUpperCase()}</legend>
                                    <div class="store-table">
                                        <c:choose>
                                            <c:when test="${!empty professionalProfileEditForm.nameDatePairs}">
                                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                    <tr>
                                                        <th width="5%">&nbsp;</th>
                                                        <th width="75%" nowrap>
                                                            Name
                                                            &nbsp;
                                                            <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                                                 alt="Sort" height="16px;"/>
                                                        </th>
                                                        <th width="20%">Date Achieved</th>
                                                        <th nowrap></th>
                                                    </tr>
                                                    <c:forEach items="${professionalProfileEditForm.nameDatePairs}" var="nameDatePair" varStatus="status">
                                                        <tr>
                                                            <td>${status.count}&nbsp;</td>
                                                            <td nowrap>${nameDatePair.name}</td>
                                                            <td>${nameDatePair.monthYear}</td>
                                                            <td nowrap>
                                                                <form action="${pageContext.request.contextPath}/access/userProfile/userProfessionalDetail/delete.htm" method="post">
                                                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                                    <input type="hidden" name="name" value="${nameDatePair.name}"/>
                                                                    <input type="hidden" name="monthYear" value="${nameDatePair.monthYear}"/>
                                                                    <input type="hidden" name="action" value="${professionalProfileEditForm.action}"/>
                                                                    <button name="delete" class="add-btn" style="margin: 0px;">Delete</button>
                                                                </form>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </table>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="alert-info">
                                                    <p>No information added.</p>
                                                    <p>
                                                        Information added here will be visible to users under your profile.
                                                        Share information relevant to your profession.
                                                    </p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </fieldset>
                            </ul>
                        </div>
                    </div>
                </c:if>
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
                    <div class="f-left">&copy; 2020 NoQueue. | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/jquery/js/jquery-ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/services.js"></script>
<script>
    $(function () {
        $(".datepicker").datepicker({
            dateFormat: 'yy-mm-dd'
        });
    });
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
