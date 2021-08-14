package com.noqapp.service.market;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.marketplace.JsonMarketplace;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.market.PropertyRentalManager;
import com.noqapp.service.MessageCustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * hitender
 * 1/11/21 12:55 AM
 */
@Service
public class PropertyRentalService {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyRentalService.class);

    private PropertyRentalManager propertyRentalManager;
    private MessageCustomerService messageCustomerService;
    private UserProfileManager userProfileManager;

    @Autowired
    public PropertyRentalService(
        PropertyRentalManager propertyRentalManager,
        MessageCustomerService messageCustomerService,
        UserProfileManager userProfileManager
    ) {
        this.propertyRentalManager = propertyRentalManager;
        this.messageCustomerService = messageCustomerService;
        this.userProfileManager = userProfileManager;
    }

    public void save(PropertyRentalEntity propertyRental) {
        propertyRentalManager.save(propertyRental);
    }

    public List<PropertyRentalEntity> findPostedProperties(String queueUserId) {
        return propertyRentalManager.findByQid(queueUserId);
    }

    public List<PropertyRentalEntity> findAllPendingApproval() {
        return propertyRentalManager.findAllPendingApproval();
    }

    public PropertyRentalEntity findOneById(String id) {
        return propertyRentalManager.findOneById(id);
    }

    public PropertyRentalEntity findOneByIdAndExpressInterest(String id) {
        return propertyRentalManager.findOneByIdAndExpressInterest(id);
    }

    public long findAllPendingApprovalCount() {
        return propertyRentalManager.findAllPendingApprovalCount();
    }

    public MarketplaceEntity changeStatusOfMarketplace(String marketplaceId, ActionTypeEnum actionType, String qid) {
        ValidateStatusEnum validateStatus;
        switch (actionType) {
            case APPROVE:
                validateStatus = ValidateStatusEnum.A;
                break;
            case REJECT:
                validateStatus = ValidateStatusEnum.R;
                break;
            default:
                LOG.warn("Reached un-reachable condition {}", actionType);
                throw new UnsupportedOperationException("Failed to update as the value supplied is invalid");
        }

        MarketplaceEntity marketplace = propertyRentalManager.findOneById(marketplaceId);
        Date publishUntil = null == marketplace.getPublishUntil() ? DateUtil.plusDays(10) : marketplace.getPublishUntil();
        marketplace = propertyRentalManager.changeStatus(marketplaceId, validateStatus, publishUntil, qid);

        String title, body;
        switch (actionType) {
            case APPROVE:
                title = "Active: " + (marketplace.getTitle().length() > 25 ? marketplace.getTitle().substring(0, 25) + "..." : marketplace.getTitle());
                body = "Your marketplace posting is live and available until " + DateUtil.convertDateToStringOf_DTF_DD_MMM_YYYY(marketplace.getPublishUntil()) + ". "
                    + "There is a free boost after a week. Visit website to boost your posting.";
                break;
            case REJECT:
                title = "Your marketplace posting requires attention";
                body = "Please rectify marketplace posting and submit again. Ref: " + marketplace.getTitle();
                break;
            default:
                LOG.warn("Reached un-reachable condition {}", actionType);
                throw new UnsupportedOperationException("Failed to update as the value supplied is invalid");
        }

        messageCustomerService.sendMessageToSpecificUser(
            title,
            body,
            marketplace.getQueueUserId(),
            MessageOriginEnum.A,
            marketplace.getBusinessType());
        return marketplace;
    }

    public PropertyRentalEntity initiateContactWithMarketplacePostOwner(String qid, JsonMarketplace jsonMarketplace) {
        PropertyRentalEntity propertyRental = propertyRentalManager.findOneByIdAndExpressInterest(jsonMarketplace.getId());

        UserProfileEntity userProfileOfExpressInterest = userProfileManager.findByQueueUserId(qid);
        UserProfileEntity userProfileOfOwner = userProfileManager.findByQueueUserId(propertyRental.getQueueUserId());

        String body = "Please contact " + userProfileOfExpressInterest.getName() + " at phone number " + userProfileOfExpressInterest.getPhoneFormatted();
        if (userProfileOfExpressInterest.isProfileVerified()) {
            body = body + ". This is a verified profile.";
        }
        body = body + "\n\n Note: This is a free service. Please be careful and contact us if there is anything suspicious.";

        messageCustomerService.sendMessageToSpecificUser(
            " interest received on rental property by " + userProfileOfExpressInterest.getInitials(),
            body,
            propertyRental.getQueueUserId(),
            MessageOriginEnum.A,
            jsonMarketplace.getBusinessType()
        );

        messageCustomerService.sendMessageToSpecificUser(
            "your interest was shared",
            "We have sent your information to the owner (" + userProfileOfOwner.getName() + ") of this post. They will contact you on phone " + userProfileOfExpressInterest.getPhoneFormatted() +
                "\n\n Note: This is a free service. Please be careful and contact us if there is anything suspicious about this post.",
            qid,
            MessageOriginEnum.A,
            jsonMarketplace.getBusinessType()
        );

        return propertyRental;
    }
}
