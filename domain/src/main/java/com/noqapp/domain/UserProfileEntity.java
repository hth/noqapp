package com.noqapp.domain;

import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.domain.types.UserLevelEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * User: hitender
 * Date: 11/18/16 6:02 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "USER_PROFILE")
@CompoundIndexes ({
        @CompoundIndex (name = "user_profile_qid_em_idx", def = "{'QID': -1, 'EM' : 1}", unique = true),
        @CompoundIndex (name = "user_profile_em_idx", def = "{'EM': 1}", unique = true),
        @CompoundIndex (name = "user_profile_ph_idx", def = "{'PH': 1}", unique = true),
        @CompoundIndex (name = "user_profile_ic_idx", def = "{'IC': 1}", unique = true),
        @CompoundIndex (name = "user_profile_guardian_idx", def = "{'GP' : 1}", unique = false, sparse = true),
})
public class UserProfileEntity extends BaseEntity {

    @NotNull
    @Field ("QID")
    private String queueUserId;

    @Field ("PI")
    private String profileImage;

    @Field ("FN")
    private String firstName;
    
    @Field ("LN")
    private String lastName;

    @Field ("GE")
    private GenderEnum gender;

    @Field ("LO")
    private Locale locale;

    @Field ("EM")
    private String email;

    @Field ("TZ")
    private String timeZone;

    @Field ("BD")
    private String birthday;

    @NotNull
    @Field ("UL")
    private UserLevelEnum level = UserLevelEnum.CLIENT;

    @Field ("AD")
    private String address;

    @Field ("CS")
    private String countryShortName;

    /* Phone number saved with country code. */
    @NotNull
    @Field ("PH")
    private String phone;

    /* To not loose user entered phone number. */
    @Field ("PR")
    private String phoneRaw;

    @Field ("GP")
    private String guardianPhone;

    @Field ("GT")
    private List<String> guardianToQueueUserId;

    @NotNull
    @Field ("IC")
    private String inviteCode;

    @Field ("AO")
    private AddressOriginEnum addressOrigin;

    /** To make bean happy. */
    public UserProfileEntity() {
        super();
    }

    private UserProfileEntity(String email, String firstName, String lastName, String queueUserId, String birthday) {
        super();
        this.email = email;
        this.firstName = WordUtils.capitalizeFully(firstName);
        this.lastName = WordUtils.capitalizeFully(lastName);
        this.queueUserId = queueUserId;
        this.birthday = birthday;
    }

    /**
     * This method is used when the Entity is created for the first time.
     *
     * @param firstName
     * @param lastName
     * @return
     */
    public static UserProfileEntity newInstance(String email, String firstName, String lastName, String queueUserId, String birthday) {
        return new UserProfileEntity(email, firstName, lastName, queueUserId, birthday);
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public void setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public UserProfileEntity setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = WordUtils.capitalizeFully(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = WordUtils.capitalizeFully(lastName);
    }

    public GenderEnum getGender() {
        return gender;
    }

    public UserProfileEntity setGender(GenderEnum gender) {
        this.gender = gender;
        return this;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public UserLevelEnum getLevel() {
        return level;
    }

    public void setLevel(UserLevelEnum level) {
        this.level = level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
    }

    public String getPhone() {
        return phone;
    }

    /* Phone number is suppose to be with country code. */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneFormatted() {
        if (StringUtils.isNotBlank(phone)) {
            return Formatter.phoneFormatter(phone, countryShortName);
        } else {
            return "";
        }
    }

    public String getPhoneRaw() {
        return phoneRaw;
    }

    public void setPhoneRaw(String phoneRaw) {
        this.phoneRaw = phoneRaw;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public UserProfileEntity setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
        return this;
    }

    public List<String> getGuardianToQueueUserId() {
        return guardianToQueueUserId;
    }

    public UserProfileEntity setGuardianToQueueUserId(List<String> guardianToQueueUserId) {
        this.guardianToQueueUserId = guardianToQueueUserId;
        return this;
    }

    public UserProfileEntity addGuardianToQueueUserId(String qid) {
        if (null == this.guardianToQueueUserId) {
            this.guardianToQueueUserId = new ArrayList<>();
        }
        this.guardianToQueueUserId.add(qid);
        return this;
    }

    public AddressOriginEnum getAddressOrigin() {
        return addressOrigin;
    }

    public UserProfileEntity setAddressOrigin(AddressOriginEnum addressOrigin) {
        this.addressOrigin = addressOrigin;
        return this;
    }

    @Transient
    public String getName() {
        if (StringUtils.isNotBlank(lastName)) {
            return StringUtils.trim(firstName + UserAccountEntity.BLANK_SPACE + lastName);
        }

        return firstName;
    }

    @Transient
    public String getInitials() {
        String name = getName();
        if (!StringUtils.isBlank(name)) {
            return WordUtils.initials(name);
        } else {
            return WordUtils.initials(getEmail()) + "@";
        }
    }

    @Transient
    public long getAge() {
        return ChronoUnit.YEARS.between(LocalDate.parse(birthday), LocalDate.now());
    }
}
