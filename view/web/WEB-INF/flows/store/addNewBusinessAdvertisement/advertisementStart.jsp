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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/external/jquery/css/jquery-ui.css" />
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
            <sec:authorize access="hasAnyRole('ROLE_M_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')">
            <div class="admin-main">
                <form:form modelAttribute="advertisementForm">
                    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                    <div class="admin-title">
                        <c:choose>
                            <c:when test="${!empty advertisementForm.advertisementId}">
                                <h2>Edit Advertisement</h2>
                            </c:when>
                            <c:otherwise>
                                <h2>Add Advertisement</h2>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="error-box">
                        <div class="error-txt">
                            <c:if test="${!empty flowRequestContext.messageContext.allMessages}">
                                <ul>
                                    <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
                                    <li>${message.text}</li>
                                    </c:forEach>
                                </ul>
                            </c:if>
                        </div>
                    </div>

                    <div class="admin-content">
                        <div class="add-new">
                            <ul class="list-form">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="title" cssErrorClass="lb_error">Title</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="title" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="shortDescription" cssErrorClass="lb_error">Short Description</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="shortDescription" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="advertisementType" cssErrorClass="lb_error">Advertisement Type</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="advertisementType" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:options items="${advertisementForm.advertisementTypes}" itemValue="name" itemLabel="description" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="advertisementDisplay" cssErrorClass="lb_error">Show Advertisement At</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="advertisementDisplay" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:options items="${advertisementForm.advertisementDisplays}" itemValue="name" itemLabel="description" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="radius" cssErrorClass="lb_error">Display in Radius of KM</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="radius" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="publishDate" cssErrorClass="lb_error">Publish On Date</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="publishDate" cssClass="datepicker form-field-admin" cssErrorClass="datepicker form-field-admin error-field" placeholder="YYYY-MM-DD"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="endDate" cssErrorClass="lb_error">End Advertisement Date</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="endDate" cssClass="datepicker form-field-admin" cssErrorClass="datepicker form-field-admin error-field" placeholder="YYYY-MM-DD"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>

                            <div class="col-lable3"></div>

                            <c:choose>
                            <c:when test="${!empty advertisementForm.advertisementId}">
                            <div class="col-fields">
                                    <%--<div class="first-btn">--%>
                                    <%--<input name="_eventId_submit" class="next-btn" value="NEXT" type="submit">--%>
                                    <%--</div>--%>
                                    <%--<div class="center-btn">--%>
                                    <%--<input name="_eventId_delete" class="cancel-btn" value="DELETE" type="submit">--%>
                                    <%--</div>--%>
                                    <%--<div class="last-btn">--%>
                                    <%--<input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">--%>
                                    <%--</div>--%>

                                <div class="button-btn">
                                    <button name="_eventId_submit" class="ladda-button next-btn" style="width:32%; float: left">Next</button>
                                    <c:choose>
                                        <c:when test="${advertisementForm.active}">
                                            <button name="_eventId_offline" class="ladda-button cancel-btn" style="width:32%; float: left; margin-left:2%">Go Offline</button>
                                        </c:when>
                                        <c:otherwise>
                                            <button name="_eventId_online" class="ladda-button cancel-btn" style="width:32%; float: left; margin-left:2%">Go Online</button>
                                        </c:otherwise>
                                    </c:choose>
                                    <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:32%; float: right">Cancel</button>
                                </div>
                                <div class="clearFix"></div>
                            </div>
                            </c:when>
                            <c:otherwise>
                            <div class="col-fields">
                                    <%--<div class="left-btn">--%>
                                    <%--<input name="_eventId_submit" class="next-btn" value="NEXT" type="submit">--%>
                                    <%--</div>--%>
                                    <%--<div class="right-btn">--%>
                                    <%--<input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">--%>
                                    <%--</div>--%>
                                <div class="button-btn">
                                    <button name="_eventId_submit" class="ladda-button next-btn" style="width:48%; float: left">Next</button>
                                    <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                </div>
                                <div class="clearFix"></div>
                            </div>
                            </c:otherwise>
                            </c:choose>
                            <div class="clearFix"></div>
                        </div>
                    </div>
                </form:form>
            </div>
            <!-- Add New Supervisor -->
            </sec:authorize>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/jquery/js/jquery-ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/ladda/js/ladda.min.js"></script>
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
<script>
    $(function () {
        $(".datepicker").datepicker({
            dateFormat: 'yy-mm-dd'
        });
    });
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
