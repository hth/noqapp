<%@ page import="com.noqapp.domain.types.ActionTypeEnum" %>
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
            <div class="admin-main">
                <form:form modelAttribute="marketplaceForm">
                <div class="admin-title">
                    <h2>View your ${marketplaceForm.marketplace.businessType.description} post</h2>
                </div>

                <div class="admin-content">
                    <div class="add-new">
                        <ul class="list-form" style="border: 1px solid black; padding-top: 20px;">
                            <li>
                                <div class="col-lable3">
                                    <form:label path="marketplace.title" cssErrorClass="lb_error">Title</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.title" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Title of your property listing" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="marketplace.description" cssErrorClass="lb_error">Description</form:label>
                                </div>
                                <div class="col-fields" style="margin-top: 10px;">
                                    <c:out value="${marketplaceForm.marketplace.description}" escapeXml="false"/>
                                </div>
                                <span class="tooltip" title="Public information about your property" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
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
                                    <form:label path="marketplace.productPrice" cssErrorClass="lb_error">Rent per Month</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.priceForDisplayWithFormatting" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Rent on this place" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="marketplace.rentalType" cssErrorClass="lb_error">Rental Type</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.rentalType.description" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Type of rental property" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
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
                                    <form:label path="marketplace.bedroom" cssErrorClass="lb_error">Number of bedrooms</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.bedroom" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Number of bedrooms available" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="marketplace.bathroom" cssErrorClass="lb_error">Number of bathrooms</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.bathroom" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Number of bathrooms available" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="marketplace.carpetArea" cssErrorClass="lb_error">Carpet Area (sq. ft.)</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.carpetArea" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Size of this place" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="marketplace.rentalAvailableDay" cssErrorClass="lb_error">Available From</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.rentalAvailableDay" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readOnly="true" />
                                </div>
                                <span class="tooltip" title="Date from when property is available. Cannot change this information later." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="marketplace.address" cssErrorClass="lb_error">Rental Address</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.address" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Address of the rental place. This is not <b><u>visible</u></b> to others. Cannot change this information later." style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
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
                                    <form:label path="marketplace.city" cssErrorClass="lb_error">City/Area</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.city" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="City the property is located" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="marketplace.town" cssErrorClass="lb_error">Town/Locality/Sector</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.town" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Town or Sector the property is located" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                <div class="clearFix"></div>
                            </li>
                            <li>
                                <div class="col-lable3">
                                    <form:label path="marketplace.landmark" cssErrorClass="lb_error">Landmark</form:label>
                                </div>
                                <div class="col-fields">
                                    <form:input path="marketplace.landmark" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" readonly="true"/>
                                </div>
                                <span class="tooltip" title="Any close by landmark" style="padding-left: 10px;"><i class="fas fa-info-circle"></i></span>
                                <sup style="color: #9f1313; font-size: 150%;"></sup>
                                <div class="clearFix"></div>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="admin-title">
                    <h2>Image of ${marketplaceForm.marketplace.businessType.description}</h2>
                </div>
                <div class="admin-content">
                    <div class="add-new">
                        <c:choose>
                            <c:when test="${!empty marketplaceForm.marketplace.postImages}">
                                <ul class="list-form">
                                    <c:forEach items="${marketplaceForm.marketplace.postImages}" var="image" varStatus="status">
                                        <li>
                                            <div class="col-fields">
                                                <img src="https://s3.ap-south-1.amazonaws.com/${bucketName}/${marketplaceForm.marketplace.id}/${image}"
                                                        onerror="this.src='/static/internal/img/pending-image.png'"
                                                        class="img-profile-circle" />
                                            </div>
                                            <div class="clearFix"></div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <div class="alert-info">
                                    <div class="no-approve">No image</div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                </form:form>
            </div>
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
