<%@ page import="com.noqapp.domain.types.BusinessTypeEnum,com.noqapp.domain.types.MessageOriginEnum,com.noqapp.domain.types.ActionTypeEnum" %>
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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>
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
                        <a href="${pageContext.request.contextPath}/business/editBusiness.htm">Edit Business</a>
                        <a href="${pageContext.request.contextPath}/business/uploadServicePhoto.htm">Business Photo</a>
                        <a href="${pageContext.request.contextPath}/business/preferredBusiness.htm">Preferred Business</a>
                        <a href="${pageContext.request.contextPath}/business/external/access.htm">Permissions</a>
                        <a href="${pageContext.request.contextPath}/business/migrateBusinessType.htm">Migrate Business Type</a>
                        <a href="${pageContext.request.contextPath}/business/dataVisibility/landing.htm">Data Visibility</a>
                        <a href="${pageContext.request.contextPath}/business/customTextToSpeech/landing.htm">Voice Announcement</a>
                        <a href="${pageContext.request.contextPath}/business/message/customer.htm">Message Your Customers</a>
                        <a href="${pageContext.request.contextPath}/business/survey/landing.htm">Survey</a>
                        <a href="${pageContext.request.contextPath}/business/paymentConfiguration/landing.htm">Payment Permission</a>
                        <a href="${pageContext.request.contextPath}/business/advertisement/landing.htm">Advertisement</a>
                        <a href="${pageContext.request.contextPath}/business/store/publishJob/landing.htm">Post Job</a>
                        <a href="${pageContext.request.contextPath}/business/discount/landing.htm">Discount</a>
                        <a href="${pageContext.request.contextPath}/business/coupon/landing.htm">Client Coupon</a>
                        <a href="${pageContext.request.contextPath}/business/coupon/businessLanding.htm">Business Coupon</a>
                        <a href="${pageContext.request.contextPath}/business/customer/landing.htm">Service Priority</a>
                        <a href="${pageContext.request.contextPath}/business/customerHistory/landing.htm">Customer Lookup</a>
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
                                <c:choose>
                                    <c:when test="${
                                    BusinessTypeEnum.RS eq businessLandingForm.businessType
                                    || BusinessTypeEnum.RSQ eq businessLandingForm.businessType
                                    || BusinessTypeEnum.FT eq businessLandingForm.businessType
                                    || BusinessTypeEnum.FTQ eq businessLandingForm.businessType
                                    || BusinessTypeEnum.BA eq businessLandingForm.businessType
                                    || BusinessTypeEnum.BAQ eq businessLandingForm.businessType
                                    || BusinessTypeEnum.ST eq businessLandingForm.businessType
                                    || BusinessTypeEnum.STQ eq businessLandingForm.businessType
                                    || BusinessTypeEnum.GS eq businessLandingForm.businessType
                                    || BusinessTypeEnum.GSQ eq businessLandingForm.businessType
                                    || BusinessTypeEnum.CF eq businessLandingForm.businessType
                                    || BusinessTypeEnum.CFQ eq businessLandingForm.businessType
                                    || BusinessTypeEnum.CD eq businessLandingForm.businessType
                                    || BusinessTypeEnum.CDQ eq businessLandingForm.businessType
                                    }">
                                        <c:if test="${businessLandingForm.bizStores.size() != 0}">
                                            <a href="/business/authorizedUsers.htm" class="add-btn">Show Authorized Users</a>
                                            <a href="/business/category.htm" class="add-btn">Show Business Category</a>
                                            <a href="/business/addStore.htm" class="add-btn">Setup Online ${businessLandingForm.businessType.classifierTitle}</a>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="/business/authorizedUsers.htm" class="add-btn">Show Authorized Users</a>
                                        <c:choose>
                                            <c:when test="${BusinessTypeEnum.PH eq businessLandingForm.businessType}">

                                            </c:when>
                                            <c:otherwise>
                                                <a href="/business/category.htm" class="add-btn">Show Business Category</a>
                                            </c:otherwise>
                                        </c:choose>
                                        <a href="/business/addStore.htm" class="add-btn">Setup Online ${businessLandingForm.businessType.classifierTitle}</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty businessLandingForm.bizStores}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th>${businessLandingForm.businessType.classifierTitle} Location</th>
                                                <th nowrap>
                                                    ${businessLandingForm.businessType.classifierTitle} Name
                                                    &nbsp;
                                                    <img src="${pageContext.request.contextPath}/static/internal/img/sortAZ.png"
                                                         alt="Sort" height="16px;"/>
                                                </th>
                                                <th>Users Pending</th>
                                                <th>Authorized Users</th>
                                                <th>&nbsp;</th>
                                            </tr>
                                            <c:forEach items="${businessLandingForm.bizStores}" var="store" varStatus="status">
                                            <tr>
                                                <td>${status.count}&nbsp;</td>
                                                <td>
                                                    <a href="/business/detail/store/${store.id}.htm" target="_blank" style="display:block; font-size:13px; color: #0000FF;">${store.addressWrappedFunky}</a>
                                                </td>
                                                <td nowrap>
                                                    <a href="/${store.codeQR}/q.htm" target="_blank" style="color: #0000FF;">${store.displayName}</a>
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
                                                            <a href="/business/averageHandling/${store.id}.htm" target="_blank" style="color: #0000FF;"><span style="display:block; font-size:13px;">AHT: ${store.averageServiceTimeFormatted} per client</span></a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="/business/averageHandling/${store.id}.htm" target="_blank" style="color: #0000FF;"><span style="display:block; font-size:13px;">AHT: ${store.averageServiceTimeFormatted}</span></a>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:choose>
                                                        <c:when test="${
                                                        BusinessTypeEnum.RS eq store.businessType
                                                        || BusinessTypeEnum.RSQ eq store.businessType
                                                        || BusinessTypeEnum.FT eq store.businessType
                                                        || BusinessTypeEnum.FTQ eq store.businessType
                                                        || BusinessTypeEnum.BA eq store.businessType
                                                        || BusinessTypeEnum.BAQ eq store.businessType
                                                        || BusinessTypeEnum.ST eq store.businessType
                                                        || BusinessTypeEnum.STQ eq store.businessType
                                                        || BusinessTypeEnum.GS eq store.businessType
                                                        || BusinessTypeEnum.GSQ eq store.businessType
                                                        || BusinessTypeEnum.CF eq store.businessType
                                                        || BusinessTypeEnum.CFQ eq store.businessType
                                                        || BusinessTypeEnum.CD eq store.businessType
                                                        || BusinessTypeEnum.CDQ eq store.businessType
                                                        }">
                                                            <span style="display:block; font-size:13px;">&nbsp;</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                        <span style="display:block; font-size:13px;"><a
                                                                href="https://noqapp.com/b/s${store.webLocation}.html"
                                                                target="_blank" style="color: #0000FF;">Web Appointment Link</a></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <a href="/business/${store.id}/listQueueSupervisor.htm" style="color: #0000FF;">${businessLandingForm.queueDetails.get(store.id).pendingApprovalToQueue}</a>
                                                </td>
                                                <td>
                                                    <a href="/business/${store.id}/listQueueSupervisor.htm" style="color: #0000FF;">${businessLandingForm.queueDetails.get(store.id).assignedToQueue}</a>
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
                                                    <span style="display:block; font-size:13px;">Offline ${businessLandingForm.businessType.classifierTitle .toLowerCase()} is not visible</span>
                                                </td>
                                            </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <p style="display:block; font-size:18px; padding-bottom: 30px;">
                                            Let's now setup online ${businessLandingForm.businessType.classifierTitle}. Select left if you have one store or right side for franchise model.
                                        </p>

                                        <div class="addbtn-store">
                                            <p align="center">
                                                <a href="/business/addStore.htm" class="add-btn" style="height: 90px; line-height: 90px; text-align: center; font-size: 18px;">&nbsp;&nbsp;One location &nbsp;&nbsp;&nbsp;</a>
                                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                <a href="/business/addFranchiseStore.htm" class="add-btn" style="height: 90px; line-height: 90px; text-align: center; font-size: 18px;">Multiple locations</a>
                                            </p>
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


    <!-- Foote -->
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
    <!-- Foote End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/services.js"></script>
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
