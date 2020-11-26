var noQAuthentication = {
    doValidateUser: function (user) {
        //console.log('User details for doValidateUser call=', JSON.stringify(user, null, '  '));
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

    doJoinQueue: function (user) {
        $('#webJoinQueue #uid').val(user.uid);
        $('#webJoinQueue #phone').val(user.phoneNumber);
        $.ajax({
            type: 'POST',
            url: '/open/join/queue.htm',
            data: $("#webJoinQueue").serialize(),
            success: function (data) {
                let json = $.parseJSON(data);
                if (json['c'] === 'pre-approved-req') {
                    window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=par--#";
                } else if (json['c'] === 'non-approved') {
                    window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=na--#";
                } else if (json['c'] === 'denied-joining-queue') {
                    window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=djq--#";
                } else if (json['c'] === 'closed') {
                    window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=c--#";
                } else if (json['c'] === 'before') {
                    window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=b--#";
                } else if (json['c'] === 'after') {
                    window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=a--#";
                } else if (json['c'] === 'alreadyServiced') {
                    window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=as--#";
                } else if (json['c'] === 'waitUntil') {
                    window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=wu--#";
                } else if (json['c'] === 'wait') {
                    window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=x--#";
                } else if (json['c'] === 'limit') {
                    window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=l--#";
                } else {
                    window.location = '/open/join/' + json['c'] + '/queueConfirm.htm';
                }
            },
            error: function (data, request) {
                window.location = "/open/join/queue/" + $('#codeQR').val() + ".htm?joinFailure=p--#";
            }
        });
    },

    doSignUpUser: function (user) {
        //console.log('User details for doSignUpUser call=', JSON.stringify(user, null, '  '));
        $('#merchantRegistration #phone').val(user.phoneNumber);
        $.ajax({
            type: 'POST',
            url: '/open/registrationMerchant.htm',
            data: $("#merchantRegistration").serialize(),
            success: function (data) {
                window.location = data.next;
            },
            error: function (data, request) {
                window.location = "/open/login.htm?loginFailure=p---#";
            }
        });
    }
};

