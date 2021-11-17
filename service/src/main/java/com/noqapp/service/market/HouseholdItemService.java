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
import com.noqapp.domain.types.catgeory.MarketplaceRejectReasonEnum;
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

    public List<HouseholdItemEntity> findAllPendingApprovalWithoutImage() {
        return householdItemManager.findAllPendingApprovalWithoutImage();
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

    public MarketplaceEntity changeStatusOfMarketplace(String marketplaceId, ActionTypeEnum actionType, MarketplaceRejectReasonEnum marketplaceRejectReason, String qid) {
        MarketplaceEntity marketplace = householdItemManager.findOneById(marketplaceId);

        Date publishUntil = null;
        ValidateStatusEnum validateStatus;
        switch (actionType) {
            case APPROVE:
                validateStatus = ValidateStatusEnum.A;
                publishUntil = null == marketplace.getPublishUntil() ? DateUtil.plusDays(10) : marketplace.getPublishUntil();
                break;
            case REJECT:
                validateStatus = ValidateStatusEnum.R;
                break;
            default:
                LOG.warn("Reached un-reachable condition {}", actionType);
                throw new UnsupportedOperationException("Failed to update as the value supplied is invalid");
        }
        marketplace = householdItemManager.changeStatus(marketplaceId, validateStatus, marketplaceRejectReason, publishUntil, qid);

        String title, body;
        switch (actionType) {
            case APPROVE:
                title = "Active: " + (marketplace.getTitle().length() > 25 ? marketplace.getTitle().substring(0, 25) + "..." : marketplace.getTitle());
                body = "Your household posting is live and available until " + DateUtil.convertDateToStringOf_DTF_DD_MMM_YYYY(marketplace.getPublishUntil()) + ". "
                    + "There is a free boost after a week. Visit website to boost your posting.";
                break;
            case REJECT:
                title = "Your household posting requires attention";
                body = "Please rectify household item posting and submit again. " +
                    "Refer: " + (marketplace.getTitle().length() > 25 ? marketplace.getTitle().substring(0, 25) + "..." : marketplace.getTitle()) + "\n" +
                    "Reason: " + marketplace.getMarketplaceRejectReason().getDescription();
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
        HouseholdItemEntity householdItem = householdItemManager.findOneById(jsonMarketplace.getId());
        UserProfileEntity userProfileOfExpressInterest = userProfileManager.findByQueueUserId(qid);
        UserProfileEntity userProfileOfOwner = userProfileManager.findByQueueUserId(householdItem.getQueueUserId());

        if (userProfileOfExpressInterest.getQueueUserId().equalsIgnoreCase(userProfileOfOwner.getQueueUserId())) {
            messageCustomerService.sendMessageToSpecificUser(
                "Interest not sent",
                "Cannot express interest on your own post",
                householdItem.getQueueUserId(),
                MessageOriginEnum.A,
                householdItem.getBusinessType()
            );

            return householdItem;
        }

        householdItem = householdItemManager.findOneByIdAndExpressInterestWithViewCount(qid, jsonMarketplace.getId());
        String body = "Please contact " + userProfileOfExpressInterest.getName() + " at phone number " + userProfileOfExpressInterest.getPhoneFormatted();
        if (userProfileOfExpressInterest.isProfileVerified()) {
            body = body + ". This is a verified profile.";
        }
        body = body + "\n\n Note: This is a free service. Please be careful and contact us if there is anything suspicious.";

        messageCustomerService.sendMessageToSpecificUser(
            "Interest received on rental property by " + userProfileOfExpressInterest.getInitials(),
            body,
            householdItem.getQueueUserId(),
            MessageOriginEnum.A,
            householdItem.getBusinessType()
        );

        messageCustomerService.sendMessageToSpecificUser(
            "Your interest was shared",
            "We have sent your information to the owner (" + userProfileOfOwner.getName() + ") of this post. They will contact you on phone " + userProfileOfExpressInterest.getPhoneFormatted() +
                "\n\n Note: This is a free service. Please be careful and contact us if there is anything suspicious about this post.",
            qid,
            MessageOriginEnum.A,
            householdItem.getBusinessType()
        );

        return householdItem;
    }
}
