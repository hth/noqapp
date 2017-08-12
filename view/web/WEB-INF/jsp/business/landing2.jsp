<%@ include file="../include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
    <meta content='width=device-width, initial-scale=1' name='viewport' />

    <link rel="stylesheet" href="static2/internal/css/style.css" type='text/css'  />
    <link rel="stylesheet" href="static2/internal/css/phone-style.css" type='text/css' media="screen" />
    <link rel="stylesheet" href="static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen" />
</head>

<body>

<!-- header -->
<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo-left"><img src="static2/internal/img/logo.png" /></div>
        <div class="top-menu-right2">
            <div class="menu-wrap">
                <div class="txt-tight mobile-icon"><a class="toggleMenu" href="#"><img src="static2/internal/img/menu-icon.png" /></a></div>
                <div class="clearFix"></div>

                <ul class="nav">
                    <li><a href="#">b@r.com </a></li>
                    <li><a href="login.html"> Split Exenses</a></li>
                    <li><a href="login.html">Report & Analysis </a></li>
                    <li><a href="login.html">Account</a></li>
                    <li><a href="login.html">Feedback</a></li>
                    <li><a href="login.html">Sign In</a></li>

                    <div class="clearFix"></div>
                </ul>
                <div class="clearFix"></div>
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
                        <h3>Business Name: <span>${businessLandingForm.bizName}</span></h3>

                        <div class="add-store">
                            <div class="addbtn-store"><input name="" class="add-btn" value="Add new store" type="submit"></div>
                            <div class="store-table">
                            <c:choose>
                                <c:when test="${!empty businessLandingForm.bizStores}">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th>Store Location</th>
                                        <th>Queue Name</th>
                                        <th># Assigned</th>
                                        <th>Since</th>
                                    </tr>
                                    <c:forEach items="${businessLandingForm.bizStores}" var="store" varStatus="status">
                                    <tr>
                                        <td>${status.count}&nbsp;</td>
                                        <td><a href="/business/store/detail/${store.id}.htm">${store.address}</a></td>
                                        <td><a href="/${store.codeQR}/q.htm" target="_blank">${store.displayName}</a></td>
                                        <td><a href="/business/${store.id}/listQueueSupervisor.htm">${businessLandingForm.assignedQueueManagers.get(store.id)}</a></td>
                                        <td><fmt:formatDate pattern="MMMM dd, yyyy" value="${store.created}"/></td>
                                    </tr>
                                    </c:forEach>
                                </table>
                                </c:when>
                                <c:otherwise>
                                    There are no new business to approve.
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
            <img src="static2/internal/img/footer-img.jpg" class="img100" />
        </div>
        <div class="footer-dark">
            <div class="footer4">
                <div class="warp-inner">
                    <div class="f-left">&copy; 2017  NoQueue.   |  <a href="#">Privacy</a>    |    <a href="#">Terms</a></div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>



</body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script type="text/javascript" src="static2/internal/js/script.js"></script>

</html>
