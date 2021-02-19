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

    <link rel="stylesheet" href="//cdn.jsdelivr.net/gh/StephanWagner/jBox@v1.0.9/dist/jBox.all.min.css" type='text/css'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>

    <!-- reference your copy Font Awesome here (from our CDN or by hosting yourself) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/fontawesome/css/fontawesome.css" type='text/css'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/fontawesome/css/brands.css" type='text/css'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/fontawesome/css/solid.css" type='text/css'>

    <!-- custom styling for all icons -->
    i.fas,
    i.fab {
        border: 1px solid red;
    }
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
                    <div class="menu-top-arrow">
                        <img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
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
            <sec:authorize access="hasAnyRole('ROLE_M_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')">
                <div class="admin-main">
                    <form:form modelAttribute="registerBusiness">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                        <div class="admin-title">
                            <c:choose>
                                <c:when test="${!empty registerBusiness.bizStoreId}">
                                    <h2>Edit ${registerBusiness.businessType.classifierTitle} details</h2>
                                </c:when>
                                <c:otherwise>
                                    <h2>Online ${registerBusiness.businessType.classifierTitle} details</h2>
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
                                            <form:label path="displayName" cssErrorClass="lb_error">Online ${registerBusiness.businessType.classifierTitle} Name</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="displayName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                        </div>
                                        <span class="tooltip" title="Your ${registerBusiness.businessType.classifierTitle.toLowerCase()} will be listed publicly by this name" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                        <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="bizCategoryId" cssErrorClass="lb_error">Category</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <c:choose>
                                                <c:when test="${!empty registerBusiness.categories}">
                                                    <form:select path="bizCategoryId" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                        <form:option value="" label="--- Select ---"/>
                                                        <form:options items="${registerBusiness.categories}" />
                                                    </form:select>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="form-field-select">Not Applicable</div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="addressStore" cssErrorClass="lb_error">Store Address</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:textarea path="addressStore" cols="" rows="3" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                            <span style="display:block; font-size:14px;">(Google address preferred. Reason: Helps find on Google Map)</span>
                                        </div>
                                        <span class="tooltip" title="Location of your ${registerBusiness.businessType.classifierTitle.toLowerCase()}. This is the location where service is rendered." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                        <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                        <div class="clearFix"></div>
                                    </li>

                                    <c:set var="displayedTownAndArea" value="false"/>
                                    <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
                                    <c:if test="${message.source eq 'areaStore' or message.source eq 'townStore' and displayedTownAndArea eq false}">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="areaStore" cssErrorClass="lb_error">Store Town</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="areaStore" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" placeholder="Santacruz" />
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="townStore" cssErrorClass="lb_error">Store City</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="townStore" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" placeholder="Mumbai" />
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <c:set var="displayedTownAndArea" value="true"/>
                                    </c:if>
                                    </c:forEach>

                                    <c:if test="${!empty registerBusiness.foundAddressStores}">
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="foundAddressStorePlaceId" cssErrorClass="lb_error">Best Matching Store Addresses</form:label>
                                        </div>
                                        <div class="col-fields pT10 pB10">
                                            <c:forEach items="${registerBusiness.foundAddressStores}" var="mapElement">
                                                <form:radiobutton path="foundAddressStorePlaceId" value="${mapElement.key}" label="${mapElement.value.formattedAddress}"
                                                                  onclick="handleFoundAddressStoreClick()"/> <br />
                                            </c:forEach>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="selectFoundAddressStore" cssErrorClass="lb_error">I choose Best Matching Store Address</form:label>
                                        </div>
                                        <div id="addressStoreCheckBox" class="col-fields">
                                            <form:checkbox path="selectFoundAddressStore" cssClass="form-check-box" cssErrorClass="form-check-box error-field" disabled="true"
                                                           onclick="handleFoundAddressStoreCheckboxUncheck()" />
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    </c:if>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="phoneStore" cssErrorClass="lb_error">Store Phone</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="phoneStore" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"/>
                                        </div>
                                        <span class="tooltip" title="<b>Phone number is public.</b> Customers will be able to connect with you on this phone number." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                        <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="businessAddressAsStore" cssErrorClass="lb_error">Same As Business</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:checkbox path="businessAddressAsStore" id="businessAddressAsStore" cssClass="form-check-box" cssErrorClass="form-check-box error-field" />
                                            <span style="display:block; font-size:14px;">(Store Address, Phone is same as Business)</span>
                                        </div>
                                        <div class="clearFix"></div>
                                    </li>
                                    <li>
                                        <div class="col-lable3">
                                            <form:label path="famousFor" cssErrorClass="lb_error">Famous For</form:label>
                                        </div>
                                        <div class="col-fields">
                                            <form:input path="famousFor" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                        placeholder="Speciality you would like everyone to know" />
                                        </div>
                                        <span class="tooltip" title="Let everyone know your speciality" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                        <div class="clearFix"></div>
                                    </li>
                                </ul>

                                <div class="col-lable3"></div>

                                <c:choose>
                                <c:when test="${!empty registerBusiness.bizStoreId}">
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
                                            <button name="_eventId_delete" class="ladda-button cancel-btn" style="width:32%; float: left; margin-left:2%">Delete</button>
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
<script type="text/javascript" src="//cdn.jsdelivr.net/gh/StephanWagner/jBox@v1.0.9/dist/jBox.all.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/services.js"></script>
<script type="text/javascript">
    $('[name="businessAddressAsStore"]').click(function () {
        if (document.getElementById('businessAddressAsStore').checked) {
            $('[name="addressStore"]').val('${registerBusiness.businessUser.bizName.address}').prop("disabled", true);
            $('[name="phoneStore"]').val('${registerBusiness.businessUser.bizName.phoneFormatted}').prop("disabled", true);
        } else {
            $('[name="addressStore"]').val("").prop("disabled", false);
            $('[name="phoneStore"]').val("").prop("disabled", false);
        }
    });
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/ladda.min.js"></script>
<script type="text/javascript">
    // Bind normal buttons
    Ladda.bind('.button-btn button', {timeout: 6000});

    // Bind progress buttons and simulate loading progress
    Ladda.bind('.progress-demo button', {
        callback: function (instance) {
            let progress = 0;
            let interval = setInterval(function () {
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
    new jBox('Tooltip', {
        attach: '.tooltip',
        adjustDistance : {
            top : 105,
            bottom : 150,
            left : 15,
            right : 50
        }
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
