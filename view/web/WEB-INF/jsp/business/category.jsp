<%@ include file="../include.jsp" %>
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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png"/></a>
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
            <sec:authorize access="hasRole('ROLE_M_ADMIN')">
                <div class="admin-main">
                    <div class="admin-content">
                        <div class="store">
                            <h3>Category count: <span>${categoryLanding.categories.size()}</span></h3>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${!empty categoryLanding.categoryId}">
                            <form:form method="post" action="${pageContext.request.contextPath}/business/category/edit.htm" modelAttribute="categoryLanding">
                                <form:hidden path="bizNameId" />
                                <form:hidden path="categoryId" />
                                <spring:hasBindErrors name="categoryLanding">
                                    <div class="error-box">
                                        <div class="error-txt">
                                            <ul>
                                                <c:if test="${errors.hasFieldErrors('categoryName')}">
                                                    <li><form:errors path="categoryName"/></li>
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
                                                    <form:label path="categoryName" cssErrorClass="lb_error">New Category</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="categoryName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Vegitarian Food, Drinks, Cardiologist, Orthopedics"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                        </ul>

                                        <div class="col-lable3"></div>
                                        <div class="col-fields">
                                            <div class="left-btn">
                                                <input name="edit" class="next-btn" value="EDIT" type="submit">
                                            </div>
                                            <div class="right-btn">
                                                <input name="cancel_Edit" class="cancel-btn" value="CANCEL" type="submit">
                                            </div>
                                            <div class="clearFix"></div>
                                        </div>
                                        <div class="clearFix"></div>
                                    </div>
                                </div>
                            </form:form>
                        </c:when>
                        <c:otherwise>
                            <form:form method="post" action="${pageContext.request.contextPath}/business/category/add.htm" modelAttribute="categoryLanding">
                                <form:hidden path="bizNameId" />
                                <spring:hasBindErrors name="categoryLanding">
                                    <div class="error-box">
                                        <div class="error-txt">
                                            <ul>
                                                <c:if test="${errors.hasFieldErrors('categoryName')}">
                                                    <li><form:errors path="categoryName"/></li>
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
                                                    <form:label path="categoryName" cssErrorClass="lb_error">New Category</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="categoryName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Vegitarian Food, Drinks, Cardiologist, Orthopedics, ENT"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                        </ul>

                                        <div class="col-lable3"></div>
                                        <div class="col-fields">
                                            <div class="left-btn">
                                                <input name="add" class="next-btn" value="ADD" type="submit">
                                            </div>
                                            <div class="right-btn">
                                                <input name="cancel_Add" class="cancel-btn" value="CANCEL" type="submit">
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
                        <c:choose>
                            <c:when test="${!empty categoryLanding.categories}">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th nowrap>Category Name</th>
                                        <th nowrap>Edit</th>
                                    </tr>
                                    <c:forEach items="${categoryLanding.categories}" var="category" varStatus="status">
                                    <tr>
                                        <td>${status.count}&nbsp;</td>
                                        <td nowrap>${category.value}</td>
                                        <td nowrap><a href="/business/category/${category.key}/edit.htm">Click to Edit</a></td>
                                    </tr>
                                    </c:forEach>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="alert-info">
                                    <p>No category added.</p>
                                    <p>
                                        What's Category?
                                        Category clubs similar or shared characteristics.
                                        Like different kinds of Mangoes in one category of Mango. Or
                                        category to distinguish Vegetarian and Non-Vegetarian foods.
                                        Similarly, you can club all Cardiologist Doctors under one Category.
                                    </p>
                                </div>
                            </c:otherwise>
                        </c:choose>
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
                    <div class="f-left">&copy; 2017 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
</html>

