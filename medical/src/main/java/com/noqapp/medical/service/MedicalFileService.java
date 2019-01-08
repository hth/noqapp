package com.noqapp.medical.service;

import static com.noqapp.service.FileService.RADIOLOGY_PRODUCT_HEADERS;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.medical.domain.MasterLabEntity;
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
 * hitender
 * 2018-12-11 06:21
 */
@Service
public class MedicalFileService {
    private static final Logger LOG = LoggerFactory.getLogger(com.noqapp.service.FileService.class);

    /** Process bulk upload of CSV file for a store. */
    List<MasterLabEntity> processUploadedForMasterProductCSVFile(InputStream in, HealthCareServiceEnum healthCareService) {
        try {
            Iterable<CSVRecord> records;
            switch (healthCareService) {
                case XRAY:
                case SONO:
                case SCAN:
                case SPEC:
                case PATH:
                    records = CSVFormat.DEFAULT
                        .withHeader(RADIOLOGY_PRODUCT_HEADERS)
                        .withFirstRecordAsHeader()
                        .parse(new InputStreamReader(in));
                    break;
                case PHYS:
                    LOG.error("Reached unsupported condition={}", healthCareService);
                    throw new UnsupportedOperationException("Reached unsupported condition " + healthCareService);
                default:
                    LOG.error("Reached unsupported condition={}", healthCareService);
                    throw new UnsupportedOperationException("Reached unsupported condition " + healthCareService);
            }

            List<MasterLabEntity> storeProducts = new ArrayList<>();
            for (CSVRecord record : records) {
                try {
                    MasterLabEntity storeProduct = getStoreProductEntityFromCSV(record, healthCareService);
                    storeProducts.add(storeProduct);
                } catch (Exception e) {
                    LOG.warn("Failed parsing lineNumber={} reason={}", record.getRecordNumber(), e.getLocalizedMessage());
                    throw new CSVProcessingException("Error at line " + record.getRecordNumber());
                }
            }
            return storeProducts;
        } catch (IOException e) {
            LOG.warn("Error reason={}", e.getLocalizedMessage());
            throw new CSVParsingException("Invalid file");
        }
    }

    /** Read from a CSV file. */
    private MasterLabEntity getStoreProductEntityFromCSV(CSVRecord record, HealthCareServiceEnum healthCareService) {
        MasterLabEntity masterLab = new MasterLabEntity();
        switch (healthCareService) {
            case XRAY:
            case SONO:
            case SCAN:
            case MRI:
            case SPEC:
            case PATH:
                masterLab
                    .setProductName(record.get("Name").trim())
                    .setProductShortName(record.get("Name").trim())
                    .setHealthCareService(healthCareService);
                break;
            case PHYS:
                break;
            default:
                LOG.error("Reached unsupported condition={}", healthCareService);
                throw new UnsupportedOperationException("Reached unsupported condition " + healthCareService);
        }
        if (StringUtils.isNotBlank(record.get("Key")) && Validate.isValidObjectId(record.get("Key"))) {
            masterLab.setId(record.get("Key"));
        } else {
            masterLab.setId(CommonUtil.generateHexFromObjectId());
        }
        return masterLab;
    }
}
