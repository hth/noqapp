<%@ include file="include.jsp"%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
    <meta content='width=device-width, initial-scale=1' name='viewport' />

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'  />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/intl-tel-input/css/intlTelInput.css">

    <script defer type="text/javascript" src="//code.getmdl.io/1.1.3/material.min.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

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
            <div class="logo-left"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue Inc"/></div>
            <div class="top-menu-right">
                <span class="help-btn"><a href="${pageContext.request.contextPath}/open/login.htm">Sign In</a></span>
                <span class="become-btn"><a href="${pageContext.request.contextPath}/open/register.htm">Merchant Register</a></span>
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
                    <form:form id="search-form" method="post" modelAttribute="searchForm" action="/open/search.htm" autocomplete="off">
                        <form:hidden path="geoIP.geoHash" />
                        <form:input path="search" cssClass="form-field" required="required" cssErrorClass="form-field error" placeholder="" autofocus="autofocus"/>
                        <img src="${pageContext.request.contextPath}/static2/internal/img/location.png" alt="Location" style="float: left;"/>
                        <c:choose>
                        <c:when test="${!empty searchForm.geoIP.cityName}">
                        <span class="left-remember">${searchForm.geoIP.cityName}</span>
                        </c:when>
                        <c:otherwise>
                        <span class="left-remember">Unknown</span>
                        </c:otherwise>
                        </c:choose>
                        <div class="button-btn">
                            <button class="ladda-button form-btn" style="width:100%">Search</button>
                        </div>
                    </form:form>
                </div>
            </div>

            <div class="form-style">
                <div class="store-table">
                    <c:if test="${!empty searchForm.search}">
                    <c:choose>
                    <c:when test="${!empty searchResult}">
                    <table width="100%" border="0" cellspacing="0" cellpadding="0" style="border: 0;">
                        <c:forEach items="${searchResult}" var="elasticBizStoreSearchSource" varStatus="status">
                        <tr>
                            <td nowrap style="border: 0px;">
                                <a href="../${elasticBizStoreSearchSource.bizStoreSearchElastic.codeQR}/q.htm" target="_blank">${elasticBizStoreSearchSource.bizStoreSearchElastic.displayName}</a>
                                <c:choose>
                                <c:when test="${!empty elasticBizStoreSearchSource.bizStoreSearchElastic.bizCategoryName}">
                                <span style="display:block; font-size:13px;">
                                    ${elasticBizStoreSearchSource.bizStoreSearchElastic.businessType.description}, ${elasticBizStoreSearchSource.bizStoreSearchElastic.bizCategoryName}, ${elasticBizStoreSearchSource.bizStoreSearchElastic.area} ${elasticBizStoreSearchSource.bizStoreSearchElastic.town};
                                        <a href="https://noqapp.com/b/s${elasticBizStoreSearchSource.bizStoreSearchElastic.webLocation}.html" target="_blank">Join walk-in queue</a>
                                </span>
                                </c:when>
                                <c:otherwise>
                                <span style="display:block; font-size:13px;">
                                    ${elasticBizStoreSearchSource.bizStoreSearchElastic.businessType.description}, ${elasticBizStoreSearchSource.bizStoreSearchElastic.area} ${elasticBizStoreSearchSource.bizStoreSearchElastic.town};
                                        <a href="https://noqapp.com/b/s${elasticBizStoreSearchSource.bizStoreSearchElastic.webLocation}.html" target="_blank">Join walk-in queue</a>
                                </span>
                                </c:otherwise>
                                </c:choose>
                                <span style="display:block; font-size:13px;">${elasticBizStoreSearchSource.bizStoreSearchElastic.businessName}</span>
                            </td>
                        </tr>
                        </c:forEach>
                    </table>
                    </c:when>
                    <c:otherwise>
                        <div class="alert-info">
                            No search result found. Modify query and search again.
                        </div>
                    </c:otherwise>
                    </c:choose>
                    </c:if>
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
                        &copy; 2020 NoQueue Inc. | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a><br/>
                        All other trademarks and logos belong to their respective owners. (<spring:eval expression="@environmentProperty.getProperty('build.version')" />.<spring:eval expression="@environmentProperty.getProperty('server')" />)
                    </div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>



</body>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/ladda.min.js"></script>
<script type="text/javascript">
    // Bind normal buttons
    Ladda.bind('.button-btn button', {timeout: 12000});

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
</html>
