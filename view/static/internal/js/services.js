function handleFoundAddressClick() {
    $('#addressCheckBox .form-check-box').prop("disabled", false).prop('checked', 'checked');
}

function handleFoundAddressCheckboxUncheck() {
    $("input[name$='foundAddressPlaceId']").prop('checked', false);
    $('#addressCheckBox .form-check-box').prop("disabled", true)
}

function handleFoundAddressStoreClick() {
    $('#addressStoreCheckBox .form-check-box').prop("disabled", false).prop('checked', 'checked');
}

function handleFoundAddressStoreCheckboxUncheck() {
    $("input[name$='foundAddressStorePlaceId']").prop('checked', false);
    $('#addressStoreCheckBox .form-check-box').prop("disabled", true)
}

function onRejectMarketplaceChange() {
    $("#rejectMarketplace > #marketplaceRejectReason").val($('#marketplaceRejectReason option:selected').val());
}

function storeOnlineOrOffline(storeId, action) {
    $.ajax({
        type: "POST",
        url: '/business/store/onlineOrOffline',
        beforeSend: function (xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
        },
        data: {
            storeId: storeId,
            action: action
        },
        mimeType: 'application/json',
        success: function (data) {
            let text;
            if (data.action === 'ACTIVE') {
                text = "Go Online";
                $("#storeOnlineOrOffline_" + data.storeId).attr("style", "background: black")
            } else {
                text = "Go Offline";
                $("#storeOnlineOrOffline_" + data.storeId).removeAttr("style")
            }
            $("#storeOnlineOrOffline_" + data.storeId).attr("onclick", "storeOnlineOrOffline('" + data.storeId + "', '" + data.action + "')").html(text);
        }
    });
}

function boostMarketplacePost(postId, businessTypeAsString) {
    let s = postId + "_S";
    let f = postId + "_F";
    $.ajax({
        type: "POST",
        url: '/access/marketplace/boost',
        beforeSend: function (xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
            $("#" + s + ' > td').attr('id', s).removeAttr("style").html("");
            $("#" + f + ' > td').attr('id', f).removeAttr("style").html("");
        },
        data: {
            postId: postId,
            businessTypeAsString: businessTypeAsString
        },
        mimeType: 'application/json',
        success: function (data) {
            if (data.action === 'SUCCESS') {
                $("#" + s).removeAttr("style");
                $("#" + s + ' > td')
                    .attr('id', s)
                    .css('text-align','right')
                    .css("background-color", "#fff0f0")
                    .css("color", "#0D8B0B")
                    .css("font-family", "'Roboto', sans-serif")
                    .html(data.text).delay(8000).fadeOut('slow');
            } else {
                $("#" + f).removeAttr("style");
                $("#" + f + ' > td')
                    .attr('id', f)
                    .css('text-align','right')
                    .css("background-color", "#fecfcf")
                    .css("color", "#c72926")
                    .css("font-family", "'Roboto', sans-serif")
                    .html(data.text).delay(8000).fadeOut('slow');
            }
        }
    });
}
