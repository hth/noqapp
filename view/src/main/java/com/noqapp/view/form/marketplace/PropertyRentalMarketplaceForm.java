package com.noqapp.view.form.marketplace;

import com.noqapp.domain.types.catgeory.RentalTypeEnum;

import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2/24/21 4:29 PM
 */
public class PropertyRentalMarketplaceForm extends MarketplaceForm {

    @Transient
    private List<RentalTypeEnum> rentalTypes = new ArrayList<>(RentalTypeEnum.rentalTypes);

    public List<RentalTypeEnum> getRentalTypes() {
        return rentalTypes;
    }

    public MarketplaceForm setRentalTypes(List<RentalTypeEnum> rentalTypes) {
        this.rentalTypes = rentalTypes;
        return this;
    }
}
