<%@ page import="com.noqapp.domain.types.BusinessTypeEnum,com.noqapp.domain.types.ActionTypeEnum" %>
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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue Inc"/></a>
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
                        <a href="${pageContext.request.contextPath}/business/survey/landing.htm">Survey</a>
                        <a href="${pageContext.request.contextPath}/business/paymentConfiguration/landing.htm">Payment Permission</a>
                        <a href="${pageContext.request.contextPath}/business/advertisement/landing.htm">Advertisement</a>
                        <a href="${pageContext.request.contextPath}/business/discount/landing.htm">Discount</a>
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
                        <h3><a href="/business/detail/business/${businessLandingForm.bizCodeQR}.htm" target="_blank"><span>${businessLandingForm.bizName}</span></a></h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <a href="/business/authorizedUsers.htm" class="add-btn">Show Authorized Users</a>
                                <c:choose>
                                    <c:when test="${BusinessTypeEnum.PH eq businessLandingForm.businessType}">

                                    </c:when>
                                    <c:otherwise>
                                        <a href="/business/category.htm" class="add-btn">Show Business Category</a>
                                    </c:otherwise>
                                </c:choose>
                                <a href="/business/addStore.htm" class="add-btn">Add New Store</a>
                            </div>
                            <div class="store-table">
                            <c:choose>
                            <c:when test="${!empty businessLandingForm.bizStores}">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th>Store Location</th>
                                        <th nowrap>
                                            Queue Name
                                            &nbsp;
                                            <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                                 alt="Sort" height="16px;"/>
                                        </th>
                                        <th>Pending</th>
                                        <th>Assigned</th>
                                        <th>&nbsp;</th>
                                    </tr>
                                    <c:forEach items="${businessLandingForm.bizStores}" var="store" varStatus="status">
                                    <tr>
                                        <td>${status.count}&nbsp;</td>
                                        <td>
                                            <a href="/business/detail/store/${store.id}.htm" target="_blank" style="display:block; font-size:13px;">${store.addressWrappedFunky}</a>
                                        </td>
                                        <td nowrap>
                                            <a href="/${store.codeQR}/q.htm" target="_blank">${store.displayName}</a>
                                            <span style="display:block; font-size:13px;">Business Type: ${store.businessType.description}</span>
                                            <span style="display:block; font-size:13px;">Category:
                                                <c:choose>
                                                    <c:when test="${!empty businessLandingForm.categories.get(store.bizCategoryId)}">
                                                        ${businessLandingForm.categories.get(store.bizCategoryId)}
                                                    </c:when>
                                                    <c:otherwise>
                                                        N/A
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                            <c:choose>
                                                <c:when test="${store.averageServiceTime > 0}">
                                                    <span style="display:block; font-size:13px;">AHT: ${store.averageServiceTimeFormatted} per client</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="display:block; font-size:13px;">AHT: ${store.averageServiceTimeFormatted}</span>
                                                </c:otherwise>
                                            </c:choose>

                                            <c:choose>
                                                <c:when test="${BusinessTypeEnum.RS eq store.businessType
                                                || BusinessTypeEnum.FT eq store.businessType
                                                || BusinessTypeEnum.BA eq store.businessType
                                                || BusinessTypeEnum.ST eq store.businessType
                                                || BusinessTypeEnum.GS eq store.businessType
                                                || BusinessTypeEnum.CF eq store.businessType}">
                                                    <span style="display:block; font-size:13px;"><del>Web Appointment</del></span>
                                                </c:when>
                                                <c:otherwise>
                                                <span style="display:block; font-size:13px;"><a
                                                        href="https://noqapp.com/b/s${store.webLocation}.html"
                                                        target="_blank">Web Appointment Link</a></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="/business/${store.id}/listQueueSupervisor.htm">${businessLandingForm.queueDetails.get(store.id).pendingApprovalToQueue}</a>
                                        </td>
                                        <td>
                                            <a href="/business/${store.id}/listQueueSupervisor.htm">${businessLandingForm.queueDetails.get(store.id).assignedToQueue}</a>
                                        </td>
                                        <td>
                                            <a href="/business/${store.id}/editStore.htm" class="add-btn">Edit</a>
                                            <c:choose>
                                                <c:when test="${store.active}">
                                                    <button id="storeOnlineOrOffline_${store.id}" class="add-btn" onclick="storeOnlineOrOffline('${store.id}', '${ActionTypeEnum.INACTIVE}')">Go Offline</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button id="storeOnlineOrOffline_${store.id}" class="add-btn" style="background: black" onclick="storeOnlineOrOffline('${store.id}', '${ActionTypeEnum.ACTIVE}')">Go Online</button>
                                                </c:otherwise>
                                            </c:choose>
                                            <br/><br/>
                                            <span style="display:block; font-size:13px;">Offline store is not visible</span>
                                        </td>
                                    </tr>
                                    </c:forEach>
                                </table>
                            </c:when>
                            <c:otherwise>
                                There are no stores associated with business.
                            </c:otherwise>
                            </c:choose>
                            </div>
                        </div>

                        <div class="alert-info">
                            <p>
                                To add supervisor to a queue, click on "Pending" value for that queue. Please,
                                notify the pending supervisor to complete their profile after login in at web site.
                            </p>
                            <p>Once supervisor completes their profile, you need to Approve their profile to be accepted as a supervisor.</p>
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
                    <div class="f-left">&copy; 2019 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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

</html>
