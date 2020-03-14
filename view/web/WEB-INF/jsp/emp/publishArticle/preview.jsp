<%@ page import="com.noqapp.domain.types.ValidateStatusEnum" %>
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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue Inc"/></a>
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
                        <h3>Publish Article Preview</h3>
                        <div class="add-new" style="padding-top: 30px;">
                            <ul class="list-form">
                                <form:form modelAttribute="publishArticleForm">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="bannerImage" cssErrorClass="lb_error">Article Image</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <img src="https://s3.ap-south-1.amazonaws.com/${bucketName}/article/${publishArticleForm.publishId}/${publishArticleForm.bannerImage}"
                                                onerror="this.src='/static2/internal/img/profile-image-192x192.png'"
                                                class="img-profile-circle" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="articleTitle" cssErrorClass="lb_error">Title</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="articleTitle" cssClass="form-field-admin" cssErrorClass="form-field-admin lb_error" readonly="true"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="article" cssErrorClass="lb_error">Content</form:label>
                                    </div>
                                    <div class="col-fields" style="margin-top: 10px;">
                                        <c:out value="${publishArticleForm.article}" escapeXml="false"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                </form:form>

                                <li class="mB0">
                                    <div class="col-lable3"></div>
                                    <div class="col-fields">
                                        <div class="button-btn">
                                            <form:form action="${pageContext.request.contextPath}/emp/landing/publishArticle/preview.htm" method="post">
                                                <input type="hidden" name="publishId" value="${publishArticleForm.publishId}"/>
                                                <input type="hidden" name="validateStatus" value="${ValidateStatusEnum.A}"/>
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                <div class="left-btn">
                                                    <input name="approve" class="ladda-button next-btn" value="APPROVE" type="submit" style="font-weight: 500;">
                                                </div>
                                            </form:form>

                                            <form:form action="${pageContext.request.contextPath}/emp/landing/publishArticle/preview.htm" method="post">
                                                <input type="hidden" name="publishId" value="${publishArticleForm.publishId}"/>
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
                    <div class="clearFix"></div>
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
                    <div class="f-left">&copy; 2020 NoQueue Inc. | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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
</html>
