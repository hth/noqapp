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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/jquery/css/jquery-ui.css"/>
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
            <!-- Complete profile -->
            <sec:authorize access="hasAnyRole('ROLE_S_MANAGER', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')">
            <div class="admin-main">
                <!-- File Upload From -->
                <form:form action="${pageContext.request.contextPath}/business/store/photo/uploadServicePhoto.htm" modelAttribute="fileUploadForm" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="codeQR" value="${codeQR}"/>
                    <div class="admin-title">
                        <h2>Add Store Photo</h2>
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
                                        <form:label path="file" cssErrorClass="lb_error">Select Store Image</form:label>
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
                                    <input name="upload" class="next-btn" value="UPLOAD STORE IMAGE" type="submit">
                                </div>
                                    <%--<div class="right-btn">--%>
                                    <%--<input name="cancel_Upload" class="cancel-btn" value="CANCEL" type="submit">--%>
                                    <%--</div>--%>
                                <div class="clearFix"></div>
                            </div>
                            <div class="clearFix"></div>
                        </div>
                    </div>
                </form:form>

                <div class="admin-title">
                    <h2>Store Photos</h2>
                </div>
                <div class="admin-content">
                    <div class="add-new">
                        <c:choose>
                        <c:when test="${!empty images}">
                            <ul class="list-form">
                            <c:forEach items="${images}" var="image" varStatus="status">
                                <li>
                                    <div class="col-fields">
                                        <img src="https://s3.ap-south-1.amazonaws.com/${bucketName}/service/${codeQR}/${image}"
                                                onerror="this.src='/static2/internal/img/profile-image-192x192.png'"
                                                class="img-profile-circle" />
                                    </div>
                                    <div class="col-lable3">
                                        <form action="${pageContext.request.contextPath}/business/store/photo/deleteServicePhoto.htm" method="post">
                                            <input type="hidden" name="storeServiceImage" value="${image}"/>
                                            <input type="hidden" name="codeQR" value="${codeQR}"/>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <div class="left-btn">
                                                <input name="upload" class="next-btn" value="DELETE" type="submit">
                                            </div>
                                        </form>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <div class="alert-info">
                                <div class="no-approve">Please upload store related photographs.</div>
                            </div>
                        </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            </sec:authorize>
            <!-- Complete profile -->
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/jquery/js/jquery-ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
</html>
