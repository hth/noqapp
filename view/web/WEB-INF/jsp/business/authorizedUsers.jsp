<%@ page import="com.noqapp.domain.types.BusinessUserRegistrationStatusEnum, com.noqapp.domain.types.UserLevelEnum, com.noqapp.domain.types.BusinessTypeEnum" %>
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
                        <form action="${pageContext.request.contextPath}/access/signoff" method="post">
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
                        <h3>Authorized users to manage <span>${queueSupervisorForm.queueName}</span></h3>

                        <div class="alert-info">
                            <p>
                                Delete operation removes user permanently from ${queueSupervisorForm.queueName}.
                                To add the user back, you would need to again add them as "Add New Queue Supervisor"
                                OR "Add New Agent"
                            </p>
                        </div>
                        <div class="add-store">
                            <div class="store-table">
                                <spring:hasBindErrors name="errorMessage">
                                <div class="error-box">
                                    <div class="error-txt">
                                        <ul>
                                            <li><form:errors path="errorMessage"/></li>
                                        </ul>
                                    </div>
                                </div>
                                <div class="space10"></div>
                                </spring:hasBindErrors>

                                <c:choose>
                                <c:when test="${!empty queueSupervisorForm.queueSupervisors}">
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <th>&nbsp;</th>
                                            <th>
                                                Name
                                                &nbsp;
                                                <img src="${pageContext.request.contextPath}/static/internal/img/sortAZ.png"
                                                     alt="Sort" height="16px;"/>
                                            </th>
                                            <th>Address</th>
                                            <th>Role</th>
                                            <th>Since</th>
                                            <th>&nbsp;</th>
                                        </tr>
                                        <c:set var="userLevelEnumValues" value="<%=UserLevelEnum.allowedBusinessUserLevel()%>"/>
                                        <c:forEach items="${queueSupervisorForm.queueSupervisors}" var="queueSupervisor" varStatus="status">
                                            <tr id="authorized_${status.count}">
                                                <td>${status.count}&nbsp;</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${queueSupervisor.businessUserRegistrationStatus eq BusinessUserRegistrationStatusEnum.V && queueSupervisor.userLevel ne UserLevelEnum.M_ADMIN}">
                                                            <span style="display:block; font-size:13px;"><a href="/business/queueUserDetail/${queueSupervisor.businessUserId}" style="color: #0000FF;">${queueSupervisor.name}</a></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="display:block; font-size:13px;">${queueSupervisor.name}</span>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:if test="${queueSupervisor.userLevel eq UserLevelEnum.S_MANAGER && queueSupervisor.businessType eq BusinessTypeEnum.DO}">
                                                        <c:choose>
                                                            <c:when test="${!empty queueSupervisor.educations}">
                                                                <span style="display:block; font-size:11px;">Educations:
                                                                <c:forEach items="${queueSupervisor.educations}" var="item" varStatus="status">
                                                                    ${item.name},&nbsp;
                                                                </c:forEach>
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="display:block; font-size:11px;">Educations: N/A</span>
                                                            </c:otherwise>
                                                        </c:choose>

                                                        <c:choose>
                                                            <c:when test="${!empty queueSupervisor.licenses}">
                                                                <span style="display:block; font-size:11px;">Licenses:
                                                                <c:forEach items="${ queueSupervisor.licenses}" var="item" varStatus="status">
                                                                    ${item.name},&nbsp;
                                                                </c:forEach>
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="display:block; font-size:11px;">Licenses: N/A</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                </td>
                                                <td nowrap>
                                                    <c:choose>
                                                        <c:when test="${fn:length(queueSupervisor.address) < 55}">
                                                            <span style="display:block; font-size:13px;">${queueSupervisor.address}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="display:block; font-size:13px;">${fn:substring(queueSupervisor.address, 0, 55)}...</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:choose>
                                                        <c:when test="${queueSupervisor.phoneValidated}">
                                                            <span style="display:block; font-size:13px;"><p>Phone: ${queueSupervisor.phone}</p></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="display:block; font-size:13px;"><p>Phone: N/A</p></span>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:choose>
                                                        <c:when test="${fn:endsWith(queueSupervisor.email, '@mail.noqapp.com')}">
                                                            <span style="display:block; font-size:13px;">
                                                                <a href="/business/queueUserProfile/${queueSupervisor.businessUserId}" style="color: #0000FF;" target="_blank">--</a>
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="display:block; font-size:13px;">
                                                                Profile: <a href="/business/queueUserProfile/${queueSupervisor.businessUserId}" style="color: #0000FF;" target="_blank">${queueSupervisor.email}</a>
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td nowrap width="130px;">
                                                <c:choose>
                                                    <c:when test="${queueSupervisor.businessUserRegistrationStatus eq BusinessUserRegistrationStatusEnum.V && queueSupervisor.userLevel ne UserLevelEnum.M_ADMIN}">
                                                        <select id="userLevel${status.count}" class="form-field-select single-dropdown"
                                                                style="display:block; font-size:13px;"
                                                                onchange="changeUserLevel('${queueSupervisor.businessUserId}', '${status.count}');">
                                                            <c:forEach var="item" items="${userLevelEnumValues}">
                                                            <option value="${item}" ${item == queueSupervisor.userLevel ? 'selected="selected"' : ''}>${item.description}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="display:block; font-size:13px;">${queueSupervisor.userLevel.description}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                                </td>
                                                <td nowrap>
                                                    <span style="display:block; font-size:13px;"><fmt:formatDate value="${queueSupervisor.created}" pattern="yyyy-MM-dd"/></span>
                                                </td>
                                                <td class="Tleft" width="180px" nowrap>
                                                <c:choose>
                                                    <c:when test="${queueSupervisor.businessUserRegistrationStatus eq BusinessUserRegistrationStatusEnum.V && queueSupervisor.userLevel ne UserLevelEnum.M_ADMIN}">
                                                        <form:form action="${pageContext.request.contextPath}/business/actionQueueSupervisor" modelAttribute="queueSupervisorActionForm" method="post">
                                                            <form:hidden path="action" value="DELETE" />
                                                            <form:hidden path="businessUserId" value="${queueSupervisor.businessUserId}" />
                                                            <input class="cancel-btn" value="Delete" type="submit">
                                                        </form:form>
                                                    </c:when>
                                                    <c:when test="${queueSupervisor.businessUserRegistrationStatus eq BusinessUserRegistrationStatusEnum.C}">
                                                        <p style="white-space: normal; display:block; font-size:13px;">
                                                            Approve / Reject (Pending) <br/>
                                                            <a href="/business/landing" style="color: #0000FF;">Click here</a> & then click on pending column to approve
                                                        </p>
                                                    </c:when>
                                                    <c:otherwise>
                                                        --
                                                    </c:otherwise>
                                                </c:choose>
                                                </td>
                                            </tr>
                                            <tr id="authorized_${status.count}_S" style="display:none;">
                                                <td colspan="7"></td>
                                            </tr>
                                            <tr id="authorized_${status.count}_F" style="display:none;">
                                                <td colspan="7"></td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert-info">
                                        <p>There is no supervisor added to manage queue. Highly recommended to add queue supervisor. Select a queue to add supervisor.</p>
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
<script>
    function changeUserLevel(id, position) {
        let userLevel = $( "#userLevel" + position).val();
        let s = "authorized_" + position + "_S";
        let f = "authorized_" + position + "_F";
        $.ajax({
            type: "POST",
            url: '${pageContext. request. contextPath}/business/changeLevel',
            data: {
                id: id,
                userLevel: userLevel
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
                $("#" + s + ' > td').attr('id', s + 's').removeAttr("style");
                $("#" + f + ' > td').attr('id', f + 'f').removeAttr("style");
            },
            success: function (data) {
                let text;
                if (data.action === 'SUCCESS') {
                    $("#" + s).removeAttr("style");
                    $("#" + s + ' > td')
                        .attr('id', s + 's')
                        .css('text-align','right').css("background-color", "#fff0f0").css("color", "#0D8B0B")
                        .html(data.text).delay(5000).fadeOut('slow');
                } else {
                    $("#" + f).removeAttr("style");
                    $("#" + f + ' > td')
                        .attr('id', f + 'f')
                        .css('text-align','right').css("background-color", "#fecfcf").css("color", "#c72926")
                        .html(data.text).delay(5000).fadeOut('slow');
                }
            }
        });
    }
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
