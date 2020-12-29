<%@ page import="com.noqapp.domain.types.ValidateStatusEnum" %>
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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a>
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
                        <h3>Publish Job</h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <a href="/business/store/publishJob/newJob.htm" class="add-btn">Add New Job</a>
                            </div>
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${fn:length(publishJobForms) gt 0}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th nowrap>Title</th>
                                                <th>Publish Date</th>
                                                <th nowrap>Article State</th>
                                                <th>Stats</th>
                                            </tr>
                                            <c:forEach items="${publishJobForms}" var="item" varStatus="status">
                                            <tr>
                                                <td>${status.count}&nbsp;</td>
                                                <td nowrap>
                                                    ${item.title}
                                                    <table width="100%" border="0" cellspacing="0" cellpadding="0" style="border: 0">
                                                        <tr>
                                                            <td style="border: 0; padding: 1px;">
                                                                <form:form action="${pageContext.request.contextPath}/business/store/publishJob/action.htm"
                                                                        modelAttribute="publishJobForm" method="post">
                                                                    <form:hidden path="action" value="EDIT" />
                                                                    <form:hidden path="publishId" value="${item.publishId}" />
                                                                    <input class="cancel-btn" value="EDIT ARTICLE" type="submit">
                                                                </form:form>
                                                            </td>
                                                            <td style="border: 0; padding: 1px;">
                                                                <c:choose>
                                                                    <c:when test="${item.active}">
                                                                        <form:form action="${pageContext.request.contextPath}/business/store/publishJob/action.htm"
                                                                                modelAttribute="publishJobForm" method="post">
                                                                            <form:hidden path="action" value="OFFLINE" />
                                                                            <form:hidden path="publishId" value="${item.publishId}" />
                                                                            <input class="cancel-btn" value="TAKE IT OFFLINE" type="submit">
                                                                        </form:form>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <form:form action="${pageContext.request.contextPath}/business/store/publishJob/action.htm"
                                                                                modelAttribute="publishJobForm" method="post">
                                                                            <form:hidden path="action" value="ONLINE" />
                                                                            <form:hidden path="publishId" value="${item.publishId}" />
                                                                            <input class="cancel-btn" style="background:#ff217c" value="TAKE IT ONLINE" type="submit">
                                                                        </form:form>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td style="border: 0; padding: 1px;">
                                                                <form:form action="${pageContext.request.contextPath}/business/store/publishJob/action.htm"
                                                                        modelAttribute="publishJobForm" method="post">
                                                                    <form:hidden path="action" value="DELETE" />
                                                                    <form:hidden path="publishId" value="${item.publishId}" />
                                                                    <input class="cancel-btn" value="DELETE" type="submit">
                                                                </form:form>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${!empty item.publishDate}">
                                                            <fmt:formatDate pattern="MMMM dd, yyyy" value="${item.publishDate}"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            N/A
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td nowrap>${item.validateStatus.description}</td>
                                                <td nowrap>
                                                    N/A
                                                </td>
                                            </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>Publish jobs. We will inform you on interest received.</p>
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