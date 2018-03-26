package com.noqapp.service;

import com.noqapp.domain.StoreProductEntity;
import com.noqapp.repository.StoreProductManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 3/21/18 5:26 PM
 */
@Service
public class StoreProductService {

    private StoreProductManager storeProductManager;

    @Autowired
    public StoreProductService(StoreProductManager storeProductManager) {
        this.storeProductManager = storeProductManager;
    }

    public List<StoreProductEntity> findAll(String storeId) {
        return storeProductManager.findAll(storeId);
    }

    public long countOfProduct(String storeId) {
        return storeProductManager.countOfProduct(storeId);
    }

    public void save(StoreProductEntity storeProduct) {
        storeProductManager.save(storeProduct);
    }

    public boolean existProductName(String storeId, String productName) {
        return storeProductManager.existProductName(storeId, productName.trim());
    }

    public StoreProductEntity findOne(String id) {
        return storeProductManager.findOne(id);
    }

    public void delete(StoreProductEntity storeProduct) {
        storeProductManager.deleteHard(storeProduct);
    }
}
