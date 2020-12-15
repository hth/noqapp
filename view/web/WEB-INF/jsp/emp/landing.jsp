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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>
</head>

<body>

<!-- header -->
<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left"><a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a></div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName" /></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png" /></div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/emp/landing/account/access.htm">Permissions</a>
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
                                        <td><a href="/emp/landing/${businessUser.id}.htm">${businessUser.bizName.businessName}</a></td>
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
                                        <td><a href="${pageContext.request.contextPath}/emp/landing/publishArticle/${publishArticle.id}/preview.htm" class="add-btn" style="margin: 0px;">Preview</a></td>
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
                                            <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
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
                                        <td><a href="${pageContext.request.contextPath}/emp/advertisement/approval/${advertisement.id}/preview.htm" class="add-btn" style="margin: 0px;">Preview</a></td>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>

</html>
