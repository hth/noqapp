package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.medical.JsonUserMedicalProfile;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.domain.types.UserLevelEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 3/25/17 2:05 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable",
    "unused"
})
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude (JsonInclude.Include.NON_NULL)
public final class JsonProfile extends AbstractDomain {
    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty ("pi")
    private String profileImage;

    @JsonProperty ("nm")
    private String name;

    @JsonProperty ("em")
    private String mail;

    @JsonProperty ("cs")
    private String countryShortName;

    @JsonProperty ("pr")
    private String phoneRaw;

    @JsonProperty ("tz")
    private String timeZone;

    @JsonProperty ("ic")
    private String inviteCode;

    @JsonProperty("ep")
    private int earnedPoint;

    @JsonProperty ("bd")
    private String birthday;

    @JsonProperty ("ge")
    private GenderEnum gender;

    @JsonProperty("ul")
    private UserLevelEnum userLevel;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("av")
    private boolean accountValidated;

    @JsonProperty ("mp")
    private JsonUserMedicalProfile jsonUserMedicalProfile;

    /* Dependents can be anyone minor or other elderly family members. */
    @JsonProperty ("dp")
    private List<JsonProfile> dependents = new ArrayList<>();

    @JsonProperty("ads")
    private List<JsonUserAddress> jsonUserAddresses = new ArrayList<>();

    @JsonProperty("up")
    private JsonUserPreference jsonUserPreference;

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("cbs")
    private Map<String, String> codeQRAndBizStoreIds = new HashMap<>();

    @JsonProperty("pv")
    private boolean profileVerified;

    public JsonProfile() {
        //Required Default Constructor
    }

    private JsonProfile(
        String queueUserId,
        String profileImage,
        String name,
        String mail,
        String countryShortName,
        String phoneRaw,
        String timeZone,
        String inviteCode,
        String birthday,
        GenderEnum gender,
        UserLevelEnum userLevel,
        BusinessTypeEnum businessType,
        boolean accountValidated,
        boolean profileVerified,
        int earnedPoint
    ) {
        this.queueUserId = queueUserId;
        this.profileImage = profileImage;
        this.name = name;
        this.mail = mail;
        this.countryShortName = countryShortName;
        this.phoneRaw = Formatter.phoneFormatter(phoneRaw, countryShortName);
        this.timeZone = timeZone;
        this.inviteCode = inviteCode;
        this.birthday = birthday;
        this.gender = gender;
        this.userLevel = userLevel;
        this.businessType = businessType;
        this.accountValidated = accountValidated;
        this.profileVerified = profileVerified;
        this.earnedPoint = earnedPoint;
    }

    public static JsonProfile newInstance(UserProfileEntity userProfile, UserAccountEntity userAccount, int earnedPoint) {
        return new JsonProfile(
            userProfile.getQueueUserId(),
            userProfile.getProfileImage(),
            userProfile.getName(),
            userProfile.getEmail(),
            userProfile.getCountryShortName(),
            StringUtils.isBlank(userProfile.getGuardianPhone()) ? userProfile.getPhoneRaw() : userProfile.getGuardianPhone(),
            userProfile.getTimeZone(),
            userProfile.getInviteCode(),
            userProfile.getBirthday(),
            userProfile.getGender(),
            userProfile.getLevel(),
            userProfile.getBusinessType(),
            userAccount.isAccountValidated(),
            userProfile.isProfileVerified(),
            earnedPoint
        );
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public String getPhoneRaw() {
        return phoneRaw;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public String getBirthday() {
        return birthday;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public UserLevelEnum getUserLevel() {
        return userLevel;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public boolean isAccountValidated() {
        return accountValidated;
    }

    public int getEarnedPoint() {
        return earnedPoint;
    }

    public JsonUserMedicalProfile getJsonUserMedicalProfile() {
        return jsonUserMedicalProfile;
    }

    public JsonProfile setJsonUserMedicalProfile(JsonUserMedicalProfile jsonUserMedicalProfile) {
        this.jsonUserMedicalProfile = jsonUserMedicalProfile;
        return this;
    }

    public List<JsonProfile> getDependents() {
        return dependents;
    }

    public JsonProfile addDependents(JsonProfile dependent) {
        this.dependents.add(dependent);
        return this;
    }

    public List<JsonUserAddress> getJsonUserAddresses() {
        return jsonUserAddresses;
    }

    public JsonProfile setJsonUserAddresses(List<JsonUserAddress> jsonUserAddresses) {
        this.jsonUserAddresses = jsonUserAddresses;
        return this;
    }

    public JsonUserPreference getJsonUserPreference() {
        return jsonUserPreference;
    }

    public JsonProfile setJsonUserPreference(JsonUserPreference jsonUserPreference) {
        this.jsonUserPreference = jsonUserPreference;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public JsonProfile setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public Map<String, String> getCodeQRAndBizStoreIds() {
        return codeQRAndBizStoreIds;
    }

    public JsonProfile setCodeQRAndBizStoreIds(Map<String, String> codeQRAndBizStoreIds) {
        this.codeQRAndBizStoreIds = codeQRAndBizStoreIds;
        return this;
    }

    public JsonProfile addCodeQRAndBizStoreId(String codeQR, String bizStoreId) {
        this.codeQRAndBizStoreIds.put(codeQR, bizStoreId);
        return this;
    }

    public boolean isProfileVerified() {
        return profileVerified;
    }

    public JsonProfile setProfileVerified(boolean profileVerified) {
        this.profileVerified = profileVerified;
        return this;
    }
}
