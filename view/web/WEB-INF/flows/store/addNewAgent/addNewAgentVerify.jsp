<%@ include file="../../../jsp/include.jsp" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
    <meta content='width=device-width, initial-scale=1' name='viewport' />

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'  />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen" />

    <script defer type="text/javascript" src="//code.getmdl.io/1.1.3/material.min.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
</head>

<body>


<div class="main-warp">
    <!-- header -->
    <div class="header">
        <div class="warp-inner">
            <div class="logo-left">
                <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" alt="NoQueue Inc"/></a>
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

    <!-- content -->
    <div class="content">
        <div class="warp-inner">
            <!-- login-box -->
            <div class="login-box">
                <div class="form-style">
                    <div class="otp">
                        <form:form modelAttribute="merchantRegistration">
                            <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                            <h2>OTP</h2>
                            <span>One time password has been sent to email ${merchantRegistration.mail}. Please enter mailed OTP here.</span>
                            <span><br></span>
                            <ul class="enter-code-box" id="verification-code">
                                <li><form:input path="code1" class="enter-f" maxlength="1"/></li>
                                <li><form:input path="code2" class="enter-f" maxlength="1"/></li>
                                <li><form:input path="code3" class="enter-f" maxlength="1"/></li>
                                <li><form:input path="code4" class="enter-f" maxlength="1"/></li>
                                <li><form:input path="code5" class="enter-f" maxlength="1"/></li>
                                <li><form:input path="code6" class="enter-f" maxlength="1"/></li>
                                <div class="clearFix"></div>
                            </ul>
                            <div id="mdl-textfield" class="error-box" style="margin-top: 5px; display: none;">
                                <div class="error-txt" style="margin-left: 10px; width: 100%; font-size:14px; float:none;display:block; padding:5px 0;">
                                    <span class="mdl-textfield__error"> </span>
                                </div>
                            </div>
                            <div class="button-btn">
                                <button name="_eventId_verify" class="ladda-button next-btn" style="width:46%; float: left">Verify</button>
                                <button name="_eventId_cancel" class="ladda-button cancel-btn" style="width:46%; float: right">Cancel</button>
                            </div>
                        </form:form>
                    </div>

                </div>
            </div>

            <!-- login-box -->

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
                    <div class="f-left">
                        &copy; 2020 NoQueue Inc. | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a><br/>
                        All other trademarks and logos belong to their respective owners. (<spring:eval expression="@environmentProperty.getProperty('build.version')" />.<spring:eval expression="@environmentProperty.getProperty('server')" />)
                    </div>

                    <div class="clearFix"></div>
                </div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>



</body>
<script type="text/javascript">
    $(document).ready(function() {
        $(".enter-f").keyup(function () {
            if (this.value.length >= this.maxLength) {
                var split = this.name.split('code');
                var nextId = Number(split[1]) + 1;
                if ($(this).length)
                {
                    $("#code"+ nextId).focus();
                    $(this).blur();
                }
            }
        });
    });
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/script.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/ladda/js/ladda.min.js"></script>
<script type="text/javascript">
    // Bind normal buttons
    Ladda.bind('.button-btn button', {timeout: 12000});

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
