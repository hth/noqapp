package com.noqapp.repository;

import com.noqapp.domain.StoreProductEntity;

import java.util.List;

/**
 * hitender
 * 3/21/18 5:07 PM
 */
public interface StoreProductManager extends RepositoryManager<StoreProductEntity> {

    void removeById(String id);

    List<StoreProductEntity> findAll(String storeId);

    List<StoreProductEntity> findAllDisplayCase(String storeId);

    long countOfProduct(String storeId);

    boolean existProductName(String storeId, String productName);

    long countCategoryUse(String storeId, String storeCategoryId);

    StoreProductEntity findOne(String id);

    void removeStoreCategoryReference(String storeCategoryId);

    long removedStoreProduct(String storeId);

    void changeInventoryCount(String productId, int count);
}
