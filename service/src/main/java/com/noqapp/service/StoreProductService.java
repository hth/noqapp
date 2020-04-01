package com.noqapp.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.StoreProductManager;
import com.noqapp.service.exceptions.CSVParsingException;
import com.noqapp.service.exceptions.CSVProcessingException;
import com.noqapp.service.transaction.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * hitender
 * 3/21/18 5:26 PM
 */
@Service
public class StoreProductService {
    private static final Logger LOG = LoggerFactory.getLogger(StoreProductService.class);

    private StoreProductManager storeProductManager;
    private BizStoreManager bizStoreManager;
    private FileService fileService;
    private TransactionService transactionService;

    @Autowired
    public StoreProductService(
        StoreProductManager storeProductManager,
        BizStoreManager bizStoreManager,
        FileService fileService,
        TransactionService transactionService
    ) {
        this.storeProductManager = storeProductManager;
        this.bizStoreManager = bizStoreManager;
        this.fileService = fileService;
        this.transactionService = transactionService;
    }

    public List<StoreProductEntity> findAll(String storeId) {
        return storeProductManager.findAll(storeId);
    }

    public long countOfProduct(String storeId) {
        return storeProductManager.countOfProduct(storeId);
    }

    public void save(StoreProductEntity storeProduct) {
        storeProductManager.save(storeProduct);
    }

    public boolean existProductName(String storeId, String productName) {
        return storeProductManager.existProductName(storeId, productName.trim());
    }

    public StoreProductEntity findOne(String id) {
        return storeProductManager.findOne(id);
    }

    public void delete(StoreProductEntity storeProduct) {
        storeProductManager.deleteHard(storeProduct);
    }

    @Mobile
    public void removeById(String id) {
        storeProductManager.removeById(id);
    }

    public int bulkUpdateStoreProduct(InputStream in, String codeQR, String qid) {
        try {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
            List<StoreProductEntity> storeProducts = fileService.processUploadedStoreProductCSVFile(in, bizStore);
            if (!storeProducts.isEmpty()) {
                transactionService.bulkProductUpdate(storeProducts, bizStore.getId(), qid);
            }
            in.close();
            return storeProducts.size();
        } catch (CSVParsingException e) {
            LOG.warn("Failed parsing CSV file codeQR={} reason={}", codeQR, e.getLocalizedMessage());
            throw e;
        } catch (CSVProcessingException e) {
            LOG.warn("Failed processing CSV file codeQR={} reason={}", codeQR, e.getLocalizedMessage());
            throw e;
        } catch (IOException e) {
            LOG.error("Error reason={}", e.getLocalizedMessage(), e);
        }

        return 0;
    }

    public File bulkStoreProductCSVFile(String codeQR) throws IOException {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        List<StoreProductEntity> storeProducts = storeProductManager.findAll(bizStore.getId());
        return fileService.writeStoreProductToCSVFile(storeProducts, bizStore);
    }

    void changeInventoryCount(String productId, int count) {
        storeProductManager.changeInventoryCount(productId, count);
    }
}
