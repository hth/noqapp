package com.noqapp.view.form.business.payout;

import com.noqapp.domain.types.TransactionViaEnum;

import java.util.Date;

/**
 * User: hitender
 * Date: 2019-03-31 12:36
 */
public class TransactionForm {

    private Date dayOfTransaction;
    private String internalTransaction;
    private String externalTransaction;
    private String unknownTransaction;

    private TransactionViaEnum transactionVia;

    public Date getDayOfTransaction() {
        return dayOfTransaction;
    }

    public TransactionForm setDayOfTransaction(Date dayOfTransaction) {
        this.dayOfTransaction = dayOfTransaction;
        return this;
    }

    public String getInternalTransaction() {
        return internalTransaction;
    }

    public TransactionForm setInternalTransaction(String internalTransaction) {
        this.internalTransaction = internalTransaction;
        return this;
    }

    public String getExternalTransaction() {
        return externalTransaction;
    }

    public TransactionForm setExternalTransaction(String externalTransaction) {
        this.externalTransaction = externalTransaction;
        return this;
    }

    public String getUnknownTransaction() {
        return unknownTransaction;
    }

    public TransactionForm setUnknownTransaction(String unknownTransaction) {
        this.unknownTransaction = unknownTransaction;
        return this;
    }

    public TransactionViaEnum getTransactionVia() {
        return transactionVia;
    }

    public TransactionForm setTransactionVia(TransactionViaEnum transactionVia) {
        this.transactionVia = transactionVia;
        return this;
    }
}
