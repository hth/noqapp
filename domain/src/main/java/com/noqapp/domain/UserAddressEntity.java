package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * hitender
 * 5/15/18 9:23 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "USER_ADDRESS")
@CompoundIndexes({
        @CompoundIndex(name = "user_address_idx", def = "{'QID': 1}", unique = false)
})
public class UserAddressEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field ("AD")
    private String address;

    @Field ("GH")
    private String geoHash;

    @Field ("CS")
    private String countryShortName;

    @Field ("LU")
    private Date lastUsed;

    @SuppressWarnings("unused")
    private UserAddressEntity() {
        super();
    }

    public UserAddressEntity(String queueUserId, String address) {
        this.queueUserId = queueUserId;
        this.address = address;
        this.setLastUsed();
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getAddress() {
        return address;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public UserAddressEntity setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public UserAddressEntity setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public UserAddressEntity setLastUsed() {
        this.lastUsed = new Date();
        return this;
    }
}
