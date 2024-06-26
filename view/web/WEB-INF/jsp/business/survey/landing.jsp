<%@ page import="com.noqapp.domain.types.PublishStatusEnum" %>
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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>

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
            <!-- Add New Supervisor -->
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <h3>Survey</h3>

                        <div class="add-store">
                            <div class="addbtn-store" style="padding-bottom: 10px;">
                                <a href="/business/survey/add" class="add-btn" target="_blank">Add New Survey</a>
                                <a href="/business/survey/dashboard" class="add-btn" target="_blank">Dashboard</a>
                            </div>
                            <div id="container"></div>
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty questionnaireForm.questionnaires}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th nowrap>Title</th>
                                                <th nowrap>Status</th>
                                                <th nowrap>Next Action</th>
                                            </tr>
                                            <c:forEach items="${questionnaireForm.questionnaires}" var="questionnaire" varStatus="status">
                                            <tr>
                                                <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                                <td nowrap>
                                                    <c:choose>
                                                    <c:when test="${fn:length(questionnaire.title) == 0}">
                                                        <a href="/business/survey/questionnaireDetail/${questionnaire.id}"><span style="display:block; font-size:13px;">No Title</span></a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="/business/survey/questionnaireDetail/${questionnaire.id}"><span style="display:block; font-size:13px;">${questionnaire.title}</span></a>
                                                    </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <span style="display:block; font-size:13px;">${questionnaire.publishStatus.description}</span>
                                                    <c:choose>
                                                        <c:when test="${questionnaire.publishStatus == ValidateStatusEnum.A}">
                                                            <span style="display:block; font-size:13px; font-weight: bold">Published: <fmt:formatDate pattern="MMM dd, yyyy hh:mm a" value="${questionnaire.publishDate}"/></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="display:block; font-size:13px;">Created: <fmt:formatDate pattern="MMM dd, yyyy hh:mm a" value="${questionnaire.created}"/></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                    <c:when test="${questionnaire.publishStatus == PublishStatusEnum.I}">
                                                        <a href="/business/survey/${questionnaire.id}/${PublishStatusEnum.I}"><span style="display:block; font-size:13px;">Edit</span></a>
                                                    </c:when>
                                                    <c:when test="${questionnaire.publishStatus == PublishStatusEnum.P}">
                                                        <a href="/business/survey/${questionnaire.id}/${PublishStatusEnum.A}"><span style="display:block; font-size:13px;">Approve</span></a>
                                                        <a href="/business/survey/${questionnaire.id}/${PublishStatusEnum.R}"><span style="display:block; font-size:13px;">Reject</span></a>
                                                    </c:when>
                                                    <c:when test="${questionnaire.publishStatus == PublishStatusEnum.A}">
                                                        <a href="/business/survey/${questionnaire.id}/${PublishStatusEnum.U}"><span style="display:block; font-size:13px;">Un-published</span></a>
                                                    </c:when>
                                                    <c:when test="${questionnaire.publishStatus == PublishStatusEnum.R}">
                                                        <a href="/business/survey/${questionnaire.id}/${PublishStatusEnum.D}"><span style="display:block; font-size:13px;">Delete</span></a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="display:block; font-size:13px;">Not Active</span>
                                                    </c:otherwise>
                                                    </c:choose>
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
            fetch('/business/survey/live/rating').then(function (response) {
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
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
