package com.noqapp.service;

import com.noqapp.domain.StoreCategoryEntity;
import com.noqapp.repository.StoreCategoryManager;
import com.noqapp.repository.StoreProductManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * hitender
 * 3/22/18 10:27 AM
 */
@Service
public class StoreCategoryService {

    private StoreCategoryManager storeCategoryManager;
    private StoreProductManager storeProductManager;

    @Autowired
    public StoreCategoryService(
        StoreCategoryManager storeCategoryManager,
        StoreProductManager storeProductManager
    ) {
        this.storeCategoryManager = storeCategoryManager;
        this.storeProductManager = storeProductManager;
    }

    public List<StoreCategoryEntity> findAll(String storeId) {
        return this.storeCategoryManager.findAll(storeId);
    }

    public void save(StoreCategoryEntity storeCategory) {
        storeCategoryManager.save(storeCategory);
    }

    public boolean existCategoryName(String storeId, String categoryName) {
        return storeCategoryManager.existCategoryName(storeId, categoryName.trim());
    }

    public Map<String, String> getStoreCategoriesAsMap(String storeId) {
        List<StoreCategoryEntity> storeCategories = findAll(storeId);

        Map<String, String> storeCategoriesAsMap = new LinkedHashMap<>();
        for (StoreCategoryEntity storeCategory : storeCategories) {
            storeCategoriesAsMap.put(storeCategory.getId(), storeCategory.getCategoryName());
        }

        return storeCategoriesAsMap;
    }

    public Map<String, Long> countCategoryUse(Set<String> categories, String storeId) {
        Map<String, Long> maps = new HashMap<>();
        for (String storeCategoryId : categories) {
            maps.put(storeCategoryId, storeProductManager.countCategoryUse(storeId, storeCategoryId));
        }

        return maps;
    }

    public StoreCategoryEntity findOne(String id) {
        return storeCategoryManager.findOne(id);
    }

    public void delete(StoreCategoryEntity storeCategory) {
        storeProductManager.removeStoreCategoryReference(storeCategory.getId());
        storeCategoryManager.deleteHard(storeCategory);
    }

    public long countOfCategory(String storeId) {
        return storeCategoryManager.countOfCategory(storeId);
    }
}
