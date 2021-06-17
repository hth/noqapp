<%@ page import="com.noqapp.domain.types.ActionTypeEnum" %>
<%@ include file="../../../../jsp/include.jsp" %>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>

    <!-- reference your copy Font Awesome here (from our CDN or by hosting yourself) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/external/fontawesome/css/fontawesome.css" type='text/css'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/external/fontawesome/css/brands.css" type='text/css'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/external/fontawesome/css/solid.css" type='text/css'>

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
            <sec:authorize access="hasAnyRole('ROLE_SUPERVISOR')">
            <div class="admin-main">
                <form:form action="${pageContext.request.contextPath}/emp/marketplace/action" modelAttribute="marketplace">
                <div class="admin-title">
                    <h2>Review your ${marketplace.businessType.description} post</h2>
                </div>

                <div class="admin-content">
                    <div class="add-new">
                        <ul class="list-form" style="border: 1px solid black; padding-top: 20px;">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="title" cssErrorClass="lb_error">Title</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="title" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Title of your listing" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="description" cssErrorClass="lb_error">Description</form:label>
                                </div>
                                <div class="col-fields" style="margin-top: 10px;">
                                    <c:out value="${marketplace.description}" escapeXml="false"/>
                                </div>
                                <span class="tooltip" title="Description of your listing" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="admin-content">
                    <div class="add-new">
                        <ul class="list-form" style="border: 1px solid black; padding-top: 20px;">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="productPrice" cssErrorClass="lb_error">List Price</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="productPrice" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Listed price of your item" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="itemCondition" cssErrorClass="lb_error">Condition</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="itemCondition.description" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Condition of your item" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="admin-content">
                    <div class="add-new">
                        <ul class="list-form" style="border: 1px solid black; padding-top: 20px;">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="city" cssErrorClass="lb_error">City/Area</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="city" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="City your item is located" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="town" cssErrorClass="lb_error">Town/Locality/Sector</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="town" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Town or Sector item is located" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="landmark" cssErrorClass="lb_error">Landmark</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="landmark" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Close by landmark" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;"></sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="businessType" cssErrorClass="lb_error">Posting For</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="businessType.description" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Your post will be listed on the market places selected here. This <b><u>cannot</u></b> be changed later." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                    </div>
                </div>
                </form:form>

                <div class="admin-content">
                    <div class="add-new">
                        <div class="col-lable3"></div>
                        <div class="col-fields">
                            <form:form action="${pageContext.request.contextPath}/emp/marketplace/action" modelAttribute="marketplaceForm" method="post">
                                <form:hidden path="marketplaceId" value="${marketplace.id}"/>
                                <form:hidden path="businessType" value="${marketplace.businessType.name}"/>
                                <form:hidden path="actionType" value="${ActionTypeEnum.APPROVE}" />
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <div class="left-btn">
                                    <input name="approve" class="ladda-button next-btn" value="APPROVE" type="submit" style="font-weight: 500;">
                                </div>
                            </form:form>

                            <form:form action="${pageContext.request.contextPath}/emp/marketplace/action" modelAttribute="marketplaceForm" method="post">
                                <form:hidden path="marketplaceId" value="${marketplace.id}"/>
                                <form:hidden path="businessType" value="${marketplace.businessType.name}"/>
                                <form:hidden path="actionType" value="${ActionTypeEnum.REJECT}" />
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <div class="right-btn">
                                    <input name="reject" class="ladda-button next-btn" value="REJECT" type="submit" style="font-weight: 500;">
                                </div>
                            </form:form>
                            <div class="clearFix"></div>
                        </div>
                        <div class="clearFix"></div>
                    </div>
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
<script type="text/javascript" src="//cdn.jsdelivr.net/gh/StephanWagner/jBox@v1.0.9/dist/jBox.all.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/editor/ck/js/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/services.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/external/ladda/js/ladda.min.js"></script>
<script>
    CKEDITOR.replace('marketplaceForm.marketplace.description');
</script>
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
