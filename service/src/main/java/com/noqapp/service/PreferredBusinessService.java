package com.noqapp.service;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PreferredBusinessEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonPreferredBusiness;
import com.noqapp.domain.json.JsonPreferredBusinessList;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.PreferredBusinessManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 8/12/18 4:03 PM
 */
@Service
public class PreferredBusinessService {

    private PreferredBusinessManager preferredBusinessManager;
    private BizStoreManager bizStoreManager;

    @Autowired
    public PreferredBusinessService(PreferredBusinessManager preferredBusinessManager, BizStoreManager bizStoreManager) {
        this.preferredBusinessManager = preferredBusinessManager;
        this.bizStoreManager = bizStoreManager;
    }

    public boolean addPreferredBusiness(String bizNameId, BizNameEntity bizName) {
        if (preferredBusinessManager.exists(bizNameId, bizName.getId())) {
            return false;
        }
        PreferredBusinessEntity preferredBusiness = new PreferredBusinessEntity(bizNameId, bizName.getId(), bizName.getBusinessType());
        preferredBusinessManager.save(preferredBusiness);

        return true;
    }

    public List<PreferredBusinessEntity> findAll(String bizNameId) {
        return preferredBusinessManager.findAll(bizNameId);
    }

    @Mobile
    public JsonPreferredBusinessList findAllAsJson(BizStoreEntity bizStore) {
        List<PreferredBusinessEntity> preferredBusinesses = findAll(bizStore.getBizName().getId());
        return getJsonPreferredBusinessList(bizStore, preferredBusinesses);
    }

    public List<PreferredBusinessEntity> findAll(String bizNameId, BusinessTypeEnum businessType) {
        return preferredBusinessManager.findAll(bizNameId, businessType);
    }

    @Mobile
    public JsonPreferredBusinessList findAllAsJson(BizStoreEntity bizStore, BusinessTypeEnum businessType) {
        List<PreferredBusinessEntity> preferredBusinesses = findAll(bizStore.getBizName().getId(), businessType);
        return getJsonPreferredBusinessList(bizStore, preferredBusinesses);
    }
    
    private JsonPreferredBusinessList getJsonPreferredBusinessList(BizStoreEntity bizStore, List<PreferredBusinessEntity> preferredBusinesses) {
        JsonPreferredBusinessList jsonPreferredBusinessList = new JsonPreferredBusinessList();
        List<JsonPreferredBusiness> jsonPreferredBusinesses = new LinkedList<>();
        for (PreferredBusinessEntity preferredBusiness : preferredBusinesses) {
            String preferredBizNameId = preferredBusiness.getPreferredBizNameId();
            List<BizStoreEntity> bizStores = bizStoreManager.getAllBizStores(preferredBizNameId, bizStore.getPoint(), 10.0);
            for (BizStoreEntity bs : bizStores) {
                jsonPreferredBusinesses.add(new JsonPreferredBusiness(bs));
            }

            jsonPreferredBusinessList.addPreferredBusinesses(jsonPreferredBusinesses);
        }
        return jsonPreferredBusinessList;
    }

    public void deleteById(String id) {
        preferredBusinessManager.deleteById(id);
    }
}
