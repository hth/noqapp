package com.noqapp.view.flow.merchant;

import static com.noqapp.common.utils.RandomString.MANAGER_NOQAPP_COM;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.BusinessHour;
import com.noqapp.domain.flow.Register;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.ProductTypeEnum;
import com.noqapp.domain.types.RoleEnum;
import com.noqapp.domain.types.UnitOfMeasurementEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.domain.types.catgeory.GroceryEnum;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.MailService;
import com.noqapp.service.StoreHourService;
import com.noqapp.service.StoreProductService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.form.MerchantRegistrationForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

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
    private MailService mailService;
    private AccountService accountService;
    private StoreProductService storeProductService;
    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;
    private StoreHourService storeHourService;
    private AddNewAgentFlowActions addNewAgentFlowActions;

    /** When in same thread use Executor and not @Async. */
    private ExecutorService executorService;

    RegistrationFlowActions(
        Environment environment,
        ExternalService externalService,
        BizService bizService,
        TokenQueueService tokenQueueService,
        BizStoreElasticService bizStoreElasticService,
        AccountService accountService,
        MailService mailService,
        StoreProductService storeProductService,
        BusinessUserService businessUserService,
        BusinessUserStoreService businessUserStoreService,
        StoreHourService storeHourService,
        AddNewAgentFlowActions addNewAgentFlowActions
    ) {
        this.environment = environment;
        this.externalService = externalService;
        this.bizService = bizService;
        this.tokenQueueService = tokenQueueService;
        this.bizStoreElasticService = bizStoreElasticService;
        this.accountService = accountService;
        this.mailService = mailService;
        this.storeProductService = storeProductService;
        this.businessUserService = businessUserService;
        this.businessUserStoreService = businessUserStoreService;
        this.storeHourService = storeHourService;
        this.addNewAgentFlowActions = addNewAgentFlowActions;

        this.executorService = newCachedThreadPool();
    }

    @SuppressWarnings("unused")
    public void updateProfile(Register register) {
        register.getRegisterUser().setPhone(new ScrubbedInput(register.getRegisterUser().getPhoneNotFormatted()));
    }

    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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

    /** For registering additional store. */
    RegisterBusiness registerBusinessDetails(RegisterBusiness registerBusiness) {
        try {
            BizNameEntity bizName = registerBusiness.getBusinessUser().getBizName();
            BizStoreEntity bizStore = registerStore(registerBusiness, bizName);
            tokenQueueService.createUpdate(bizStore, registerBusiness.getAppendPrefixToToken());
            populateStoreWithDefaultProduct(bizStore);

            if (RegisterBusiness.StoreFranchise.OFF == registerBusiness.getStoreFranchise()) {
                switch (registerBusiness.getBusinessType()) {
                    case RS:
                    case RSQ:
                    case FT:
                    case FTQ:
                    case BA:
                    case BAQ:
                    case ST:
                    case STQ:
                    case GS:
                    case GSQ:
                    case CF:
                    case CFQ:
                        if (businessUserStoreService.countNumberOfStoreUsers(bizName.getId()) == 0) {
                            List<BusinessUserEntity> businessUsers = businessUserService.getAllForBusiness(bizName.getId(), UserLevelEnum.M_ADMIN);
                            BusinessUserEntity businessUser = businessUsers.iterator().next();
                            UserProfileEntity userProfileOfAdmin = accountService.findProfileByQueueUserId(businessUser.getQueueUserId());

                            /* Step 1: Registered agent and add user to store. */
                            String storeManagerRandomPassword = RandomString.newInstance(6).nextString();
                            String storeManagerMailAddress = userProfileOfAdmin.getEmail().split("@")[0] + MANAGER_NOQAPP_COM;
                            while (null != accountService.doesUserExists(storeManagerMailAddress)) {
                                storeManagerMailAddress = RandomString.generateManagerEmailAddressWithDomain(
                                    new ScrubbedInput(userProfileOfAdmin.getFirstName()),
                                    new ScrubbedInput(userProfileOfAdmin.getLastName()),
                                    userProfileOfAdmin.getQueueUserId());

                                Assert.state(storeManagerMailAddress.contains(MANAGER_NOQAPP_COM), "Email created should contain " + MANAGER_NOQAPP_COM);
                            }

                            MerchantRegistrationForm merchantRegistrationForm = MerchantRegistrationForm.newInstance()
                                .setBirthday(new ScrubbedInput(userProfileOfAdmin.getBirthday()))
                                .setGender(new ScrubbedInput(userProfileOfAdmin.getGender().name()))
                                .setFirstName(new ScrubbedInput(userProfileOfAdmin.getFirstName()))
                                .setLastName(new ScrubbedInput(userProfileOfAdmin.getLastName()))
                                .setMail(new ScrubbedInput(storeManagerMailAddress))
                                .setPassword(new ScrubbedInput(storeManagerRandomPassword))
                                .setCode1("S").setCode2("2").setCode3("K").setCode4("X").setCode5("0").setCode6("Z");
                            addNewAgentFlowActions.createAccountAndInvite(merchantRegistrationForm, "S2KX0Z", bizStore.getId(), null);

                            LOG.info("Send email to admin={} to use email={} and password={} for login to manager account",
                                userProfileOfAdmin.getEmail(), storeManagerMailAddress, storeManagerRandomPassword);

                            /* Step 2: Auto approve the added user. */
                            UserProfileEntity userProfile = accountService.doesUserExists(storeManagerMailAddress);
                            BusinessUserEntity businessUserRegistered = businessUserService.findBusinessUser(userProfile.getQueueUserId(), bizName.getId());
                            businessUserStoreService.approve(bizStore.getId(), userProfileOfAdmin.getQueueUserId(), businessUserRegistered);

                            /* Step 3: Send email with credential to admin. */
                            Map<String, Object> rootMap = new HashMap<>();
                            rootMap.put("login", storeManagerMailAddress);
                            rootMap.put("password", storeManagerRandomPassword);
                            mailService.sendAnyMail(
                                userProfileOfAdmin.getEmail(),
                                userProfileOfAdmin.getName(),
                                "NoQueue: Account credential for Manager login",
                                rootMap, "mail/storeManagerLoginCredential.ftl");

                            /* Step 4: Change account to to new agent and set store manager role. */
                            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                            List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
                            updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_CLIENT.name()));
                            updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_Q_SUPERVISOR.name()));
                            updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_S_MANAGER.name()));

                            UserAccountEntity userAccount = accountService.findByUserId(storeManagerMailAddress);
                            QueueUser queueUser = new QueueUser(
                                storeManagerMailAddress,
                                userAccount.getUserAuthentication().getPassword(),
                                updatedAuthorities,
                                userAccount.getQueueUserId(),
                                UserLevelEnum.S_MANAGER,
                                userAccount.isActive(),
                                userAccount.isAccountValidated(),
                                userProfile.getCountryShortName(),
                                userAccount.getDisplayName()
                            );

                            Authentication newAuth = new UsernamePasswordAuthenticationToken(queueUser, auth.getCredentials(), updatedAuthorities);
                            SecurityContextHolder.getContext().setAuthentication(newAuth);
                        }
                        break;
                    default:
                        //Do nothing
                }
            }
        } catch (Exception e) {
            LOG.error("Failed registering new store bizNameId={} bizName={} reason={}",
                registerBusiness.getBusinessUser().getBizName().getId(),
                registerBusiness.getBusinessUser().getBizName().getBusinessName(),
                e.getLocalizedMessage(), e);
        }
        return registerBusiness;
    }

    /** This is for simplification as not to have empty grocery store. */
    private void populateStoreWithDefaultProduct(BizStoreEntity bizStore) {
        switch (bizStore.getBusinessType()) {
            case RS:
                break;
            case FT:
                break;
            case BA:
                break;
            case ST:
                break;
            case GS:
                /* Each grocery store gets one product added for free. */
                if (storeProductService.countOfProduct(bizStore.getId()) == 0) {
                    StoreProductEntity storeProduct = new StoreProductEntity();
                    storeProduct.setBizStoreId(bizStore.getId())
                        .setProductName("Special Bread")
                        .setProductPrice(4000)
                        .setProductDiscount(0)
                        .setProductInfo("Fresh, Whole Wheat")
                        .setStoreCategoryId(GroceryEnum.BRD.name())
                        .setProductType(ProductTypeEnum.VE)
                        .setUnitValue(100)
                        .setUnitOfMeasurement(UnitOfMeasurementEnum.CN)
                        .setPackageSize(1)
                        .setInventoryCurrent(0)
                        .setInventoryLimit(100);
                    storeProductService.save(storeProduct);
                }
            case CF:
                break;
            default:
                //Do nothing
        }
    }

    /** For registering new or for editing business. */
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
            .setAmenities(registerBusiness.getAmenities())
            .setFacilities(registerBusiness.getFacilities())
            .setDayClosed(registerBusiness.isDayClosed())
            .setLimitServiceByDays(Integer.parseInt(registerBusiness.getLimitServiceByDays()))
            .setSmsLocale(registerBusiness.getSmsLocale())
            .setClaimed(registerBusiness.isClaimed());
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
            mailWhenBusinessProfileHasChanged(bizName);
            updateAllStoresWhenBizNameUpdated(bizName);
            return bizName;
        } catch (Exception e) {
            LOG.error("Error saving business reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving business");
        }
    }

    private void mailWhenBusinessProfileHasChanged(BizNameEntity bizName) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUser.getQueueUserId());
        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("changeInitiateReason", "Business profile, modified by " + userProfile.getEmail());
        rootMap.put("displayName", bizName.getBusinessName());
        rootMap.put("isClosed", bizName.isDayClosed() ? "Yes" : "No");

        mailService.sendAnyMail(
            userProfile.getEmail(),
            userProfile.getName(),
            bizName.getBusinessName() + " business profile changed",
            rootMap,
            "mail/changedBusinessProfile.ftl");
    }

    private void updateAllStoresWhenBizNameUpdated(BizNameEntity bizName) {
        List<BizStoreEntity> bizStores = bizService.getAllBizStores(bizName.getId());
        for (BizStoreEntity bizStore : bizStores) {
            List<StoreHourEntity> storeHours = storeHourService.findAllStoreHours(bizStore.getId());
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
            bizStore.setId(CommonUtil.generateHexFromObjectId());
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
            .setWalkInState(registerBusiness.getWalkInState())
            .setRemoteJoin(registerBusiness.isRemoteJoin())
            .setDeliveryRange(registerBusiness.getDeliveryRange())
            .setAverageServiceTime(registerBusiness.getAverageServiceTime())
            .setAllowLoggedInUser(registerBusiness.isAllowLoggedInUser())
            .setAvailableTokenCount(registerBusiness.getAvailableTokenCount())
            .setFamousFor(registerBusiness.getFamousFor())
            .setFacilities(registerBusiness.getFacilitiesStore())
            .setAmenities(registerBusiness.getAmenitiesStore())
            .setAppointmentState(registerBusiness.getAppointmentState())
            .setAppointmentDuration(registerBusiness.getAppointmentDuration())
            .setAppointmentOpenHowFar(registerBusiness.getAppointmentOpenHowFar());

        /* Populate only when not empty. */
        if (null != registerBusiness.getAcceptedPayments() && !registerBusiness.getAcceptedPayments().isEmpty()) {
            bizStore.setAcceptedPayments(registerBusiness.getAcceptedPayments());
        }

        /* Populate only when not empty. */
        if (null != registerBusiness.getAcceptedDeliveries() && !registerBusiness.getAcceptedDeliveries().isEmpty()) {
            bizStore.setAcceptedDeliveries(registerBusiness.getAcceptedDeliveries());
        }

        /* If preferred Google Address then, do an update. Otherwise skip. */
        if (registerBusiness.isSelectFoundAddressStore()) {
            validateAddress(bizStore);
        } else {
            bizStore.setDistrict(bizName.getDistrict())
                .setState(bizName.getState())
                .setStateShortName(bizName.getStateShortName())
                .setPostalCode(bizName.getPostalCode())
                .setCountry(bizName.getCountry())
                .setCountryShortName(bizName.getCountryShortName())
                .setCoordinate(bizName.getCoordinate())
                .setPlaceId(bizName.getPlaceId())
                .setPlaceType(bizName.getPlaceType());
        }

        try {
            List<StoreHourEntity> storeHours = saveStoreHours(registerBusiness, bizStore);
            String area = StringUtils.isBlank(registerBusiness.getAreaStore()) ? bizStore.getArea() : new ScrubbedInput(registerBusiness.getAreaStore()).getText();
            String webLocation = bizService.buildWebLocationForStore(
                area,
                bizStore.getTown(),
                bizStore.getStateShortName(),
                registerBusiness.getCountryShortNameStore(),
                registerBusiness.getName(),
                registerBusiness.getDisplayName(),
                bizStore.getId(),
                bizName.getWebLocation());

            bizStore
                .setWebLocation(webLocation)
                .setArea(area);
            bizService.saveStore(bizStore, "Added/Updated New/Existing Store");

            /* Add timezone later as its missing id of bizStore. */
            addTimezone(bizStore);

            /* Update Elastic. */
            if (bizStore.isRemoteJoin()) {
                executorService.submit(() -> updateBizStoreElastic(bizStore, storeHours));
            } else {
                executorService.submit(() -> bizStoreElasticService.delete(bizStore.getId()));
            }
            bizStoreElasticService.updateSpatial(bizStore.getBizName().getId());
            return bizStore;
        } catch (Exception e) {
            LOG.error("Error saving store for  bizName={} bizId={} reason={}",
                bizName.getBusinessName(),
                bizName.getId(),
                e.getLocalizedMessage(),
                e);

            if (0 == bizService.getAllBizStores(bizName.getId()).size()) {
                LOG.error("Found no store hence, starting rollback... {}", bizName.getBusinessName());
                bizService.deleteBizName(bizName);
                LOG.info("Rollback successful");
            }
            throw new RuntimeException("Error saving store");
        }
    }

    private List<StoreHourEntity> saveStoreHours(RegisterBusiness registerBusiness, BizStoreEntity bizStore) {
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
                storeHour.setAppointmentStartHour(businessHour.getAppointmentStartHour());
                storeHour.setAppointmentEndHour(businessHour.getAppointmentEndHour());
                storeHour.setLunchTimeStart(businessHour.getLunchTimeStart());
                storeHour.setLunchTimeEnd(businessHour.getLunchTimeEnd());
            }

            storeHours.add(storeHour);
        }

        /* Add store hours. */
        bizService.insertAll(storeHours);
        bizStore.setStoreHours(storeHours);
        return storeHours;
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
