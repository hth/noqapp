package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.flow.BusinessHour;
import com.noqapp.domain.flow.Register;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.service.BizService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.TokenQueueService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * User: hitender
 * Date: 11/23/16 4:18 PM
 */
class RegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationFlowActions.class);

    private Environment environment;
    private ExternalService externalService;
    private BizService bizService;
    private TokenQueueService tokenQueueService;
    private BizStoreElasticService bizStoreElasticService;

    /** When in same thread use Executor and not @Async. */
    private ExecutorService executorService;

    RegistrationFlowActions(
            Environment environment,
            ExternalService externalService,
            BizService bizService,
            TokenQueueService tokenQueueService,
            BizStoreElasticService bizStoreElasticService
    ) {
        this.environment = environment;
        this.externalService = externalService;
        this.bizService = bizService;
        this.tokenQueueService = tokenQueueService;
        this.bizStoreElasticService = bizStoreElasticService;

        this.executorService = newCachedThreadPool();
    }

    @SuppressWarnings ("unused")
    public void updateProfile(Register register) {
        register.getRegisterUser().setPhone(new ScrubbedInput(register.getRegisterUser().getPhoneNotFormatted()));
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(Register register) {
        register.getRegisterBusiness().setPhone(new ScrubbedInput(Formatter.phoneCleanup(register.getRegisterBusiness().getPhone())));
        register.getRegisterBusiness().setAddressStore(new ScrubbedInput(""));
        register.getRegisterBusiness().setPhoneStore(new ScrubbedInput(""));
        register.getRegisterBusiness().setCountryShortNameStore(new ScrubbedInput(""));
    }

    /**
     * Add hours to store.
     * 
     * @param registerBusiness
     */
    @SuppressWarnings ("unused")
    public void fillWithBusinessHour(RegisterBusiness registerBusiness) {
        registerBusiness.setPhone(new ScrubbedInput(Formatter.phoneCleanup(registerBusiness.getPhone())));
        List<BusinessHour> businessHours = registerBusiness.getBusinessHours();
        for (BusinessHour businessHour : businessHours) {
            if (0 == businessHour.getTokenNotAvailableFrom()) {
                businessHour.setTokenNotAvailableFrom(businessHour.getEndHourStore());
            }
        }

        registerBusiness.setBusinessHours(businessHours);
    }

    private void validateAddress(BizStoreEntity bizStore) {
        if (null == bizStore.getId() || !bizStore.isValidatedUsingExternalAPI()) {
            externalService.decodeAddress(bizStore);
        }
    }

    private void validateAddress(BizNameEntity bizName) {
        if (null == bizName.getId() || !bizName.isValidatedUsingExternalAPI()) {
            externalService.decodeAddress(bizName);
        }
    }

    private void addTimezone(BizStoreEntity bizStore) {
        if (null != bizStore.getCoordinate() && bizStore.isValidatedUsingExternalAPI()) {
            externalService.updateTimezone(bizStore);
        } else {
            LOG.warn("Found no coordinates={} validateUsingAPI={}",
                    bizStore.getCoordinate(),
                    bizStore.isValidatedUsingExternalAPI());
        }
    }

    /**
     * For registering additional store.
     *
     * @param registerBusiness
     * @return
     */
    RegisterBusiness registerBusinessDetails(RegisterBusiness registerBusiness) {
        try {
            BizNameEntity bizName = registerBusiness.getBusinessUser().getBizName();
            BizStoreEntity bizStore = registerStore(registerBusiness, bizName);
            tokenQueueService.createUpdate(bizStore.getCodeQR(), bizStore.getTopic(), bizStore.getDisplayName(), bizStore.getBusinessType());
        } catch (Exception e) {
            LOG.error("Failed registering new bizNameId={} bizName={} reason={}",
                    registerBusiness.getBusinessUser().getBizName().getId(),
                    registerBusiness.getBusinessUser().getBizName().getBusinessName(),
                    e.getLocalizedMessage(), e);
        }
        return registerBusiness;
    }

    /**
     * For registering new or for editing business.
     * 
     * @param register
     * @return
     */
    BizNameEntity registerBusinessDetails(Register register) {
        RegisterBusiness registerBusiness = register.getRegisterBusiness();
        BizNameEntity bizName;
        if (StringUtils.isNotBlank(registerBusiness.getBizId())) {
            bizName = bizService.getByBizNameId(registerBusiness.getBizId());
        } else {
            bizName = bizService.findByPhone(registerBusiness.getPhoneWithCountryCode());
        }

        if (null == bizName) {
            bizName = BizNameEntity.newInstance(CommonUtil.generateCodeQR(environment.getProperty("build.env")));
        }

        /* Marked address invalid when address is different. */
        if (null != bizName.getAddress() && !bizName.getAddress().equalsIgnoreCase(registerBusiness.getAddress())) {
            bizName.setValidatedUsingExternalAPI(false);
        }

        bizName.setBusinessName(registerBusiness.getName())
                .setBusinessType(registerBusiness.getBusinessType())
                .setPhone(registerBusiness.getPhoneWithCountryCode())
                .setPhoneRaw(registerBusiness.getPhoneNotFormatted())
                .setAddress(registerBusiness.getAddress())
                .setArea(registerBusiness.getArea())
                .setTown(registerBusiness.getTown())
                .setTimeZone(registerBusiness.getTimeZone())
                .setInviteeCode(registerBusiness.getInviteeCode())
                .setAddressOrigin(registerBusiness.getAddressOrigin())
                .addBusinessServiceImages(registerBusiness.getBusinessServiceImage())
                .setAmenities(registerBusiness.getAmenities())
                .setFacilities(registerBusiness.getFacilities());
        validateAddress(bizName);

        try {
            String webLocation = bizService.buildWebLocationForBiz(
                    bizName.getTown(),
                    bizName.getStateShortName(),
                    registerBusiness.getCountryShortName(),
                    registerBusiness.getName(),
                    bizName.getId());

            bizName.setWebLocation(webLocation);
            bizService.saveName(bizName);
            updateAllStoresWhenBizNameUpdated(bizName);
            return bizName;
        } catch(Exception e) {
            LOG.error("Error saving business reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving business");
        }
    }

    private void updateAllStoresWhenBizNameUpdated(BizNameEntity bizName) {
        List<BizStoreEntity> bizStores = bizService.getAllBizStores(bizName.getId());
        for (BizStoreEntity bizStore : bizStores) {
            List<StoreHourEntity> storeHours = bizService.findAllStoreHours(bizStore.getId());
            executorService.submit(() -> updateBizStoreElastic(bizStore, storeHours));
        }
    }

    private void updateBizStoreElastic(BizStoreEntity bizStore, List<StoreHourEntity> storeHours) {
        BizStoreElastic bizStoreElastic = DomainConversion.getAsBizStoreElastic(bizStore, storeHours);
        bizStoreElasticService.save(bizStoreElastic);
    }

    /**
     * Create new or edit existing store registration.
     *
     * @param registerBusiness
     * @param bizName
     * @return
     */
    private BizStoreEntity registerStore(RegisterBusiness registerBusiness, BizNameEntity bizName) {
        BizStoreEntity bizStore = null;
        if (StringUtils.isNotBlank(registerBusiness.getBizStoreId())) {
            LOG.info("Updating existing store id={}", registerBusiness.getBizStoreId());
            bizStore = bizService.getByStoreId(registerBusiness.getBizStoreId());
        }

        if (null == bizStore) {
            bizStore = BizStoreEntity.newInstance();
        }
        return saveStoreAndHours(registerBusiness, bizName, bizStore);
    }

    /**
     * Saves to database.
     *
     * @param registerBusiness
     * @param bizName
     * @param bizStore
     * @return
     */
    private BizStoreEntity saveStoreAndHours(
            RegisterBusiness registerBusiness,
            BizNameEntity bizName,
            BizStoreEntity bizStore
    ) {
        bizStore.setBizName(bizName)
                .setDisplayName(registerBusiness.getDisplayName())
                .setBusinessType(registerBusiness.getStoreBusinessType())
                .setPhone(registerBusiness.getPhoneStoreWithCountryCode())
                .setPhoneRaw(registerBusiness.getPhoneStoreNotFormatted())
                .setAddress(registerBusiness.getAddressStore())
                .setArea(registerBusiness.getAreaStore())
                .setTown(registerBusiness.getTownStore())
                .setTimeZone(registerBusiness.getTimeZoneStore())
                .setCodeQR(StringUtils.isBlank(bizStore.getCodeQR()) ? CommonUtil.generateCodeQR(environment.getProperty("build.env")) : bizStore.getCodeQR())
                .setAddressOrigin(registerBusiness.getAddressStoreOrigin())
                .setBizCategoryId(registerBusiness.getBizCategoryId())
                .setRemoteJoin(registerBusiness.isRemoteJoin())
                .setAllowLoggedInUser(registerBusiness.isAllowLoggedInUser())
                .setAvailableTokenCount(registerBusiness.getAvailableTokenCount())
                .addStoreServiceImage(registerBusiness.getBusinessServiceImageStore())
                .setFamousFor(registerBusiness.getFamousFor())
                .setFacilities(registerBusiness.getFacilitiesStore())
                .setAmenities(registerBusiness.getAmenitiesStore());

        //TODO(hth) check if the store and business address are selected as same. Then don't call the code below.
        validateAddress(bizStore);
        try {
            String webLocation = bizService.buildWebLocationForStore(
                    bizStore.getArea(),
                    bizStore.getTown(),
                    bizStore.getStateShortName(),
                    registerBusiness.getCountryShortNameStore(),
                    registerBusiness.getName(),
                    registerBusiness.getDisplayName(),
                    bizStore.getId());

            bizStore.setWebLocation(webLocation);
            bizService.saveStore(bizStore);

            String bizStoreId = bizStore.getId();
            List<StoreHourEntity> storeHours = new LinkedList<>();
            for (BusinessHour businessHour : registerBusiness.getBusinessHours()) {
                StoreHourEntity storeHour = new StoreHourEntity(bizStoreId, businessHour.getDayOfWeek().getValue());
                if (businessHour.isDayClosed()) {
                    storeHour.setDayClosed(businessHour.isDayClosed());
                } else {
                    storeHour.setStartHour(businessHour.getStartHourStore());
                    storeHour.setEndHour(businessHour.getEndHourStore());
                    storeHour.setTokenAvailableFrom(businessHour.getTokenAvailableFrom());
                    storeHour.setTokenNotAvailableFrom(businessHour.getTokenNotAvailableFrom());
                }

                storeHours.add(storeHour);
            }

            /* Add store hours. */
            bizService.insertAll(storeHours);
            bizStore.setStoreHours(storeHours);

            /* Add timezone later as its missing id of bizStore. */
            addTimezone(bizStore);
            executorService.submit(() -> updateBizStoreElastic(bizStore, storeHours));
            return bizStore;
        } catch (Exception e) {
            LOG.error("Error saving store for  bizName={} bizId={} reason={}",
                    bizName.getBusinessName(),
                    bizName.getId(),
                    e.getLocalizedMessage(),
                    e);

            if (0 == bizService.getAllBizStores(bizName.getId()).size()) {
                LOG.error("Found no store hence, starting rollback...", bizName.getBusinessName());
                bizService.deleteBizName(bizName);
                LOG.info("Rollback successful");
            }
            throw new RuntimeException("Error saving store");
        }
    }

    String isBusinessUserRegistrationComplete(BusinessUserRegistrationStatusEnum businessUserRegistrationStatus) {
        switch (businessUserRegistrationStatus) {
            case C:
                return "complete";
            case I:
            case N:
                return "in-complete";
            case V:
                return "edit";
            default:
                LOG.error("Reached unsupported condition={}", businessUserRegistrationStatus);
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUserRegistrationStatus);
        }
    }
}
