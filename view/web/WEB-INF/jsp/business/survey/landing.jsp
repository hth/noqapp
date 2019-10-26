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

    <style type="text/css" media="screen">
        #container {
            min-width: 310px;
            height: 200px;
            margin: 0 auto;
        }
    </style>
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
                        <h3>Survey</h3>

                        <div class="add-store">
                            <div class="addbtn-store" style="padding-bottom: 10px;">
                                <a href="/business/survey/add.htm" class="add-btn" target="_blank">Add New Survey</a>
                                <a href="/business/survey/dashboard.htm" class="add-btn" target="_blank">Dashboard</a>
                            </div>
                            <div id="container"></div>
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty questionnaireForm.questionnaires}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th nowrap>Survey</th>
                                                <th nowrap>Published</th>
                                            </tr>
                                            <c:forEach items="${questionnaireForm.questionnaires}" var="questionnaire" varStatus="status">
                                            <tr>
                                                <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                                <td nowrap>
                                                    <c:choose>
                                                        <c:when test="${fn:length(questionnaire.firstEntry) > 100}">
                                                            <a href="/business/survey/questionnaireDetail/${questionnaire.id}.htm" target="_blank"><span style="display:block; font-size:13px;">${fn:substring(questionnaire.firstEntry, 0, 100)}...</span></a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="/business/survey/questionnaireDetail/${questionnaire.id}.htm" target="_blank"><span style="display:block; font-size:13px;">${questionnaire.firstEntry}</span></a>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <span style="display:block; font-size:13px;"><fmt:formatDate pattern="MMMM dd, yyyy hh:mm a" value="${questionnaire.created}"/></span>
                                                </td>
                                            </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>There are no survey listed.</p>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>

<script src="https://code.highcharts.com/highcharts.js"></script>
<script type="application/javascript">
    // Chart
    var options = {
        chart: {
            type: 'spline',
            events: {
                load: getData
            }
        },
        time: {
            useUTC: false
        },
        credits: {
            enabled: false
        },
        title: {
            text: 'Live Survey Overall Rating'
        },
        xAxis: {
            type: 'datetime'
        },
        yAxis: {
            title: {
                text: 'Rating &amp; Review Sentiments'
            }
        },
        tooltip: {
            headerFormat: '',
            pointFormat: '<b>{point.location} {point.y}</b>'
        },
        legend: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        series: [{
            name: 'Live Survey Overall Rating',
            data: [],
            lineWidth: 4
        }]
    };
    var chart = Highcharts.chart('container', options)

    // Data
    function getData() {
        setInterval(function () {
            fetch('/business/survey/live/rating.htm').then(function (response) {
                return response.json()
            }).then(function (data) {
                console.log(data);
                chart.series[0].addPoint({x: data.d, y: Number(data.v), location: data.l});
                if (data.sc.lenght !== 0) {
                    chart.series[0].options.color = data.sc;
                    chart.series[0].update(chart.series[0].options);
                }
            })
        }, 12000)
    }
</script>
</html>
