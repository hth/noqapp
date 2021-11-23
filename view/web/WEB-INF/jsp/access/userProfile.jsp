<%@ page import="com.noqapp.domain.types.PointActivityEnum" %>
<%@ include file="../../jsp/include.jsp" %>
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
                        <a href="${pageContext.request.contextPath}/access/sos">SOS Message</a>
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
            <!-- Complete profile -->
            <div class="admin-main">
                <!-- File Upload From -->
                <c:if test="${userProfileForm.emailValidated and userProfileForm.email eq pageContext.request.userPrincipal.principal.username}">
                <form:form action="${pageContext.request.contextPath}/access/userProfile/upload" modelAttribute="fileUploadForm"  method="post" enctype="multipart/form-data">
                    <div class="admin-title">
                        <h2>Profile Image</h2>
                    </div>

                    <spring:hasBindErrors name="fileUploadForm">
                    <div class="error-box">
                        <div class="error-txt">
                            <ul>
                                <c:if test="${errors.hasFieldErrors('file')}">
                                <li><form:errors path="file"/></li>
                                </c:if>
                            </ul>
                        </div>
                    </div>
                    </spring:hasBindErrors>

                    <div class="admin-content">
                        <div class="add-new">
                            <ul class="list-form">
                                <li>
                                    <div class="col-lable3" style="padding-top: 30px;">
                                        <form:label path="file" cssErrorClass="lb_error">Select Profile Image</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input class="next-btn" type="file" path="file" id="file"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>

                            <div class="col-lable3"></div>
                            <div class="col-fields">
                                <div class="left-btn">
                                    <input name="upload" class="next-btn" value="UPLOAD PROFILE IMAGE" type="submit">
                                </div>
                                <div class="right-btn">
                                    <input name="cancel_Upload" class="cancel-btn" value="CANCEL" type="submit">
                                </div>
                                <div class="clearFix"></div>
                            </div>
                            <div class="clearFix"></div>
                        </div>
                    </div>
                </form:form>
                </c:if>

                <form:form action="${pageContext.request.contextPath}/access/userProfile/updateProfile" method="post" modelAttribute="userProfileForm">
                <div class="admin-title">
                    <h2>Profile</h2>
                </div>
                <div class="error-box">
                    <div class="error-txt">
                        <spring:hasBindErrors name="userProfileForm">
                        <div class="error-box">
                            <div class="error-txt">
                                <ul>
                                    <c:forEach items="${errors.allErrors}" var="message">
                                    <li><spring:message message="${message}" /></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                        </spring:hasBindErrors>
                    </div>
                </div>
                <div class="admin-content">
                    <div class="add-new">
                        <img src="${userProfileForm.profileImage}"
                             onerror="this.src='/static/internal/img/profile-image-192x192.png'"
                             class="img-profile-circle" />

                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="firstName" cssErrorClass="lb_error">First Name</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="firstName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>

                            <li>
                                <div class="col-lable3">
                                    <form:label path="lastName" cssErrorClass="lb_error">Last Name</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="lastName" cssClass="form-field-admin" cssErorrClass="form-field-admin error-field"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>

                            <li>
                                <div class="col-lable3">
                                    <form:label path="birthday" cssErrorClass="lb_error">Date of Birth</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="birthday" cssClass="datepicker form-field-admin" cssErrorClass="datepicker form-field-admin error-field"
                                                placeholder="Date of Birth YYYY-MM-DD"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>

                            <li>
                                <div class="col-lable3">
                                    <form:label path="gender" cssErrorClass="lb_error">Gender</form:label>
                                </div>

                                <c:choose>
                                <c:when test="${userProfileForm.emailValidated and userProfileForm.email eq pageContext.request.userPrincipal.principal.username}">
                                <div class="col-fields pT10 pB10">
                                    <form:radiobutton path="gender" value="M" label="Male"/> &nbsp; &nbsp;
                                    <form:radiobutton path="gender" value="F" label="Female"/>
                                </div>
                                </c:when>
                                <c:otherwise>
                                <div class="col-fields pT10 pB10">
                                    <form:radiobutton path="gender" value="M" label="Male" disabled="true"/> &nbsp; &nbsp;
                                    <form:radiobutton path="gender" value="F" label="Female" disabled="true"/>
                                </div>
                                </c:otherwise>
                                </c:choose>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="address" cssErrorClass="lb_error">Your Address</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:textarea path="address" cols="" rows="3" cssClass="form-field-admin" cssStyle="background-color: lightgrey" cssErrorClass="form-field-admin error-field" readonly="${userProfileForm.phoneValidated}"/>
                                    <span style="display:block; font-size:14px;">(Address change supported via <a href="https://play.google.com/store/apps/details?id=com.noqapp.android.client&hl=en" target="_blank" style="color: #0000FF;">NoQueue App</a>)</span>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <%--<c:if test="${!empty registerUser.foundAddresses}">--%>
                            <%--<li>--%>
                                <%--<div class="col-lable3">--%>
                                    <%--<form:label path="foundAddressPlaceId" cssErrorClass="lb_error">Best Matching Addresses</form:label>--%>
                                <%--</div>--%>
                                <%--<div class="col-fields pT10 pB10">--%>
                                    <%--<c:forEach items="${registerUser.foundAddresses}" var="mapElement">--%>
                                        <%--<form:radiobutton path="foundAddressPlaceId" value="${mapElement.key}" label="${mapElement.value.formattedAddress}"--%>
                                                          <%--onclick="handleFoundAddressClick();"/> <br />--%>
                                    <%--</c:forEach>--%>
                                <%--</div>--%>
                                <%--<div class="clearFix"></div>--%>
                            <%--</li>--%>
                            <%--<li>--%>
                                <%--<div class="col-lable3">--%>
                                    <%--<form:label path="selectFoundAddress" cssErrorClass="lb_error">I choose Best Matching Address</form:label>--%>
                                <%--</div>--%>
                                <%--<div id="addressCheckBox" class="col-fields">--%>
                                    <%--<form:checkbox path="selectFoundAddress" cssClass="form-check-box" cssErrorClass="form-check-box error-field" disabled="true"--%>
                                                   <%--onclick="handleFoundAddressCheckboxUncheck()" />--%>
                                <%--</div>--%>
                                <%--<div class="clearFix"></div>--%>
                            <%--</li>--%>
                            <%--</c:if>--%>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="phone" cssErrorClass="lb_error">Your Phone</form:label>
                                </div>
                                <div class="col-fields">
                                    <c:choose>
                                        <c:when test="${userProfileForm.phoneValidated}">
                                            <form:input path="phone" cssClass="form-field-admin" cssStyle="background-color: lightgrey" cssErrorClass="form-field-admin error-field" readonly="${userProfileForm.phoneValidated}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="display:block; font-size:15px; padding-top: 10px;">N/A</span>
                                        </c:otherwise>
                                    </c:choose>

                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="email" cssErrorClass="lb_error">Email Address</form:label>
                                </div>
                                <div class="col-fields">
                                    <c:choose>
                                        <c:when test="${fn:endsWith(userProfileForm.email, '@mail.noqapp.com')}">
                                            <span style="display:block; font-size:15px; padding-top: 10px;">--</span>
                                        </c:when>
                                        <c:otherwise>
                                            <form:input path="email" cssClass="form-field-admin" cssStyle="background-color: lightgrey; text-transform : lowercase;" cssErrorClass="form-field-admin error-field" readonly="${userProfileForm.phoneValidated}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="timeZone" cssErrorClass="lb_error">Time Zone</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="timeZone" cssClass="form-field-admin" cssStyle="background-color: lightgrey" cssErrorClass="form-field-admin error-field" readonly="${userProfileForm.phoneValidated}"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>

                            <c:if test="${!userProfileForm.emailValidated}">
                                <c:choose>
                                <c:when test="${fn:endsWith(userProfileForm.email, '@mail.noqapp.com')}">
                                <li>
                                    <div class="alert-info error-box">
                                        <p>
                                            There is no email address on record. Please add email address. Email address will help in your account recovery.
                                        </p>
                                    </div>
                                </li>
                                </c:when>
                                <c:otherwise>
                                <li>
                                    <div class="alert-info">
                                        <p>
                                            Your email address
                                            <span class="txt-red">${registerUser.email}</span>
                                            has not been validated. Please validated email address to continue business account registration.
                                        </p>
                                        <p>To resend account validation email, <a href="${pageContext.request.contextPath}/access/sendVerificationMail">click here.</a>
                                        </p>
                                    </div>
                                </li>
                                </c:otherwise>
                                </c:choose>
                            </c:if>

                            <c:if test="${userProfileForm.emailValidated and userProfileForm.email eq pageContext.request.userPrincipal.principal.username}">
                            <div class="col-lable3"></div>
                            <div class="col-fields">
                                <div class="left-btn">
                                    <input name="update" class="next-btn" value="UPDATE" type="submit">
                                </div>
                                <%--<div class="right-btn">--%>
                                    <%--<input name="cancel_Update" class="cancel-btn" value="CANCEL" type="submit">--%>
                                <%--</div>--%>
                                <div class="clearFix"></div>
                            </div>
                            <div class="clearFix"></div>
                            </c:if>
                        </ul>
                    </div>
                </div>

                <div class="admin-title">
                    <h2><a id="points">Points Earned</a></h2>
                </div>
                <div class="admin-content">
                    <div class="add-new">
                        <ul class="list-form">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="userPreference.earnedPoint" cssErrorClass="lb_error">Total Points Earned</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="userPreference.earnedPoint" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="reviewPointsEarned" cssErrorClass="lb_error">Points Earned by Review</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="reviewPointsEarned" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="totalInvitePointsEarned" cssErrorClass="lb_error">Point Earned by Invite</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="totalInvitePointsEarned" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="alert-info">
                                    <p style="font-weight: bold">How to earn points?</p>
                                    <p>${PointActivityEnum.REV.point} points per ${PointActivityEnum.REV.description}. Give review and earn points.</p>
                                    <p>${PointActivityEnum.INV.point} points per ${PointActivityEnum.INV.description}. Invite friends and earn points. Share your invitee code to earn point.</p>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
                </form:form>

                <c:if test="${professionalProfileForm.professionalProfile}">
                <div class="admin-title">
                    <h2>Professional Profile</h2>
                </div>
                <div class="error-box">
                    <div class="error-txt">
                        <spring:hasBindErrors name="professionalProfileForm">
                        <div class="error-box">
                            <div class="error-txt">
                                <ul>
                                    <c:forEach items="${errors.allErrors}" var="message">
                                    <li><spring:message message="${message}" /></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                        </spring:hasBindErrors>
                    </div>
                </div>
                <div class="admin-content">
                    <div class="add-new">
                        <ul class="list-form">
                            <form:form action="${pageContext.request.contextPath}/access/userProfile/updateProfessionalProfile" method="post" modelAttribute="professionalProfileForm">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="practiceStart" cssErrorClass="lb_error">Practicing Since</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="practiceStart" cssClass="datepicker form-field-admin" cssErrorClass="datepicker form-field-admin error-field" placeholder="YYYY-MM"/>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="aboutMe" cssErrorClass="lb_error">About Me</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:textarea path="aboutMe" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" rows="6" />
                                    <span style="display:block; font-size:13px;">About you would be visible in your medical profile</span>
                                </div>
                                <div class="clearFix"></div>
                            </li>
                            <c:if test="${userProfileForm.emailValidated and userProfileForm.email eq pageContext.request.userPrincipal.principal.username}">
                            <div class="col-lable3"></div>
                            <div class="col-fields">
                                <div class="left-btn">
                                    <input name="update" class="next-btn" value="UPDATE" type="submit">
                                </div>
                                    <%--<div class="right-btn">--%>
                                    <%--<input name="cancel_Update" class="cancel-btn" value="CANCEL" type="submit">--%>
                                    <%--</div>--%>
                                <div class="clearFix"></div>
                            </div>
                            <div class="clearFix"></div>
                            </c:if>
                            </form:form>

                            <fieldset>
                                <legend>Awards</legend>
                                <div class="store-table">
                                    <c:choose>
                                        <c:when test="${!empty professionalProfileForm.awards}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <c:forEach items="${professionalProfileForm.awards}" var="nameDatePair" varStatus="status">
                                            <tr>
                                                <td width="5%">${status.count}&nbsp;</td>
                                                <td width="75%" nowrap>${nameDatePair.name}</td>
                                                <td width="20%">${nameDatePair.monthYear}</td>
                                            </tr>
                                            </c:forEach>
                                        </table>
                                        </c:when>
                                        <c:otherwise>
                                        <div class="alert-info">
                                            <p>No award information added.</p>
                                        </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <c:if test="${userProfileForm.emailValidated and userProfileForm.email eq pageContext.request.userPrincipal.principal.username}">
                                <a href="${pageContext.request.contextPath}/access/userProfile/userProfessionalDetail/awards/modify" class="add-btn">Add/Edit Awards</a>
                                </c:if>
                                <span style="display:block; font-size:13px; padding-top: 20px;">This information is public</span>
                            </fieldset>

                            <fieldset>
                                <legend>Education <span style="color: red">**</span></legend>
                                <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty professionalProfileForm.education}">
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <c:forEach items="${professionalProfileForm.education}" var="nameDatePair" varStatus="status">
                                        <tr>
                                            <td width="5%">${status.count}&nbsp;</td>
                                            <td width="75%" nowrap>${nameDatePair.name}</td>
                                            <td width="20%">${nameDatePair.monthYear}</td>
                                        </tr>
                                        </c:forEach>
                                    </table>
                                    </c:when>
                                    <c:otherwise>
                                    <div class="alert-info">
                                        <p>No education information added.</p>
                                    </div>
                                    </c:otherwise>
                                </c:choose>
                                </div>

                                <c:if test="${userProfileForm.emailValidated and userProfileForm.email eq pageContext.request.userPrincipal.principal.username}">
                                <a href="${pageContext.request.contextPath}/access/userProfile/userProfessionalDetail/education/modify" class="add-btn">Add/Edit Education</a>
                                </c:if>
                                <span style="display:block; font-size:13px; padding-top: 20px;">This information is public</span>
                            </fieldset>

                            <fieldset>
                                <legend>Licenses <span style="color: red">**</span></legend>
                                <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty professionalProfileForm.licenses}">
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <c:forEach items="${professionalProfileForm.licenses}" var="nameDatePair" varStatus="status">
                                        <tr>
                                            <td width="5%">${status.count}&nbsp;</td>
                                            <td width="75%" nowrap>${nameDatePair.name}</td>
                                            <td width="20%">${nameDatePair.monthYear}</td>
                                        </tr>
                                        </c:forEach>
                                    </table>
                                    </c:when>
                                    <c:otherwise>
                                    <div class="alert-info">
                                        <p>No license information added.</p>
                                    </div>
                                    </c:otherwise>
                                </c:choose>
                                </div>

                                <c:if test="${userProfileForm.emailValidated and userProfileForm.email eq pageContext.request.userPrincipal.principal.username}">
                                <a href="${pageContext.request.contextPath}/access/userProfile/userProfessionalDetail/licenses/modify" class="add-btn">Add/Edit Licenses</a>
                                </c:if>
                                <span style="display:block; font-size:13px; padding-top: 20px;">This information is public. Will be shown in prescriptions and all printouts.</span>
                            </fieldset>
                        </ul>
                    </div>
                </div>
                </c:if>
            </div>
            <!-- Complete profile -->
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/services.js"></script>
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
