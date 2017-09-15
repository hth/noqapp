function handleUserFoundAddressClick() {
    $('.form-check-box').prop("disabled", false).prop('checked', 'checked');
}

function handleUserFoundAddressCheckboxUncheck() {
    $("input[name$=foundAddressPlaceId]").prop('checked', false);
}