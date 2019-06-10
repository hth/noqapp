package com.noqapp.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.DiscountEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonDiscount;
import com.noqapp.domain.json.JsonDiscountList;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.CouponManager;
import com.noqapp.repository.DiscountManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-09 19:05
 */
@Service
public class DiscountCouponService {
    private static final Logger LOG = LoggerFactory.getLogger(DiscountCouponService.class);

    private DiscountManager discountManager;
    private CouponManager couponManager;
    private BizStoreManager bizStoreManager;

    @Autowired
    public DiscountCouponService(
        DiscountManager discountManager,
        CouponManager couponManager,
        BizStoreManager bizStoreManager
    ) {
        this.discountManager = discountManager;
        this.couponManager = couponManager;
        this.bizStoreManager = bizStoreManager;
    }

    public void saveDiscount(DiscountEntity discount) {
        discountManager.save(discount);
    }

    public void saveDiscount(CouponEntity coupon) {
        couponManager.save(coupon);
    }

    public List<DiscountEntity> findAll(String bizNameId) {
        return discountManager.findAll(bizNameId);
    }

    public void inActive(String discountId) {
        discountManager.inActive(discountId);
    }

    public void removeDiscount(String discountId) {
        discountManager.removeById(discountId);
    }

    @Mobile
    public JsonDiscountList findAllDiscountAsJson(String codeQR) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        List<DiscountEntity> discounts = discountManager.findAll(bizStore.getBizName().getId());

        JsonDiscountList jsonDiscountList = new JsonDiscountList();
        for (DiscountEntity discount : discounts) {
            jsonDiscountList.addDiscount(
                new JsonDiscount()
                    .setDiscountId(discount.getId())
                    .setDiscountName(discount.getDiscountName())
                    .setDiscountDescription(discount.getDiscountDescription())
                    .setDiscountType(discount.getDiscountType())
                    .setDiscountAmount(discount.getDiscountAmount())
            );
        }

        return jsonDiscountList;
    }
}
