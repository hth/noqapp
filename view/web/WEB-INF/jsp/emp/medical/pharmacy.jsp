<%@ include file="../../include.jsp" %>
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
            <sec:authorize access="hasAnyRole('ROLE_MEDICAL_TECHNICIAN')">
                <div class="admin-main">
                    <div class="admin-content">
                        <div class="store">
                            <h3>Pharmacy count: <span>${pharmacyForm.pharmacies.size()}</span></h3>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${!empty pharmacyForm.id}">
                            <form:form method="post" action="${pageContext.request.contextPath}/emp/medical/pharmacy/edit.htm" modelAttribute="pharmacyForm">
                                <form:hidden path="id" />
                                <spring:hasBindErrors name="pharmacyForm">
                                    <div class="error-box">
                                        <div class="error-txt">
                                            <ul>
                                                <c:if test="${errors.hasFieldErrors('name')}">
                                                    <li><form:errors path="name"/></li>
                                                </c:if>
                                            </ul>
                                        </div>
                                    </div>
                                </spring:hasBindErrors>

                                <div class="admin-content">
                                    <div class="add-new">
                                        <ul class="list-form">
                                            <li>
                                                <div class="col-lable3">
                                                    <form:label path="name" cssErrorClass="lb_error">Name</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="name" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Vegetarian Food, Drinks, Cardiologist, Orthopedics, ENT"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                        </ul>

                                        <div class="col-lable3"></div>
                                        <div class="col-fields">
                                                <%--<div class="left-btn">--%>
                                                <%--<input name="edit" class="next-btn" value="EDIT" type="submit">--%>
                                                <%--</div>--%>
                                                <%--<div class="right-btn">--%>
                                                <%--<input name="cancel_Edit" class="cancel-btn" value="CANCEL" type="submit">--%>
                                                <%--</div>--%>
                                            <div class="button-btn">
                                                <button name="edit" class="ladda-button next-btn" style="width:48%; float: left">Edit</button>
                                                <button name="cancel_Edit" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                            </div>
                                            <div class="clearFix"></div>
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                </div>
                            </form:form>
                        </c:when>
                        <c:otherwise>
                            <form:form method="post" action="${pageContext.request.contextPath}/emp/medical/pharmacy/add.htm" modelAttribute="pharmacyForm">
                                <spring:hasBindErrors name="pharmacyForm">
                                    <div class="error-box">
                                        <div class="error-txt">
                                            <ul>
                                                <c:if test="${errors.hasFieldErrors('name')}">
                                                    <li><form:errors path="name"/></li>
                                                </c:if>
                                            </ul>
                                        </div>
                                    </div>
                                </spring:hasBindErrors>

                                <div class="admin-content">
                                    <div class="add-new">
                                        <ul class="list-form">
                                            <li>
                                                <div class="col-lable3">
                                                    <form:label path="name" cssErrorClass="lb_error">Name</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="name" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Vegetarian Food, Drinks, Cardiologist, Orthopedics, ENT"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                            <li>
                                                <div class="col-lable3">
                                                    <form:label path="value" cssErrorClass="lb_error">Size</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="value" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Quantity/Amount/Size"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                            <li>
                                                <div class="col-lable3">
                                                    <form:label path="pharmacyMeasurementUnit" cssErrorClass="lb_error">Unit of Measurement</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:select path="pharmacyMeasurementUnit" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                        <%--&lt;%&ndash;<form:option value="NONE" label="--- Select ---"/>&ndash;%&gt; Bug in 5.0.2--%>
                                                        <form:options items="${pharmacyForm.availablePharmacyMeasurementUnit}" itemValue="name" itemLabel="description"/>
                                                    </form:select>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                            <li>
                                                <div class="col-lable3">
                                                    <form:label path="companyName" cssErrorClass="lb_error">Product of Company</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="companyName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Name of the company product belongs to"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                            <li>
                                                <div class="col-lable3">
                                                    <form:label path="referStaticLink" cssErrorClass="lb_error">Web URL</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="referStaticLink" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Static Web Location to detailed information"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                        </ul>

                                        <div class="col-lable3"></div>
                                        <div class="col-fields">
                                                <%--<div class="left-btn">--%>
                                                <%--<input name="add" class="next-btn" value="ADD" type="submit">--%>
                                                <%--</div>--%>
                                                <%--<div class="right-btn">--%>
                                                <%--<input name="cancel_Add" class="cancel-btn" value="CANCEL" type="submit">--%>
                                                <%--</div>--%>
                                            <div class="button-btn">
                                                <button name="add" class="ladda-button next-btn" style="width:48%; float: left">Add</button>
                                                <button name="cancel_Add" class="ladda-button cancel-btn" style="width:48%; float: right">Cancel</button>
                                            </div>
                                            <div class="clearFix"></div>
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                </div>
                            </form:form>
                        </c:otherwise>
                    </c:choose>

                    <div class="store-table">
                    <c:if test="${!empty pharmacyForm.pharmacies}">
                        <div class="alert-info">
                            Store products are listed under the categories defined below. It helps to find relevant product under similar category.
                        </div>
                        <h2>Total products in Pharmacy: <span>${pharmacyForm.pharmacies.size()}</span></h2>

                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <th>&nbsp;</th>
                                <th nowrap>
                                    Name
                                    &nbsp;
                                    <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                         alt="Sort" height="16px;"/>
                                </th>
                                <th>Units</th>
                                <th>Product of Company</th>
                                <th nowrap>Detailed link</th>
                                <th nowrap></th>
                                <th nowrap></th>
                            </tr>
                            <c:forEach items="${pharmacyForm.pharmacies}" var="pharmacy" varStatus="status">
                                <tr>
                                    <td>${status.count}&nbsp;</td>
                                    <td nowrap>${pharmacy.name}</td>
                                    <td nowrap>${pharmacy.value} ${pharmacy.pharmacyMeasurementUnit.description}</td>
                                    <td nowrap>${pharmacy.companyName}</td>
                                    <td nowrap>${pharmacy.referStaticLink}</td>
                                    <td nowrap>
                                        <a href="/emp/medical/pharmacy/${pharmacy.id}/edit.htm" class="add-btn">Edit</a>
                                    </td>
                                    <td nowrap>
                                        <form:form method="post" action="${pageContext.request.contextPath}/emp/medical/pharmacy/delete.htm" modelAttribute="pharmacyForm">
                                            <form:hidden path="id" value="${pharmacy.id}" />
                                            <button name="delete" class="add-btn">Delete</button>
                                        </form:form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </c:if>
                    </div>
                </div>
                <!-- Add New Supervisor -->
            </sec:authorize>
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
                    <div class="f-left">&copy; 2018 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

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

