var noQAuthentication = {
    doValidateUser: function (user) {
        console.log('User details for doValidateUser call=', JSON.stringify(user, null, '  '));
        var formData = {
            'emailId'  : user.uid,
            'password' : user.phoneNumber
        };  
        $.ajax({
            type: 'POST',
            beforeSend: function (xhr) {
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
            },
            url: ctx + '/open/login.htm',
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: formData,
            error: function (error) {
                alert('In Error=' + JSON.stringify(error, null, '  '));
                console.log('Error during doValidateUser call=', JSON.stringify(error, null, '  '));
                $('.mdl-textfield__error').text('An error has occurred');
            },
            success: function (data) {
                alert('In Success');
                console.log('Success during doValidateUser', JSON.stringify(user, null, '  '));
                $('.mdl-textfield__error').text('Success!!');
            }
        });
    }
};
