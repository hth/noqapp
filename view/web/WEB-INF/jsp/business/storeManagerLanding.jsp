<%@ page import="com.noqapp.domain.types.BusinessTypeEnum" %>
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
                <button onclick="myFunction()" class="dropbtn">
                    <sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow">
                        <img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
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
                                            <p>Contact Administrator to modify details</p>
                                        </div>
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
                                        <th>Serving</th>
                                        <th>In Queue</th>
                                        <th>Rating</th>
                                        <th>Rating Count</th>
                                        <th>Average Service Time</th>
                                        <th nowrap>Create Date</th>
                                    </tr>
                                    <c:forEach items="${storeManagerForm.bizStores}" var="store" varStatus="status">
                                    <tr>
                                        <td>${status.count}&nbsp;</td>
                                        <td>
                                            <a href="/business/detail/store/${store.id}.htm">
                                                <span style="display:block; font-size:13px;">${store.address}</span>
                                            </a>
                                            <c:choose>
                                                <c:when test="${BusinessTypeEnum.ST eq store.businessType}">
                                                    <span style="display:block; font-size:13px;"><a href="/business/store/product/${store.id}.htm" target="_blank">Product List</a> | <a href="/business/store/category/${store.id}.htm" target="_blank">Store Category</a></span>
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
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${storeManagerForm.tokenQueues.get(store.codeQR).currentlyServing}</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${storeManagerForm.tokenQueues.get(store.codeQR).lastNumber - storeManagerForm.tokenQueues.get(store.codeQR).currentlyServing}</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${store.ratingFormatted}</span>
                                        </td>
                                        <td>
                                            <span style="display:block; font-size:13px;">${store.ratingCount}</span>
                                        </td>
                                        <td nowrap>
                                            <span style="display:block; font-size:13px;">${store.averageServiceTimeFormatted}</span>
                                        </td>
                                        <td nowrap>
                                            <span style="display:block; font-size:13px;"><fmt:formatDate pattern="MMM dd, yyyy" value="${store.created}"/></span>
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
                    <div class="f-left">&copy; 2018 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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

</html>
