package com.noqapp.domain.flow;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.types.BusinessTypeEnum;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * hitender
 * 1/18/18 11:24 PM
 */
public class AuthorizedQueueUser implements Serializable {

    private int queueLimit;
    private String qid;
    private String name;
    private BusinessTypeEnum businessType;
    private List<BizStoreEntity> enrolledInStores = new LinkedList<>();
    private List<BizStoreEntity> bizStores = new ArrayList<>();
    private Map<String, String> categories = new HashMap<>();

    private boolean selectAll;
    private String[] interests;

    public int getQueueLimit() {
        return queueLimit;
    }

    public AuthorizedQueueUser setQueueLimit(int queueLimit) {
        this.queueLimit = queueLimit;
        return this;
    }

    public String getQid() {
        return qid;
    }

    public AuthorizedQueueUser setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public String getName() {
        return name;
    }

    public AuthorizedQueueUser setName(String name) {
        this.name = name;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public AuthorizedQueueUser setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public List<BizStoreEntity> getEnrolledInStores() {
        return enrolledInStores;
    }

    public AuthorizedQueueUser setEnrolledInStores(List<BizStoreEntity> enrolledInStores) {
        this.enrolledInStores = enrolledInStores;
        return this;
    }

    public List<BizStoreEntity> getBizStores() {
        return bizStores;
    }

    public AuthorizedQueueUser setBizStores(List<BizStoreEntity> bizStores) {
        this.bizStores = bizStores;
        return this;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public AuthorizedQueueUser setCategories(Map<String, String> categories) {
        this.categories = categories;
        return this;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public AuthorizedQueueUser setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
        return this;
    }

    public String[] getInterests() {
        return interests;
    }

    public AuthorizedQueueUser setInterests(String[] interests) {
        this.interests = interests;
        return this;
    }

    @Transient
    public int maxSelectedStore() {
        return interests.length + enrolledInStores.size();
    }
}
