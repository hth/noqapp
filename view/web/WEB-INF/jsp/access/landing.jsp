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
    <div class="sidebar">
        <div class="sidebar-top-summary">
            <div class="sidebar-top-summary-upper clearfix">
                <h1 id="pendingCountInitial">
                    <a href='${pageContext. request. contextPath}/access/document/pending.htm' class="big-view">
                        ${documentStatsForm.pendingCount}
                    </a>
                </h1>
                <h1 id="pendingCountId"></h1>

                <div class="sts-upper-right">
                <span class="top-summary-textb">
                <c:choose>
                    <c:when test="${documentStatsForm.pendingCount le 1}">Receipt pending</c:when>
                    <c:otherwise>Receipts pending</c:otherwise>
                </c:choose>
                </span>
                    <span class="general-text">
                    Last sync: <span class="timestamp" id="pendingCountSyncedId"></span>
                </span>
                </div>
            </div>
            <div class="sidebar-top-summary-lower clearfix">
                <h1 id="rejectedCountInitial">
                    <a href='${pageContext. request. contextPath}/access/document/rejected.htm' class="big-view-lower">
                        ${documentStatsForm.rejectedCount}
                    </a>
                </h1>
                <h1 id="rejectedCountId"></h1>

                <div class="sts-upper-right">
				<span class="top-summary-textb">
                    <c:choose>
                        <c:when test="${documentStatsForm.rejectedCount le 1}">Receipt rejected</c:when>
                        <c:otherwise>Receipts rejected</c:otherwise>
                    </c:choose>
                </span>
                    <span class="general-text">
                    Last sync: <span class="timestamp" id="rejectedCountSyncedId"></span>
                </span>
                </div>
            </div>
        </div>
        <div class="sidebar-git-datum">
            <div class="gd-title">
                <h1 class="widget-title-text">Upload new receipt</h1>
            </div>
            <div id="fine-uploader-validation" class="upload-text"></div>
        </div>
        <div class="sidebar-indication">
            <div class="si-title">
                <h1 class="widget-title-text">Notifications (${notificationForm.count})</h1>
            </div>
            <div class="si-list-holder" when-scrolled="loadMore()">
                <c:choose>
                    <c:when test="${!empty notificationForm.notifications}">
                        <ul>
                            <c:forEach var="notification" items="${notificationForm.notifications}" varStatus="status">
                                <li class="si-list">
                                    <img class="si-notification-icon" alt="Notification icon" src="${pageContext.request.contextPath}/static/img/notification-icon.png">
                                    <span class="si-general-text">${notification.notificationMessageForDisplay}</span>
                                    <span class="si-date-text"><fmt:formatDate value="${notification.created}" pattern="MMM. dd" /></span>
                                </li>
                            </c:forEach>
                            <li class="si-list" ng-repeat="i in items">
                                <img class="si-notification-icon" alt="Notification icon" src="${pageContext.request.contextPath}/static/img/notification-icon.png">
                                <span class="si-general-text"><a class='rightside-li-middle-text full-li-middle-text' href="{{i.href}}">{{i.message}}</a></span>
                                <span class="si-date-text">{{i.created}}</span>
                            </li>
                        </ul>
                        <p class="si-list-footer si-list-footer-success" ng-show="loading">
                                <%--<img src="${pageContext.request.contextPath}/static/img/notification-loading.gif"/>--%>
                            <em>Loading ...</em>
                        </p>
                        <p class="si-list-footer si-list-footer-error" ng-show="failed">
                            <em>Failed to retrieve data</em>
                        </p>
                    </c:when>
                    <c:otherwise>
                        <p class="si-general-text">There are no Notifications &nbsp;</p>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="si-footer">
                <c:if test="${!empty notificationForm.notifications}">
                    <p class="view-more-text">
                        <a class="view-more-text" ng-href="${pageContext.request.contextPath}/access/notification.htm">View All Notifications</a>
                    </p>
                </c:if>
            </div>
        </div>
        <div class="sidebar-invite">
            <div class="gd-title">
                <h1 class="widget-title-text">Friend Invite</h1>
            </div>
            <div id="inviteTextMessage"></div>
            <form>
                <input type="text" placeholder="Email address of friend here ..." size="20"
                        onfocus="changeInviteText(this, 'focus')"
                        onblur="changeInviteText(this, 'blur')"
                        id="inviteEmailId"/>
            </form>
            <div class="gd-button-holder">
                <button class="gd-button" style="background: #808080;" onclick="submitInvitationForm()" id="sendInvite_bt" disabled="disabled">SEND INVITE</button>
            </div>
            <div id="inviteText" class="si-general-text invite-general-text">Invitation is sent with your name and email address</div>
        </div>
    </div>

    <div id="off_screen">
        <div id="map-canvas"></div>
    </div>

    <div class="rightside-content">
        <div id="tabs" class="nav-list">
            <ul class="nav-block">
                <li><a href="#tab1">OVERVIEW</a></li>
            </ul>
            <div id="tab1" class="ajx-content">
                <div class="rightside-title">
                    <h1 class="rightside-title-text left" id="monthShownId"></h1>
                    <span class="right right_view" style="width: 24%;">
					<input type="button" value="List" class="overview_view toggle_button_left toggle_selected" id="btnList" onclick="toggleListCalendarView(this)">
					<span style="width:1px;background:white;float:left;">&nbsp;</span>
					<input type="button" value="Calendar" class="overview_view toggle_button_right" id="btnCalendar" onclick="toggleListCalendarView(this)">
				</span>
                </div>

                <div id="onLoadReceiptForMonthId">
                    Hello
                </div>
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
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
</html>
