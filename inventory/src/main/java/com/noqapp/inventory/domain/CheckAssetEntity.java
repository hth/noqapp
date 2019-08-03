package com.noqapp.inventory.domain;

import com.noqapp.domain.BaseEntity;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 2019-07-29 23:18
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "CHECK_ASSET")
@CompoundIndexes({
    @CompoundIndex(name = "check_asset_idx", def = "{'BN': 1}", unique = false)
})
public class CheckAssetEntity extends BaseEntity {

    @Field("BN")
    private String bizNameId;

    @Field ("FL")
    private String floor;

    @Field ("RN")
    private String roomNumber;

    @Field ("AN")
    private String assetName;

    public String getBizNameId() {
        return bizNameId;
    }

    public CheckAssetEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getFloor() {
        return floor;
    }

    public CheckAssetEntity setFloor(String floor) {
        this.floor = floor;
        return this;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public CheckAssetEntity setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
        return this;
    }

    public String getAssetName() {
        return assetName;
    }

    public CheckAssetEntity setAssetName(String assetName) {
        this.assetName = assetName;
        return this;
    }

    @Transient
    public String toCommaSeparatedString() {
        return assetName + "," + roomNumber + "," + floor + "," + id;
    }
}
