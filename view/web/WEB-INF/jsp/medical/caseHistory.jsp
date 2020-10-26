<%@ page import="com.noqapp.domain.types.BusinessTypeEnum" %>
<%@ include file="../include.jsp" %>
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
                        <h3>${medicalRecordForm.patientName}</h3>

                        <div class="add-store">
                            <div class="details-box" style="padding: 10px 0 10px 0; text-align: center; width: 100%; float: none;">
                                <span style="display:block; font-size:13px;">${medicalRecordForm.gender} ${medicalRecordForm.age}</span>

                                <c:if test="${!empty medicalRecordForm.guardianName}">
                                <span style="display:block; font-size:13px;">Guardian Name: ${medicalRecordForm.guardianName}, Phone: ${medicalRecordForm.guardianPhoneFormatted}</span>
                                </c:if>
                            </div>
                        </div>
                    </div>

                    <form:form method="post" action="${pageContext.request.contextPath}/medical/record/add.htm" modelAttribute="medicalRecordForm">
                    <div class="add-new">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="hidden" name="token" value="${medicalRecordForm.token}"/>
                        <input type="hidden" name="businessType" value="${medicalRecordForm.businessType}"/>
                        <input type="hidden" name="queueUserId" value="${medicalRecordForm.queueUserId}"/>
                        <input type="hidden" name="codeQR" value="${medicalRecordForm.codeQR}"/>

                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="chiefComplain" cssErrorClass="lb_error">Chief Complain</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:textarea path="chiefComplain" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                </div>
                                <div class="col-fields">
                                    <c:if test="${!empty historicalMedicalRecordForms}">
                                        <span style="display:block; font-size:13px;">
                                        <c:forEach items="${historicalMedicalRecordForms}" var="medicalHistory" varStatus="status">
                                        ${medicalHistory.chiefComplain},
                                        </c:forEach>
                                        </span>
                                    </c:if>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="pastHistory" cssErrorClass="lb_error">Past History</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:textarea path="pastHistory" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                </div>
                                <div class="col-fields">
                                    <c:if test="${!empty historicalMedicalRecordForms}">
                                        <span style="display:block; font-size:13px;">
                                        <c:forEach items="${historicalMedicalRecordForms}" var="medicalHistory" varStatus="status">
                                            ${medicalHistory.pastHistory},
                                        </c:forEach>
                                        </span>
                                    </c:if>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="familyHistory" cssErrorClass="lb_error">Family History</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:textarea path="familyHistory" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                </div>
                                <div class="col-fields">
                                    <c:if test="${!empty historicalMedicalRecordForms}">
                                        <span style="display:block; font-size:13px;">
                                        <c:forEach items="${historicalMedicalRecordForms}" var="medicalHistory" varStatus="status">
                                            ${medicalHistory.familyHistory},
                                        </c:forEach>
                                        </span>
                                    </c:if>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="knownAllergies" cssErrorClass="lb_error">Known Allergies</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:textarea path="knownAllergies" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                </div>
                                <div class="col-fields">
                                    <c:if test="${!empty historicalMedicalRecordForms}">
                                        <span style="display:block; font-size:13px;">
                                        <c:forEach items="${historicalMedicalRecordForms}" var="medicalHistory" varStatus="status">
                                            ${medicalHistory.knownAllergies},
                                        </c:forEach>
                                        </span>
                                    </c:if>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="medicalPhysical.pulse" cssErrorClass="lb_error">Pluse</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="medicalPhysical.pulse" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="medicalPhysical.bloodPressure" cssErrorClass="lb_error">Blood Pressure</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="medicalPhysical.bloodPressure" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="medicalPhysical.weight" cssErrorClass="lb_error">Weight</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="medicalPhysical.weight" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                        <c:forEach items="${medicalRecordForm.medicalPhysicalHistoricals}" var="medicalPhysicalHistoricals" varStatus="status">
                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="medicalPhysicalHistoricals[${status.index}].pluse" cssErrorClass="lb_error">Pluse</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="medicalPhysicalHistoricals[${status.index}].pluse" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="medicalPhysicalHistoricals[${status.index}].bloodPressure" cssErrorClass="lb_error">Blood Pressure</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="medicalPhysicalHistoricals[${status.index}].bloodPressure" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="medicalPhysicalHistoricals[${status.index}].weight" cssErrorClass="lb_error">Weight</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="medicalPhysicalHistoricals[${status.index}].weight" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                        </c:forEach>
                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="clinicalFinding" cssErrorClass="lb_error">Clinical Finding</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:textarea path="clinicalFinding" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="provisionalDifferentialDiagnosis" cssErrorClass="lb_error">Provisional / Differential Diagnosis</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:textarea path="provisionalDifferentialDiagnosis" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                    </div>

                    <div class="col-lable3"></div>
                    <div class="col-fields">
                            <%--<div class="left-btn">--%>
                            <%--<input name="_eventId_submit" class="next-btn" value="NEXT" type="submit">--%>
                            <%--</div>--%>
                            <%--<div class="right-btn">--%>
                            <%--<input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">--%>
                            <%--</div>--%>

                        <div class="button-btn">
                            <button name="mr_add" class="ladda-button next-btn" style="width:48%; float: left">Submit</button>
                            <button name="mr_cancel" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                        </div>
                        <div class="clearFix"></div>
                    </div>
                    <div class="clearFix"></div>

                    </form:form>

                    <br />
                    <c:if test="${!empty historicalMedicalRecordForms}">
                    <c:forEach items="${historicalMedicalRecordForms}" var="medicalHistory" varStatus="status">
                        ${medicalHistory.chiefComplain}; ${medicalHistory.pastHistory}; ${medicalHistory.familyHistory};
                    </c:forEach>
                    </c:if>
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
