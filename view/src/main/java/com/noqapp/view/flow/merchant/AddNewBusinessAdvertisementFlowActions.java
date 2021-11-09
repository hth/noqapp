package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.AdvertisementEntity;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.AdvertisementViewerTypeEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.service.AdvertisementService;
import com.noqapp.service.BizService;
import com.noqapp.view.form.business.AdvertisementForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.ZoneOffset;

/**
 * User: hitender
 * Date: 2019-05-16 23:41
 */
@Component
public class AddNewBusinessAdvertisementFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(AddNewBusinessAdvertisementFlowActions.class);

    private BizService bizService;
    private AdvertisementService advertisementService;

    @Autowired
    public AddNewBusinessAdvertisementFlowActions(
        BizService bizService,
        AdvertisementService advertisementService
    ) {
        this.bizService = bizService;
        this.advertisementService = advertisementService;
    }

    public AdvertisementForm createBlankAdvertisementForm(String advertisementId, String bizNameId) {
        Assert.hasText(bizNameId, "BizName Id cannot be empty");
        if (StringUtils.isBlank(advertisementId)) {
            BizNameEntity bizName = bizService.getByBizNameId(bizNameId);
            return new AdvertisementForm(bizNameId)
                .setCoordinate(bizName.getCoordinate());
        } else {
            AdvertisementEntity advertisement = advertisementService.findAdvertisementById(advertisementId);
            return AdvertisementForm.populate(advertisement);
        }
    }

    public void addTermAndCondition(AdvertisementForm advertisementForm) {
        advertisementForm.getTermsAndConditions().add(advertisementForm.getTermAndCondition());
    }

    public void removeAllTermsAndConditions(AdvertisementForm advertisementForm) {
        advertisementForm.getTermsAndConditions().clear();
    }

    public void confirm(AdvertisementForm advertisementForm) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AdvertisementEntity advertisement;
        if (StringUtils.isNotBlank(advertisementForm.getAdvertisementId())) {
            advertisement = advertisementService.findAdvertisementById(advertisementForm.getAdvertisementId());
        } else {
            advertisement = new AdvertisementEntity();
        }
        advertisement
            .setTitle(advertisementForm.getTitle())
            .setShortDescription(advertisementForm.getShortDescription())
            .setBizNameId(advertisementForm.getBizNameId())
            .setQueueUserId(queueUser.getQueueUserId())
            .setCoordinate(advertisementForm.getCoordinate())
            .setRadius(advertisementForm.getRadius())
            .setAdvertisementType(advertisementForm.getAdvertisementType())
            .setAdvertisementDisplay(advertisementForm.getAdvertisementDisplay())
            .setValidateStatus(ValidateStatusEnum.P)
            .setPublishDate(DateUtil.convertToDate(advertisementForm.getPublishDate(), ZoneOffset.UTC))
            .setEndDate(DateUtil.convertToDate(advertisementForm.getEndDate(), ZoneOffset.UTC))
            .setTermsAndConditions(advertisementForm.getTermsAndConditions())
            .setAdvertisementViewerType(
                advertisementForm.getTermsAndConditions().isEmpty()
                    ? AdvertisementViewerTypeEnum.JBA
                    : AdvertisementViewerTypeEnum.WTC);
        advertisementService.save(advertisement);
    }

    public void takeOffOrOnline(String advertisementId, boolean active) {
        if (active) {
            QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            AdvertisementEntity advertisement = advertisementService.findAdvertisementById(advertisementId);
            advertisement
                .setValidateStatus(ValidateStatusEnum.P)
                .setQueueUserId(queueUser.getQueueUserId())
                .active();
            advertisementService.save(advertisement);
        } else {
            AdvertisementEntity advertisement = advertisementService.findAdvertisementById(advertisementId);
            advertisement.inActive();
            advertisementService.save(advertisement);
        }
    }
}
