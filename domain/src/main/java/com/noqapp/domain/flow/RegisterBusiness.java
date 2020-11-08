package com.noqapp.domain.flow;

import com.noqapp.common.utils.Constants;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.domain.types.AmenityEnum;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.FacilityEnum;
import com.noqapp.domain.types.LocaleEnum;
import com.noqapp.domain.types.SupportedDeliveryEnum;
import com.noqapp.domain.types.SupportedPaymentEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.domain.types.WalkInStateEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: hitender
 * Date: 11/23/16 4:36 PM
 */
public class RegisterBusiness implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(RegisterBusiness.class);

    public enum StoreFranchise {ON, OFF}

    private String bizId;
    private String name;
    private String address;
    private String area;
    private String town;
    private String countryShortName;
    private String phone;
    private String timeZone;
    private List<AmenityEnum> amenities = new ArrayList<>();
    private List<FacilityEnum> facilities = new ArrayList<>();
    private boolean populateAmenitiesAndFacilities = false;
    private boolean claimed = true;
    private String limitServiceByDays;
    private LocaleEnum smsLocale = LocaleEnum.en_IN;
    private boolean dayClosed = false;
    /* Reference to person who has recommended business. */
    private String inviteeCode;
    private AddressOriginEnum addressOrigin;
    private BusinessUserEntity businessUser;

    private String bizStoreId;
    private String displayName;
    private BusinessTypeEnum storeBusinessType;
    private String addressStore;
    private String areaStore;
    private String townStore;
    private String countryShortNameStore;
    private String phoneStore;
    private String timeZoneStore;
    private List<AmenityEnum> amenitiesStore = new ArrayList<>();
    private List<FacilityEnum> facilitiesStore = new ArrayList<>();
    private AddressOriginEnum addressStoreOrigin;
    private String bizCategoryId;
    private String famousFor;
    private boolean remoteJoin = true;
    private String appendPrefixToToken = Constants.appendPrefix;
    private long averageServiceTime = 300000;
    /* Now defaults to allow logged in user ONLY. */
    private boolean allowLoggedInUser = true;
    private int availableTokenCount;
    private List<BusinessHour> businessHours = new LinkedList<>();

    /* Appointment. */
    private AppointmentStateEnum appointmentState;
    private int appointmentDuration;
    private int appointmentOpenHowFar;

    /* Business Payment and Delivery. */
    private Set<SupportedPaymentEnum> acceptedPayments;
    private Set<SupportedDeliveryEnum> acceptedDeliveries;

    /* Business User Registration Status. This help if editing un-approved business for approval again. */
    private BusinessUserRegistrationStatusEnum businessUserRegistrationStatus = BusinessUserRegistrationStatusEnum.I;

    private HashMap<String, DecodedAddress> foundAddresses = new LinkedHashMap<>();
    private String foundAddressPlaceId;

    private HashMap<String, DecodedAddress> foundAddressStores = new LinkedHashMap<>();
    private String foundAddressStorePlaceId;

    private StoreFranchise storeFranchise = StoreFranchise.OFF;

    public RegisterBusiness(BusinessTypeEnum businessType, StoreFranchise storeFranchise) {
        this.businessType = businessType;
        this.storeFranchise = storeFranchise;

        for (int i = 1; i <= 7; i++) {
            BusinessHour businessHour = new BusinessHour(DayOfWeek.of(i));
            if (StoreFranchise.OFF == storeFranchise) {
                switch (businessType) {
                    case RS:
                    case RSQ:
                    case FT:
                    case FTQ:
                        businessHour
                            .setTokenAvailableFrom(1000)
                            .setStartHourStore(1030)
                            .setTokenNotAvailableFrom(2000)
                            .setEndHourStore(2030);
                        break;
                    case BA:
                    case BAQ:
                        businessHour
                            .setTokenAvailableFrom(1100)
                            .setStartHourStore(1130)
                            .setTokenNotAvailableFrom(2000)
                            .setEndHourStore(2030);
                        break;
                    case ST:
                    case STQ:
                    case GS:
                    case GSQ:
                        businessHour
                            .setTokenAvailableFrom(530)
                            .setStartHourStore(600)
                            .setTokenNotAvailableFrom(2000)
                            .setEndHourStore(2030);
                        break;
                    case CF:
                    case CFQ:
                        businessHour
                            .setTokenAvailableFrom(800)
                            .setStartHourStore(800)
                            .setTokenNotAvailableFrom(1700)
                            .setEndHourStore(1800);
                        break;
                    case CD:
                    case CDQ:
                        businessHour
                            .setTokenAvailableFrom(830)
                            .setStartHourStore(915)
                            .setTokenNotAvailableFrom(1630)
                            .setEndHourStore(1700);
                        break;
                    default:
                        //Do Nothing for other business types. Default hours are 0.
                }
            }
            businessHours.add(businessHour);
        }
    }

    public RegisterBusiness() {
        for (int i = 1; i <= 7; i++) {
            BusinessHour businessHour = new BusinessHour(DayOfWeek.of(i));
            businessHours.add(businessHour);
        }
    }

    /* Business types are initialized in flow. Why? Show off. */
    @Transient
    private BusinessTypeEnum businessType = BusinessTypeEnum.GS;

    @Transient
    private boolean selectFoundAddress;

    @Transient
    private boolean selectFoundAddressStore;

    @Transient
    private boolean businessAddressAsStore;

    @Transient
    private List<BusinessTypeEnum> availableBusinessTypes;

    @Transient
    private List<LocaleEnum> availableLocaleTypes;

    @Transient
    public Map<String, String> categories;

    @Transient
    private Set<AmenityEnum> amenitiesAvailable = new LinkedHashSet<>();

    @Transient
    private Set<FacilityEnum> facilitiesAvailable = new LinkedHashSet<>();

    @Transient
    private Set<SupportedDeliveryEnum> supportedDeliveries = SupportedDeliveryEnum.all();

    @Transient
    private Set<SupportedPaymentEnum> supportedPayments = SupportedPaymentEnum.all();

    @Transient
    private WalkInStateEnum walkInState = WalkInStateEnum.E;

    @Transient
    private Map<String, String> walkinStates = WalkInStateEnum.asMapWithNameAsKey();

    @Transient
    private Map<String, String> appointmentStates;

    @Transient
    private AppointmentStateEnum appointmentIsOff = AppointmentStateEnum.O;

    @Transient
    private Character[] tokenPrefixes = RandomString.alphabetCharacters();

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getName() {
        return name;
    }

    public void setName(ScrubbedInput name) {
        this.name = name.getText();
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(ScrubbedInput address) {
        /* Java 8 regex engine supports \R which represents any line separator. */
        this.address = address.getText().replaceAll("\\R", " ");
    }

    public String getArea() {
        return area;
    }

    public RegisterBusiness setArea(ScrubbedInput area) {
        this.area = area.getText();
        return this;
    }

    public String getTown() {
        return town;
    }

    public RegisterBusiness setTown(ScrubbedInput town) {
        this.town = town.getText();
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(ScrubbedInput countryShortName) {
        this.countryShortName = countryShortName.getText();
    }

    public String getPhone() {
        if (StringUtils.isNotBlank(phone)) {
            return Formatter.phoneFormatter(phone, countryShortName);
        } else {
            return phone;
        }
    }

    @Transient
    public String getPhoneNotFormatted() {
        return Formatter.phoneCleanup(phone);
    }

    @Transient
    public String getPhoneWithCountryCode() {
        Assert.notNull(countryShortName, "Country code cannot be null");
        if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(countryShortName)) {
            return Formatter.phoneNumberWithCountryCode(Formatter.phoneCleanup(phone), countryShortName);
        }

        return null;
    }

    public void setPhone(ScrubbedInput phone) {
        this.phone = phone.getText();
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(ScrubbedInput timeZone) {
        this.timeZone = timeZone.getText();
    }

    public List<AmenityEnum> getAmenities() {
        return amenities;
    }

    public RegisterBusiness setAmenities(List<AmenityEnum> amenities) {
        this.amenities = amenities;
        return this;
    }

    public List<FacilityEnum> getFacilities() {
        return facilities;
    }

    public RegisterBusiness setFacilities(List<FacilityEnum> facilities) {
        this.facilities = facilities;
        return this;
    }

    public boolean isPopulateAmenitiesAndFacilities() {
        return populateAmenitiesAndFacilities;
    }

    public RegisterBusiness setPopulateAmenitiesAndFacilities(boolean populateAmenitiesAndFacilities) {
        this.populateAmenitiesAndFacilities = populateAmenitiesAndFacilities;
        return this;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public RegisterBusiness setClaimed(boolean claimed) {
        this.claimed = claimed;
        return this;
    }

    public String getLimitServiceByDays() {
        return limitServiceByDays;
    }

    public RegisterBusiness setLimitServiceByDays(String limitServiceByDays) {
        this.limitServiceByDays = limitServiceByDays;
        return this;
    }

    public LocaleEnum getSmsLocale() {
        return smsLocale;
    }

    public RegisterBusiness setSmsLocale(LocaleEnum smsLocale) {
        this.smsLocale = smsLocale;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public RegisterBusiness setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
        return this;
    }

    public String getInviteeCode() {
        return inviteeCode;
    }

    public void setInviteeCode(String inviteeCode) {
        this.inviteeCode = inviteeCode;
    }

    public AddressOriginEnum getAddressOrigin() {
        return addressOrigin;
    }

    public RegisterBusiness setAddressOrigin(AddressOriginEnum addressOrigin) {
        this.addressOrigin = addressOrigin;
        return this;
    }

    public BusinessUserEntity getBusinessUser() {
        return businessUser;
    }

    public void setBusinessUser(BusinessUserEntity businessUser) {
        this.businessUser = businessUser;
    }

    public List<BusinessTypeEnum> getAvailableBusinessTypes() {
        return availableBusinessTypes;
    }

    public void setAvailableBusinessTypes(List<BusinessTypeEnum> availableBusinessTypes) {
        this.availableBusinessTypes = availableBusinessTypes;
    }

    public List<LocaleEnum> getAvailableLocaleTypes() {
        return availableLocaleTypes;
    }

    public RegisterBusiness setAvailableLocaleTypes(List<LocaleEnum> availableLocaleTypes) {
        this.availableLocaleTypes = availableLocaleTypes;
        return this;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public RegisterBusiness setCategories(Map<String, String> categories) {
        this.categories = categories;
        return this;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public void setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(ScrubbedInput displayName) {
        this.displayName = displayName.getText();
    }

    public BusinessTypeEnum getStoreBusinessType() {
        return storeBusinessType;
    }

    public RegisterBusiness setStoreBusinessType(BusinessTypeEnum storeBusinessType) {
        this.storeBusinessType = storeBusinessType;
        return this;
    }

    public String getAddressStore() {
        return addressStore;
    }

    public void setAddressStore(ScrubbedInput addressStore) {
        /* Java 8 regex engine supports \R which represents any line separator. */
        this.addressStore = addressStore.getText().replaceAll("\\R", " ");
    }

    public String getAreaStore() {
        return areaStore;
    }

    public RegisterBusiness setAreaStore(ScrubbedInput areaStore) {
        this.areaStore = areaStore.getText();
        return this;
    }

    public String getTownStore() {
        return townStore;
    }

    public RegisterBusiness setTownStore(ScrubbedInput townStore) {
        this.townStore = townStore.getText();
        return this;
    }

    public boolean isBusinessAddressAsStore() {
        return businessAddressAsStore;
    }

    public RegisterBusiness setBusinessAddressAsStore(boolean businessAddressAsStore) {
        this.businessAddressAsStore = businessAddressAsStore;
        return this;
    }

    public String getPhoneStore() {
        if (StringUtils.isNotBlank(phoneStore)) {
            return Formatter.phoneFormatter(phoneStore, countryShortName);
        } else {
            return phoneStore;
        }
    }

    @Transient
    public String getPhoneStoreNotFormatted() {
        return Formatter.phoneCleanup(phoneStore);
    }

    public void setPhoneStore(ScrubbedInput phoneStore) {
        this.phoneStore = phoneStore.getText();
    }

    @Transient
    public String getPhoneStoreWithCountryCode() {
        Assert.notNull(countryShortNameStore, "Country code cannot be null");
        if (StringUtils.isNotBlank(phoneStore) && StringUtils.isNotBlank(countryShortNameStore)) {
            return Formatter.phoneNumberWithCountryCode(Formatter.phoneCleanup(phoneStore), countryShortNameStore);
        }

        return null;
    }

    public String getCountryShortNameStore() {
        return countryShortNameStore;
    }

    public void setCountryShortNameStore(ScrubbedInput countryShortNameStore) {
        this.countryShortNameStore = countryShortNameStore.getText();
    }

    public String getTimeZoneStore() {
        return timeZoneStore;
    }

    public void setTimeZoneStore(ScrubbedInput timeZoneStore) {
        this.timeZoneStore = timeZoneStore.getText();
    }

    public List<AmenityEnum> getAmenitiesStore() {
        return amenitiesStore;
    }

    public RegisterBusiness setAmenitiesStore(List<AmenityEnum> amenitiesStore) {
        this.amenitiesStore = amenitiesStore;
        return this;
    }

    public List<FacilityEnum> getFacilitiesStore() {
        return facilitiesStore;
    }

    public RegisterBusiness setFacilitiesStore(List<FacilityEnum> facilitiesStore) {
        this.facilitiesStore = facilitiesStore;
        return this;
    }

    public AddressOriginEnum getAddressStoreOrigin() {
        return addressStoreOrigin;
    }

    public RegisterBusiness setAddressStoreOrigin(AddressOriginEnum addressStoreOrigin) {
        this.addressStoreOrigin = addressStoreOrigin;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public RegisterBusiness setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getFamousFor() {
        return famousFor;
    }

    public RegisterBusiness setFamousFor(String famousFor) {
        this.famousFor = famousFor;
        return this;
    }

    public boolean isRemoteJoin() {
        return remoteJoin;
    }

    public RegisterBusiness setRemoteJoin(boolean remoteJoin) {
        this.remoteJoin = remoteJoin;
        return this;
    }

    public String getAppendPrefixToToken() {
        return appendPrefixToToken;
    }

    public RegisterBusiness setAppendPrefixToToken(String appendPrefixToToken) {
        this.appendPrefixToToken = appendPrefixToToken;
        return this;
    }

    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    public RegisterBusiness setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
        return this;
    }

    public boolean isAllowLoggedInUser() {
        return allowLoggedInUser;
    }

    public void setAllowLoggedInUser(boolean allowLoggedInUser) {
        this.allowLoggedInUser = allowLoggedInUser;
    }

    public int getAvailableTokenCount() {
        return availableTokenCount;
    }

    public RegisterBusiness setAvailableTokenCount(int availableTokenCount) {
        this.availableTokenCount = availableTokenCount;
        return this;
    }

    public List<BusinessHour> getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(List<BusinessHour> businessHours) {
        this.businessHours = businessHours;
    }

    public WalkInStateEnum getWalkInState() {
        return walkInState;
    }

    public void setWalkInState(WalkInStateEnum walkInState) {
        this.walkInState = walkInState;
    }

    public Map<String, String> getWalkinStates() {
        return walkinStates;
    }

    public void setWalkinStates(Map<String, String> walkinStates) {
        this.walkinStates = walkinStates;
    }

    public AppointmentStateEnum getAppointmentState() {
        return appointmentState;
    }

    public RegisterBusiness setAppointmentState(AppointmentStateEnum appointmentState) {
        this.appointmentState = appointmentState;
        return this;
    }

    public Map<String, String> getAppointmentStates() {
        return appointmentStates;
    }

    public RegisterBusiness setAppointmentStates(Map<String, String> appointmentStates) {
        this.appointmentStates = appointmentStates;
        return this;
    }

    public AppointmentStateEnum getAppointmentIsOff() {
        return appointmentIsOff;
    }

    public RegisterBusiness setAppointmentIsOff(AppointmentStateEnum appointmentIsOff) {
        this.appointmentIsOff = appointmentIsOff;
        return this;
    }

    public Character[] getTokenPrefixes() {
        return tokenPrefixes;
    }

    public RegisterBusiness setTokenPrefixes(Character[] tokenPrefixes) {
        this.tokenPrefixes = tokenPrefixes;
        return this;
    }

    public int getAppointmentDuration() {
        return appointmentDuration;
    }

    public RegisterBusiness setAppointmentDuration(int appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
        return this;
    }

    public int getAppointmentOpenHowFar() {
        return appointmentOpenHowFar;
    }

    public RegisterBusiness setAppointmentOpenHowFar(int appointmentOpenHowFar) {
        this.appointmentOpenHowFar = appointmentOpenHowFar;
        return this;
    }

    public Set<SupportedPaymentEnum> getAcceptedPayments() {
        return acceptedPayments;
    }

    public RegisterBusiness setAcceptedPayments(Set<SupportedPaymentEnum> acceptedPayments) {
        this.acceptedPayments = acceptedPayments;
        return this;
    }

    public Set<SupportedDeliveryEnum> getAcceptedDeliveries() {
        return acceptedDeliveries;
    }

    public RegisterBusiness setAcceptedDeliveries(Set<SupportedDeliveryEnum> acceptedDeliveries) {
        this.acceptedDeliveries = acceptedDeliveries;
        return this;
    }

    public BusinessUserRegistrationStatusEnum getBusinessUserRegistrationStatus() {
        return businessUserRegistrationStatus;
    }

    public RegisterBusiness setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum businessUserRegistrationStatus) {
        this.businessUserRegistrationStatus = businessUserRegistrationStatus;
        return this;
    }

    public HashMap<String, DecodedAddress> getFoundAddresses() {
        return foundAddresses;
    }

    public RegisterBusiness setFoundAddresses(HashMap<String, DecodedAddress> foundAddresses) {
        this.foundAddresses = foundAddresses;
        return this;
    }

    public String getFoundAddressPlaceId() {
        return foundAddressPlaceId;
    }

    public RegisterBusiness setFoundAddressPlaceId(String foundAddressPlaceId) {
        this.foundAddressPlaceId = foundAddressPlaceId;
        return this;
    }

    public boolean isSelectFoundAddress() {
        return selectFoundAddress;
    }

    public RegisterBusiness setSelectFoundAddress(boolean selectFoundAddress) {
        this.selectFoundAddress = selectFoundAddress;
        return this;
    }

    public HashMap<String, DecodedAddress> getFoundAddressStores() {
        return foundAddressStores;
    }

    public RegisterBusiness setFoundAddressStores(HashMap<String, DecodedAddress> foundAddressStores) {
        this.foundAddressStores = foundAddressStores;
        return this;
    }

    public String getFoundAddressStorePlaceId() {
        return foundAddressStorePlaceId;
    }

    public RegisterBusiness setFoundAddressStorePlaceId(String foundAddressStorePlaceId) {
        this.foundAddressStorePlaceId = foundAddressStorePlaceId;
        return this;
    }

    public StoreFranchise getStoreFranchise() {
        return storeFranchise;
    }

    public RegisterBusiness setStoreFranchise(StoreFranchise storeFranchise) {
        this.storeFranchise = storeFranchise;
        return this;
    }

    public boolean isSelectFoundAddressStore() {
        return selectFoundAddressStore;
    }

    public RegisterBusiness setSelectFoundAddressStore(boolean selectFoundAddressStore) {
        this.selectFoundAddressStore = selectFoundAddressStore;
        return this;
    }

    public Set<AmenityEnum> getAmenitiesAvailable() {
        return amenitiesAvailable;
    }

    public RegisterBusiness setAmenitiesAvailable(Set<AmenityEnum> amenitiesAvailable) {
        this.amenitiesAvailable = amenitiesAvailable;
        return this;
    }

    public RegisterBusiness addAmenitiesAvailable(Set<AmenityEnum> amenitiesAvailable) {
        this.amenitiesAvailable.addAll(amenitiesAvailable);
        return this;
    }

    public Set<FacilityEnum> getFacilitiesAvailable() {
        return facilitiesAvailable;
    }

    public RegisterBusiness setFacilitiesAvailable(Set<FacilityEnum> facilitiesAvailable) {
        this.facilitiesAvailable = facilitiesAvailable;
        return this;
    }

    public RegisterBusiness addFacilitiesAvailable(Set<FacilityEnum> facilitiesAvailable) {
        this.facilitiesAvailable.addAll(facilitiesAvailable);
        return this;
    }

    public Set<SupportedDeliveryEnum> getSupportedDeliveries() {
        return supportedDeliveries;
    }

    public RegisterBusiness setSupportedDeliveries(Set<SupportedDeliveryEnum> supportedDeliveries) {
        this.supportedDeliveries = supportedDeliveries;
        return this;
    }

    public Set<SupportedPaymentEnum> getSupportedPayments() {
        return supportedPayments;
    }

    public RegisterBusiness setSupportedPayments(Set<SupportedPaymentEnum> supportedPayments) {
        this.supportedPayments = supportedPayments;
        return this;
    }

    @Transient
    public void populateWithBizStore(BizStoreEntity bizStore, TokenQueueEntity tokenQueue) {
        this.bizStoreId = bizStore.getId();
        this.displayName = bizStore.getDisplayName();
        this.storeBusinessType = bizStore.getBusinessType();
        this.addressStore = bizStore.getAddress();
        this.areaStore = bizStore.getArea();
        this.townStore = bizStore.getTown();
        this.countryShortNameStore = bizStore.getCountryShortName();
        this.phoneStore = Formatter.phoneNationalFormat(bizStore.getPhoneRaw(), bizStore.getCountryShortName());
        this.timeZoneStore = bizStore.getTimeZone();
        this.addressStoreOrigin = bizStore.getAddressOrigin();
        this.foundAddressStorePlaceId = bizStore.getPlaceId();
        this.bizCategoryId = bizStore.getBizCategoryId();
        this.walkInState = bizStore.getWalkInState() == null ? WalkInStateEnum.D : bizStore.getWalkInState();
        this.averageServiceTime = bizStore.getAverageServiceTime() == 0 ? Constants.MINUTES_2_IN_MILLISECOND : bizStore.getAverageServiceTime();
        this.remoteJoin = bizStore.isRemoteJoin();
        this.appendPrefixToToken = tokenQueue.getAppendPrefix();
        this.allowLoggedInUser = bizStore.isAllowLoggedInUser();
        this.availableTokenCount = bizStore.getAvailableTokenCount();
        this.famousFor = bizStore.getFamousFor();
        this.amenitiesStore = bizStore.getAmenities();
        this.facilitiesStore = bizStore.getFacilities();
        this.appointmentState = bizStore.getAppointmentState();
        this.appointmentDuration = bizStore.getAppointmentDuration();
        this.appointmentOpenHowFar = bizStore.getAppointmentOpenHowFar();
        this.acceptedPayments = bizStore.getAcceptedPayments();
        this.acceptedDeliveries = bizStore.getAcceptedDeliveries();
    }

    @Transient
    public static RegisterBusiness populateWithBizName(BizNameEntity bizName, StoreFranchise storeFranchise) {
        RegisterBusiness registerBusiness = new RegisterBusiness(bizName.getBusinessType(), storeFranchise);
        registerBusiness.setBizId(bizName.getId());
        registerBusiness.setName(new ScrubbedInput(bizName.getBusinessName()));
        registerBusiness.setAddress(new ScrubbedInput(bizName.getAddress()));
        registerBusiness.setCountryShortName(new ScrubbedInput(bizName.getCountryShortName()));
        registerBusiness.setPhone(new ScrubbedInput(Formatter.phoneNationalFormat(bizName.getPhoneRaw(), bizName.getCountryShortName())));
        registerBusiness.setTimeZone(new ScrubbedInput(bizName.getTimeZone()));
        registerBusiness.setInviteeCode(bizName.getInviteeCode());
        registerBusiness.setAddressOrigin(bizName.getAddressOrigin());
        registerBusiness.setFoundAddressPlaceId(bizName.getPlaceId());
        return registerBusiness;
    }

    /**
     * Populate BusinessHours from StoreHour when editing existing records.
     *
     * @param storeHours
     * @return
     */
    public void convertToBusinessHours(List<StoreHourEntity> storeHours) {
        List<BusinessHour> businessHours = new LinkedList<>();
        for (StoreHourEntity storeHour : storeHours) {
            BusinessHour businessHour = new BusinessHour(DayOfWeek.of(storeHour.getDayOfWeek()));
            businessHour
                .setStartHourStore(storeHour.getStartHour())
                .setEndHourStore(storeHour.getEndHour())
                .setAppointmentStartHour(storeHour.getAppointmentStartHour())
                .setAppointmentEndHour(storeHour.getAppointmentEndHour())
                .setTokenAvailableFrom(storeHour.getTokenAvailableFrom())
                .setTokenNotAvailableFrom(storeHour.getTokenNotAvailableFrom())
                .setLunchTimeStart(storeHour.getLunchTimeStart())
                .setLunchTimeEnd(storeHour.getLunchTimeEnd())
                .setDayClosed(storeHour.isDayClosed());

            businessHours.add(businessHour);
        }

        this.businessHours = businessHours;
    }

    @SuppressWarnings("unused")
    @Transient
    public boolean decidePathTraversalOnRegistrationComplete() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return queueUser.getUserLevel() == UserLevelEnum.M_ADMIN;
    }


    public BigDecimal getAverageServiceTimeInMinutes() {
        return new BigDecimal(averageServiceTime).divide(new BigDecimal(60_000), MathContext.DECIMAL64).setScale(2, RoundingMode.CEILING);
    }

    public void setAverageServiceTimeInMinutes(BigDecimal averageServiceTimeInMinutes) {
        averageServiceTime = averageServiceTimeInMinutes.multiply(new BigDecimal(60_000)).longValue();
    }

    @Override
    public String toString() {
        return "RegisterBusiness{" +
                "bizId='" + bizId + '\'' +
                ", name='" + name + '\'' +
                ", businessType=" + businessType +
                ", address='" + address + '\'' +
                ", countryShortName='" + countryShortName + '\'' +
                ", phone='" + phone + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", addressOrigin=" + addressOrigin +
                ", businessUser=" + businessUser +
                ", bizStoreId='" + bizStoreId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", addressStore='" + addressStore + '\'' +
                ", countryShortNameStore='" + countryShortNameStore + '\'' +
                ", phoneStore='" + phoneStore + '\'' +
                ", timeZoneStore='" + timeZoneStore + '\'' +
                ", addressStoreOrigin=" + addressStoreOrigin +
                ", remoteJoin=" + remoteJoin +
                ", allowLoggedInUser=" + allowLoggedInUser +
                ", businessHours=" + businessHours +
                ", foundAddresses=" + foundAddresses +
                ", foundAddressPlaceId='" + foundAddressPlaceId + '\'' +
                ", selectFoundAddress=" + selectFoundAddress +
                ", foundAddressStores=" + foundAddressStores +
                ", foundAddressStorePlaceId='" + foundAddressStorePlaceId + '\'' +
                ", selectFoundAddressStore=" + selectFoundAddressStore +
                ", availableBusinessTypes=" + availableBusinessTypes +
                '}';
    }
}
