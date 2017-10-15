function handleFoundAddressClick() {
    $('#addressCheckBox .form-check-box').prop("disabled", false).prop('checked', 'checked');
}

function handleFoundAddressCheckboxUncheck() {
    $("input[name$=foundAddressPlaceId]").prop('checked', false);
    $('#addressCheckBox .form-check-box').prop("disabled", true);
}

function handleFoundAddressStoreClick() {
    $('#addressStoreCheckBox .form-check-box').prop("disabled", false).prop('checked', 'checked');
}

function handleFoundAddressStoreCheckboxUncheck() {
    $("input[name$=foundAddressStorePlaceId]").prop('checked', false);
    $('#addressStoreCheckBox .form-check-box').prop("disabled", true);
}
}