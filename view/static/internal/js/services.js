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
            var text;
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
