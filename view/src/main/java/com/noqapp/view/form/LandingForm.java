package com.noqapp.view.form;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.view.form.marketplace.MarketplaceForm;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 12/10/16 4:37 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public class LandingForm {

    private BusinessUserRegistrationStatusEnum businessUserRegistrationStatus;
    private Date businessAccountSignedUp;
    private List<QueueEntity> currentQueues;
    private List<QueueEntity> historicalQueues;
    private List<MarketplaceForm> marketplaceForms = new LinkedList<>();
    private UserPreferenceEntity userPreference;

    private List<UserProfileEntity> minorUserProfiles;

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

    public List<QueueEntity> getCurrentQueues() {
        return currentQueues;
    }

    public LandingForm setCurrentQueues(List<QueueEntity> currentQueues) {
        this.currentQueues = currentQueues;
        return this;
    }

    public List<QueueEntity> getHistoricalQueues() {
        return historicalQueues;
    }

    public LandingForm setHistoricalQueues(List<QueueEntity> historicalQueues) {
        this.historicalQueues = historicalQueues;
        return this;
    }

    /** Sorted by publishing date descending. */
    @SuppressWarnings("unused")
    public List<MarketplaceForm> getMarketplaceForms() {
        return marketplaceForms.stream().sorted((s1, s2) ->
            (s2.getMarketplace().getPublishUntil() == null ? DateUtil.plusDays(10) : s2.getMarketplace().getPublishUntil())
                .compareTo(s1.getMarketplace().getPublishUntil() == null ? DateUtil.plusDays(10) : s1.getMarketplace().getPublishUntil()))
                .collect(Collectors.toList());
    }

    public LandingForm setMarketplaceForms(List<MarketplaceForm> marketplaceForms) {
        this.marketplaceForms = marketplaceForms;
        return this;
    }

    public UserPreferenceEntity getUserPreference() {
        return userPreference;
    }

    public LandingForm setUserPreference(UserPreferenceEntity userPreference) {
        this.userPreference = userPreference;
        return this;
    }

    public LandingForm addPropertyMarketplaceForm(List<PropertyRentalEntity> marketplaces) {
        for (MarketplaceEntity marketplace : marketplaces) {
            this.marketplaceForms.add(new MarketplaceForm().setMarketplace(marketplace));
        }
        return this;
    }

    public LandingForm addHouseholdItemMarketplaceForm(List<HouseholdItemEntity> marketplaces) {
        for (MarketplaceEntity marketplace : marketplaces) {
            this.marketplaceForms.add(new MarketplaceForm().setMarketplace(marketplace));
        }
        return this;
    }

    public List<UserProfileEntity> getMinorUserProfiles() {
        return minorUserProfiles;
    }

    public LandingForm setMinorUserProfiles(List<UserProfileEntity> minorUserProfiles) {
        this.minorUserProfiles = minorUserProfiles;
        return this;
    }
}
