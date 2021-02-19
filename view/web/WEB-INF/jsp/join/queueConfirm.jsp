<%@ include file="../include.jsp"%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
    <meta content='width=device-width, initial-scale=1' name='viewport' />

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/intl-tel-input/css/intlTelInput.css" type='text/css'>

    <script defer type="text/javascript" src="//code.getmdl.io/1.1.3/material.min.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.20.1/moment-with-locales.min.js"></script>

    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-101872684-1"></script>
    <script>
        window.dataLayer = window.dataLayer || [];
        function gtag(){dataLayer.push(arguments);}
        gtag('js', new Date());

        gtag('config', 'UA-101872684-1');
    </script>
</head>

<body>


<div class="main-warp">
    <!-- header -->
    <div class="header">
        <div class="warp-inner">
            <div class="logo-left"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></div>
            <div class="top-menu-right">
                <span class="help-btn"><a href="${pageContext.request.contextPath}/open/login.htm">Sign In</a></span>
                <span class="become-btn"><a href="${pageContext.request.contextPath}/open/register.htm">Business Register</a></span>
            </div>

            <div class="clearFix"></div>
        </div>
    </div>
    <!-- header end -->

    <!-- content -->
    <div class="content">
        <div class="warp-inner">
            <!-- login-box -->
            <div class="login-box">
                <div class="form-style">
                    <form id="sign-in-form" action="">
                        <div class="qr-data">
                            <div class="qr-address">
                                <h3>${webJoinQueue.rootMap.get("displayName")}</h3>
                                <p>${webJoinQueue.rootMap.get("phone")}</p>
                            </div>
                            <div class="qr-queue">
                                <p style="margin: 5px 0"><strong>${webJoinQueue.rootMap.get("dayOfWeek")} Hours: </strong> ${webJoinQueue.rootMap.get("startHour")} - ${webJoinQueue.rootMap.get("endHour")}</p>

                                <br/>
                                <p style="margin: 5px 0"><strong>Queue Status: </strong>${webJoinQueue.rootMap.get("queueStatus")}</p>
                                <p style="margin: 5px 0"><strong>Currently Serving: </strong>${webJoinQueue.rootMap.get("currentlyServing")}</p>
                                <p style="margin: 5px 0"><strong>People in Queue: </strong>${webJoinQueue.rootMap.get("peopleInQueue")}</p>
                            </div>

                            <div class="qr-queue">
                                <c:choose>
                                    <c:when test="${!empty webJoinQueue.rootMap.get('registration')}">
                                        <h4>Failed to generate token. You are not a registered user. </h4>
                                        <h4>${webJoinQueue.rootMap.get("displayName")} only accepts registered users. <a href="/open/register.htm" style="font-size: large;">Click here to register</a></h4>
                                        <br/>
                                        We are on mobile too. Please download NoQueue from Google Play.
                                        <div class="download-app-icon">
                                            <p>Get NoQueue</p>
                                            <div>
                                                    <%--<a href="https://itunes.apple.com/us/app/noqapp/id1237327532?ls=1&mt=8">--%>
                                                    <%--<img src="${parentHost}/static2/internal/img/apple-store.png"/>--%>
                                                    <%--</a>--%>
                                                <a href="https://play.google.com/store/apps/details?id=com.noqapp.android.client">
                                                    <img src="${parentHost}/static2/internal/img/google-play.png"/>
                                                </a>
                                            </div>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <h3>You are confirmed</h3>
                                        <p style="margin: 5px 0"><strong>Your position in Queue: </strong>${webJoinQueue.rootMap.get("token")}</p>
                                        <p style="margin: 5px 0"><strong>Expected Time of Service: </strong><span id="showTime"></span></p>
                                        <p style="margin: 5px 0"><strong>Location: </strong>${webJoinQueue.rootMap.get("storeAddress")}</p>

                                        <br/>
                                        <p>Please plan your arrival on time.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- login-box -->

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
                    <div class="f-left">
                        &copy; 2021 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a><br/>
                        All other trademarks and logos belong to their respective owners. (<spring:eval expression="@environmentProperty.getProperty('build.version')" />.<spring:eval expression="@environmentProperty.getProperty('server')" />)
                    </div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>
<script>
    <c:choose>
        <c:when test="${!empty webJoinQueue.rootMap.get('expectedServiceTime')}">
            var c = moment.parseZone('${webJoinQueue.rootMap.get("expectedServiceTime")}').format("hh:mm A");
            $("#showTime").text(c);
        </c:when>
        <c:otherwise>
            $("#showTime").text("N/A");
        </c:otherwise>
    </c:choose>
</script>
</body>
</html>
