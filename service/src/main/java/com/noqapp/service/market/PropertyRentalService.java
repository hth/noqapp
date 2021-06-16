package com.noqapp.service.market;

import static com.noqapp.domain.types.ActionTypeEnum.APPROVE;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.market.PropertyRentalManager;
import com.noqapp.service.MessageCustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public PropertyRentalService(PropertyRentalManager propertyRentalManager, MessageCustomerService messageCustomerService) {
        this.propertyRentalManager = propertyRentalManager;
        this.messageCustomerService = messageCustomerService;
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

    public void changeStatusOfMarketplace(String marketplaceId, ActionTypeEnum actionType, String qid) {
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

        MarketplaceEntity marketplace = propertyRentalManager.changeStatus(marketplaceId, validateStatus, qid);
        if (null != marketplace) {
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
        }
    }
}
