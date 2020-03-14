<%@ include file="../../../jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="utf-8">
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
                <button onclick="myFunction()" class="dropbtn">
                    <sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow">
                        <img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
                    <div class="dropdown-inner">
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
                <div class="admin-title">
                    <h2>Add User to Stores</h2>
                </div>

                <div class="admin-content">
                    <div class="store">
                        <h3>${authorizedQueueUser.name}</h3>

                        <div class="add-store">
                            <div class="store-table">
                                <span style="float: left;">${authorizedQueueUser.name} is authorized to manage <strong>${authorizedQueueUser.enrolledInStores.size()}</strong> stores out of ${authorizedQueueUser.enrolledInStores.size() + authorizedQueueUser.bizStores.size()} stores</span>
                                <span style="float: right;"><strong>Max managing allowed: </strong>${authorizedQueueUser.queueLimit} stores</span>
                                <div style="clear:both;"></div>
                                <br/>

                                <h2>Stores available to manage</h2>
                                <c:choose>
                                <c:when test="${!empty authorizedQueueUser.bizStores}">
                                <form:form modelAttribute="authorizedQueueUser">
                                    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                                    <div class="alert-info">
                                        <p>Add stores to authorize ${authorizedQueueUser.name} to manage it.</p>
                                    </div>

                                    <div class="error-box">
                                        <div class="error-txt">
                                            <c:if test="${!empty flowRequestContext.messageContext.allMessages}">
                                                <ul>
                                                    <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
                                                    <li>${message.text}</li>
                                                    </c:forEach>
                                                </ul>
                                            </c:if>
                                        </div>
                                    </div>

                                    <div class="add-new">
                                        <div class="col-fields">
                                            <%--<div class="left-btn">--%>
                                                <%--<input name="_eventId_submit" class="next-btn" value="NEXT" type="submit">--%>
                                            <%--</div>--%>
                                            <div class="button-btn">
                                                <button name="_eventId_submit" class="ladda-button next-btn" style="width:48%; float: left">Next</button>
                                                <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                            </div>
                                            <%--<div class="right-btn">--%>
                                                <%--<input name="_eventId_cancel" class="cancel-btn" value="CANCEL" type="submit">--%>
                                            <%--</div>--%>

                                            <div class="clearFix"></div>
                                        </div>
                                    </div>

                                    <div class="clearFix"></div>
                                    <br/>
                                    <form:checkbox path="selectAll" onclick="selectAllClicked(this);"/>&nbsp;Select All

                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <th>&nbsp;</th>
                                            <th>&nbsp;</th>
                                            <th>Store Location</th>
                                            <th nowrap>
                                                Queue Name
                                                &nbsp;
                                                <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                                     alt="Sort" height="16px;"/>
                                            </th>
                                        </tr>
                                        <c:forEach items="${authorizedQueueUser.bizStores}" var="store" varStatus="status">
                                        <tr>
                                            <td style="text-align: center;">
                                                <form:checkbox path="interests" value="${store.id}" />
                                            </td>
                                            <td>
                                                &nbsp;${status.count}
                                            </td>
                                            <td>
                                                <a href="/business/detail/store/${store.id}.htm" target="_blank">${store.addressWrappedFunky}</a>
                                            </td>
                                            <td nowrap>
                                                <a href="/${store.codeQR}/q.htm" target="_blank">${store.displayName}</a>
                                                <c:choose>
                                                <c:when test="${!empty store.bizCategoryId}">
                                                <span style="display:block; font-size:13px;">${authorizedQueueUser.categories.get(store.bizCategoryId)}</span>
                                                </c:when>
                                                <c:otherwise>
                                                <span style="display:block; font-size:13px;">N/A</span>
                                                </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        </c:forEach>
                                    </table>
                                </form:form>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert-info">
                                        <p>${authorizedQueueUser.name} has no more stores left to manage.</p>
                                    </div>
                                </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="store-table">
                                <h2>Enrolled in stores</h2>
                                <c:choose>
                                <c:when test="${!empty authorizedQueueUser.enrolledInStores}">
                                    <div class="alert-info">
                                        <p>${authorizedQueueUser.name} is authorized to manage stores listed below.</p>
                                    </div>

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
                                        </tr>
                                        <c:forEach items="${authorizedQueueUser.enrolledInStores}" var="store" varStatus="status">
                                            <tr>
                                                <td>${status.count}&nbsp;</td>
                                                <td>
                                                    <a href="/business/detail/store/${store.id}.htm" target="_blank">${store.addressWrappedFunky}</a>
                                                </td>
                                                <td nowrap>
                                                    <a href="/${store.codeQR}/q.htm" target="_blank">${store.displayName}</a>
                                                    <span style="display:block; font-size:13px;">${authorizedQueueUser.categories.get(store.bizCategoryId)}</span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert-info">
                                        <p>${authorizedQueueUser.name} is not authorized to manage any store queues.</p>
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
    <!-- Foote End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script type="text/javascript">
    function selectAllClicked(source) {
        var checkboxes = document.getElementsByName('interests');
        for (var i = 0, n = checkboxes.length; i < n; i++) {
            checkboxes[i].checked = source.checked;
        }
    }
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/ladda.min.js"></script>
<script type="text/javascript">
    // Bind normal buttons
    Ladda.bind('.button-btn button', {timeout: 6000});

    // Bind progress buttons and simulate loading progress
    Ladda.bind('.progress-demo button', {
        callback: function (instance) {
            var progress = 0;
            var interval = setInterval(function () {
                progress = Math.min(progress + Math.random() * 0.1, 1);
                instance.setProgress(progress);

                if (progress === 1) {
                    instance.stop();
                    clearInterval(interval);
                }
            }, 200);
        }
    });

    // You can control loading explicitly using the JavaScript API
    // as outlined below:

    // var l = Ladda.create( document.querySelector( 'button' ) );
    // l.start();
    // l.stop();
    // l.toggle();
    // l.isLoading();
    // l.setProgress( 0-1 );
</script>
</html>
