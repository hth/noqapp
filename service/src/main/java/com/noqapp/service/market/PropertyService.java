package com.noqapp.service.market;

import com.noqapp.domain.market.PropertyEntity;
import com.noqapp.repository.market.PropertyManager;

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
public class PropertyService {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyService.class);

    private PropertyManager propertyManager;

    @Autowired
    public PropertyService(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    public void save(PropertyEntity property) {
        propertyManager.save(property);
    }

    public List<PropertyEntity> findPostedProperties(String queueUserId) {
        return propertyManager.findByQid(queueUserId);
    }
}
