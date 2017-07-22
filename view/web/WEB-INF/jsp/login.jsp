<%@ include file="include.jsp"%>
<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang=""> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang=""> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" lang=""> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang=""> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="apple-touch-icon" href="apple-touch-icon.png">

    <link href='//fonts.googleapis.com/css?family=Open+Sans:400,300|Merriweather' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="//receiptofi.com/css/reset.css"> <!-- CSS reset -->
    <link rel="stylesheet" href="//receiptofi.com/css/style.css"> <!-- Resource style -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin-nn.css"> <!-- Resource style -->
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.css">

    <script src="//receiptofi.com/js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script> <!-- Modernizr -->

    <link rel="stylesheet" href="//code.getmdl.io/1.1.3/material.orange-indigo.min.css">
    <link rel="stylesheet" href="//fonts.googleapis.com/icon?family=Material+Icons">

    <script defer src="//code.getmdl.io/1.1.3/material.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="//www.gstatic.com/firebasejs/4.1.3/firebase.js"></script>
    <script src="//www.gstatic.com/firebasejs/4.1.3/firebase-app.js"></script>
    <script src="//www.gstatic.com/firebasejs/4.1.3/firebase-auth.js"></script>

    <title>Receiptofi | Receipt App to park your Receipts</title>
    <meta name="keywords" content="receiptapp, receipt app, scan receipts, receipt organizer, organize receipts, receipt detail, online bookkeeping, online expense reporting, receipt tracker, receipt scanning, itemized, tag expense, travel, business"/>

    <script>
        // Initialize Firebase
        var config = {
            apiKey: "AIzaSyCjItDxJb8bl_0pHTfis6xSv8tpRtoL4Do",
            authDomain: "noq-app-inc.firebaseapp.com",
            databaseURL: "https://noq-app-inc.firebaseio.com",
            projectId: "noq-app-inc",
            storageBucket: "noq-app-inc.appspot.com",
            messagingSenderId: "129734883266"
        };
        firebase.initializeApp(config);
    </script>

    <style type="text/css">
        div.mdl-card,.mdl-shadow--2dp,.mdl-cell,.mdl-cell--12-col,.mdl-cell--12-col-tablet,.mdl-cell--12-col-desktop,.mdl-card__supporting-text,.mdl-color-text--grey-600{
            width: 80%;
            height: 10%;
            text-align: center;
            margin:auto;
            box-sizing: border-box;
        }
        div.mdl-textfield,.mdl-js-textfield,.mdl-textfield--floating-label{
            margin-top:100px;
        }
        div.verify-view-component{
            width: 80%;
            height: 10%;
            border: solid 1px grey;
            align-items: inline;
            display: none;
            box-sizing: border-box;
            overflow: auto;
        }
        input.mdl-textfield__input{
            padding:2px;
            font-size: 1.2em;
            text-align: center;
        }
        button.active,.mdl-button,.mdl-js-button,.mdl-button--raised{
            display: block;
            margin-top:10px;
            left: calc(50% - 40px);
            font-size: 1.1em;
            border-radius: 10px;
            background: -webkit-linear-gradient(270deg, #3366ff, #2760ca);
        }
        #account-details{
        }
        .signin-view{
            display: block;
        }
        .verify-view{
            display: none;
        }
        .inactive{
            display: none;
        }
        pre{
            text-align: left;
        }
        input.digital-input{
            width: 80%;
            height: 10%;
            margin: 1px 3px;
            padding:2px;
            font-size: 1.2em;
            text-align: center;
        }
    </style>
</head>
<body>
<!--[if lt IE 8]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
<![endif]-->
<header class="cd-header">
    <div id="cd-logo">
        <a href="//receiptofi.com"><div id="cd-logo-img"></div></a>
    </div>

    <h3>Receiptofi</h3>

    <nav class="cd-main-nav">
        <ul>
            <!-- inser more links here -->
            <li><a href="#0">Sign In</a></li>
            <li><a href="${pageContext.request.contextPath}/open/registrationMerchant.htm">Merchant Register</a></li>
        </ul>
    </nav> <!-- cd-main-nav -->
</header>

<section class="cd-fixed-background" style="background-color: #93a748; min-height: 1054px;" data-type="slider-item">
    <div class="cd-content">
        <fieldset class="cd-form floating-labels" id="login-title-fieldset">
            <h2>ReceiptApp for receipts</h2>
            <p>Traveling, Budgeting, Expensing. Just snap it and we do the rest. Paperless.</p>
        </fieldset>

        <fieldset class="cd-form floating-labels">
            <legend>Phone Sign In</legend>

            <div class="cd-form floating-labels">
                <label class="cd-label" for="emailId">Phone</label>
                <input class="email" type="email" name="emailId" id="emailId" required>

                <%--<form:label for="emailId" path="emailId" cssClass="cd-label">Email</form:label>--%>
                <%--<form:input path="emailId" cssClass="email" required="required" type="email" cssErrorClass="email error" />--%>
            </div>
            


            <div class="demo-layout mdl-layout mdl-js-layout mdl-layout--fixed-header">
                <div class="mdl-cell mdl-cell--12-col mdl-cell--12-col-tablet mdl-grid">
                    <div id="sign-in-card" class="mdl-card mdl-shadow--2dp mdl-cell mdl-cell--12-col mdl-cell--12-col-tablet mdl-cell--12-col-desktop">
                        <div class="mdl-card__supporting-text mdl-color-text--grey-600">
                            <!-- Input to enter the phone number -->
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input signin-view" type="text" pattern="\+[0-9\s\-\(\)]+" id="phone-number">
                                <label class="mdl-textfield__label signin-view" for="phone-number">Enter your phone number...</label>
                                <span class="mdl-textfield__error signin-view">Input is not an international phone number!</span>
                                <input class="mdl-textfield__input verify-view" type="text" pattern="\+[0-9\s\-\(\)]+" id="verify-code">

                                <div class="verify-view-component">
                                    <input type="text" class="digital-input">
                                    <input type="text" class="digital-input">
                                    <input type="text" class="digital-input">
                                    <input type="text" class="digital-input">
                                    <input type="text" class="digital-input">
                                    <input type="text" class="digital-input">
                                </div>

                                <label class="mdl-textfield__label verify-view" for="verify-code">Enter verify code</label>
                                <span class="mdl-textfield__error verify-view">Do not copy or paste verify code!</span>
                            </div>
                            <button class="mdl-button mdl-js-button mdl-button--raised active sign-in-button" id="sign-in-button">Submit</button>
                            <button class="mdl-button mdl-js-button mdl-button--raised verify-view" id="cancel_btn">cancel</button>
                            <pre><code id="account-details"></code></pre>
                        </div>
                    </div>
                </div>
            </div>
        </fieldset>

        <form:form class="cd-form floating-labels"  method="post" modelAttribute="userLoginForm" action="/login" autocomplete="on">
            <fieldset>
                <legend>Sign in to continue</legend>
                <c:if test="${!empty param.loginFailure and param.loginFailure eq '--' and !empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}">
                    <div class="r-error" style="margin-left: 0; width: 100%">
                        Login not successful. Reason: ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}
                    </div>
                    <c:remove var="SPRING_SECURITY_LAST_EXCEPTION" scope="session"/>
                </c:if>
                <c:if test="${!empty param.error and param.error eq 'provider'}">
                    <div class="r-error" style="margin-left: 0; width: 100%">
                        Login not successful. Reason: You seems to be already registered with one of the other social provider or either signed up directly.
                    </div>
                </c:if>
                <c:if test="${!empty param.error and param.error eq 'multiple_users'}">
                    <div class="r-error" style="margin-left: 0; width: 100%">
                        Login not successful. Reason: You seem to have exceed number of connections allowed.
                        Please wait and try after some time.
                    </div>
                </c:if>

                <div class="icon">
                        <%--<label class="cd-label" for="emailId">Email</label>--%>
                        <%--<input class="email" type="email" name="emailId" id="emailId" required>--%>

                    <form:label for="emailId" path="emailId" cssClass="cd-label">Email</form:label>
                    <form:input path="emailId" cssClass="email" required="required" type="email" cssErrorClass="email error" />
                </div>

                <div class="icon">
                        <%--<label class="cd-label" for="password">Password</label>--%>
                        <%--<input class="password" type="password" name="password" id="password" required>--%>

                    <form:label for="password" path="password" cssClass="cd-label">Password</form:label>
                    <form:password path="password" cssClass="password" required="required" cssErrorClass="password error" />
                </div>

                <div class="icon" style="text-align: right">
                    <span class="cd-link"><a href="${pageContext.request.contextPath}/open/forgot/password.htm">Forgot your password?</a></span>
                </div>
            </fieldset>

            <ul class="cd-form-list">
                <li>
                    <input type="checkbox" name="remember-me" id="cd-checkbox-1">
                    <label for="cd-checkbox-1">Remember me on this device</label>
                </li>
            </ul>

            <fieldset>
                <div>
                    <input type="submit" value="Sign In">
                </div>
            </fieldset>
        </form:form>

        <fieldset class="cd-form floating-labels" id="register-fieldset">
            <legend>New User</legend>

            <div class="icon">
                <span class="cd-link"><a href="${pageContext.request.contextPath}/open/registration.htm">Click here to Register as Merchant</a></span>
            </div>
        </fieldset>
    </div>
</section>

<div class="footer-container">
    <footer class="wrapper fine-print">
        &#169; 2017 Receiptofi, Inc. <a href="//receiptofi.com/termsofuse">Terms</a> and <a href="//receiptofi.com/privacypolicy">Privacy</a>.<br>
        All other trademarks and logos belong to their respective owners. (<spring:eval expression="@environmentProperty.getProperty('build.version')" />.<spring:eval expression="@environmentProperty.getProperty('server')" />)<br>
    </footer>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script>
    jQuery(document).ready(function ($) {
        if ($('.floating-labels').length > 0) {
            floatLabels();
        }

        function floatLabels() {
            var inputFields = $('.floating-labels .cd-label').next();
            inputFields.each(function() {
                var singleInput = $(this);
                // check if user is filling one of the form fields
                checkVal(singleInput);
                singleInput.on('change keyup', function() {
                    checkVal(singleInput);
                });
            });
        }

        function checkVal(inputField) {
            (inputField.val() == '') ? inputField.prev('.cd-label').removeClass('float') : inputField.prev('.cd-label').addClass('float');
        }
    });
</script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
<script src="//receiptofi.com/js/main.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/5.0.0/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
<script src="${pageContext.request.contextPath}/static/external/js/fineuploader/jquery.fine-uploader.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.0.1/fullcalendar.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/fineupload.js"></script>

<script>
    (function(b,o,i,l,e,r){b.GoogleAnalyticsObject=l;b[l]||(b[l]=
            function(){(b[l].q=b[l].q||[]).push(arguments)});b[l].l=+new Date;
        e=o.createElement(i);r=o.getElementsByTagName(i)[0];
        e.src='//www.google-analytics.com/analytics.js';
        r.parentNode.insertBefore(e,r)}(window,document,'script','ga'));
    ga('create','UA-101872684-1','auto');ga('send','pageview');
</script>


<script type="text/javascript">

    $(document).ready(function() {
        function grecaptchafunc(){
            if($('.verify-view-component').css('display')=='none'){
                if(isPhoneNumberValid()){
                    onSignInSubmit();
                }
                else{
                    alert('Please check your type!');
                    return;
                }
            }
            else{
                onVerifyCodeSubmit();
            }
        }

        $(".sign-in-button").on('click',grecaptchafunc);
        $(".digital-input").on('keydown',checkvalidate);
    });

    function checkvalidate(e){
        e.preventDefault();
        console.log(e.keyCode);
        var ekey=e.keyCode;
        if (ekey >= 48 && ekey <= 57) {
            $(this).val(ekey-48);
            var nextInput = $(this).next('input');
            if (nextInput.length)
                nextInput.focus();
            else
                $(this).blur();
        }
        else
        {
            switch(ekey){
                case 39:
                    var nextInput = $(this).next('input');
                    if (nextInput.length)
                        nextInput.focus();
                    else
                        $(this).blur();
                    break;
                case 37:
                    var prevInput = $(this).prev('input');
                    if (prevInput.length)
                        prevInput.focus();
                    else
                        $(this).blur();
                    break;
                case 8:
                    $(this).val('');
                    var prevInput = $(this).prev('input');
                    if (prevInput.length)
                        prevInput.focus();
                    else
                        $(this).blur();
                    break;
                case 46:
                    $(this).val('');
            }

        }
    }

    window.onload = function() {
        firebase.auth().onAuthStateChanged(function(user) {
            if (user) {
                // User is signed in.
                var uid = user.uid;
                var email = user.email;
                var photoURL = user.photoURL;
                var phoneNumber = user.phoneNumber;
                var isAnonymous = user.isAnonymous;
                var displayName = user.displayName;
                var providerData = user.providerData;
                var emailVerified = user.emailVerified;
            }

        });
        window.recaptchaVerifier = new firebase.auth.RecaptchaVerifier('sign-in-button', {
            'size': 'invisible',
            'callback': function(response) {
                onSignInSubmit();
            }
        });


        recaptchaVerifier.render().then(function(widgetId) {
            window.recaptchaWidgetId = widgetId;
        });
    };

    function onSignInSubmit() {
        if (isPhoneNumberValid())
        {
            window.signingIn = true;
            //updateSignInButtonUI();
            var phoneNumber = getPhoneNumberFromUserInput();
            var appVerifier = window.recaptchaVerifier;
            firebase.auth().signInWithPhoneNumber(phoneNumber, appVerifier)
                .then(function (confirmationResult) {
                    window.confirmationResult = confirmationResult;
                    window.signingIn = false;
                    $('.signin-view').css('display','none');
                    $('.verify-view-component').css('display','flex');
                }).catch(function (error) {
                console.error('Error during signInWithPhoneNumber', error);
                window.signingIn = false;
            });
        }
    }

    function onVerifyCodeSubmit() {
        if (!!getCodeFromUserInput()) {

            window.verifyingCode = true;
            var code = getCodeFromUserInput();
            confirmationResult.confirm(code).then(function (result) {
                var user = result.user;
                window.verifyingCode = false;
                window.confirmationResult = null;
                displayinfo(user);
            }).catch(function (error) {
                window.verifyingCode = false;
                alert('Wrong Verification code');
                $('.digital-input').val();
            });
        }
    }

    function displayinfo(user){
        document.getElementById('account-details').textContent = JSON.stringify(user, null, '  ');
    }
    function cancelVerification(e) {
        e.preventDefault();
        window.confirmationResult = null;
        updateVerificationCodeFormUI();
        updateSignInFormUI();
    }

    function onSignOutClick() {
        firebase.auth().signOut();
    }

    function getCodeFromUserInput() {
        var verifystr='';
        var items=$('.verify-view-component').children();
        for(i in items){
            verifystr+=items[i].value;
        }
        return verifystr;
    }

    function getPhoneNumberFromUserInput() {
        return document.getElementById('phone-number').value;
    }

    function isPhoneNumberValid() {
        var pattern = /^\+[0-9\s\-\(\)]+$/;
        var phoneNumber = getPhoneNumberFromUserInput();
        return phoneNumber.search(pattern) !== -1;
    }

    function resetReCaptcha() {
        if (typeof grecaptcha !== 'undefined'
            && typeof window.recaptchaWidgetId !== 'undefined') {
            grecaptcha.reset(window.recaptchaWidgetId);
        }
    }

    function updateSignInButtonUI() {
        document.getElementById('sign-in-button').disabled =
            !isPhoneNumberValid()
            || !!window.signingIn;
    }
    function updateSignInFormUI() {
        if (firebase.auth().currentUser || window.confirmationResult) {
            document.getElementById('sign-in-form').style.display = 'none';
        } else {
            resetReCaptcha();
            document.getElementById('sign-in-form').style.display = 'block';
        }
    }

    function updateVerificationCodeFormUI() {
        if (!firebase.auth().currentUser && window.confirmationResult) {
            document.getElementById('verification-code-form').style.display = 'block';
        } else {
            // document.getElementById('verification-code-form').style.display = 'none';
        }
    }

    function updateSignOutButtonUI() {
        if (firebase.auth().currentUser) {
            document.getElementById('sign-out-button').style.display = 'block';
        } else {
            // document.getElementById('sign-out-button').style.display = 'none';
        }
    }
</script>

</body>
</html>