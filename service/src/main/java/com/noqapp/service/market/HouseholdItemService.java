package com.noqapp.service.market;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.marketplace.JsonMarketplace;
import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.market.HouseholdItemManager;
import com.noqapp.service.MessageCustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * hitender
 * 2/25/21 6:19 PM
 */
@Service
public class HouseholdItemService {
    private static final Logger LOG = LoggerFactory.getLogger(HouseholdItemService.class);

    private HouseholdItemManager householdItemManager;
    private MessageCustomerService messageCustomerService;
    private UserProfileManager userProfileManager;

    @Autowired
    public HouseholdItemService(
        HouseholdItemManager householdItemManager,
        MessageCustomerService messageCustomerService,
        UserProfileManager userProfileManager
    ) {
        this.householdItemManager = householdItemManager;
        this.messageCustomerService = messageCustomerService;
        this.userProfileManager = userProfileManager;
    }

    public void save(HouseholdItemEntity householdItem) {
        householdItemManager.save(householdItem);
    }

    public List<HouseholdItemEntity> findPostedByMeOnMarketplace(String queueUserId) {
        return householdItemManager.findByQid(queueUserId);
    }

    public List<HouseholdItemEntity> findAllPendingApproval() {
        return householdItemManager.findAllPendingApproval();
    }

    public HouseholdItemEntity findOneById(String id) {
        return householdItemManager.findOneById(id);
    }

    public HouseholdItemEntity findOneById(String qid, String id) {
        return householdItemManager.findOneById(qid, id);
    }

    @Mobile
    public HouseholdItemEntity findOneByIdAndExpressInterestWithViewCount(String qid, String id) {
        return householdItemManager.findOneByIdAndExpressInterestWithViewCount(qid, id);
    }

    @Mobile
    public HouseholdItemEntity findOneByIdAndViewCount(String id) {
        return householdItemManager.findOneByIdAndViewCount(id);
    }

    public long findAllPendingApprovalCount() {
        return householdItemManager.findAllPendingApprovalCount();
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

        MarketplaceEntity marketplace = householdItemManager.findOneById(marketplaceId);
        Date publishUntil = null == marketplace.getPublishUntil() ? DateUtil.plusDays(10) : marketplace.getPublishUntil();
        marketplace = householdItemManager.changeStatus(marketplaceId, validateStatus, publishUntil, qid);

        String title, body;
        switch (actionType) {
            case APPROVE:
                title = "Active: " + (marketplace.getTitle().length() > 25 ? marketplace.getTitle().substring(0, 25) + "..." : marketplace.getTitle());
                body = "Your household posting is live and available until " + DateUtil.convertDateToStringOf_DTF_DD_MMM_YYYY(marketplace.getPublishUntil()) + ". "
                    + "There is a free boost after a week. Visit website to boost your posting.";
                break;
            case REJECT:
                title = "Your household posting requires attention";
                body = "Please rectify household posting and submit again. Ref: " + marketplace.getTitle();
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

    @Mobile
    public HouseholdItemEntity initiateContactWithMarketplacePostOwner(String qid, JsonMarketplace jsonMarketplace) {
        HouseholdItemEntity householdItem = householdItemManager.findOneByIdAndExpressInterestWithViewCount(qid, jsonMarketplace.getId());

        UserProfileEntity userProfileOfExpressInterest = userProfileManager.findByQueueUserId(qid);
        UserProfileEntity userProfileOfOwner = userProfileManager.findByQueueUserId(householdItem.getQueueUserId());

        String body = "Please contact " + userProfileOfExpressInterest.getName() + " at phone number " + userProfileOfExpressInterest.getPhoneFormatted();
        if (userProfileOfExpressInterest.isProfileVerified()) {
            body = body + ". This is a verified profile.";
        }
        body = body + "\n\n Note: This is a free service. Please be careful and contact us if there is anything suspicious.";

        messageCustomerService.sendMessageToSpecificUser(
            " interest received on rental property by " + userProfileOfExpressInterest.getInitials(),
            body,
            householdItem.getQueueUserId(),
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

        return householdItem;
    }
}
