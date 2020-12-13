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
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue"/></a>
        </div>
        <div class="top-menu-right2">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><sec:authentication property="principal.userShortName"/></button>
                <div id="myDropdown" class="dropdown-content">
                    <div class="menu-top-arrow"><img src="${pageContext.request.contextPath}/static2/internal/img/menu-top-arrow.png"/></div>
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
            <sec:authorize access="hasAnyRole('ROLE_S_MANAGER', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')">
            <div class="admin-main">
                <div class="admin-content">
                    <div class="store">
                        <h3>Store category count: <span>${storeCategoryForm.categories.size()}</span></h3>
                    </div>
                </div>

                <c:choose>
                <c:when test="${!empty storeCategoryForm.storeCategoryId}">
                <form:form method="post" action="${pageContext.request.contextPath}/business/store/category/edit.htm" modelAttribute="storeCategoryForm">
                    <form:hidden path="bizStoreId" />
                    <form:hidden path="storeCategoryId" />
                    <form:hidden path="businessType" />

                    <spring:hasBindErrors name="storeCategoryForm">
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
                                        <form:label path="categoryName" cssErrorClass="lb_error">Category Name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="categoryName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                    placeholder="Vegetarian Food, Starter, Drinks"/>
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
                <form:form method="post" action="${pageContext.request.contextPath}/business/store/category/add.htm" modelAttribute="storeCategoryForm">
                    <form:hidden path="bizStoreId" />
                    <form:hidden path="businessType" />

                    <spring:hasBindErrors name="storeCategoryForm">
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
                                        <form:label path="categoryName" cssErrorClass="lb_error">Category Name</form:label>
                                    </div>
                                    <div class="col-fields">
                                        <form:input path="categoryName" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                    placeholder="Vegetarian Food, Starter, Drinks"/>
                                    </div>
                                    <div class="clearFix"></div>
                                </li>
                            </ul>

                            <div class="col-lable3"></div>
                            <div class="col-fields">
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
                    <c:choose>
                        <c:when test="${!empty storeCategoryForm.categories}">
                            <div class="alert-info">
                                Store products are listed under the categories defined below. It helps to find relevant product under similar category.
                            </div>
                            <h2>Total Categories: <span>${storeCategoryForm.categories.size()}</span></h2>

                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <th>&nbsp;</th>
                                    <th nowrap>
                                        Category Name
                                        &nbsp;
                                        <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                             alt="Sort" height="16px;"/>
                                    </th>
                                    <th>Referred Count</th>
                                    <th nowrap>Edit</th>
                                    <th nowrap>Delete</th>
                                </tr>
                                <c:forEach items="${storeCategoryForm.categories}" var="category" varStatus="status">
                                <tr>
                                    <td>${status.count}&nbsp;</td>
                                    <td nowrap>${category.value}</td>
                                    <td nowrap>${storeCategoryForm.categoryCounts.get(category.key)}</td>
                                    <td nowrap>
                                        <c:choose>
                                            <c:when test="${BusinessTypeEnum.PH eq store.businessType}">
                                                N/A
                                            </c:when>
                                            <c:otherwise>
                                                <a href="/business/store/category/${storeCategoryForm.bizStoreId}/${category.key}/edit.htm" class="add-btn" style="color: #0000FF;">Edit</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td nowrap>
                                        <c:choose>
                                            <c:when test="${BusinessTypeEnum.PH eq store.businessType}">
                                                N/A
                                            </c:when>
                                            <c:otherwise>
                                                <form:form method="post" action="${pageContext.request.contextPath}/business/store/category/delete.htm" modelAttribute="storeCategoryForm">
                                                    <form:hidden path="bizStoreId" value="${storeCategoryForm.bizStoreId}" />
                                                    <form:hidden path="storeCategoryId" value="${category.key}" />
                                                    <button name="delete" class="add-btn">Delete</button>
                                                </form:form>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                </c:forEach>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="alert-info">
                                <p>No category added.</p>
                                <p>
                                    What's Store Category?
                                    Category clubs similar or shared characteristics.
                                    Like different kinds of Mangoes in one category of Mango. Or
                                    category to distinguish Vegetarian and Non-Vegetarian foods.
                                    Similarly, you can club all Cardiologist Doctors under one category.
                                </p>
                                <p>
                                    Note: You need a minimum of two categories to distinguish.
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
<script>
    (function(w, u, d){var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};var l = function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://code.upscope.io/F3TE6jAMct.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(typeof u!=="function"){w.Upscope=i;l();}})(window, window.Upscope, document);
    Upscope('init');
    Upscope('updateConnection', {
        uniqueId: '<sec:authentication property="principal.queueUserId"/>',
        identities: ['<sec:authentication property="principal.emailWithoutDomain"/>']
    });
</script>

</html>

