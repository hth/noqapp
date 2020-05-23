package com.noqapp.view.form.business;

import com.noqapp.domain.types.BusinessTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 4/18/20 9:52 PM
 */
public class MigrateBusinessTypeForm {
    private static final Logger LOG = LoggerFactory.getLogger(MigrateBusinessTypeForm.class);

    private BusinessTypeEnum existingBusinessType;
    private BusinessTypeEnum allowedMigrationBusinessType;

    private boolean migrate;
    private boolean migrationSuccess;
    private String migrationMessage;

    public BusinessTypeEnum getExistingBusinessType() {
        return existingBusinessType;
    }

    public MigrateBusinessTypeForm setExistingBusinessType(BusinessTypeEnum existingBusinessType) {
        this.existingBusinessType = existingBusinessType;
        return this;
    }

    public BusinessTypeEnum getAllowedMigrationBusinessType() {
        return allowedMigrationBusinessType;
    }

    public MigrateBusinessTypeForm setAllowedMigrationBusinessType(BusinessTypeEnum allowedMigrationBusinessType) {
        this.allowedMigrationBusinessType = allowedMigrationBusinessType;
        return this;
    }

    public boolean isMigrate() {
        return migrate;
    }

    public MigrateBusinessTypeForm setMigrate(boolean migrate) {
        this.migrate = migrate;
        return this;
    }

    public boolean isMigrationSuccess() {
        return migrationSuccess;
    }

    public MigrateBusinessTypeForm setMigrationSuccess(boolean migrationSuccess) {
        this.migrationSuccess = migrationSuccess;
        return this;
    }

    public String getMigrationMessage() {
        return migrationMessage;
    }

    public MigrateBusinessTypeForm setMigrationMessage(String migrationMessage) {
        this.migrationMessage = migrationMessage;
        return this;
    }
}
