package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 2019-03-31 01:56
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "PAYOUT")
@CompoundIndexes(value = {
    @CompoundIndex(name = "payout_idx", def = "{'BN' : 1}", unique = false),
})
public class PayoutEntity extends BaseEntity {

    @Field("BN")
    private String bizNameId;

    @Field("WD")
    private String withdraw;

    @Field("BL")
    private String balance;

    public String getBizNameId() {
        return bizNameId;
    }

    public PayoutEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getWithdraw() {
        return withdraw;
    }

    public PayoutEntity setWithdraw(String withdraw) {
        this.withdraw = withdraw;
        return this;
    }

    public String getBalance() {
        return balance;
    }

    public PayoutEntity setBalance(String balance) {
        this.balance = balance;
        return this;
    }
}
