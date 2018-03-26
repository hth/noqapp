package com.noqapp.repository;

import com.noqapp.domain.StoreCategoryEntity;

import java.util.List;

/**
 * hitender
 * 3/22/18 11:10 AM
 */
public interface StoreCategoryManager extends RepositoryManager<StoreCategoryEntity> {

    List<StoreCategoryEntity> findAll(String storeId);

    long countOfCategory(String storeId);

    boolean existCategoryName(String storeId, String categoryName);

    StoreCategoryEntity findOne(String id);
}
