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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/intl-tel-input/css/intlTelInput.css">

    <script defer type="text/javascript" src="//code.getmdl.io/1.1.3/material.min.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script type="text/javascript" src="//www.gstatic.com/firebasejs/4.5.0/firebase.js"></script>
    <script type="text/javascript" src="//www.gstatic.com/firebasejs/4.5.0/firebase-app.js"></script>
    <script type="text/javascript" src="//www.gstatic.com/firebasejs/4.5.0/firebase-auth.js"></script>
    <script>
        // Initialize Firebase
        var config = {
            apiKey: "${firebaseApiKey}",
            authDomain: "${firebaseAuthDomain}",
            databaseURL: "${firebaseDatabaseURL}",
            projectId: "${firebaseProjectId}",
            storageBucket: "${firebaseStorageBucket}",
            messagingSenderId: "${firebaseMessagingSenderId}"
        };
        firebase.initializeApp(config);
    </script>

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


<div class="main-warp">
    <!-- header -->
    <div class="header">
        <div class="warp-inner">
            <div class="logo"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png"/></div>
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
                        <h2>Enter Phone Number</h2>
                        <p class="mB20 Tcenter">Please enter a valid phone number to continue sign up</p>
                        <c:if test="${merchantRegistration.notAdult}">
                            <br/>
                            <span class="left-remember" style="padding-bottom: 15px;"><form:checkbox id="guardian" path="merchantRegistration.notAdult" />
                            Based on date of birth provide, we find you a minor. Uncheck if this phone number is owned by you.
                            </span>
                        </c:if>
                        <input name="phone" id="phone" type="tel" class="form-fe" pattern="\+[0-9\s\-\(\)]+" placeholder="Please fill the phone number" />
                        <div id="mdl-textfield" class="error-box" style="margin-top: 5px; display: none;">
                            <div class="error-txt" style="margin-left: 10px; width: 100%; font-size:14px; float:none;display:block; padding:5px 0;">
                                <span class="mdl-textfield__error"> </span>
                            </div>
                        </div>
                        <%--<input name="" id="sign-in-button" type="button"  class="form-btn" value="CONTINUE SIGN UP" onClick = "onSignInSubmit()"/>--%>
                        <div class="button-btn">
                            <button id="sign-in-button" type="button" class="ladda-button form-btn" style="width:100%" onClick="onSignInSubmit()">CONTINUE SIGN UP</button>
                        </div>
                        <span class="left-remember" style="padding-top: 15px;">We abide by our strict no solicitation policy and nor we share your phone number with third party.</span>
                        <!--<button disabled class="mdl-button mdl-js-button mdl-button--raised" id="sign-in-button">Sign-in</button>-->
                    </form>

                    <div class="otp">
                        <c:if test="${!empty param.loginFailure and param.loginFailure eq 'p--'}">
                        <div class="error-box">
                            <div class="error-txt" style="margin-left: 10px; width: 100%; font-size:14px; float:none;display:block; padding:5px 0;">
                                User not registered with this number. <a href="${pageContext.request.contextPath}/open/register.htm">Please click here to register</a>
                            </div>
                        </div>
                        </c:if>
                        <c:if test="${!empty param.loginFailure and param.loginFailure eq 'i--'}">
                        <div class="error-box">
                            <div class="error-txt" style="margin-left: 10px; width: 100%; font-size:14px; float:none;display:block; padding:5px 0;">
                                You have been blocked. Please contact support through NoQueue Mobile Application or through email.
                            </div>
                        </div>
                        </c:if>
                        <form id="verification-code-form" action="" style="display: none;">
                            <span><br></span>
                            <span><br></span>
                            <h2>OTP</h2>
                            <span>One time password has been sent to phone number above. Please enter here.</span>
                            <span><br></span>
                            <ul class="enter-code-box" id="verification-code">
                                <li><input id="code1" name="1" type="text" class="enter-f" maxlength="1" style="text-transform: capitalize;"/></li>
                                <li><input id="code2" name="2" type="text" class="enter-f" maxlength="1" style="text-transform: capitalize;"/></li>
                                <li><input id="code3" name="3" type="text" class="enter-f" maxlength="1" style="text-transform: capitalize;"/></li>
                                <li><input id="code4" name="4" type="text" class="enter-f" maxlength="1" style="text-transform: capitalize;"/></li>
                                <li><input id="code5" name="5" type="text" class="enter-f" maxlength="1" style="text-transform: capitalize;"/></li>
                                <li><input id="code6" name="6" type="text" class="enter-f" maxlength="1" style="text-transform: capitalize;"/></li>
                                <div class="clearFix"></div>
                            </ul>
                            <div id="mdl-textfield" class="error-box" style="margin-top: 5px; display: none;">
                                <div class="error-txt" style="margin-left: 10px; width: 100%; font-size:14px; float:none;display:block; padding:5px 0;">
                                    <span class="mdl-textfield__error"> </span>
                                </div>
                            </div>
                            <%--<input id="verify-code-button"  name="" type="button"  class="form-btn mT10" value="verIfy now" style="width: 46%;" onClick = "onVerifyCodeSubmit()"/>--%>
                            <div class="button-btn">
                                <button id="verify-code-button" type="button" class="ladda-button form-btn" style="width:46%; float: left" onClick="onVerifyCodeSubmit()">Verify Now</button>
                                <button id="cancel-verify-code-button" type="button" class="ladda-button form-btn" style="width:46%; float: right" onClick="cancelVerification()">Cancel</button>
                            </div>
                            <%--<input id="cancel-verify-code-button"  name="" type="button"  class="form-btn mT10" value="Cancel" style="width: 46%;" onClick = "cancelVerification()"/>--%>
                            <!--<button class="mdl-button mdl-js-button mdl-button--raised" id="">Cancel</button>-->
                        </form>

                        <form:form id="merchantRegistration" method="post" modelAttribute="merchantRegistration">
                            <form:hidden path="firstName" />
                            <form:hidden path="lastName" />
                            <form:hidden path="birthday" />
                            <form:hidden path="gender" />
                            <form:hidden path="mail" />
                            <form:hidden path="password" />
                            <form:hidden path="phone" />
                            <form:hidden path="notAdult" />
                            <form:hidden path="acceptsAgreement" />
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
                        &copy; 2020 NoQueue | <a href="https://noqapp.com/#/pages/privacy">Privacy</a> | <a href="https://noqapp.com/#/pages/terms">Terms</a><br/>
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

        <c:if test="${!empty param.loginFailure and param.loginFailure eq 'p--'}">
        onSignOutClick();
        </c:if>

        <c:if test="${!empty param.loginFailure and param.loginFailure eq 'i--'}">
        onSignOutClick();
        </c:if>

        $('#guardian').change(function () {
            if (this.checked !== true) {
                $('#notAdult').val(false);
            } else {
                $('#notAdult').val(true);
            }
        });
    });
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static2/external/intl-tel-input/js/intlTelInput.js"></script>
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
        preferredCountries: ['in', 'us'],
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
                    document.getElementById("verification-code-form").style.display="block";  //login-form

                    $(".mdl-textfield__error").text("");
                    $("#mdl-textfield").hide();

                }).catch(function (error) {
                console.error('Error during signInWithPhoneNumber', error);
                window.signingIn = false;
            });

        } else {
            //alert('Please fill the valid phone number');
            $(".mdl-textfield__error").text("Please enter valid a phone number");
            $("#mdl-textfield").show();
            return;
        }
    }

    window.onload = function() {
        firebase.auth().onAuthStateChanged(function(user) {
            if (user) {
                //alert('User signed in with Number'+ user.phoneNumber);
                //noQAuthentication.doSignUpUser(user);
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

                noQAuthentication.doSignUpUser(user);
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
        if(verifystr.length !== 6 ) {
            //alert('Please fill valid code');
            $(".mdl-textfield__error").text("Please enter valid code");
            $("#mdl-textfield").show();
            return false;
        } else {
            $(".mdl-textfield__error").text("");
            $("#mdl-textfield").hide();
        }

        //alert(verifystr);
        return verifystr;
    }

    function getPhoneNumberFromUserInput() {
        var ccode= $('.selected-flag').attr('title');
        var index = ccode.indexOf(':');
        var phone = document.getElementById('phone').value;
        if (phone === "") {
            $(".mdl-textfield__error").text("Please enter valid a phone number");
            $("#mdl-textfield").show();
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

</html>
