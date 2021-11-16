<%@ page import="com.noqapp.domain.types.BusinessTypeEnum, com.noqapp.domain.types.ValidateStatusEnum" %>
<%@ include file="../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="utf-8">
    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
    <meta content='width=device-width, initial-scale=1' name='viewport'/>

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
                        <a href="${pageContext.request.contextPath}/access/userProfile">Profile</a>
                        <a href="${pageContext.request.contextPath}/access/sos">SOS Message</a>
                        <a href="${pageContext.request.contextPath}/access/rewards">Rewards</a>
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
                <div class="admin-content">
                    <c:if test="${empty landingForm.minorUserProfiles}">
                    <div class="register-c">
                        <h3>Welcome <sec:authentication property="principal.userShortName"/></h3>

                        <sec:authorize access="hasRole('ROLE_CLIENT')">
                        <c:choose>
                            <c:when test="${!empty landingForm.businessUserRegistrationStatus}">
                                <c:if test="${landingForm.businessUserRegistrationStatus eq 'C'}">
                                    Awaiting approval for business account. <br/>
                                    Since: ${landingForm.businessAccountSignedUp}
                                </c:if>
                                <c:if test="${landingForm.businessUserRegistrationStatus eq 'N'}">
                                    Application marked as not complete.
                                    <a href="/access/landing/business/migrate" class="add-btn">Please modify your application for approval</a>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <a href="/access/landing/business/migrate" class="add-btn">Do you own a business which you would like to register?</a>
                            </c:otherwise>
                        </c:choose>
                        </sec:authorize>
                    </div>
                    <p>&nbsp;</p>
                    </c:if>

                    <div class="store">
                        <div class="add-store">
                            <div class="store-table">
                                Post on Marketplace: <a href="/access/marketplace/post" class="add-btn">Start Here</a>
                            </div>
                        </div>
                    </div>

                    <div class="store">
                        <c:if test="${!empty landingForm.minorUserProfiles}">
                            <h3>Guardian</h3>

                            <div class="add-store">
                                <div class="store-table" style="width: 50%">
                                    Assigned as guardian to following account. Please log into the account with email to see details.
                                    <br/><br/>
                                    <table width="50%" border="0" cellspacing="0" cellpadding="0">
                                        <c:forEach items="${landingForm.minorUserProfiles}" var="profile" varStatus="status">
                                        <tr>
                                            <td>${profile.name}</td>
                                            <td>
                                            <c:choose>
                                                <c:when test="${fn:endsWith(profile.email, '@mail.noqapp.com')}">
                                                    Email: N/A
                                                </c:when>
                                                <c:otherwise>
                                                    ${profile.email}
                                                </c:otherwise>
                                            </c:choose>
                                            </td>
                                            <td>Age ${profile.ageAsString}</td>
                                        </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                            </div>

                            <div class="clearFix"></div>
                        </c:if>

                        <h3>Current Queue</h3>
                        <div class="add-store">
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty landingForm.currentQueues}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th>Name</th>
                                                <th>Status</th>
                                                <th>Token Number</th>
                                            </tr>
                                            <c:forEach items="${landingForm.currentQueues}" var="store" varStatus="status">
                                            <tr>
                                                <td>${status.count}&nbsp;</td>
                                                <td><a href="/${store.codeQR}/q" target="_blank">${store.displayName}</a></td>
                                                <td>${store.queueUserState.description}</td>
                                                <td>${store.tokenNumber}</td>
                                            </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        No queue joined for today. 
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <h3>Listed on Marketplace</h3>
                        <div class="add-store">
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty landingForm.marketplaceForms}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th>Name</th>
                                                <th>Price</th>
                                                <th>City/Area</th>
                                                <th>Visible Until</th>
                                                <th>Stats</th>
                                                <th></th>
                                                <th></th>
                                            </tr>
                                            <c:forEach items="${landingForm.marketplaceForms}" var="marketplaceForm" varStatus="status">
                                            <tr>
                                                <td>${status.count}&nbsp;</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${marketplaceForm.marketplace.postingExpired}">
                                                            <c:choose>
                                                                <c:when test="${marketplaceForm.marketplace.businessType eq BusinessTypeEnum.PR}">
                                                                    <i class="fas fa-home" style="color:#ff217c;" title="${marketplaceForm.marketplace.businessType.description}"></i>
                                                                    ${marketplaceForm.marketplace.title}
                                                                </c:when>
                                                                <c:when test="${marketplaceForm.marketplace.businessType eq BusinessTypeEnum.HI}">
                                                                    <i class="fas fa-chair" style="color:#ff217c;" title="${marketplaceForm.marketplace.businessType.description}"></i>
                                                                    ${marketplaceForm.marketplace.title}
                                                                </c:when>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:choose>
                                                                <c:when test="${marketplaceForm.marketplace.businessType eq BusinessTypeEnum.PR}">
                                                                    <i class="fas fa-home" style="color:#ff217c;" title="${marketplaceForm.marketplace.businessType.description}"></i>
                                                                    <a href="/access/marketplace/property/edit/${marketplaceForm.marketplace.businessType.name}/${marketplaceForm.marketplace.id}" target="_blank">${marketplaceForm.marketplace.title}</a>
                                                                </c:when>
                                                                <c:when test="${marketplaceForm.marketplace.businessType eq BusinessTypeEnum.HI}">
                                                                    <i class="fas fa-chair" style="color:#ff217c;" title="${marketplaceForm.marketplace.businessType.description}"></i>
                                                                    <a href="/access/marketplace/household/edit/${marketplaceForm.marketplace.businessType.name}/${marketplaceForm.marketplace.id}" target="_blank">${marketplaceForm.marketplace.title}</a>
                                                                </c:when>
                                                            </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${marketplaceForm.marketplace.priceForDisplayWithFormatting}</td>
                                                <td>${marketplaceForm.marketplace.city}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${empty marketplaceForm.marketplace.publishUntil}">
                                                            NA
                                                        </c:when>
                                                        <c:when test="${marketplaceForm.marketplace.postingExpired}">
                                                            <fmt:formatDate pattern="MMMM dd" value="${marketplaceForm.marketplace.publishUntil}"/> <span style="font-size: x-small">(Expired)</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="/access/marketplace/property/${marketplaceForm.marketplace.businessType.name}/${marketplaceForm.marketplace.id}/boost">
                                                                <fmt:formatDate pattern="MMMM dd" value="${marketplaceForm.marketplace.publishUntil}"/> <i class="fas fa-rocket" style="color:#ff217c;" title="Boost"></i>
                                                            </a>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${marketplaceForm.marketplace.viewCount == 0}">
                                                            <i class="fas fa-eye" style="color:#4e4d4d;" title="Viewed"></i> ${marketplaceForm.marketplace.viewCount}
                                                        </c:when>
                                                        <c:otherwise>
                                                            <i class="fas fa-eye" style="color:#ff217c;" title="Viewed"></i> ${marketplaceForm.marketplace.viewCount}
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:choose>
                                                        <c:when test="${marketplaceForm.marketplace.expressedInterestCount == 0}">
                                                            <i class="fas fa-fire" style="color:#4e4d4d;" title="Interest"></i> ${marketplaceForm.marketplace.expressedInterestCount}
                                                        </c:when>
                                                        <c:otherwise>
                                                            <i class="fas fa-fire" style="color:#ff217c;" title="Interest"></i> ${marketplaceForm.marketplace.expressedInterestCount}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <c:choose>
                                                    <c:when test="${marketplaceForm.marketplace.postingExpired}">
                                                        <td>${marketplaceForm.marketplace.postImages.size()} <i class="fas fa-images" style="color:#4e4d4d;" title="Shared Images"></i></a></td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${empty marketplaceForm.marketplace.postImages}">
                                                                <td><a href="/access/marketplace/${marketplaceForm.marketplace.businessType.name}/${marketplaceForm.marketplace.id}/uploadImage">0 <i class="fas fa-images" style="color:#4e4d4d;" title="Shared Images"></i></a></td>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <td><a href="/access/marketplace/${marketplaceForm.marketplace.businessType.name}/${marketplaceForm.marketplace.id}/uploadImage">${marketplaceForm.marketplace.postImages.size()} <i class="fas fa-images" style="color:#ff217c;" title="Shared Images"></i></a></td>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:choose>
                                                    <c:when test="${marketplaceForm.marketplace.validateStatus == ValidateStatusEnum.P}">
                                                        <td>
                                                            <i class="fas fa-ban" style="color:#4e4d4d;" title="${marketplaceForm.marketplace.validateStatus.description}"></i>&nbsp;&nbsp;&nbsp;<a href="/view.htm">View</a>
                                                            <span style="font-size: x-small">(Pending Approval)</span>
                                                        </td>
                                                    </c:when>
                                                    <c:when test="${marketplaceForm.marketplace.validateStatus == ValidateStatusEnum.A}">
                                                        <td>
                                                            <i class="fas fa-check-circle" style="color:#ff217c;" title="${marketplaceForm.marketplace.validateStatus.description}"></i>&nbsp;&nbsp;&nbsp;<a href="/view.htm">View</a>
                                                            <span style="font-size: x-small">(Approved)</span>
                                                        </td>
                                                    </c:when>
                                                    <c:when test="${marketplaceForm.marketplace.validateStatus == ValidateStatusEnum.R}">
                                                        <td>
                                                            <i class="fas fa-redo" style="color:#4e4d4d;" title="Re-Submit"></i>&nbsp;&nbsp;&nbsp;<a href="/view.htm">View</a>
                                                            <span style="font-size: x-small">(Re-Submit)</span>
                                                        </td>
                                                    </c:when>
                                                    <c:when test="${marketplaceForm.marketplace.validateStatus == ValidateStatusEnum.D}">
                                                        <td>
                                                            <i class="fas fa-trash-alt" style="color:#4e4d4d;" title="${marketplaceForm.marketplace.validateStatus.description}"></i>&nbsp;&nbsp;&nbsp;<a href="/view.htm">View</a>
                                                            <span style="font-size: x-small">(Deleted)</span>
                                                        </td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td>
                                                            <i class="fas fa-trash-alt" style="color:#4e4d4d;" title="${marketplaceForm.marketplace.validateStatus.description}"></i>&nbsp;&nbsp;&nbsp;<a href="/view.htm">View</a>
                                                            <span style="font-size: x-small">(Submit)</span>
                                                        </td>
                                                    </c:otherwise>
                                                </c:choose>
                                            </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        No posting on marketplace.
                                    </c:otherwise>
                                </c:choose>
                            </div>

                        </div>

                        <h3>Historical Queue</h3>
                        <div class="add-store">
                            <div class="store-table">
                                <c:choose>
                                    <c:when test="${!empty landingForm.historicalQueues}">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <th>&nbsp;</th>
                                                <th>Name</th>
                                                <th>Date Serviced</th>
                                                <th>Hour Saved</th>
                                                <th>Rating</th>
                                            </tr>
                                            <c:forEach items="${landingForm.historicalQueues}" var="store" varStatus="status">
                                            <tr>
                                                <td>${status.count}&nbsp;</td>
                                                <td>
                                                    <a href="/${store.codeQR}/q" target="_blank">${store.displayName}</a>
                                                </td>
                                                <td>
                                                    <fmt:formatDate pattern="MMMM dd, yyyy hh:mm a" value="${store.serviceEndTime}"/>
                                                </td>
                                                <td>
                                                    ${store.hoursSaved}
                                                </td>
                                                <td>
                                                    ${store.ratingCount}
                                                </td>
                                            </tr>
                                            </c:forEach>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        You don't have any recent history.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static/internal/js/script.js"></script>
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
