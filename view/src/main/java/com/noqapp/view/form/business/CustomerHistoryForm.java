package com.noqapp.view.form.business;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 12/1/19 1:50 AM
 */
public class CustomerHistoryForm {

    private ScrubbedInput lookupPhone;
    private UserProfileEntity userProfile;
    private List<UserProfileEntity> userProfileOfDependents = new ArrayList<>();

    private List<QueueEntity> currentAndHistoricalQueues = new ArrayList<>();

    private List<PurchaseOrderEntity> currentPurchaseOrders = new ArrayList<>();
    private List<PurchaseOrderEntity> historicalPurchaseOrders = new ArrayList<>();

    public boolean isBusinessCustomer;
    private Map<String, String> qidNameMaps = new HashMap<>();

    public ScrubbedInput getLookupPhone() {
        return lookupPhone;
    }

    public CustomerHistoryForm setLookupPhone(ScrubbedInput lookupPhone) {
        this.lookupPhone = lookupPhone;
        return this;
    }

    public UserProfileEntity getUserProfile() {
        return userProfile;
    }

    public CustomerHistoryForm setUserProfile(UserProfileEntity userProfile) {
        this.userProfile = userProfile;
        return this;
    }

    public List<UserProfileEntity> getUserProfileOfDependents() {
        return userProfileOfDependents;
    }

    public CustomerHistoryForm setUserProfileOfDependents(List<UserProfileEntity> userProfileOfDependents) {
        this.userProfileOfDependents = userProfileOfDependents;
        return this;
    }

    public CustomerHistoryForm addUserProfileOfDependent(UserProfileEntity userProfileOfDependent) {
        this.userProfileOfDependents.add(userProfileOfDependent);
        return this;
    }

    public List<QueueEntity> getCurrentAndHistoricalQueues() {
        return currentAndHistoricalQueues;
    }

    public CustomerHistoryForm addCurrentAndHistoricalQueues(List<QueueEntity> currentAndHistoricalQueues) {
        this.currentAndHistoricalQueues.addAll(currentAndHistoricalQueues);
        return this;
    }

    public List<PurchaseOrderEntity> getCurrentPurchaseOrders() {
        return currentPurchaseOrders;
    }

    public CustomerHistoryForm addCurrentPurchaseOrder(List<PurchaseOrderEntity> currentPurchaseOrders) {
        this.currentPurchaseOrders.addAll(currentPurchaseOrders);
        return this;
    }

    public List<PurchaseOrderEntity> getHistoricalPurchaseOrders() {
        return historicalPurchaseOrders;
    }

    public CustomerHistoryForm addHistoricalPurchaseOrder(List<PurchaseOrderEntity> historicalPurchaseOrders) {
        this.historicalPurchaseOrders.addAll(historicalPurchaseOrders);
        return this;
    }

    public boolean isBusinessCustomer() {
        return isBusinessCustomer;
    }

    public CustomerHistoryForm setBusinessCustomer(boolean businessCustomer) {
        isBusinessCustomer = businessCustomer;
        return this;
    }

    public Map<String, String> getQidNameMaps() {
        return qidNameMaps;
    }

    public CustomerHistoryForm setQidNameMaps(Map<String, String> qidNameMaps) {
        this.qidNameMaps = qidNameMaps;
        return this;
    }

    public CustomerHistoryForm addQidNameMap(String qid, String name) {
        this.qidNameMaps.put(qid, name);
        return this;
    }
}
