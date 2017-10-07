<%@ include file="include.jsp"%>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/intl-tel-input/css/intlTelInput.css">

    <script defer type="text/javascript" src="//code.getmdl.io/1.1.3/material.min.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script type="text/javascript" src="//www.gstatic.com/firebasejs/4.5.0/firebase.js"></script>
    <script type="text/javascript" src="//www.gstatic.com/firebasejs/4.5.0/firebase-app.js"></script>
    <script type="text/javascript" src="//www.gstatic.com/firebasejs/4.5.0/firebase-auth.js"></script>
    <script>
        // Initialize Firebase
        var config = {
            apiKey: "AIzaSyCjItDxJb8bl_0pHTfis6xSv8tpRtoL4Do",
            authDomain: "${pageContext.request.serverName}",
            databaseURL: "https://noq-app-inc.firebaseio.com",
            projectId: "noq-app-inc",
            storageBucket: "noq-app-inc.appspot.com",
            messagingSenderId: "129734883266"
        };
        firebase.initializeApp(config);
    </script>
</head>

<body>


<div class="main-warp">
    <!-- header -->
    <div class="header">
        <div class="warp-inner">
            <div class="logo-left"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" /></div>
            <div class="top-menu-right">
                <span class="help-btn"><a href="#">Sign In</a></span>
                <span class="become-btn"><a href="${pageContext.request.contextPath}/open/register.htm">Merchant Register</a></span>
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
                    <form id="sign-in-form" action="">
                        <h2>Login</h2>
                        <input name=""  id="phone" type="tel" class="form-fe" pattern="\+[0-9\s\-\(\)]+" placeholder="Please fill the phone number" />
                        <span class="mdl-textfield__error"> </span>
                        <input name="" id="sign-in-button" type="button"  class="form-btn" value="NEXT" onClick = "onSignInSubmit()"/>
                        <!--<button disabled class="mdl-button mdl-js-button mdl-button--raised" id="sign-in-button">Sign-in</button>-->
                    </form>

                    <div class="otp">
                        <c:if test="${!empty param.loginFailure and param.loginFailure eq 'p--'}">
                            <div class="r-error" style="margin-left: 0; width: 100%">
                                User not registered with this number. <a href="${pageContext.request.contextPath}/open/registrationMerchant.htm">Please click here to register</a>
                            </div>
                        </c:if>
                        <form id="verification-code-form" action="" style="display: none;">
                            <span><br></span>
                            <span><br></span>
                            <h2>OTP</h2>
                            <ul class="enter-code-box" id="verification-code">
                                <li><input id="code1" name="1" type="text" class="enter-f" maxlength="1" /></li>
                                <li><input id="code2" name="2" type="text" class="enter-f" maxlength="1"/></li>
                                <li><input id="code3" name="3" type="text" class="enter-f" maxlength="1"/></li>
                                <li><input id="code4" name="4" type="text" class="enter-f" maxlength="1"/></li>
                                <li><input id="code5" name="5" type="text" class="enter-f" maxlength="1"/></li>
                                <li><input id="code6" name="6" type="text" class="enter-f" maxlength="1"/></li>
                                <div class="clearFix"></div>
                            </ul>
                            <span class="mdl-textfield__error"> </span>
                            <input id="verify-code-button"  name="" type="button"  class="form-btn mT10" value="verIfy now" style="width: 46%;" onClick = "onVerifyCodeSubmit()"/>
                            <input id="cancel-verify-code-button"  name="" type="button"  class="form-btn mT10" value="Cancel" style="width: 46%;" onClick = "cancelVerification()"/>
                            <!--<button class="mdl-button mdl-js-button mdl-button--raised" id="">Cancel</button>-->
                        </form>

                        <form:form id="loginPhoneForm" method="post" modelAttribute="userLoginPhoneForm" action="/open/phone/login.htm">
                            <form:hidden path="uid" cssClass="form-field" />
                            <form:hidden path="phone" cssClass="form-field" />
                        </form:form>
                    </div>

                    <form:form id="login-form" method="post" modelAttribute="userLoginForm" action="/login" autocomplete="on">
                        <div class="or">Or</div>

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

                        <form:input path="emailId" cssClass="form-field" required="required" cssErrorClass="email error" />
                        <form:password path="password" cssClass="form-field" required="required" cssErrorClass="password error" />
                        <input name="" type="submit" class="form-btn mT0" value="Login">
                        <span class="left-remember"><input name="remember-me" type="checkbox" value="" id="cd-checkbox-1"/>Remember me on this device</span>
                        <span class="right-forgot"><a href="${pageContext.request.contextPath}/open/forgot/password.htm">Forgot your password</a></span>
                    </form:form>

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
                        &copy; 2017 NoQueue Inc. | <a href="#">Privacy</a> | <a href="#">Terms</a><br/>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/internal/js/firebaseAuthenticate.js"></script>
<script type="text/javascript">

    $(document).ready(function() {
        $(".enter-f").keyup(function () {
            if (this.value.length >= this.maxLength) {
                var nextId = Number(this.name) + 1;
                if ($(this).length)
                {
                    $("#code"+ nextId).focus();
                    $(this).blur();
                }
            }
        });

        <c:if test="${!empty param.logoutSuccess and param.logoutSuccess eq 's--'}">
        onSignOutClick();
        </c:if>
    });
</script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/static2/external/intl-tel-input/js/intlTelInput.js"></script>
<script>
    $("#phone").intlTelInput({
        // allowDropdown: false,
        // autoHideDialCode: false,
        // autoPlaceholder: "off",
        // dropdownContainer: "body",
        // excludeCountries: ["us"],
        // formatOnDisplay: false,
        // geoIpLookup: function(callback) {
        //   $.get("http://ipinfo.io", function() {}, "jsonp").always(function(resp) {
        //     var countryCode = (resp && resp.country) ? resp.country : "";
        //     callback(countryCode);
        //   });
        // },
        // initialCountry: "auto",
        // nationalMode: false,
        // onlyCountries: ['us', 'gb', 'ch', 'ca', 'do'],
        // placeholderNumberType: "MOBILE",
        preferredCountries: ['in'],
        // separateDialCode: true,
        utilsScript: "${pageContext.request.contextPath}/static2/external/intl-tel-input/js/utils.js"
    });

    //$("#next").onclick(window.location = "http://example.com/foo.php?option=500";);

    function onSignInSubmit() {
        //$('sign-in-form').css('display','none');

        //alert('Pass 1');
        //noQAuthentication.doValidateUser();

        if (isPhoneNumberValid())
        {
            window.signingIn = true;
            //updateSignInButtonUI();
            var phoneNumber = getPhoneNumberFromUserInput();
            //alert('phoneNumber='+ phoneNumber);

            //window.location.href("next.html");

            var appVerifier = window.recaptchaVerifier;
            //alert('appVerifier='+ phoneNumber);

            firebase.auth().signInWithPhoneNumber(phoneNumber, appVerifier)
                .then(function (confirmationResult) {
                    //alert('window.signingIn 0='+confirmationResult);
                    window.confirmationResult = confirmationResult;
                    window.signingIn = false;
                    //alert('window.signingIn 1');
                    //$(location).attr('href', 'next.html');
                    $('sign-in-form').css('display','none');
                    $('.verify-view-component').css('display','flex');

                    //document.getElementById("sign-in-form").style.display="none";
                    document.getElementById("sign-in-button").style.display="none";
                    document.getElementById("login-form").style.display="none";
                    document.getElementById("verification-code-form").style.display="block";  //login-form

                    $(".mdl-textfield__error").text("");

                }).catch(function (error) {
                console.error('Error during signInWithPhoneNumber', error);
                window.signingIn = false;
            });

        }
        else
        {
            //alert('Please fill the valid phone number');
            $(".mdl-textfield__error").text("Please enter valid phone");
            return;
        }
    }

    window.onload = function() {
        firebase.auth().onAuthStateChanged(function(user) {
            if (user) {
                //alert('User signed in with Number'+ user.phoneNumber);
                noQAuthentication.doValidateUser(user);
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

    function onVerifyCodeSubmit() {
        if (!!getCodeFromUserInput()) {
            window.verifyingCode = true;
            var code = getCodeFromUserInput();
//            alert('start getCodeFromUserInput code='+ code);
            confirmationResult.confirm(code).then(function (result) {
                var user = result.user;

                window.verifyingCode = false;
                window.confirmationResult = null;
//                alert('Pass 1');

                noQAuthentication.doValidateUser(user);
                //displayinfo(user);
            }).catch(function (error) {
                //alert('fail');
                window.verifyingCode = false;
                //alert('Wrong Verification code');
                $('.digital-input').val();
            });
        }
    }

    function displayinfo(user){
        document.getElementById('account-details').textContent = JSON.stringify(user, null, '  ');
    }
    function cancelVerification() {
        //e.preventDefault();
        window.confirmationResult = null;
        updateVerificationCodeFormUI();
        updateSignInFormUI();
    }

    function onSignOutClick() {
        firebase.auth().signOut();
        console.log("Logged out complete");
    }

    function getCodeFromUserInput() {
        //alert('getCodeFromUserInput');
        var verifystr='';
        //var items=$('.verify-view-component').children();
        //for(i in items){
        //    verifystr+=items[i].value;
        //}
        //alert('getCodeFromUserInput 2' + verifystr);
        var verifystr = document.getElementById('code1').value + document.getElementById('code2').value + document.getElementById('code3').value
            + document.getElementById('code4').value + document.getElementById('code5').value + document.getElementById('code6').value;
        if(verifystr.length !== 6 )
        {
            //alert('Please fill valid code');
            $(".mdl-textfield__error").text("Please enter valid code");
            return false;
        }
        else
            $(".mdl-textfield__error").text("");

        //alert(verifystr);
        return verifystr;
    }

    function getPhoneNumberFromUserInput() {
        var ccode= $('.selected-flag').attr('title');
        var index = ccode.indexOf(':');
        var phone = document.getElementById('phone').value;
        if (phone === "") {
            $(".mdl-textfield__error").text("Please enter valid phone number");
            return;
        }
        return (ccode.substring(index+1) + phone);
    }

    function isPhoneNumberValid() {
        //alert('isPhoneNumberValid');
        var pattern = /^\+[0-9\s\-\(\)]+$/;
        var phoneNumber = $.trim(getPhoneNumberFromUserInput());
        return phoneNumber.search(pattern) !== -1;
    }

    function resetReCaptcha() {
        if (typeof grecaptcha !== 'undefined' && typeof window.recaptchaWidgetId !== 'undefined') {
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
</html>
