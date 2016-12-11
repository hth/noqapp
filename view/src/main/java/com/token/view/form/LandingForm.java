package com.token.view.form;

import com.token.domain.types.BusinessUserRegistrationStatusEnum;

import java.util.Date;

/**
 * User: hitender
 * Date: 12/10/16 4:37 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class LandingForm {

    private BusinessUserRegistrationStatusEnum businessUserRegistrationStatus;
    private Date businessAccountSignedUp;

    public BusinessUserRegistrationStatusEnum getBusinessUserRegistrationStatus() {
        return businessUserRegistrationStatus;
    }

    public LandingForm setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum businessUserRegistrationStatus) {
        this.businessUserRegistrationStatus = businessUserRegistrationStatus;
        return this;
    }

    public Date getBusinessAccountSignedUp() {
        return businessAccountSignedUp;
    }

    public LandingForm setBusinessAccountSignedUp(Date businessAccountSignedUp) {
        this.businessAccountSignedUp = businessAccountSignedUp;
        return this;
    }
}
