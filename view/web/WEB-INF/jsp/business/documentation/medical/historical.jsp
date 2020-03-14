<%@ page import="com.noqapp.common.utils.DateFormatter" %>
<%@ include file="../../../include.jsp" %>
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
    <link href="https://transloadit.edgly.net/releases/uppy/v1.9.0/uppy.min.css" rel="stylesheet">

    <style type="text/css">
        .card {
            height: 70px;
            width: 200px;
            background-color: whitesmoke;
            margin-left: 5px;
            margin-right: 5px;
            margin-top: 8px;
            padding: 3px 3px 3px 5px;
        }

        .card-container {
            margin-left: 10px;
            margin-right: 10px;
            display: flex;
        }
    </style>
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
                        <a href="${pageContext.request.contextPath}/business/documentation/medical/landing.htm">Medical Documents</a>
                        <a href="${pageContext.request.contextPath}/business/coupon/landing.htm">Client Coupon</a>
                        <a href="${pageContext.request.contextPath}/business/coupon/businessLanding.htm">Business Coupon</a>
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
                        <h3>Business: <span>${medicalDocumentUploadListForm.businessName}</span></h3>

                        <div class="add-store">
                            <div class="details-box" style="padding: 10px 0 10px 0;">
                                Queue Name: <span>${medicalDocumentUploadListForm.medicalDocumentUploadForms.get(0).bizStore.displayName}</span>
                            </div>
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty medicalDocumentUploadListForm.jsonQueuedPersonMap}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th nowrap>Date</th>
                                                <th nowrap>Queued Person</th>
                                            </tr>
                                            <c:forEach items="${medicalDocumentUploadListForm.jsonQueuedPersonMap}" var="jsonQueuedPersonMap" varStatus="status">
                                                <tr>
                                                    <td>${status.count}&nbsp;</td>
                                                    <td>
                                                        ${jsonQueuedPersonMap.key}
                                                    </td>
                                                    <td>
                                                        <c:forEach items="${jsonQueuedPersonMap.value}" var="jsonQueuedPerson" varStatus="status">
                                                        <div class="card-container">
                                                            <div class="card">
                                                                ${jsonQueuedPerson.customerName} <br/>
                                                                <span style="display:block; font-size:13px;">${jsonQueuedPerson.phoneFormatted}</span>
                                                                <span style="display:block; font-size:13px; text-align: right;">
                                                                    <c:choose>
                                                                        <c:when test="${empty jsonQueuedPerson.recordReferenceId}">
                                                                            N/A
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <a href="/business/documentation/medical/${jsonQueuedPerson.recordReferenceId}/uploadHistorical/${medicalDocumentUploadListForm.medicalDocumentUploadForms.get(0).bizStore.codeQR}.htm" target="_blank">Upload</a>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </span>
                                                            </div>
                                                        </div>
                                                        </c:forEach>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert-info">
                                            <p>No patient found.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
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
                    <div class="f-left">&copy; 2020 NoQueue Inc. | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

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
