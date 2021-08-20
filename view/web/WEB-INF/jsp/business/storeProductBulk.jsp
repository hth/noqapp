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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/external/jquery/css/jquery-ui.css"/>
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
            <!-- Complete profile -->
            <div class="admin-main">
                <!-- File Upload From -->

                <form:form action="${pageContext.request.contextPath}/business/store/product/bulk/upload" modelAttribute="fileUploadForm" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="codeQR" value="${codeQR}"/>
                    <div class="admin-title">
                        <h2>Store Product Bulk Upload</h2>
                    </div>

                    <spring:hasBindErrors name="fileUploadForm">
                    <div class="error-box">
                        <div class="error-txt">
                            <ul>
                                <li><form:errors path="*"/></li>
                            </ul>
                        </div>
                    </div>
                    </spring:hasBindErrors>

                    <c:if test="${uploadSuccess}">
                    <div class="alert-info">
                        <p>Uploaded file successfully. ${recordsUpdated} store products updated</p>
                    </div>
                    </c:if>

                    <div class="admin-content">
                        <div class="add-new">
                            <ul class="list-form">
                                <li>
                                    <div class="col-lable3" style="padding-top: 30px;">
                                        <form:label path="file" cssErrorClass="lb_error">Select Product List</form:label>
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
                                    <input name="upload" class="next-btn" value="UPLOAD PRODUCT LIST" type="submit">
                                </div>
                                <div class="right-btn">
                                    <input name="cancel_Upload" class="cancel-btn" value="CANCEL" type="submit">
                                </div>
                                <div class="clearFix"></div>
                            </div>
                            <div class="clearFix"></div>
                        </div>

                        <spring:hasBindErrors name="fileUploadForm">
                        <div class="alert-info">
                            <p>
                                <span style="font-weight: bold; color: red">Recommended</span>: First Download Product List. Modify and then upload again.
                                Or contact support if help needed.
                            </p>
                        </div>
                        </spring:hasBindErrors>

                        <div class="alert-info">
                            <p>Bulk Upload:</p>
                            <p>
                                <span style="font-weight: bold; color: red">Warning</span>: This process replaces all the existing products with the uploaded list.
                                It is a non recoverable process. All changes successful are final.
                            </p>
                            <p>
                                Note: For data integrity, it is recommended to make bulk upload after store closing hours or at a non peak hours.
                                Always download product list from below and then upload this list with changes. That will limit errors if any.
                            </p>
                        </div>
                    </div>
                </form:form>

                <form:form action="${pageContext.request.contextPath}/business/store/product/bulk/download" method="post">
                    <input type="hidden" name="codeQR" value="${codeQR}"/>
                    <div class="admin-title">
                        <h2>Store Product Bulk Download</h2>
                    </div>

                    <div class="admin-content">
                        <div class="add-new">
                            <div class="col-fields">
                                <div class="right-btn">
                                    <input name="upload" class="next-btn" value="DOWNLOAD PRODUCT LIST" type="submit">
                                </div>
                                <div class="clearFix"></div>
                            </div>
                            <div class="clearFix"></div>
                        </div>
                    </div>
                </form:form>
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
                    <div class="f-left">&copy; 2021 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> |
                        <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

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
