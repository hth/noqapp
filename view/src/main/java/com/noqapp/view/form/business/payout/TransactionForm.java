package com.noqapp.view.form.business.payout;

import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.TransactionViaEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-03-31 12:36
 */
public class TransactionForm {

    private Date dayOfTransaction;
    private PaymentStatusEnum paymentStatus;
    private String grandTotal;

    /** All payment types holder. */
    private Map<PaymentStatusEnum, String> paymentStatusNetPayments = new HashMap<>();

    private TransactionViaEnum transactionVia;

    public Date getDayOfTransaction() {
        return dayOfTransaction;
    }

    public TransactionForm setDayOfTransaction(Date dayOfTransaction) {
        this.dayOfTransaction = dayOfTransaction;
        return this;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public TransactionForm setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public TransactionForm setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
        return this;
    }

    public Map<PaymentStatusEnum, String> getPaymentStatusNetPayments() {
        return paymentStatusNetPayments;
    }

    public TransactionForm setPaymentStatusNetPayments(Map<PaymentStatusEnum, String> paymentStatusNetPayments) {
        this.paymentStatusNetPayments = paymentStatusNetPayments;
        return this;
    }

    public TransactionForm addPaymentStatusNetPayment(PaymentStatusEnum paymentStatusEnum, String payment) {
        this.paymentStatusNetPayments.put(paymentStatusEnum, payment);
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
