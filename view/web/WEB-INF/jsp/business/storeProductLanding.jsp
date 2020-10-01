<%@ page import="com.noqapp.domain.types.BusinessTypeEnum,com.noqapp.domain.types.ProductTypeEnum,com.noqapp.domain.types.UnitOfMeasurementEnum" %>
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

    <link rel="stylesheet" href="//cdn.jsdelivr.net/gh/StephanWagner/jBox@v1.0.9/dist/jBox.all.min.css" type='text/css'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>

    <!-- reference your copy Font Awesome here (from our CDN or by hosting yourself) -->
    <link href="${pageContext.request.contextPath}/static2/external/fontawesome/css/fontawesome.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static2/external/fontawesome/css/brands.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static2/external/fontawesome/css/solid.css" rel="stylesheet">

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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
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
                                                <c:if test="${errors.hasFieldErrors('storeCategoryId')}">
                                                <li><form:errors path="storeCategoryId"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('productName')}">
                                                <li><form:errors path="productName"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('productPrice')}">
                                                <li><form:errors path="productPrice"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('unitValue')}">
                                                <li><form:errors path="unitValue"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('unitOfMeasurement')}">
                                                <li><form:errors path="unitOfMeasurement"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('packageSize')}">
                                                <li><form:errors path="packageSize"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('productType')}">
                                                <li><form:errors path="productType"/></li>
                                                </c:if>
                                            </ul>
                                        </div>
                                    </div>
                                    </spring:hasBindErrors>

                                    <c:if test="${!empty storeProductForm.message}">
                                    <div class="alert-info" style="text-align: left;">
                                        <p>
                                            <span style="display:block; font-size:13px; text-align: center;"><c:out value="${storeProductForm.message}" /> -- <a href="#products">(product list of #${storeProductForm.storeProducts.size()})</a></span>
                                        </p>
                                    </div>
                                    </c:if>

                                    <div class="admin-content">
                                        <div class="add-new">
                                            <ul class="list-form">
                                                <li>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="col-lable3">
                                                        <form:label path="storeCategoryId" cssErrorClass="lb_error">Category</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="storeCategoryId" cssClass="form-field-select single-dropdown"
                                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                            <form:option value="" label="--- Select ---"/>
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
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="col-lable3">
                                                        <form:label path="productName" cssErrorClass="lb_error">Name</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                    placeholder="Name of the product"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
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
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="col-lable3">
                                                        <form:label path="unitValue" cssErrorClass="lb_error">Unit</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="unitValue" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Number"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="col-lable3">
                                                        <form:label path="unitOfMeasurement" cssErrorClass="lb_error">Measurement</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="unitOfMeasurement" cssClass="form-field-select single-dropdown"
                                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                            <form:option value="" label="--- Select ---"/>
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
                                                <li>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="col-lable3">
                                                        <form:label path="packageSize" cssErrorClass="lb_error">Package Size</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="packageSize" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Number"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="col-lable3">
                                                        <form:label path="productType" cssErrorClass="lb_error">Product Categorization</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="productType" cssClass="form-field-select single-dropdown" cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                            <form:option value="" label="--- Select ---"/>
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
                                                        <form:label path="inventoryLimit" cssErrorClass="lb_error">Inventory</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="inventoryLimit" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Number"/>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productDiscount" cssErrorClass="lb_error">Product Discount</form:label>
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
                                                        <form:label path="displayCaseTurnedOn" cssErrorClass="lb_error">Put on Display</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:checkbox path="displayCaseTurnedOn" cssClass="form-check-box" cssErrorClass="form-check-box error-field" cssStyle="float:left; padding:5px 0;"
                                                                placeholder="Show product on home screen"/>
                                                    </div>
                                                    <span class="tooltip" title="Display on store home screen"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">&nbsp;</sup>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                    </div>
                                                    <div class="col-fields">
                                                        Required fields are marked with <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                            </ul>
                                            <div class="col-lable3"></div>
                                            <div class="col-fields">
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
                                                <c:if test="${errors.hasFieldErrors('storeCategoryId')}">
                                                <li><form:errors path="storeCategoryId"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('productName')}">
                                                <li><form:errors path="productName"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('productPrice')}">
                                                <li><form:errors path="productPrice"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('unitValue')}">
                                                <li><form:errors path="unitValue"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('unitOfMeasurement')}">
                                                <li><form:errors path="unitOfMeasurement"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('packageSize')}">
                                                <li><form:errors path="packageSize"/></li>
                                                </c:if>

                                                <c:if test="${errors.hasFieldErrors('productType')}">
                                                <li><form:errors path="productType"/></li>
                                                </c:if>
                                            </ul>
                                        </div>
                                    </div>
                                    </spring:hasBindErrors>


                                    <c:if test="${!empty storeProductForm.message}">
                                    <div class="alert-info" style="text-align: left;">
                                        <p>
                                            <span style="display:block; font-size:13px; text-align: center;"><c:out value="${storeProductForm.message}" /> -- <a href="#products">(product list of #${storeProductForm.storeProducts.size()})</a></span>
                                        </p>
                                    </div>
                                    </c:if>

                                    <div class="admin-content">
                                        <div class="add-new">
                                            <ul class="list-form">
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="storeCategoryId" cssErrorClass="lb_error">Category</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="storeCategoryId" cssClass="form-field-select single-dropdown"
                                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                            <form:option value="" label="--- Select ---"/>
                                                            <form:options items="${storeProductForm.categories}" />
                                                        </form:select>
                                                    </div>
                                                    <span class="tooltip" title="My first tooltip"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productName" cssErrorClass="lb_error">Name</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                    placeholder="Name of the product"/>
                                                    </div>
                                                    <span class="tooltip" title="My first tooltip"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
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
                                                    <span class="tooltip" title="My first tooltip"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="unitValue" cssErrorClass="lb_error">Unit</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="unitValue" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Number (Example: 500gm or 1kg or 2lt or 2dz)"/>
                                                    </div>
                                                    <span class="tooltip" title="My first tooltip"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="unitOfMeasurement" cssErrorClass="lb_error">Measurement</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="unitOfMeasurement" cssClass="form-field-select single-dropdown"
                                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                            <c:if test="${fn:length(storeProductForm.unitOfMeasurements) ne 1}">
                                                                <form:option value="" label="--- Select ---"/>
                                                            </c:if>
                                                            <c:forEach items="${storeProductForm.unitOfMeasurements}" var="unitOfMeasurement">
                                                                <c:choose>
                                                                    <c:when test="${unitOfMeasurement.name eq storeProductForm.unitOfMeasurement.text}">
                                                                        <option value="${unitOfMeasurement.name}" selected>${unitOfMeasurement.description}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${unitOfMeasurement.name}">${unitOfMeasurement.description}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </form:select>
                                                    </div>
                                                    <span class="tooltip" title="My first tooltip"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="packageSize" cssErrorClass="lb_error">Package Size</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="packageSize" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Number"/>
                                                    </div>
                                                    <span class="tooltip" title="My first tooltip"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productType" cssErrorClass="lb_error">Product Categorization</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:select path="productType" cssClass="form-field-select single-dropdown"
                                                                cssErrorClass="form-field-select single-dropdown error-field" multiple="false">
                                                            <c:if test="${fn:length(storeProductForm.productTypes) ne 1}">
                                                                <form:option value="" label="--- Select ---"/>
                                                            </c:if>
                                                            <c:forEach items="${storeProductForm.productTypes}" var="productType">
                                                                <c:choose>
                                                                    <c:when test="${productType.name eq storeProductForm.productType.text}">
                                                                        <option value="${productType.name}" selected>${productType.description}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${productType.name}">${productType.description}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </form:select>
                                                    </div>
                                                    <span class="tooltip" title="My first tooltip"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="inventoryLimit" cssErrorClass="lb_error">Inventory</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="inventoryLimit" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Number"/>
                                                    </div>
                                                    <span class="tooltip" title="My first tooltip"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">&nbsp;</sup>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="productDiscount" cssErrorClass="lb_error">Product Discount</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:input path="productDiscount" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Any specific discount on product"/>
                                                    </div>
                                                    <span class="tooltip" title="Discount on product available. Defaults to no discount."><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">&nbsp;</sup>
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
                                                    <span class="tooltip" title="Describe the product"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">&nbsp;</sup>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                        <form:label path="displayCaseTurnedOn" cssErrorClass="lb_error">Put on Display</form:label>
                                                    </div>
                                                    <div class="col-fields">
                                                        <form:checkbox path="displayCaseTurnedOn" cssClass="form-check-box" cssErrorClass="form-check-box error-field" cssStyle="float:left; padding:5px 0;"
                                                                placeholder="Show product on home screen"/>
                                                    </div>
                                                    <span class="tooltip" title="Display on store home screen"><i class="fas fa-info-circle"></i></span>
                                                    <sup style="color: #9f1313; font-size: 150%;">&nbsp;</sup>
                                                    <div class="clearFix"></div>
                                                </li>
                                                <li>
                                                    <div class="col-lable3">
                                                    </div>
                                                    <div class="col-fields">
                                                        Required fields are marked with <sup style="color: #9f1313; font-size: 150%;">*</sup>
                                                    </div>
                                                    <div class="clearFix"></div>
                                                </li>
                                            </ul>
                                            <div class="col-lable3"></div>
                                            <div class="col-fields">
                                                <div class="button-btn">
                                                    <button name="add" class="ladda-button next-btn" style="width:32%; float: left">Add</button>
                                                    <button name="reset" class="ladda-button cancel-btn" style="width:32%; float: left; margin-left:2%" type="reset">Reset</button>
                                                    <button name="cancel_Add" class="ladda-button cancel-btn" style="width:32%; float: right">Cancel</button>
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
                                <c:if test="${BusinessTypeEnum.PH eq storeProductForm.businessType}">
                                    <form:form method="post" action="${pageContext.request.contextPath}/business/store/product/preferredRefresh.htm" modelAttribute="storeProductForm">
                                        <form:hidden path="bizStoreId" value="${storeProductForm.bizStoreId}" />
                                        <button name="refresh" class="add-btn">Refresh</button>
                                        <span style="display:block; font-size:13px;">Only 3 forced refresh allowed in a month *. Use this when you have made lots of changes to product list & would like to push out an update at the earliest</span>
                                    </form:form>
                                </c:if>

                                <c:choose>
                                <c:when test="${!empty storeProductForm.storeProducts}">
                                <div class="alert-info">
                                    <c:choose>
                                        <c:when test="${BusinessTypeEnum.PH eq storeProductForm.businessType}">
                                            <span style="display:block; font-size:13px;">Preferred business partner such as doctors can prescribe these medicines.</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="display:block; font-size:13px;">Users can directly purchase product from '${storeProductForm.displayName}' when listed here.</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <h2><span style="display:block; font-size:13px;">Total Products: <span id="products">${storeProductForm.storeProducts.size()}</span></span></h2>
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th style="text-align: left;">Category</th>
                                        <th style="text-align: left;" nowrap>
                                            Name
                                            &nbsp;
                                            <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                                 alt="Sort" height="16px;"/>
                                        </th>
                                        <th style="text-align: left;">Price</th>
                                        <th style="text-align: left;">Categorization</th>
                                        <th style="text-align: left;">Units</th>
                                        <th nowrap></th>
                                        <th nowrap></th>
                                        <th nowrap></th>
                                    </tr>
                                    <c:forEach items="${storeProductForm.storeProducts}" var="storeProduct" varStatus="status">
                                    <tr>
                                        <td>
                                            <span style="display:block; font-size:13px;">${status.count}&nbsp;</span>
                                        </td>
                                        <td nowrap>
                                            <span style="display:block; font-size:13px;">${storeProductForm.categories.get(storeProduct.storeCategoryId)}</span>
                                        </td>
                                        <td style="${storeProduct.displayCaseTurnedOn == true ? "background: lightpink" : ""}">
                                            <span style="display:block; font-size:13px;">${storeProduct.productName}</span>
                                            <span style="display:block; font-size:13px;">Inventory: ${storeProduct.inventoryCurrent} out of ${storeProduct.inventoryLimit}</span>
                                            <span style="font-size:13px;">Description: ${storeProduct.productInfo}</span>
                                        </td>
                                        <td nowrap>
                                            <span style="display:block; font-size:13px;">${storeProduct.displayPrice}</span>
                                            <span style="display:block; font-size:13px;">Discount: ${storeProduct.displayDiscount}</span>
                                        </td>
                                        <td style="${storeProduct.displayCaseTurnedOn == true ? "background: lightpink" : ""}" nowrap>
                                            <span style="display:block; font-size:13px;">${storeProduct.productType.description}</span>
                                            <br/>
                                            <span style="display:block; font-size:13px;">${storeProduct.displayCaseTurnedOn == true ? "Is on display" : ""}</span>
                                        </td>
                                        <td nowrap>
                                            <span style="display:block; font-size:13px;">${storeProduct.unitValue_Formatted}${storeProduct.unitOfMeasurement.name.toLowerCase()}</span>
                                            <span style="display:block; font-size:13px;">Package: ${storeProduct.packageSize}</span>
                                        </td>
                                        <td>
                                            <a href="/business/store/product/photo/${storeProductForm.bizStoreId}/${storeProduct.id}/image.htm" class="add-btn">Image</a>
                                        </td>
                                        <td>
                                            <a href="/business/store/product/${storeProductForm.bizStoreId}/${storeProduct.id}/edit.htm" class="add-btn">Edit</a>
                                        </td>
                                        <td>
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
                    <div class="f-left">&copy; 2020 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="//cdn.jsdelivr.net/gh/StephanWagner/jBox@v1.0.9/dist/jBox.all.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/ladda.min.js"></script>
<script type="text/javascript">
    // Bind normal buttons
    Ladda.bind('.button-btn button', {timeout: 6000});
    Ladda.bind('.button-btn button[name=reset]', {timeout: 10});

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
<script>
    new jBox('Tooltip', {
        attach: '.tooltip',
        adjustDistance : {
            top : 105,
            bottom : 150,
            left : 15,
            right : 50
        }
    });
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
