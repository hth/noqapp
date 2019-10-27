<%@ include file="../../../jsp/include.jsp" %>
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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue Inc"/></a>
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
                        <h3>Dashboard</h3>
                        <div class="add-store">
                            <c:choose>
                            <c:when test="${!empty surveyGroupedValues}">
                                <div class="store-table">
                                    <p align="right"><span style="display:block; font-size:13px;">Last 30 days</span></p>
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <thead>
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th nowrap>Location</th>
                                                <th nowrap>Survey Count</th>
                                                <th nowrap>Overall Rating</th>
                                                <th colspan="3" nowrap>Reviews</th>
                                            </tr>
                                            <tr>
                                                <th style="border-top: 0"></th>
                                                <th style="border-top: 0"></th>
                                                <th style="border-top: 0"></th>
                                                <th style="border-top: 0"></th>
                                                <th nowrap>Positive</th>
                                                <th nowrap>Negative</th>
                                                <th nowrap>Missing</th>
                                            </tr>
                                        </thead>
                                        <c:forEach items="${surveyGroupedValues}" var="surveyGroupedValue" varStatus="status">
                                        <tr>
                                            <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                            <td nowrap>
                                                <c:choose>
                                                    <c:when test="${empty surveyGroupedValue.town}">
                                                        <span style="display:block; font-size:13px;"><b>${surveyGroupedValue.area}</b></span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="display:block; font-size:13px;"><b>${surveyGroupedValue.area}, ${surveyGroupedValue.town}</b></span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td nowrap><span style="display:block; font-size:13px;"><b>${surveyGroupedValue.numberOfSurvey}</b></span></td>
                                            <td nowrap><span style="display:block; font-size:13px;"><b><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${surveyGroupedValue.summationOverallRating}" /></b></span></td>
                                            <td nowrap><span style="display:block; font-size:13px;"><b>${surveyGroupedValue.sumOfPositiveSentiments}</b></span></td>
                                            <td nowrap><span style="display:block; font-size:13px;"><b>${surveyGroupedValue.sumOfNegativeSentiments}</b></span></td>
                                            <td nowrap><span style="display:block; font-size:13px;"><b>${surveyGroupedValue.numberOfSurvey - surveyGroupedValue.sumOfPositiveSentiments - surveyGroupedValue.sumOfNegativeSentiments}</b></span></td>
                                        </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert-info">
                                    <p>There is no data available.</p>
                                </div>
                            </c:otherwise>
                            </c:choose>
                            <div class="col-lable3"></div>
                            <div class="clearFix"></div>
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
                    <div class="f-left">&copy; 2019 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/ladda.min.js"></script>
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
</html>
