package com.noqapp.domain;

import com.noqapp.domain.types.PointActivityEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 6/24/21 6:38 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "POINT_EARNED")
@CompoundIndexes(value = {
    @CompoundIndex(name = "point_earned_idx", def = "{'QID' : 1}", unique = false),
})
public class PointEarnedEntity extends BaseEntity {

    @Field("QID")
    private String queueUserId;

    @Field("PN")
    private int point;

    @Field("PA")
    private PointActivityEnum pointActivity;

    @Field("MC")
    private boolean markedComputed;

    @SuppressWarnings("unused")
    public PointEarnedEntity() {
        //Default constructor, required to keep bean happy
    }

    public PointEarnedEntity(String queueUserId, PointActivityEnum pointActivity) {
        this.queueUserId = queueUserId;
        this.point = pointActivity.getPoint();
        this.pointActivity = pointActivity;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public int getPoint() {
        return point;
    }

    public PointActivityEnum getPointActivity() {
        return pointActivity;
    }

    public boolean isMarkedComputed() {
        return markedComputed;
    }

    public PointEarnedEntity setMarkedComputed(boolean markedComputed) {
        this.markedComputed = markedComputed;
        return this;
    }
}
