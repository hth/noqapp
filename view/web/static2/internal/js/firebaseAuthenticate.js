var noQAuthentication = {
    doValidateUser: function (user) {
        console.log('User details for doValidateUser call=', JSON.stringify(user, null, '  '));
        $('#loginPhoneForm #uid').val(user.uid);
        $('#loginPhoneForm #phone').val(user.phoneNumber);
        $.ajax({
            type: 'POST',
            url: '/open/phone/login.htm',
            data: $("#loginPhoneForm").serialize(),
            success: function (data) {
                window.location = data.next;
            },
            error: function (data, request) {
                window.location = "/open/login.htm?loginFailure=p--#";
            }
        });
    },

    doSignUpUser: function (user) {
        console.log('User details for doSignUpUser call=', JSON.stringify(user, null, '  '));
        $('#merchantRegistration #phone').val(user.phoneNumber);
        $.ajax({
            type: 'POST',
            url: '/open/registrationMerchant.htm',
            data: $("#merchantRegistration").serialize(),
            success: function (data) {
                window.location = data.next;
            },
            error: function (data, request) {
                window.location = "/open/login.htm?loginFailure=p--#";
            }
        });
    }
};

