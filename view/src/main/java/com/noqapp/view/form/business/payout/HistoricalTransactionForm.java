package com.noqapp.view.form.business.payout;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.PurchaseOrderEntity;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-03-31 12:28
 */
public class HistoricalTransactionForm {

    private int durationInDays;
    private Map<Date, TransactionForm> historicalTransaction = new LinkedHashMap<>();

    public int getDurationInDays() {
        return durationInDays;
    }

    public HistoricalTransactionForm setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
        return this;
    }

    public Map<Date, TransactionForm> getHistoricalTransaction() {
        return historicalTransaction;
    }

    public HistoricalTransactionForm setHistoricalTransaction(Map<Date, TransactionForm> historicalTransaction) {
        this.historicalTransaction = historicalTransaction;
        return this;
    }

    private void addTransactionForm(TransactionForm transactionForm) {
        if (historicalTransaction.containsKey(transactionForm.getDayOfTransaction())) {
            TransactionForm existingForm = historicalTransaction.get(transactionForm.getDayOfTransaction());
            populateExistingForm(transactionForm, existingForm);
        } else {
            TransactionForm existingForm = new TransactionForm().setDayOfTransaction(transactionForm.getDayOfTransaction());
            populateExistingForm(transactionForm, existingForm);
            historicalTransaction.put(transactionForm.getDayOfTransaction(), existingForm);
        }
    }

    private void populateExistingForm(TransactionForm transactionForm, TransactionForm existingForm) {
        existingForm
            .setTransactionVia(transactionForm.getTransactionVia())
            .addPaymentStatusNetPayment(transactionForm.getPaymentStatus(), transactionForm.getGrandTotal());
    }

    public void populate(List<PurchaseOrderEntity> purchaseOrders, String countryShortName) {
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            TransactionForm transactionForm = new TransactionForm()
                .setPaymentStatus(purchaseOrder.getPaymentStatus())
                .setDayOfTransaction(purchaseOrder.getCreated())
                .setGrandTotal(CommonUtil.displayWithCurrencyCode(purchaseOrder.getGrandTotalForDisplay(), countryShortName))
                .setTransactionVia(purchaseOrder.getTransactionVia());
            addTransactionForm(transactionForm);
        }
    }
}
