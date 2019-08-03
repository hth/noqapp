package com.noqapp.inventory.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.inventory.domain.CheckAssetEntity;
import com.noqapp.service.exceptions.CSVParsingException;
import com.noqapp.service.exceptions.CSVProcessingException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-08-02 13:31
 */
@Service
public class AssetFileService {
    private static final Logger LOG = LoggerFactory.getLogger(AssetFileService.class);

    static String[] ASSET_HEADERS = {
        "Name",
        "Room",
        "Floor",
        "Key",
    };

    /** Process bulk upload of CSV file for a store. */
    List<CheckAssetEntity> processUploadedForMasterProductCSVFile(InputStream in, String bizNameId) {
        try {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(ASSET_HEADERS)
                .withFirstRecordAsHeader()
                .parse(new InputStreamReader(in));

            List<CheckAssetEntity> checkAssets = new ArrayList<>();
            for (CSVRecord record : records) {
                try {
                    CheckAssetEntity storeProduct = getStoreProductEntityFromCSV(record, bizNameId);
                    checkAssets.add(storeProduct);
                } catch (Exception e) {
                    LOG.warn("Failed parsing lineNumber={} reason={}", record.getRecordNumber(), e.getLocalizedMessage());
                    throw new CSVProcessingException("Error at line " + record.getRecordNumber());
                }
            }
            return checkAssets;
        } catch (IOException e) {
            LOG.warn("Error reason={}", e.getLocalizedMessage());
            throw new CSVParsingException("Invalid file");
        }
    }

    /** Read from a CSV file. */
    private CheckAssetEntity getStoreProductEntityFromCSV(CSVRecord record, String bizNameId) {
        CheckAssetEntity checkAsset = new CheckAssetEntity();
        checkAsset
            .setAssetName(record.get("Name").trim())
            .setRoomNumber(record.get("Room").trim())
            .setFloor(record.get("Floor").trim())
            .setBizNameId(bizNameId);
        if (StringUtils.isNotBlank(record.get("Key")) && Validate.isValidObjectId(record.get("Key"))) {
            checkAsset.setId(record.get("Key"));
        } else {
            checkAsset.setId(CommonUtil.generateHexFromObjectId());
        }
        return checkAsset;
    }
}
