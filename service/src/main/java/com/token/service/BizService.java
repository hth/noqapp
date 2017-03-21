package com.token.service;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.token.domain.BizNameEntity;
import com.token.domain.BizStoreEntity;
import com.token.repository.BizNameManager;
import com.token.repository.BizStoreManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 11/23/16 4:41 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BizService {
    private static final Logger LOG = LoggerFactory.getLogger(BizService.class);

    private double degreeInMiles;
    private double degreeInKilometers;

    private BizNameManager bizNameManager;
    private BizStoreManager bizStoreManager;

    @Autowired
    public BizService(
            @Value ("${degreeInMiles:69.172}")
            double degreeInMiles,

            @Value("${degreeInKilometers:111.321}")
            double degreeInKilometers,

            BizNameManager bizNameManager,
            BizStoreManager bizStoreManager) {
        this.degreeInMiles = degreeInMiles;
        this.degreeInKilometers = degreeInKilometers;
        this.bizNameManager = bizNameManager;
        this.bizStoreManager = bizStoreManager;
    }

    public BizNameEntity getByBizNameId(String bizId) {
        return bizNameManager.getById(bizId);
    }

    public void saveName(BizNameEntity bizName) {
        bizNameManager.save(bizName);
    }

    public BizStoreEntity getByStoreId(String storeId) {
        return bizStoreManager.getById(storeId);
    }

    public void saveStore(BizStoreEntity bizStoreEntity) {
        bizStoreManager.save(bizStoreEntity);
    }

    public Set<BizStoreEntity> bizSearch(String businessName, String bizAddress, String bizPhone) {
        Set<BizStoreEntity> bizStoreEntities = new HashSet<>();

        if (StringUtils.isNotEmpty(businessName)) {
            List<BizNameEntity> bizNameEntities = bizNameManager.findAllBizWithMatchingName(businessName);
            for (BizNameEntity bizNameEntity : bizNameEntities) {
                List<BizStoreEntity> bizStores = bizStoreManager.findAllWithStartingAddressStartingPhone(
                        bizAddress,
                        bizPhone,
                        bizNameEntity);
                bizStoreEntities.addAll(bizStores);
            }
        } else {
            List<BizStoreEntity> bizStores = bizStoreManager.findAllWithStartingAddressStartingPhone(
                    bizAddress,
                    bizPhone,
                    null);
            bizStoreEntities.addAll(bizStores);
        }
        return bizStoreEntities;
    }

    public void deleteBizStore(BizStoreEntity bizStore) {
        bizStoreManager.deleteHard(bizStore);
    }

    public void deleteBizName(BizNameEntity bizName) {
        bizNameManager.deleteHard(bizName);
    }

    public BizStoreEntity findStoreByPhone(String phone) {
        return bizStoreManager.findByPhone(phone);
    }

    public BizNameEntity findByPhone(String phone) {
        return bizNameManager.findByPhone(phone);
    }

    public BizStoreEntity findOneBizStore(String bizNameId) {
        return bizStoreManager.findOne(bizNameId);
    }

    public long getCountOfStore(String bizNameId) {
        return bizStoreManager.getCountOfStore(bizNameId);
    }

    public List<BizStoreEntity> getAllBizStores(String bizNameId) {
        return bizStoreManager.getAllBizStores(bizNameId);
    }

    public BizStoreEntity findByCodeQR(String codeQR) {
        return bizStoreManager.findByCodeQR(codeQR);
    }

    public boolean isValidCodeQR(String codeQR) {
        return bizStoreManager.isValidCodeQR(codeQR);
    }
}
