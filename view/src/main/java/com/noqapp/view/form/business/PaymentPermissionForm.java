package com.noqapp.view.form.business;

import com.noqapp.domain.types.PaymentPermissionEnum;

import java.util.Map;

/**
 * User: hitender
 * Date: 2019-04-27 02:02
 */
public class PaymentPermissionForm {
    private PaymentPermissionEnum paymentPermissionForSupervisor;
    private PaymentPermissionEnum paymentPermissionForManager;

    private Map<String, String> paymentPermissions;

    public PaymentPermissionEnum getPaymentPermissionForSupervisor() {
        return paymentPermissionForSupervisor;
    }

    public PaymentPermissionForm setPaymentPermissionForSupervisor(PaymentPermissionEnum paymentPermissionForSupervisor) {
        this.paymentPermissionForSupervisor = paymentPermissionForSupervisor;
        return this;
    }

    public PaymentPermissionEnum getPaymentPermissionForManager() {
        return paymentPermissionForManager;
    }

    public PaymentPermissionForm setPaymentPermissionForManager(PaymentPermissionEnum paymentPermissionForManager) {
        this.paymentPermissionForManager = paymentPermissionForManager;
        return this;
    }

    public Map<String, String> getPaymentPermissions() {
        return paymentPermissions;
    }

    public PaymentPermissionForm setPaymentPermissions(Map<String, String> paymentPermissions) {
        this.paymentPermissions = paymentPermissions;
        return this;
    }
}
