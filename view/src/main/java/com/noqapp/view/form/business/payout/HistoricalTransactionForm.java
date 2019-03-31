package com.noqapp.view.form.business.payout;

import com.noqapp.common.utils.MathUtil;
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
        switch (transactionForm.getTransactionVia()) {
            case E:
                existingForm.setExternalTransaction(transactionForm.getExternalTransaction());
                break;
            case I:
                existingForm.setInternalTransaction(transactionForm.getExternalTransaction());
                break;
            case U:
                existingForm.setUnknownTransaction(transactionForm.getExternalTransaction());
                break;
        }
    }

    public void populate(List<PurchaseOrderEntity> purchaseOrders) {
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            TransactionForm transactionForm = new TransactionForm()
                .setDayOfTransaction(purchaseOrder.getCreated())
                .setExternalTransaction(MathUtil.displayPrice(purchaseOrder.getOrderPrice()))
                .setTransactionVia(purchaseOrder.getTransactionVia());
            addTransactionForm(transactionForm);
        }
    }
}
