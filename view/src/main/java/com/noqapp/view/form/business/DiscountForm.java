package com.noqapp.view.form.business;

import com.noqapp.domain.DiscountEntity;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.domain.types.DiscountTypeEnum;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-06-09 19:34
 */
public class DiscountForm {

    private String discountName;
    private String discountDescription;
    private int discountAmount;
    private DiscountTypeEnum discountType;

    private String discountId;

    public List<DiscountEntity> discounts = new LinkedList<>();

    private ActionTypeEnum actionType;

    private Map<String, String> discountTypes = DiscountTypeEnum.asMapWithNameAsKey();

    public String getDiscountName() {
        return discountName;
    }

    public DiscountForm setDiscountName(String discountName) {
        this.discountName = discountName;
        return this;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public DiscountForm setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
        return this;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public DiscountForm setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public DiscountTypeEnum getDiscountType() {
        return discountType;
    }

    public DiscountForm setDiscountType(DiscountTypeEnum discountType) {
        this.discountType = discountType;
        return this;
    }

    public String getDiscountId() {
        return discountId;
    }

    public DiscountForm setDiscountId(String discountId) {
        this.discountId = discountId;
        return this;
    }

    public List<DiscountEntity> getDiscounts() {
        return discounts;
    }

    public DiscountForm setDiscounts(List<DiscountEntity> discounts) {
        this.discounts = discounts;
        return this;
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public DiscountForm setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
        return this;
    }

    public Map<String, String> getDiscountTypes() {
        return discountTypes;
    }

    public DiscountForm setDiscountTypes(Map<String, String> discountTypes) {
        this.discountTypes = discountTypes;
        return this;
    }
}
