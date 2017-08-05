var noQAuthentication = {
    doValidateUser: function (user) {
        console.log('User details for doValidateUser call=', JSON.stringify(user, null, '  '));
        $('#loginPhoneForm #uid').val(user.uid);
        $('#loginPhoneForm #phone').val(user.phoneNumber);
        $.ajax({
            url: ctx + '/open/phone/login.htm',
            type: 'POST',
            dataType: 'application/json',
            data: $("#loginPhoneForm").serialize()
        });
    }
};
