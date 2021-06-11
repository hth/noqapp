package com.noqapp.service.market;

import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.repository.market.PropertyRentalManager;

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

    @Autowired
    public PropertyRentalService(PropertyRentalManager propertyRentalManager) {
        this.propertyRentalManager = propertyRentalManager;
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
}
