package com.noqapp.repository;

import com.noqapp.domain.BizCategoryEntity;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 12/20/17 3:55 PM
 */
public interface BizCategoryManager extends RepositoryManager<BizCategoryEntity> {

    List<BizCategoryEntity> getByBizNameId(String bizNameId);

    boolean existCategory(String categoryName, String bizNameId);

    BizCategoryEntity findById(String id);

    void updateBizCategoryName(String categoryId, String categoryName);
}
