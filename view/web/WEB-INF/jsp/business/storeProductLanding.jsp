<%@ page import="com.noqapp.domain.types.BusinessTypeEnum,com.noqapp.domain.types.ProductTypeEnum" %>
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
                <button onclick="myFunction()" class="dropbtn">
                    <sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow">
                        <img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
                    <div class="dropdown-inner">
                        <a href="${pageContext.request.contextPath}/business/external/access.htm">Permissions</a>
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
                        <div class="add-store">
                            <div class="addbtn-store">
                                <c:choose>
                                <c:when test="${!empty storeProductForm.storeProductId}">
                                <div class="admin-content">
                                    <div class="admin-title">
                                        <h3>Edit Product for <span>${storeProductForm.displayName}</span></h3>
                                    </div>
                                </div>

                                <form:form method="post" action="${pageContext.request.contextPath}/business/store/product/edit.htm" modelAttribute="storeProductForm">
                                    <form:hidden path="bizStoreId" />
                                    <form:hidden path="storeProductId" />
                                    <form:hidden path="businessType" />

                                    <spring:hasBindErrors name="storeProductForm">
                                    <div class="error-box">
                                        <div class="error-txt">
                                            <ul>
                                                <c:if test="${errors.hasFieldErrors('productName')}">
                                                <li><form:errors path="productName"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('productPrice')}">
                                                <li><form:errors path="productPrice"/></li>
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
                                                        <form:label path="productName" cssErrorClass="lb_error">New Product</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                    placeholder="Name of the product"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productPrice" cssErrorClass="lb_error">Price of Product</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productPrice" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                    placeholder="Price of the product"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productDiscount" cssErrorClass="lb_error">Special Discount %</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productDiscount" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                    placeholder="Any specific discount on product"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productInfo" cssErrorClass="lb_error">Description</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productInfo" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                    placeholder="Describe the product"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="storeCategoryId" cssErrorClass="lb_error">Store Category</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="storeCategoryId" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                            <%--&lt;%&ndash;<form:option value="NONE" label="--- Select ---"/>&ndash;%&gt; Bug in 5.0.2--%>
                                                            <c:forEach items="${storeProductForm.categories}" var="category" varStatus="cnt">
                                                                <c:choose>
                                                                    <c:when test="${category.key eq storeProductForm.storeCategoryId}">
                                                                        <option value="${category.key}" selected="selected">${category.value}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${category.key}">${category.value}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </form:select>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productType" cssErrorClass="lb_error">Product Categorization</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="productType" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                            <c:forEach items="${storeProductForm.productTypes}" var="productType">
                                                                <c:choose>
                                                                    <c:when test="${productType eq storeProductForm.productType.text}">
                                                                        <option value="${productType.name}" selected="selected">${productType.description}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${productType.name}">${productType.description}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </form:select>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="unitOfMeasurement" cssErrorClass="lb_error">Unit</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="unitOfMeasurement" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                            <c:forEach items="${storeProductForm.unitOfMeasurements}" var="unitOfMeasurement">
                                                                <c:choose>
                                                                    <c:when test="${unitOfMeasurement eq storeProductForm.unitOfMeasurement.text}">
                                                                        <option value="${unitOfMeasurement.name}" selected="selected">${unitOfMeasurement.description}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${unitOfMeasurement.name}">${unitOfMeasurement.description}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </form:select>
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
                                <div class="admin-content">
                                    <div class="admin-title">
                                        <h3>Add Product for <span>${storeProductForm.displayName}</span></h3>
                                    </div>
                                </div>

                                <form:form method="post" action="${pageContext.request.contextPath}/business/store/product/add.htm" modelAttribute="storeProductForm">
                                    <form:hidden path="bizStoreId" />
                                    <form:hidden path="businessType" />

                                    <spring:hasBindErrors name="storeProductForm">
                                    <div class="error-box">
                                        <div class="error-txt">
                                            <ul>
                                                <c:if test="${errors.hasFieldErrors('productName')}">
                                                <li><form:errors path="productName"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('productPrice')}">
                                                <li><form:errors path="productPrice"/></li>
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
                                                        <form:label path="productName" cssErrorClass="lb_error">New Product</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                    placeholder="Name of the product"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productPrice" cssErrorClass="lb_error">Price of Product</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productPrice" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                    placeholder="Price of the product"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productDiscount" cssErrorClass="lb_error">Special Discount %</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productDiscount" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                    placeholder="Any specific discount on product"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productInfo" cssErrorClass="lb_error">Description</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productInfo" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                    placeholder="Describe the product"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="storeCategoryId" cssErrorClass="lb_error">Store Category</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="storeCategoryId" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                        <%--&lt;%&ndash;<form:option value="NONE" label="--- Select ---"/>&ndash;%&gt; Bug in 5.0.2--%>
                                                        <form:options items="${storeProductForm.categories}" />
                                                        </form:select>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productType" cssErrorClass="lb_error">Product Categorization</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="productType" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                        <c:forEach items="${storeProductForm.productTypes}" var="productType">
                                                        <option value="${productType.name}">${productType.description}</option>
                                                        </c:forEach>
                                                        </form:select>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="unitOfMeasurement" cssErrorClass="lb_error">Units</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="unitOfMeasurement" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                            <c:forEach items="${storeProductForm.unitOfMeasurements}" var="unitOfMeasurement">
                                                                <option value="${unitOfMeasurement.name}">${unitOfMeasurement.description}</option>
                                                            </c:forEach>
                                                        </form:select>
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
                            </div>
                            <div class="store-table">
                                <c:choose>
                                <c:when test="${!empty storeProductForm.storeProducts}">
                                <div class="alert-info">
                                    Users can directly purchase product from '${storeProductForm.displayName}' when listed here.
                                </div>
                                <h2>Total Products: <span>${storeProductForm.storeProducts.size()}</span></h2>

                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th style="text-align: left;" nowrap>
                                            Name
                                            &nbsp;
                                            <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                                 alt="Sort" height="16px;"/>
                                        </th>
                                        <th style="text-align: left;">Price</th>
                                        <th style="text-align: left;">Special Discount</th>
                                        <th style="text-align: left;">Store Category</th>
                                        <th style="text-align: left;">Categorization</th>
                                        <th style="text-align: left;">Units</th>
                                        <th nowrap></th>
                                        <th nowrap></th>
                                    </tr>
                                    <c:forEach items="${storeProductForm.storeProducts}" var="storeProduct" varStatus="status">
                                    <tr>
                                        <td>${status.count}&nbsp;</td>
                                        <td nowrap>
                                            ${storeProduct.productName}
                                            <span style="display:block; font-size:13px;">${storeProduct.productInfo}</span>
                                        </td>
                                        <td nowrap>${storeProduct.displayPrice}</td>
                                        <td nowrap>${storeProduct.displayDiscount}%</td>
                                        <td nowrap>${storeProductForm.categories.get(storeProduct.storeCategoryId)}</td>
                                        <td nowrap>${storeProduct.productType.description}</td>
                                        <td nowrap>${storeProduct.unitOfMeasurement.description}</td>
                                        <td nowrap>
                                            <a href="/business/store/product/${storeProductForm.bizStoreId}/${storeProduct.id}/edit.htm" class="add-btn">Edit</a>
                                        </td>
                                        <td nowrap>
                                            <form:form method="post" action="${pageContext.request.contextPath}/business/store/product/delete.htm" modelAttribute="storeProductForm">
                                                <form:hidden path="bizStoreId" value="${storeProductForm.bizStoreId}" />
                                                <form:hidden path="storeProductId" value="${storeProduct.id}" />
                                                <button name="delete" class="add-btn">Delete</button>
                                            </form:form>
                                        </td>
                                    </tr>
                                    </c:forEach>
                                </table>
                                </c:when>
                                <c:otherwise>
                                <div class="alert-info">
                                    <p>No product added.</p>
                                    <p>
                                        What's Store Product?
                                        Product listed here shows up in '${storeProductForm.displayName}'. Users can directly purchase product and place order to store.
                                    </p>
                                    <p>
                                        Note: Add description where necessary.
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
