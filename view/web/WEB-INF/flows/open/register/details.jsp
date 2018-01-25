<%@ include file="../../../jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
    <meta content='width=device-width, initial-scale=1' name='viewport'/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/intl-tel-input/css/intlTelInput.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/jquery/css/jquery-ui.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/toggle/css/toggle-style.css" />

    <script defer type="text/javascript" src="//code.getmdl.io/1.1.3/material.min.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-101872684-1"></script>
    <script>
        window.dataLayer = window.dataLayer || [];
        function gtag(){dataLayer.push(arguments);}
        gtag('js', new Date());

        gtag('config', 'UA-101872684-1');
    </script>
</head>

<body>

<!-- header -->
<div class="header">
    <div class="warp-inner">
        <div class="logo"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue Inc"/></div>
    </div>
</div>
<!-- header end -->
<div class="main-warp">

    <!-- content -->
    <div class="content">
        <div class="warp-inner">
            <!-- login-box -->
            <div class="login-box">
                <div class="form-style">
                    <h2><fmt:message key="account.register.title" /></h2>
                    <form:form modelAttribute="merchantRegistration">
                        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                        <input type="hidden" id="gender" name="gender" value="M"/>

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

                        <div class="admin-content" style="background:white;">
                            <form:input path="firstName" cssClass="form-field-left" cssErrorClass="form-field-left error-field" placeholder="First Name"/>
                            <form:input path="lastName" cssClass="form-field-right" cssErrorClass="form-field-right error-field" placeholder="Last Name"/>
                            <form:input path="birthday" cssClass="datepicker form-field-left" cssErrorClass="datepicker form-field-left error-field" placeholder="Date of Birth YYYY-MM-DD"/>
                            <div class="register-switch form-field-right">
                                <input type="radio" name="sex" value="F" id="genderFemale" class="register-switch-input" onclick="genderClick('F')">
                                <label for="genderFemale" class="register-switch-label">Female</label>
                                <input type="radio" name="sex" value="M" id="genderMale" class="register-switch-input" onclick="genderClick('M')" checked>
                                <label for="genderMale" class="register-switch-label">Male</label>
                            </div>

                            <div class="clearFix"></div>

                            <form:input path="mail" cssClass="form-field" cssErrorClass="form-field error-field" placeholder="Email ID"/>
                            <form:password path="password" cssClass="form-field" cssErrorClass="form-field error-field" placeholder="Password"/>
                            <span class="left-remember">
                                By signing up, user allows NoQueue&trade; or its representatives to connect with user via email, phone, or SMS for
                                feedback, for improvements and or for marketing purposes. User's have read and understand the relevant
                                <a href="https://noqapp.com/privacy.html" style="color:#8339ff">Privacy Statement</a>.
                            </span>
                            <span class="left-remember"><form:checkbox path="acceptsAgreement" value="" />
                                Agree to <a href="https://noqapp.com/terms.html" style="color:#8339ff">NoQueue Terms</a>
                            </span>
                            <span class="left-remember"><div id="mailErrors"></div></span>
                            <c:choose>
                                <c:when test="${merchantRegistration.accountExists}">
                                    <%--<input id="recover_btn_id" type="submit" value="Recover Password" name="_eventId_recover" class="form-btn mT10" />--%>

                                    <div class="button-btn">
                                        <button id="recover_btn_id" name="_eventId_recover" class="ladda-button form-btn" style="width:100%">Recover Password</button>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <%--<input id="recover_btn_id" type="submit" value="Recover Password" name="_eventId_recover" style="display: none;" class="form-btn mT10" />--%>

                                    <div class="button-btn">
                                        <button id="recover_btn_id" name="_eventId_recover" class="ladda-button form-btn" style="width:100%; display: none;">Recover Password</button>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <%--<input name="_eventId_submit" class="form-btn mT10" value="Sign Up" type="submit">--%>
                            <div class="button-btn">
                                <button name="_eventId_submit" class="ladda-button form-btn" style="width:100%;">Sign Up</button>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>

            <!-- login-box -->

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
                    <div class="f-left">
                        &copy; 2018 NoQueue Inc. | <a href="https://noqapp.com/privacy.html">Privacy</a> | <a href="https://noqapp.com/terms.html">Terms</a><br/>
                        All other trademarks and logos belong to their respective owners. (<spring:eval expression="@environmentProperty.getProperty('build.version')" />.<spring:eval expression="@environmentProperty.getProperty('server')" />)
                    </div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/jquery/js/jquery-ui.js"></script>
<script type="text/javascript">

    $(document).ready(function() {
        // check name availability on focus lost
        $('#mail').blur(function() {
            if ($('#mail').val()) {
                checkAvailability();
            } else {
                $("#recover_btn_id").css({'display': 'none'});
            }
        });
    });

    function checkAvailability() {
        $.ajax({
            type: "POST",
            url: '${pageContext. request. contextPath}/open/registrationMerchant/availability.htm',
            beforeSend: function(xhr) {
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
            },
            data: JSON.stringify({
                mail: $('#mail').val()
            }),
            contentType: 'application/json;charset=UTF-8',
            mimeType: 'application/json',
            dataType:'json',
            success: function (data) {
                console.log('response=', data);
                fieldValidated(data);
            }
        });
    }

    function fieldValidated(result) {
        if (result.valid === true) {
            $("#mailErrors")
                .html("Email to activate account will be sent to above email address")
                .removeClass("r-error")
                .addClass("r-info");

            $("#recover_btn_id")
                .css({'display': 'none', 'float': 'left'});
            $('#firstName').prop('required',true);
            $('#lastName').prop('required',true);
            $('#password').prop('required',true);
        } else {
            $("#mailErrors")
                .html(result.message)
                .removeClass("r-info")
                .addClass("r-error");

            //Add the button for recovery and hide button for SignUp
            $("#recover_btn_id")
                .css({'display': 'inline'});

            $('#firstName').removeAttr('required');
            $('#lastName').removeAttr('required');
            $('#password').removeAttr('required');
        }
    }
</script>
<script>
    $(function () {
        $(".datepicker").datepicker({
            dateFormat: 'yy-mm-dd'
        });
    });
</script>
<script>
    $(document).ready(function() {
        if (document.getElementById('gender').value === 'M') {
            document.getElementById('genderMale').checked = true;
        } else {
            document.getElementById('genderFemale').checked = true;
        }
    });
    function genderClick(gender) {
        document.getElementById('gender').value = gender;
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
