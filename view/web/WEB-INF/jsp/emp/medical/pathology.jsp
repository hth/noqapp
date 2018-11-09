<%@ include file="../../include.jsp" %>
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
                            <h3>Pathology count: <span>${pathologyForm.pathologies.size()}</span></h3>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${!empty pathologyForm.id}">
                            <form:form method="post" action="${pageContext.request.contextPath}/emp/medical/pathology/edit.htm" modelAttribute="pathologyForm">
                                <form:hidden path="id" />
                                <spring:hasBindErrors name="pathologyForm">
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
                                                                placeholder="Name of test"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                            <li>
                                                <div class="col-lable3">
                                                    <form:label path="description" cssErrorClass="lb_error">Description</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="description" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Describe test"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                            <li>
                                                <div class="col-lable3">
                                                    <form:label path="category" cssErrorClass="lb_error">Category</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="category" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Add test to a specific category if any"/>
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
                            <form:form method="post" action="${pageContext.request.contextPath}/emp/medical/pathology/add.htm" modelAttribute="pathologyForm">
                                <spring:hasBindErrors name="pathologyForm">
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
                                                                placeholder="Name of test"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                            <li>
                                                <div class="col-lable3">
                                                    <form:label path="description" cssErrorClass="lb_error">Description</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="description" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Describe test"/>
                                                </div>
                                                <div class="clearFix"></div>
                                            </li>
                                            <li>
                                                <div class="col-lable3">
                                                    <form:label path="category" cssErrorClass="lb_error">Category</form:label>
                                                </div>
                                                <div class="col-fields">
                                                    <form:input path="category" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field"
                                                                placeholder="Add test to a specific category if any"/>
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
                    <c:if test="${!empty pathologyForm.pathologies}">
                        <div class="alert-info">
                            All supported pathology test are listed below.
                        </div>
                        <h2>Total Pathologies: <span>${pathologyForm.pathologies.size()}</span></h2>

                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <th>&nbsp;</th>
                                <th nowrap>
                                    Name
                                    &nbsp;
                                    <img src="${pageContext.request.contextPath}/static2/internal/img/sortAZ.png"
                                         alt="Sort" height="16px;"/>
                                </th>
                                <th>Category</th>
                                <th nowrap></th>
                                <th nowrap></th>
                            </tr>
                            <c:forEach items="${pathologyForm.pathologies}" var="pathology" varStatus="status">
                            <tr>
                                <td>${status.count}&nbsp;</td>
                                <td nowrap>
                                    ${pathology.name}
                                    <span style="display:block; font-size:13px;">${pathology.description}</span>
                                </td>
                                <td nowrap>${pathology.category}</td>
                                <td nowrap>
                                    <a href="/emp/medical/pathology/${pathology.id}/edit.htm" class="add-btn">Edit</a>
                                </td>
                                <td nowrap>
                                    <form:form method="post" action="${pageContext.request.contextPath}/emp/medical/pathology/delete.htm" modelAttribute="pathologyForm">
                                        <form:hidden path="id" value="${pathology.id}" />
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

