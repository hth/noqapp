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
            <a href="${pageContext.request.contextPath}"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue Inc"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/">Home</a>
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
            <sec:authorize access="hasAnyRole('ROLE_S_MANAGER', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')">
            <!-- Add New Supervisor -->
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <h3><span>${storeManagerForm.bizName}</span></h3>

                        <div class="add-store">
                            <div class="addbtn-store">
                                <c:choose>
                                    <c:when test="${not empty storeManagerForm.businessTypeMap[BusinessTypeEnum.DO]}">
                                        <div class="alert-info" style="text-align: left">
                                            <p>Contact Administrator to modify store details</p>
                                        </div>
                                        <a href="/business/store/publishArticle/landing.htm" class="add-btn">Publish Article</a>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- Currently Managers are not supported to add new store. -->
                                        <a href="/business/addStore.htm" class="add-btn">Add new store</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="store-table">
                            <c:choose>
                            <c:when test="${!empty storeManagerForm.bizStores}">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th>Store Location</th>
                                        <th>Queue Name</th>
                                        <th>Rating & AHT</th>
                                        <th nowrap>Create Date</th>
                                        <th></th>
                                    </tr>
                                    <c:forEach items="${storeManagerForm.bizStores}" var="store" varStatus="status">
                                    <tr>
                                        <td>${status.count}&nbsp;</td>
                                        <td>
                                            <a href="/business/detail/store/${store.id}.htm">
                                                <span style="display:block; font-size:13px;">${store.addressWrappedFunky}</span>
                                            </a>
                                            <br/>
                                            <c:choose>
                                                <c:when test="${BusinessTypeEnum.RS eq store.businessType
                                                || BusinessTypeEnum.FT eq store.businessType
                                                || BusinessTypeEnum.BA eq store.businessType
                                                || BusinessTypeEnum.ST eq store.businessType
                                                || BusinessTypeEnum.GS eq store.businessType
                                                || BusinessTypeEnum.CF eq store.businessType
                                                || BusinessTypeEnum.PH eq store.businessType
                                                || BusinessTypeEnum.HS eq store.businessType}">
                                                    <span style="display:block; font-size:13px;"><a href="/business/store/product/${store.id}.htm" style="color: #1c1c1c;">Product List</a>
                                                        &nbsp; <span style="font-size:18px;">|</span> &nbsp;
                                                        <a href="/business/store/category/${store.id}.htm" style="color: #1c1c1c;">Store Category</a>
                                                    </span>
                                                    <span style="display:block; font-size:13px;"><a href="/business/store/photo/uploadServicePhoto/${store.codeQR}.htm" style="color: #1c1c1c;">Menu Image</a>
                                                        &nbsp; <span style="font-size:18px;">|</span> &nbsp;
                                                        <a href="/business/store/photo/uploadInteriorPhoto/${store.codeQR}.htm" style="color: #1c1c1c;">Interior or Exterior Image</a>
                                                    </span>
                                                    <span style="display:block; font-size:13px;">
                                                        <a href="/business/store/product/bulk/${store.codeQR}.htm" class="add-btn">Bulk Product Change</a>
                                                    </span>
                                                </c:when>
                                                <c:when test="${BusinessTypeEnum.BK eq store.businessType || BusinessTypeEnum.HS eq store.businessType}">
                                                    <span style="display:block; font-size:13px;">
                                                        <a href="/business/store/photo/uploadInteriorPhoto/${store.codeQR}.htm" style="color: #1c1c1c;">Interior or Exterior Image</a>
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="display:block; font-size:13px;">Blank Here</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td nowrap>
                                            <a href="/${store.codeQR}/q.htm" target="_blank">
                                                <span style="display:block; font-size:13px;">${store.displayName}</span>
                                            </a>
                                            <br/>
                                            <span style="display:block; font-size:13px;">Serving: ${storeManagerForm.tokenQueues.get(store.codeQR).currentlyServing}</span>
                                            <span style="display:block; font-size:13px;">In Queue: ${storeManagerForm.tokenQueues.get(store.codeQR).lastNumber - storeManagerForm.tokenQueues.get(store.codeQR).currentlyServing}</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">Rating: ${store.ratingFormatted} (Count: ${store.reviewCount})</span>
                                            <br/>
                                            <span style="display:block; font-size:13px;">AHT: ${store.averageServiceTimeFormatted}</span>
                                        </td>
                                        <td nowrap>
                                            <span style="display:block; font-size:13px;"><fmt:formatDate pattern="MMM dd, yyyy" value="${store.created}"/></span>
                                        </td>
                                        <td>
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
                                Found no stores.
                            </c:otherwise>
                            </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Add New Supervisor -->
            </sec:authorize>
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
