<%@ page import="com.noqapp.domain.types.ValidateStatusEnum" %>
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

            <div class="admin-main">
                <div class="admin-title">
                    <h2>Advertisement Approval Process</h2>
                </div>

                <div class="admin-content">
                    <div class="add-new">
                        <ul class="list-form">
                            <form:form modelAttribute="advertisementForm">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="title" cssErrorClass="lb_error">Title</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="title" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="shortDescription" cssErrorClass="lb_error">Short Description</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="shortDescription" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="advertisementType" cssErrorClass="lb_error">Advertisement Type</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:select path="advertisementType" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                        <form:options items="${advertisementForm.advertisementTypes}" itemValue="name" itemLabel="description" disabled="true" />
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
                                        <form:options items="${advertisementForm.advertisementDisplays}" itemValue="name" itemLabel="description" disabled="true" />
                                    </form:select>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="advertisementViewerType" cssErrorClass="lb_error">Advertisement Viewer Type</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:select path="advertisementViewerType" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                            <form:options items="${advertisementForm.advertisementViewerTypes}" itemValue="name" itemLabel="description" disabled="true" />
                                        </form:select>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="radius" cssErrorClass="lb_error">Display in Radius of KM</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="radius" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="publishDate" cssErrorClass="lb_error">Publish On Date</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="publishDate" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="endDate" cssErrorClass="lb_error">End Advertisement Date</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="endDate" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>

                            <c:choose>
                            <c:when test="${!empty advertisementForm.termsAndConditions}">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="termsAndConditions" cssErrorClass="lb_error">Terms And Conditions</form:label>
                                </div>
                                <div class="col-fields">
                                    &nbsp;
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <c:forEach items="${advertisementForm.termsAndConditions}" var="termAndCondition" varStatus="status">
                            <li>
                                <div class="col-lable3">
                                    &nbsp;
                                </div>
                                <div class="col-fields">
                                    &bull; ${termAndCondition}
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            </c:forEach>
                            </c:when>
                            <c:otherwise>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="termsAndConditions" cssErrorClass="lb_error">Terms And Conditions</form:label>
                                </div>
                                <div class="col-fields">
                                    <span class="form-field-admin" style="border:none; line-height: 42px;">Without Terms And Condition advertisement could be rejected</span>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            </c:otherwise>
                            </c:choose>
                            </form:form>

                            <li class="mB0">
                                <div class="col-lable3"></div>
                                <div class="col-fields">
                                    <div class="button-btn">
                                        <form:form action="${pageContext.request.contextPath}/emp/advertisement/approval/preview.htm" method="post">
                                            <input type="hidden" name="advertisementId" value="${advertisementForm.advertisementId}"/>
                                            <input type="hidden" name="validateStatus" value="${ValidateStatusEnum.A}"/>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <div class="left-btn">
                                                <input name="approve" class="ladda-button next-btn" value="APPROVE" type="submit" style="font-weight: 500;">
                                            </div>
                                        </form:form>

                                        <form:form action="${pageContext.request.contextPath}/emp/advertisement/approval/preview.htm" method="post">
                                            <input type="hidden" name="advertisementId" value="${advertisementForm.advertisementId}"/>
                                            <input type="hidden" name="validateStatus" value="${ValidateStatusEnum.R}"/>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <div class="right-btn">
                                                <input name="reject" class="ladda-button next-btn" value="REJECT" type="submit" style="font-weight: 500;">
                                            </div>
                                        </form:form>
                                    </div>
                                    <div class="clearFix"></div>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
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
