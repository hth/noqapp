package com.noqapp.service.market;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.market.HouseholdItemManager;
import com.noqapp.service.MessageCustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Autowired
    public HouseholdItemService(HouseholdItemManager householdItemManager, MessageCustomerService messageCustomerService) {
        this.householdItemManager = householdItemManager;
        this.messageCustomerService = messageCustomerService;
    }

    public void save(HouseholdItemEntity householdItem) {
        householdItemManager.save(householdItem);
    }

    public List<HouseholdItemEntity> findPostedProperties(String queueUserId) {
        return householdItemManager.findByQid(queueUserId);
    }

    public List<HouseholdItemEntity> findAllPendingApproval() {
        return householdItemManager.findAllPendingApproval();
    }

    public HouseholdItemEntity findOneById(String id) {
        return householdItemManager.findOneById(id);
    }

    public HouseholdItemEntity findOneByIdAndExpressInterest(String id) {
        return householdItemManager.findOneByIdAndExpressInterest(id);
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

        MarketplaceEntity marketplace = householdItemManager.changeStatus(marketplaceId, validateStatus, qid);
        if (null != marketplace) {
            String title , body;
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

        return null;
    }
}
