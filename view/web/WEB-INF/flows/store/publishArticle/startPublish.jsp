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
            <sec:authorize access="hasAnyRole('ROLE_S_MANAGER')">
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <h3>Publish Article</h3>
                        <form:form modelAttribute="publishArticleForm" enctype="multipart/form-data">
                            <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

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

                            <div class="add-new">
                                <ul class="list-form">
                                    <%--<li>--%>
                                        <%--<div class="col-lable3" style="padding-top: 30px;">--%>
                                            <%--<form:label path="file" cssErrorClass="lb_error">Select Article Image</form:label>--%>
                                        <%--</div>--%>
                                        <%--<div class="col-fields">--%>
                                            <%--<form:input class="next-btn" type="file" path="file" id="file"/>--%>
                                        <%--</div>--%>
                                        <%--<div class="clearFix"></div>--%>
                                    <%--</li>--%>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="articleTitle" cssErrorClass="lb_error">Title</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="articleTitle" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                </ul>

                                <form:textarea path="article" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" />
                                <span style="display:block; font-size:13px; padding-top: 5px; text-align: right;">minimum 1000 characters</span>
                            </div>

                            <div class="col-lable3"></div>
                            <div class="col-fields">
                                <div class="button-btn">
                                    <button name="_eventId_preview" class="ladda-button next-btn" style="width:48%; float: left">Preview</button>
                                    <button name="_eventId_cancelPublish" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                </div>
                                <div class="clearFix"></div>
                            </div>
                        </form:form>
                    </div>
                    <div class="clearFix"></div>
                </div>
            </div>
            <!-- Add New Supervisor -->
            </sec:authorize>
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
                    <div class="f-left">&copy; 2020 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/editor/ck/js/ckeditor.js"></script>
<script>
    CKEDITOR.replace('article');
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
