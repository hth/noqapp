<%@ page import="com.noqapp.domain.types.BusinessTypeEnum,com.noqapp.domain.types.ActionTypeEnum" %>
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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/business/editBusiness.htm">Edit Business</a>
                        <a href="${pageContext.request.contextPath}/business/uploadServicePhoto.htm">Business Photo</a>
                        <a href="${pageContext.request.contextPath}/business/preferredBusiness.htm">Preferred Business</a>
                        <a href="${pageContext.request.contextPath}/business/external/access.htm">Permissions</a>
                        <a href="${pageContext.request.contextPath}/business/dataVisibility/landing.htm">Data Visibility</a>
                        <a href="${pageContext.request.contextPath}/business/paymentConfiguration/landing.htm">Payment Permission</a>
                        <a href="${pageContext.request.contextPath}/business/advertisement/landing.htm">Advertisement</a>
                        <a href="${pageContext.request.contextPath}/access/userProfile.htm">Profile</a>
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
                        <h3><span>Advertisement</span></h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <a href="${pageContext.request.contextPath}/business/advertisement/create.htm" class="add-btn">Add New Advertisement</a>
                            </div>
                            <div class="store-table">
                                <c:choose>
                                <c:when test="${!empty advertisementForm.advertisements}">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th>Title</th>
                                        <th>Image</th>
                                        <th>Advt Type & Schedule</th>
                                        <th>Status</th>
                                        <th nowrap>
                                            Created
                                            &nbsp;
                                            <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                                    alt="Sort" height="16px;"/>
                                        </th>
                                        <th>&nbsp;</th>
                                    </tr>
                                    <c:forEach items="${advertisementForm.advertisements}" var="advertisement" varStatus="status">
                                    <tr>
                                        <td>
                                            <span style="display:block; font-size:13px;">${status.count}&nbsp;</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${advertisement.title}</span>
                                            <br/>
                                            <c:choose>
                                                <c:when test="${advertisement.active}">
                                                    <span style="display:block; font-size:13px; background: lightgrey; color: black; padding: 2px;">Advertisement Is Online (Visible)</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="display:block; font-size:13px; background: lightgrey; color: black; padding: 2px;">Advertisement Is Offline (Not Visible)</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${!empty advertisement.imageUrls}">
                                                    <span style="display:block; font-size:13px;">Image Exists</span>
                                                    <a href="${pageContext.request.contextPath}/business/advertisement/${advertisement.id}/upload.htm" class="add-btn">Edit Image</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="display:block; font-size:13px;">Image Missing</span>
                                                    <a href="${pageContext.request.contextPath}/business/advertisement/${advertisement.id}/upload.htm" class="add-btn">Add Image</a>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${advertisement.advertisementType.description} for ${advertisement.advertisementDisplay.description}</span>
                                            <br/>
                                            <span style="display:block; font-size:13px;">From: ${advertisement.publishDate} Until: ${advertisement.endDate}</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${advertisement.validateStatus.description}</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${advertisement.created}</span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/business/advertisement/edit/${advertisement.id}.htm" class="add-btn" style="margin: 0px;">Edit</a>
                                            <c:if test="${!advertisement.active}">
                                                <form:form action="${pageContext.request.contextPath}/business/advertisement/delete.htm" modelAttribute="advertisementForm" method="post">
                                                    <input type="hidden" name="advertisementId" value="${advertisement.id}"/>
                                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                    <input name="delete" class="ladda-button next-btn" value="DELETE" type="submit" style="font-weight: 400; padding: 7%; background: #666;">
                                                </form:form>
                                            </c:if>
                                        </td>
                                    </tr>
                                    </c:forEach>
                                </table>
                                </c:when>
                                <c:otherwise>
                                    There are no advertisement.
                                </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="alert-info">
                            <p>
                                Add advertisement to be shown on TV or Mobile App.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
                <!-- Add New Supervisor -->

        </div>
    </div>
    <!-- content end -->


    <!-- Foote -->
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
    <!-- Foote End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/services.js"></script>
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
