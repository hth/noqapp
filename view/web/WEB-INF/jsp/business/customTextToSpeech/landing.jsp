<%@ page import="com.noqapp.domain.types.TextToSpeechTypeEnum, com.noqapp.domain.types.ActionTypeEnum, java.util.Locale" %>
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
                <div class="admin-content">
                    <div class="store">
                        <h3>Voice Announcement</h3>
                        <div class="space10"></div>
                        <form:form method="POST" action="./landing.htm" modelAttribute="customTextToSpeechForm">
                        <spring:hasBindErrors name="customTextToSpeechForm">
                        <div class="error-box">
                            <div class="error-txt">
                                <ul>
                                    <c:forEach items="${errors.allErrors}" var="message">
                                    <li><spring:message message="${message}" /></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                        <div class="space10"></div>
                        </spring:hasBindErrors>

                        <div class="add-store">
                            <ul class="list-form">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="textToSpeechType" cssErrorClass="lb_error">Customize for</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="textToSpeechType" cssClass="form-field-select single-dropdown"
                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:option value="" label="--- Select ---"/>
                                            <form:options items="${customTextToSpeechForm.textToSpeechTypes}" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="languageTag" cssErrorClass="lb_error">Select Language</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="languageTag" cssClass="form-field-select single-dropdown"
                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:option value="" label="--- Select ---"/>
                                            <form:options items="${customTextToSpeechForm.supportedSpeechLocaleMap}" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="template" cssErrorClass="lb_error">Voice Announcement</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="template" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li class="mB0">
                                    <div class="col-lable3"></div>
                                    <div class="col-fields">
                                        <div class="left-btn">
                                            <input type="submit" value="ADD" class="next-btn" name="add-announcement">
                                        </div>
                                        <div class="right-btn">
                                            <input type="submit" value="CANCEL" class="cancel-btn" name="cancel-announcement">
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>
                        </div>
                        </form:form>
                    </div>
                </div>
            </div>

            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <div class="add-store">
                            <div id="container"></div>
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty customTextToSpeechForm.customTextToSpeech}">
                                        <c:forEach items="${customTextToSpeechForm.customTextToSpeech.textToSpeechTemplates}" var="textToSpeechTemplate" varStatus="status">
                                        <h4>Custom Message for ${TextToSpeechTypeEnum.valueOf(textToSpeechTemplate.key).description}</h4>
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th nowrap>Language</th>
                                                <th nowrap>Text</th>
                                                <th nowrap></th>
                                            </tr>
                                            <c:forEach items="${textToSpeechTemplate.value}" var="textToSpeech" varStatus="status">
                                            <tr>
                                                <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                                <td><span style="display:block; font-size:13px;">${Locale.forLanguageTag(textToSpeech.languageTag).displayCountry} (${Locale.forLanguageTag(textToSpeech.languageTag).displayLanguage})&nbsp;</span></td>
                                                <td><span style="display:block; font-size:13px;">${textToSpeech.template}&nbsp;</span></td>
                                                <td>
                                                    <form:form action="${pageContext.request.contextPath}/business/customTextToSpeech/action.htm" modelAttribute="customTextToSpeechForm" method="post">
                                                        <form:hidden path="actionType" value="${ActionTypeEnum.REMOVE}" />
                                                        <form:hidden path="textToSpeechType" value="${textToSpeechTemplate.key}" />
                                                        <form:hidden path="languageTag" value="${textToSpeech.languageTag}" />
                                                        <input class="cancel-btn" style="margin: 0;" value="Delete" type="submit" name="action-announcement">
                                                    </form:form>
                                                </td>
                                            </tr>
                                            </c:forEach>
                                        </table>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>System is set for default announcement as voice announcement is not configured.</p>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
