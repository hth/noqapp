package com.noqapp.domain;

import com.noqapp.domain.shared.GeoPointOfQ;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 5/15/18 9:23 PM
 */
@SuppressWarnings({
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

    /* Can be null. Mostly provided during delivery address. */
    @Field("CN")
    private String customerName;

    @Field("AD")
    private String address;

    @Field("AR")
    private String area;

    @Field("TO")
    private String town;

    @Field("DT")
    private String district;

    @Field("ST")
    private String state;

    @Field("SS")
    private String stateShortName;

    @Field("CS")
    private String countryShortName;

    @Field("PA")
    private boolean primaryAddress;

    @Field("GH")
    private String geoHash;

    /* Format Longitude and then Latitude. */
    @Field("COR")
    private double[] coordinate;

    @Field("LU")
    private Date lastUsed;

    @SuppressWarnings("unused")
    public UserAddressEntity() {
        super();
    }

    public UserAddressEntity(
        String queueUserId,
        String customerName,
        String address,
        String area,
        String town,
        String district,
        String state,
        String stateShortName,
        String countryShortName,
        String geoHash,
        double[] coordinate
    ) {
        this.queueUserId = queueUserId;
        this.customerName = customerName;
        this.address = address;
        this.area = area;
        this.town = town;
        this.district = district;
        this.state = state;
        this.stateShortName = stateShortName;
        this.countryShortName = countryShortName;
        this.geoHash = geoHash;
        this.coordinate = coordinate;
        this.setLastUsed();
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAddress() {
        return address;
    }

    public String getArea() {
        return area;
    }

    public String getTown() {
        return town;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public boolean isPrimaryAddress() {
        return primaryAddress;
    }

    public UserAddressEntity setPrimaryAddress(boolean primaryAddress) {
        this.primaryAddress = primaryAddress;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public UserAddressEntity setLastUsed() {
        this.lastUsed = new Date();
        return this;
    }

    @Transient
    public GeoPointOfQ getGeoPointOfQ() {
        /* Latitude and then Longitude. */
        return new GeoPointOfQ(coordinate[1], coordinate[0]);
    }
}
