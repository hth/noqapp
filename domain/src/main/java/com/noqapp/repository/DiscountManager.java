package com.noqapp.repository;

import com.noqapp.domain.DiscountEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-09 14:41
 */
public interface DiscountManager extends RepositoryManager<DiscountEntity> {

    List<DiscountEntity> findAll(String bizNameId);

    List<DiscountEntity> findAllActive(String bizNameId);

    void inActive(String discountId);

    void removeById(String discountId);

    DiscountEntity findById(String discountId);
}
