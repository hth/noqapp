<%@ include file="include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
    <meta content='width=device-width, initial-scale=1' name='viewport' />

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'  />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/intl-tel-input/css/intlTelInput.css">

    <script defer src="https://code.getmdl.io/1.1.3/material.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://www.gstatic.com/firebasejs/4.1.3/firebase.js"></script>
    <script src="https://www.gstatic.com/firebasejs/4.1.3/firebase-app.js"></script>
    <script src="https://www.gstatic.com/firebasejs/4.1.3/firebase-auth.js"></script>
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
</head>

<body>


<div class="main-warp">
    <!-- header -->
    <div class="header">
        <div class="warp-inner">
            <div class="logo"><img src="${pageContext.request.contextPath}/static2/internal/img/logo.png" /></div>
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

                        <form id="verification-code-form" action="" method="get" style="display: none;">
                            <h2>OTP</h2>
                            <ul class="enter-code-box" id="verification-code">
                                <li><input id="code1" name="1" type="text" class="enter-f" maxlength="1" /></li>
                                <li><input id="code2" name="2" type="text" class="enter-f" maxlength="1"/></li>
                                <li><input id="code3" name="3" type="text" class="enter-f"  maxlength="1"/></li>
                                <li><input id="code4" name="4" type="text" class="enter-f"  maxlength="1"/></li>
                                <li><input id="code5" name="5" type="text" class="enter-f"  maxlength="1"/></li>
                                <li><input id="code6" name="6" type="text" class="enter-f"  maxlength="1"/></li>
                                <div class="clearFix"></div>
                            </ul>
                            <span class="mdl-textfield__error"> </span>
                            <input id="verify-code-button"  name="" type="button"  class="form-btn mT10" value="verIfy now" style="width: 46%;" onClick = "onVerifyCodeSubmit()"/>
                            <input id="cancel-verify-code-button"  name="" type="button"  class="form-btn mT10" value="Cancel" style="width: 46%;" onClick = "cancelVerification()"/>
                            <!--<button class="mdl-button mdl-js-button mdl-button--raised" id="">Cancel</button>-->

                        </form>

                    </div>



                    <form id="login-form" action="" method="get">
                        <div class="or">Or</div>
                        <input name=""   type="text" class="form-field" placeholder="User name" />
                        <input name=""   type="password" class="form-field" placeholder="Password" />
                        <input name="" type="submit"  class="form-btn mT0" value="Login"/>

                    </form>

                </div>
            </div>

            <!-- login-box -->

        </div>
    </div>
    <!-- content end -->


    <!-- Foote -->
    <div class="footer">

        <div class="footer-dark">
            <div class="warp-inner">
                <div class="f-left">&copy; 2017  NoQueue.   |  <a href="#">Privacy</a>    |    <a href="#">Terms</a></div>

                <div class="clearFix"></div>
            </div>
        </div>

    </div>
    <!-- Foote End -->

</div>



</body>
<script type="text/javascript">

    $(document).ready(function() {
        //alert('Please fill phone number');
    });
</script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
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
                alert('User signed in with Number'+ user.phoneNumber);
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
            alert('start getCodeFromUserInput code='+ code);
            confirmationResult.confirm(code).then(function (result) {
                var user = result.user;
                alert('Pass');
                window.verifyingCode = false;
                window.confirmationResult = null;
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
        if(verifystr.length != 6 )
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
        if (phone == "") {
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
