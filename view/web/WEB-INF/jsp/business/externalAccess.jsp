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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
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
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <h3>Permissions: <span>${externalAccessForm.externalAccesses.size()}</span></h3>
                        <c:if test="${!empty externalAccessForm.externalAccesses}">
                        <div class="alert-info">
                            <p>Permission to NoQueue Support for helping in your account related activities.</p>
                        </div>
                        </c:if>
                        <div class="add-store">
                            <h2>&nbsp;</h2>
                            <div class="store-table">
                                <c:choose>
                                <c:when test="${!empty externalAccessForm.externalAccesses}">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th>Authorization</th>
                                        <th>Permission</th>
                                        <th>&nbsp;</th>
                                    </tr>
                                    <c:forEach items="${externalAccessForm.externalAccesses}" var="externalAccess" varStatus="status">
                                    <tr>
                                        <td>${status.count}&nbsp;</td>
                                        <td>${externalAccess.idAsBase64}&nbsp;</td>
                                        <td nowrap>${externalAccess.externalPermission.customerFriendlyDescription}</td>
                                        <td class="Tleft" nowrap>
                                            <c:choose>
                                                <c:when test="${empty externalAccess.approverQID}">
                                                    <div>
                                                        <form:form action="${pageContext.request.contextPath}/business/external/access/actionExternalAccess.htm" modelAttribute="externalAccessForm" method="post">
                                                            <form:hidden path="action" value="APPROVE" />
                                                            <form:hidden path="id" value="${externalAccess.idAsBase64}" />
                                                            <input class="cancel-btn" value="Approve" type="submit">
                                                        </form:form>
                                                        <br />
                                                        <form:form action="${pageContext.request.contextPath}/business/external/access/actionExternalAccess.htm" modelAttribute="externalAccessForm" method="post">
                                                            <form:hidden path="action" value="REJECT" />
                                                            <form:hidden path="id" value="${externalAccess.idAsBase64}" />
                                                            <input class="cancel-btn" value="Reject" type="submit">
                                                        </form:form>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div>
                                                        <form:form action="${pageContext.request.contextPath}/business/external/access/actionExternalAccess.htm" modelAttribute="externalAccessForm" method="post">
                                                            <form:hidden path="action" value="REMOVE" />
                                                            <form:hidden path="id" value="${externalAccess.idAsBase64}" />
                                                            <input class="cancel-btn" style="margin: 0;" value="Remove" type="submit">
                                                        </form:form>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                    </c:forEach>
                                </table>
                                </c:when>
                                <c:otherwise>
                                <div class="alert-info">
                                    <p>No permissions given to NoQueue Support.</p>
                                </div>
                                </c:otherwise>
                                </c:choose>
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
                    <div class="f-left">&copy; 2020 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

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
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
