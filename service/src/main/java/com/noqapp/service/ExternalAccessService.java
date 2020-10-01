package com.noqapp.service;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.ExternalAccessEntity;
import com.noqapp.domain.site.JsonBusiness;
import com.noqapp.repository.ExternalAccessManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2/4/18 5:20 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class ExternalAccessService {
    private ExternalAccessManager externalAccessManager;
    private BusinessUserService businessUserService;
    private BizService bizService;

    @Autowired
    public ExternalAccessService(
        ExternalAccessManager externalAccessManager,
        BusinessUserService businessUserService,
        BizService bizService
    ) {
        this.externalAccessManager = externalAccessManager;
        this.businessUserService = businessUserService;
        this.bizService = bizService;
    }

    public List<ExternalAccessEntity> findAll(String bizId) {
        return externalAccessManager.findAll(bizId);
    }

    public void requestPermission(String bizId, String qid) {
        ExternalAccessEntity externalAccess = new ExternalAccessEntity(bizId, qid);
        //TODO remove if condition.
        if (qid.equalsIgnoreCase("100000000002")) {
            externalAccess.setApproverQID(qid);
        }
        externalAccessManager.save(externalAccess);
    }

    public void grantPermission(String id, String qid) {
        ExternalAccessEntity externalAccess = findById(id);
        externalAccess.setApproverQID(qid);
        externalAccessManager.save(externalAccess);
    }

    public void revokePermission(String id) {
        ExternalAccessEntity externalAccess = findById(id);
        externalAccessManager.deleteHard(externalAccess);

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(externalAccess.getQid(), externalAccess.getBizId());
        if (null != businessUser && externalAccess.getId().equalsIgnoreCase(businessUser.getExternalAccessId())) {
            businessUserService.deleteHard(businessUser);
        }
    }

    public ExternalAccessEntity findById(String id) {
        return externalAccessManager.findById(id);
    }

    public List<JsonBusiness> findAccessToAllBiz(String qid) {
        List<JsonBusiness> jsonBusinesses = new ArrayList<>();

        List<ExternalAccessEntity> externalAccesses = externalAccessManager.findByQid(qid);
        for (ExternalAccessEntity externalAccess : externalAccesses) {
            BizNameEntity bizName = bizService.getByBizNameId(externalAccess.getBizId());

            JsonBusiness jsonBusiness = new JsonBusiness()
                .setBizId(externalAccess.getBizId())
                .setBizName(bizName.getBusinessName())
                .setExternalAccessId(externalAccess.getId())
                .setApproverQID(externalAccess.getApproverQID())
                .setExternalPermission(externalAccess.getExternalPermission());

            jsonBusinesses.add(jsonBusiness);
        }

        return jsonBusinesses;
    }
}
