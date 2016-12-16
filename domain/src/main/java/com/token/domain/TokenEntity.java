package com.token.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Token should exists only when open for business or when token is suppose to be made available.
 *
 * User: hitender
 * Date: 12/15/16 9:42 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "TOKEN")
public class TokenEntity extends BaseEntity {

    @Field("LN")
    private int lastNumber;

    @Field ("CS")
    private int currentlyServing;

    @Field ("CQ")
    private boolean closeQueue;

    public TokenEntity(String codeQR) {
        this.id = codeQR;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
    }

    public int getCurrentlyServing() {
        return currentlyServing;
    }

    public void setCurrentlyServing(int currentlyServing) {
        this.currentlyServing = currentlyServing;
    }

    public boolean isCloseQueue() {
        return closeQueue;
    }

    public void setCloseQueue(boolean closeQueue) {
        this.closeQueue = closeQueue;
    }
}
