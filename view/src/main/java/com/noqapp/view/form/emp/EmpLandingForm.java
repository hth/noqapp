package com.noqapp.view.form.emp;

import com.noqapp.domain.AdvertisementEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.PublishArticleEntity;
import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.view.form.marketplace.MarketplaceForm;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 12/11/16 8:46 AM
 */
public class EmpLandingForm {
    private long awaitingApprovalCount;
    private List<BusinessUserEntity> businessUsers;
    private List<PublishArticleEntity> publishArticles = new LinkedList<>();
    private List<AdvertisementEntity> awaitingAdvertisementApprovals = new LinkedList<>();
    private List<MarketplaceForm> awaitingMarketplaceApprovals = new LinkedList<>();

    public long getAwaitingApprovalCount() {
        return awaitingApprovalCount;
    }

    public EmpLandingForm setAwaitingApprovalCount(long awaitingApprovalCount) {
        this.awaitingApprovalCount = awaitingApprovalCount;
        return this;
    }

    public List<BusinessUserEntity> getBusinessUsers() {
        return businessUsers;
    }

    public EmpLandingForm setBusinessUsers(List<BusinessUserEntity> businessUsers) {
        this.businessUsers = businessUsers;
        return this;
    }

    public List<PublishArticleEntity> getPublishArticles() {
        return publishArticles;
    }

    public EmpLandingForm setPublishArticles(List<PublishArticleEntity> publishArticles) {
        this.publishArticles = publishArticles;
        return this;
    }

    public List<AdvertisementEntity> getAwaitingAdvertisementApprovals() {
        return awaitingAdvertisementApprovals;
    }

    public EmpLandingForm setAwaitingAdvertisementApprovals(List<AdvertisementEntity> awaitingAdvertisementApprovals) {
        this.awaitingAdvertisementApprovals = awaitingAdvertisementApprovals;
        return this;
    }

    public List<MarketplaceForm> getAwaitingMarketplaceApprovals() {
        return awaitingMarketplaceApprovals;
    }

    public EmpLandingForm addPropertyMarketplaceForm(List<PropertyRentalEntity> marketplaces) {
        for (MarketplaceEntity marketplace : marketplaces) {
            this.awaitingMarketplaceApprovals.add(new MarketplaceForm().setMarketplace(marketplace));
        }
        return this;
    }

    public EmpLandingForm addHouseholdItemMarketplaceForm(List<HouseholdItemEntity> marketplaces) {
        for (MarketplaceEntity marketplace : marketplaces) {
            this.awaitingMarketplaceApprovals.add(new MarketplaceForm().setMarketplace(marketplace));
        }
        return this;
    }
}
