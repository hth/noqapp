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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/intl-tel-input/css/intlTelInput.css">
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
                        <a href="${pageContext.request.contextPath}/">Home</a>
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
                        <h3>Customer History</h3>
                        <br/>
                        <form:form method="POST" action="./landing.htm" modelAttribute="customerHistoryForm">
                            <form:hidden path="lookupPhone" />
                            <ul class="list-form">
                                <li>
                                    <div class="col-lable3">
                                        <form:label path="lookupPhone" cssErrorClass="lb_error">Lookup by Phone</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <input name="phone" id="phone" type="tel" class="form-fe" pattern="\[0-9\s\-\(\)]+" placeholder="Please fill the phone number" />
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                                <li class="mB0">
                                    <div class="col-lable3"></div>
                                    <div class="col-fields">
                                        <div class="left-btn">
                                            <input type="submit" value="SEARCH" class="next-btn" name="search" onclick="getPhoneNumberFromUserInput();">
                                        </div>
                                        <div class="right-btn">
                                            <input type="submit" value="CANCEL" class="cancel-btn" name="cancel">
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>
                        </form:form>
                    </div>
                </div>
            </div>

            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                    <c:choose>
                        <c:when test="${!empty customerHistoryForm.lookupPhone}">
                            <c:choose>
                                <c:when test="${customerHistoryForm.businessCustomer eq false}">
                                    <div class="alert-info">
                                        <p>Could not find customer ${userProfile.formattedPhone} associated to your business.</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="store">
                                        <div class="add-store">
                                            <div class="details-box" style="padding: 10px 0 10px 0;">
                                                <h3>Account Owner: ${customerHistoryForm.userProfile.phoneFormatted}</h3>
                                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                    <tr>
                                                        <td><span style="display:block; font-size:13px;">${customerHistoryForm.userProfile.name}</span></td>
                                                        <td><span style="display:block; font-size:13px;">${customerHistoryForm.userProfile.ageAsString}</span></td>
                                                        <td><span style="display:block; font-size:13px;">${customerHistoryForm.userProfile.gender}</span></td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="3"><span style="display:block; font-size:13px;">Address: ${customerHistoryForm.userProfile.address}</span></td>
                                                    </tr>
                                                </table>
                                            </div>

                                            <div class="details-box" style="padding: 10px 0 10px 0;">
                                                <h3>Dependents:</h3>
                                                <c:choose>
                                                    <c:when test="${!empty customerHistoryForm.userProfileOfDependents}">
                                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                        <c:forEach items="${customerHistoryForm.userProfileOfDependents}" var="userProfile" varStatus="status">
                                                        <tr>
                                                            <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                                            <td><span style="display:block; font-size:13px;">${userProfile.name}</span></td>
                                                            <td><span style="display:block; font-size:13px;">${userProfile.ageAsString}</span></td>
                                                            <td><span style="display:block; font-size:13px;">${userProfile.gender}</span></td>
                                                            <td><span style="display:block; font-size:13px;">${userProfile.address}</span></td>
                                                        </tr>
                                                        </c:forEach>
                                                    </table>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="alert-info">
                                                            <p>No dependents</p>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="clearFix"></div>
                                        </div>

                                        <h3>Today's Purchase Order</h3>
                                        <div class="add-store">
                                            <div class="store-table">
                                                <c:choose>
                                                    <c:when test="${!empty customerHistoryForm.currentPurchaseOrders}">
                                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <th>&nbsp;</th>
                                                                <th nowrap>Name</th>
                                                                <th nowrap>Amount</th>
                                                                <th nowrap>Paid Via</th>
                                                                <th>Order State</th>
                                                                <th>User Rating</th>
                                                                <th>Date of Service</th>
                                                            </tr>
                                                            <c:forEach items="${customerHistoryForm.currentPurchaseOrders}" var="purchaseOrder" varStatus="status">
                                                            <tr>
                                                                <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${customerHistoryForm.qidNameMaps.get(purchaseOrder.queueUserId)}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${purchaseOrder.orderPriceForDisplay}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${purchaseOrder.paymentMode.description}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${purchaseOrder.presentOrderState.description}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">
                                                                        <c:choose>
                                                                            <c:when test="${queue.ratingCount > 0}">
                                                                                ${queue.ratingCount} <br/>
                                                                                ${queue.review}
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                N/A
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;"><fmt:formatDate pattern="MMMM dd, yyyy hh:mm a" value="${purchaseOrder.updated}"/></span>
                                                                </td>
                                                            </tr>
                                                            </c:forEach>
                                                        </table>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="alert-info">
                                                            <p>No history</p>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="clearFix"></div>
                                        </div>

                                        <h3>Historical Purchase Order</h3>
                                        <div class="add-store">
                                            <div class="store-table">
                                                <c:choose>
                                                    <c:when test="${!empty customerHistoryForm.historicalPurchaseOrders}">
                                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <th>&nbsp;</th>
                                                                <th nowrap>Name</th>
                                                                <th nowrap>Amount</th>
                                                                <th nowrap>Paid Via</th>
                                                                <th>Order State</th>
                                                                <th>User Rating</th>
                                                                <th>Date of Service</th>
                                                            </tr>
                                                            <c:forEach items="${customerHistoryForm.historicalPurchaseOrders}" var="purchaseOrder" varStatus="status">
                                                            <tr>
                                                                <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${customerHistoryForm.qidNameMaps.get(purchaseOrder.queueUserId)}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${purchaseOrder.orderPriceForDisplay}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${purchaseOrder.paymentMode.description}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${purchaseOrder.presentOrderState.description}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">
                                                                        <c:choose>
                                                                            <c:when test="${queue.ratingCount > 0}">
                                                                                ${queue.ratingCount} <br/>
                                                                                ${queue.review}
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                N/A
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;"><fmt:formatDate pattern="MMMM dd, yyyy hh:mm a" value="${purchaseOrder.updated}"/></span>
                                                                </td>
                                                            </tr>
                                                            </c:forEach>
                                                        </table>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="alert-info">
                                                            <p>No history</p>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="clearFix"></div>
                                        </div>

                                        <h3>Queue Current and Historical</h3>
                                        <div class="add-store">
                                            <div class="store-table">
                                                <c:choose>
                                                    <c:when test="${!empty customerHistoryForm.currentAndHistoricalQueues}">
                                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                        <tr>
                                                            <th>&nbsp;</th>
                                                            <th nowrap>Name</th>
                                                            <th nowrap>Queue Name</th>
                                                            <th nowrap>End State</th>
                                                            <th>User Rating</th>
                                                            <th>Date of Service</th>
                                                        </tr>
                                                        <c:forEach items="${customerHistoryForm.currentAndHistoricalQueues}" var="queue" varStatus="status">
                                                            <tr>
                                                                <td><span style="display:block; font-size:13px;">${status.count}&nbsp;</span></td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${customerHistoryForm.qidNameMaps.get(queue.queueUserId)}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${queue.displayName}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">${queue.queueUserState.description}</span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;">
                                                                        <c:choose>
                                                                            <c:when test="${queue.ratingCount > 0}">
                                                                                ${queue.ratingCount} <br/>
                                                                                ${queue.review}
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                N/A
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </span>
                                                                </td>
                                                                <td nowrap>
                                                                    <span style="display:block; font-size:13px;"><fmt:formatDate pattern="MMMM dd, yyyy hh:mm a" value="${queue.updated}"/></span>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </table>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="alert-info">
                                                            <p>No history</p>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <div class="alert-info">
                                <p>Enter phone number to lookup customer history.</p>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/intl-tel-input/js/intlTelInput.js"></script>
<script>
    $("#phone").intlTelInput({
        // allowDropdown: false,
        // autoHideDialCode: false,
        // autoPlaceholder: "off",
        // dropdownContainer: "body",
        // excludeCountries: ["us"],
        // formatOnDisplay: false,
        // geoIpLookup: function(callback) {
        //   $.get("http://ipinfo.io", function() {}, "jsonp").always(function(resp) {
        //     var countryCode = (resp && resp.country) ? resp.country : "";
        //     callback(countryCode);
        //   });
        // },
        // initialCountry: "auto",
        // nationalMode: false,
        // onlyCountries: ['us', 'gb', 'ch', 'ca', 'do'],
        // placeholderNumberType: "MOBILE",
        preferredCountries: ['in', 'us'],
        // separateDialCode: true,
        utilsScript: "${pageContext.request.contextPath}/static2/external/intl-tel-input/js/utils.js"
    });

    function getPhoneNumberFromUserInput() {
        var ccode= $('.selected-flag').attr('title');
        var index = ccode.indexOf(':');
        var phone = document.getElementById('phone').value;
        if (phone === "") {
            $(".mdl-textfield__error").text("Please enter valid a phone number");
            $("#mdl-textfield").show();
            return;
        }
        document.getElementById('lookupPhone').value = (ccode.substring(index+1) + phone);
        return;
    }
</script>
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>
