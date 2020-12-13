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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a>
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
                <form:form modelAttribute="questionnaire">
                    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                    <div class="admin-title">
                        <h2>Create New Survey</h2>
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
                                        <form:label path="locale" cssErrorClass="lb_error">Language</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="locale" cssClass="form-field-select single-dropdown"
                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:option value="" label="--- Select ---"/>
                                            <c:forEach items="${questionnaire.localeWithQuestions}" var="localeWithQuestions" varStatus="status">
                                                <option value="${localeWithQuestions.key}">${localeWithQuestions.key.displayLanguage}</option>
                                            </c:forEach>
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="question" cssErrorClass="lb_error">Question</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="question" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="false"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="questionType" cssErrorClass="lb_error">Response Format</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="questionType" cssClass="form-field-select single-dropdown"
                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:option value="" label="--- Select ---"/>
                                            <form:options items="${questionnaire.questionTypes}" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>

                            <div class="col-lable3"></div>

                            <div class="col-fields">
                                <div class="button-btn">
                                    <button name="_eventId_add" class="ladda-button next-btn" style="width:32%; float: left">Add More</button>
                                    <button name="_eventId_submit" class="ladda-button cancel-btn" style="width:32%; float: left; margin-left:2%">Review</button>
                                    <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:32%; float: right">Cancel</button>
                                </div>
                                <div class="clearFix"></div>
                            </div>
                            <div class="clearFix"></div>
                        </div>
                    </div>

                    <div class="add-store">
                        <div class="admin-title">
                            <h2>Survey</h2>
                        </div>
                        <c:forEach items="${questionnaire.localeWithQuestions}" var="localeWithQuestions" varStatus="status">
                            <div class="admin-title">
                                <h3>Language: ${localeWithQuestions.key.displayLanguage}</h3>
                            </div>
                            <c:choose>
                                <c:when test="${fn:length(localeWithQuestions.value) > 0}">
                                <div class="store-table">
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <th>&nbsp;</th>
                                            <th nowrap>Survey Questions</th>
                                            <th nowrap>Response Format</th>
                                        </tr>
                                        <c:forEach items="${localeWithQuestions.value}" var="localeWithQuestion" varStatus="status">
                                        <tr>
                                            <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                            <td nowrap>
                                                <span style="display:block; font-size:13px;">${localeWithQuestion.question}</span>
                                            </td>
                                            <td nowrap>
                                                <span style="display:block; font-size:13px;"><b>${localeWithQuestion.questionType.description}</b></span>
                                            </td>
                                        </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                                </c:when>
                                <c:otherwise>
                                <div class="alert-info">
                                    <p>There are no survey questions to show.</p>
                                </div>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </div>
                </form:form>
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
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>

