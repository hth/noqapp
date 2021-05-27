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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/external/jquery/css/jquery-ui.css"/>
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
                        <a href="${pageContext.request.contextPath}/access/userProfile">Profile</a>
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
            <!-- Complete profile -->
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <h3><span>SOS Message</span></h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <a href="/access/sos/add" class="add-btn">Add SOS Receiver</a>
                            </div>
                            <div class="alert-info">
                                <p>
                                    You can add multiple contact before enabling SOS Message so that when you are in any emergency situation,
                                    you can send them message/information about yourself by just clicking SOS on NoQueue mobile app.

                                    Your location detail will be delivered instantaneously.
                                </p>
                            </div>

                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty userProfiles}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th>Name</th>
                                                <th>Phone</th>
                                                <th>&nbsp;</th>
                                            </tr>
                                            <c:forEach items="${userProfiles}" var="userProfile" varStatus="status">
                                            <tr>
                                                <td>${status.count}&nbsp;</td>
                                                <td>${userProfile.name}</td>
                                                <td>${userProfile.phoneFormatted}</td>
                                                <td width="150px;">
                                                    <form:form action="${pageContext.request.contextPath}/access/sos/delete" modelAttribute="addPrimaryContactMessageSOSForm" method="post">
                                                        <form:hidden path="phoneNumber" value="${userProfile.phone}" />
                                                        <input class="cancel-btn" value="Remove" type="submit">
                                                    </form:form>
                                                </td>
                                            </tr>
                                            </c:forEach>
                                        </table>

                                        <div class="alert-info">
                                            <p>People listed above will receive your SOS.</p>
                                            <p>
                                                <span style="text-decoration: underline; color: #1b1b1b;">Fine print:</span>
                                                SOS service is not a guaranteed service. There has to be a network connection and
                                                other factors that can affect this service. In any case, your SOS will be transmitted
                                                immediately and upon successful delivery you will be notified.
                                            </p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>You have not added any contact for SOS to be enabled on your phone.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Complete profile -->
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
                    <div class="f-left">&copy; 2021 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> |
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/jquery/js/jquery-ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/services.js"></script>
<script>
    $(function () {
        $(".datepicker").datepicker({
            dateFormat: 'yy-mm-dd'
        });
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
