<%@ include file="../include.jsp"%>
<!DOCTYPE html>
<html lang="en" ng-app="scroll" ng-controller="Main">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
    <link rel='stylesheet' href='${pageContext.request.contextPath}/static/external/css/fineuploader/fine-uploader.css'/>
    <link rel='stylesheet' href='//cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.0.1/fullcalendar.min.css'/>
    <link rel='stylesheet' href='//cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.0.1/fullcalendar.print.css' media='print'/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/5.0.0/highcharts.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/fineuploader/jquery.fine-uploader.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.0.1/fullcalendar.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/randomcolor/0.4.4/randomColor.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/classie.js"></script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><a href="/access/landing.htm"><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/>Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                    <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                        <input type="submit" value="LOG OUT" class="logout_btn"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                <sec:authentication var="validated" property="principal.accountValidated"/>
                <c:choose>
                    <c:when test="${!validated}">
                        <a class="top-account-bar-text user-email" href="/access/userprofilepreference/i.htm">
                            <sec:authentication property="principal.username" />
                            <span class="notification-counter">1</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text user-email" href="#">
                            <sec:authentication property="principal.username" />
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<header>
</header>
<div class="main clearfix">
    <div class="down_form" style="width: 90%">
        Business Name: ${businessLandingForm.bizName}
    </div>
    <div class="rightside-list-holder full-list-holder"
            style="overflow-y: hidden; height: 800px; margin-left: 0; padding-left: 0">
        <div class="down_form" style="width: 96%;">
            <a href="/business/addStore.htm">Click Add new store</a>

            <div class="down_form" style="width: 96%;">
                <c:choose>
                    <c:when test="${!empty businessLandingForm.bizStores}">
                        <table width="80%" style="margin: 0 4px 0 4px">
                            <thead>
                            <tr>
                                <th></th>
                                <th width="440px;">Store Location</th>
                                <th width="260px;">Queue Name</th>
                                <th width="260px;">Since</th>
                            </tr>
                            </thead>
                            <c:forEach items="${businessLandingForm.bizStores}" var="store" varStatus="status">
                                <tr>
                                    <td style="padding: 10px; border: 1px solid #ccc" rowspan="0">${status.count}&nbsp;</td>
                                    <td style="padding: 10px; border: 1px solid #ccc;">
                                        <a href="/business/bm/store/${store.id}.htm">${store.address}</a>
                                    </td>
                                    <td style="padding: 10px; border: 1px solid #ccc;">
                                        ${store.displayName}
                                    </td>
                                    <td style="padding: 10px; border: 1px solid #ccc;">
                                        <fmt:formatDate pattern="MMMM dd, yyyy" value="${store.created}"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </c:when>
                    <c:otherwise>
                        There are no new business to approve.
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <div class="footer-tooth clearfix">
        <div class="footer-tooth-middle"></div>
        <div class="footer-tooth-right"></div>
    </div>
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2017 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
</html>
