var noQAuthentication = {
    doValidateUser: function (user) {
        console.log('User details for doValidateUser call=', JSON.stringify(user, null, '  '));
        $.ajax({
            //type: 'POST',
            url: 'https://sandbox.noqapp.com',
            contentType: "application/json; charset=utf-8",
            data: {
                format: 'json',
                data: JSON.stringify(user, null, '  '),
            },
            error: function (error) {
                alert('In Error=' + JSON.stringify(error, null, '  '));
                console.log('Error during doValidateUser call=', JSON.stringify(error, null, '  '));
                $('.mdl-textfield__error').text('An error has occurred');
            },
            dataType: 'jsonp',
            success: function (data) {
                alert('In Success');
                console.log('Success during doValidateUser', JSON.stringify(user, null, '  '));
                $('.mdl-textfield__error').text('Success!!');
            },
            type: 'POST'
        });
    }
};
