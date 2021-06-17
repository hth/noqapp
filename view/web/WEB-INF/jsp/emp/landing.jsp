<%@ page import="com.noqapp.domain.types.BusinessTypeEnum, com.noqapp.domain.types.ValidateStatusEnum" %>
<%@ include file="../include.jsp"%>
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
        <div class="logo-left"><a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static/internal/img/logo.png" alt="NoQueue"/></a></div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName" /></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static/internal/img/menu-top-arrow.png" /></div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/emp/landing/account/access">Permissions</a>
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
                    <div class="store">
                        <h3>Total business awaiting approvals: <span>${empLandingForm.awaitingApprovalCount}</span></h3>
                        <c:choose>
                        <c:when test="${!empty empLandingForm.businessUsers}">
                        <div class="add-store">
                            <div class="store-table">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th width="4%">&nbsp;</th>
                                        <th width="64%">Business Name</th>
                                        <th width="32%">Since</th>
                                    </tr>
                                    <c:forEach items="${empLandingForm.businessUsers}" var="businessUser" varStatus="status">
                                    <tr>
                                        <td>${status.count}&nbsp;</td>
                                        <td><a href="/emp/landing/${businessUser.id}">${businessUser.bizName.businessName}</a></td>
                                        <td>
                                            <fmt:formatDate pattern="MMMM dd, yyyy" value="${businessUser.updated}"/>
                                            <span class="light-color">&nbsp;<fmt:formatDate value="${businessUser.updated}" type="time"/></span>
                                        </td>
                                    </tr>
                                    </c:forEach>
                                </table>
                            </div>
                        </div>
                        </c:when>
                        <c:otherwise>
                        <div class="alert-info">
                            <div class="no-approve">There are no new business to approve.</div>
                        </div>
                        </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <br/>
                <div class="admin-content">
                    <div class="store">
                        <h3>Total article awaiting approvals: <span>${empLandingForm.publishArticles.size()}</span></h3>
                        <c:choose>
                        <c:when test="${!empty empLandingForm.publishArticles}">
                        <div class="add-store">
                            <div class="store-table">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th width="4%">&nbsp;</th>
                                        <th width="64%">Article Title</th>
                                        <th width="20%">Category</th>
                                        <th width="12%"></th>
                                    </tr>
                                    <c:forEach items="${empLandingForm.publishArticles}" var="publishArticle" varStatus="status">
                                    <tr>
                                        <td>${status.count}&nbsp;</td>
                                        <td>${publishArticle.title}</td>
                                        <td>${publishArticle.businessType.description}</td>
                                        <td><a href="${pageContext.request.contextPath}/emp/landing/publishArticle/${publishArticle.id}/preview" class="add-btn" style="margin: 0px;">Preview</a></td>
                                    </tr>
                                    </c:forEach>
                                </table>
                            </div>
                        </div>
                        </c:when>
                        <c:otherwise>
                        <div class="alert-info">
                            <div class="no-approve">There are no new article to approve.</div>
                        </div>
                        </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <br/>
                <div class="admin-content">
                    <div class="store">
                        <h3>Total advertisement awaiting approvals: <span>${empLandingForm.awaitingAdvertisementApprovals.size()}</span></h3>
                        <c:choose>
                        <c:when test="${!empty empLandingForm.awaitingAdvertisementApprovals}">
                        <div class="add-store">
                            <div class="store-table">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th>Title</th>
                                        <th>Advt Type</th>
                                        <th>Display At</th>
                                        <th>Visible From</th>
                                        <th>Visible Until</th>
                                        <th nowrap>
                                            Created
                                            &nbsp;
                                            <img src="${pageContext.request.contextPath}/static/internal/img/sortAZ.png"
                                                    alt="Sort" height="16px;"/>
                                        </th>
                                        <th></th>
                                    </tr>
                                    <c:forEach items="${empLandingForm.awaitingAdvertisementApprovals}" var="advertisement" varStatus="status">
                                    <tr>
                                        <td>
                                            <span style="display:block; font-size:13px;">${status.count}&nbsp;</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${advertisement.title}</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${advertisement.advertisementType.description}</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${advertisement.advertisementDisplay.description}</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;"><fmt:formatDate value="${advertisement.publishDate}" pattern="yyyy-MM-dd"/></span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;"><fmt:formatDate value="${advertisement.endDate}" pattern="yyyy-MM-dd"/></span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;"><fmt:formatDate value="${advertisement.created}" pattern="yyyy-MM-dd"/></span>
                                        </td>
                                        <td><a href="${pageContext.request.contextPath}/emp/advertisement/approval/${advertisement.id}/preview" class="add-btn" style="margin: 0px;">Preview</a></td>
                                    </tr>
                                    </c:forEach>
                                </table>
                            </div>
                        </div>
                        </c:when>
                        <c:otherwise>
                        <div class="alert-info">
                            <div class="no-approve">There are no new advertisement to approve.</div>
                        </div>
                        </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <br/>
                <div class="admin-content">
                    <div class="store">
                        <h3>Total marketplace awaiting approvals: <span>${empLandingForm.awaitingMarketplaceApprovals.size()}</span></h3>
                        <c:choose>
                        <c:when test="${!empty empLandingForm.awaitingMarketplaceApprovals}">
                            <div class="add-store">
                                <div class="store-table">
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <th>&nbsp;</th>
                                            <th>Name</th>
                                            <th>Price</th>
                                            <th>City/Area</th>
                                            <th>Expires On</th>
                                            <th></th>
                                        </tr>
                                        <c:forEach items="${empLandingForm.awaitingMarketplaceApprovals}" var="marketplaceForm" varStatus="status">
                                        <tr>
                                            <td>${status.count}&nbsp;</td>
                                            <td>
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
                                            </td>
                                            <td>${marketplaceForm.marketplace.priceForDisplay}</td>
                                            <td>${marketplaceForm.marketplace.city}</td>
                                            <td>
                                                <fmt:formatDate pattern="MMMM dd, yyyy" value="${marketplaceForm.marketplace.publishUntil}"/>
                                            </td>
                                            <td><a href="${pageContext.request.contextPath}/emp/marketplace/approval/${marketplaceForm.marketplace.id}/${marketplaceForm.marketplace.businessType}/preview" class="add-btn" style="margin: 0px;">Preview</a></td>
                                        </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                        <div class="alert-info">
                            <div class="no-approve">There are no new marketplace to approve.</div>
                        </div>
                        </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <br/>
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

</html>
