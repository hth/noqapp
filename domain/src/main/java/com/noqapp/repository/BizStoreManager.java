package com.noqapp.repository;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.types.ServicePaymentEnum;

import org.springframework.data.geo.Point;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/23/16 4:44 PM
 */
public interface BizStoreManager extends RepositoryManager<BizStoreEntity> {

    BizStoreEntity getById(String id);

    BizStoreEntity noStore();

    /** Search for specific Biz, Address or Phone. Limited to 10. */
    List<BizStoreEntity> findAllWithStartingAddressStartingPhone(String bizAddress, String bizPhone, BizNameEntity bizName);

    List<BizStoreEntity> findAllWithAnyAddressAnyPhone(String bizAddress, String bizPhone, BizNameEntity bizName);

    /** Used for Ajax. Populates BizStoreEntity with just fieldName. */
    List<BizStoreEntity> getAllWithJustSpecificField(String bizPhone, String bizAddress, String bizId, String fieldName);

    /**
     * BizStore sorted on create date and limited to latest records
     *
     * @param bizNameEntity
     * @param limit
     * @return
     * @deprecated replaced by findAllWithStartingAddressStartingPhone
     */
    @Deprecated
    List<BizStoreEntity> findAllAddress(BizNameEntity bizNameEntity, int limit);

    /** Find just one store with matching BizName Id. */
    BizStoreEntity findOne(String bizNameId);

    /** This is mostly being used when data is corrupted, like missing addresses or lat or lng. */
    List<BizStoreEntity> getAll(int skip, int limit);

    /** Gets all the data where the addresses have not been validated using external api. */
    List<BizStoreEntity> getAllWhereNotValidatedUsingExternalAPI(int validationCountTry, int skip, int limit);

    /** Get count of all the stores for business. */
    long getCountOfStore(String bizNameId);

    /** Get all the stores for business. */
    List<BizStoreEntity> getAllBizStores(String bizNameId);

    List<BizStoreEntity> getAllBizStores(String bizNameId, Point point, double maxDistance);

    /** Get not deleted stores with matching address. */
    List<BizStoreEntity> getAllBizStoresMatchingAddress(String bizStoreAddress, String bizNameId);

    BizStoreEntity findByCodeQR(String codeQR);

    boolean isValidCodeQR(String codeQR);

    /** Set next cron run date for moving history from previous day. */
    boolean updateNextRun(String id, String zoneId, Date archiveNextRun);

    boolean updateNextRunAndRatingWithAverageServiceTime(
            String id,
            String zoneId,
            Date archiveNextRun,
            float rating,
            int ratingCount,
            long averageServiceTime);

    List<BizStoreEntity> findAllQueueEndedForTheDay(Date now);

    List<BizStoreEntity> findAllOrderEndedForTheDay(Date now);

    /** Stream all documents. */
    Stream<BizStoreEntity> findAllWithStream();

    void updateBizStoreAvailableTokenCount(int availableTokenCount, String codeQR);

    /** Counts number of times the category has been used. */
    long countCategoryUse(String bizCategoryId, String bizNameId);

    List<BizStoreEntity> getBizStoresByCategory(String bizCategoryId, String bizNameId);

    boolean doesWebLocationExists(String webLocation, String id);

    void unsetScheduledTask(String id);

    void setScheduleTaskId(String codeQR, String scheduleTaskId);

    void activeInActive(String id, boolean active);

    /** Need to store references for deleted store. */
    void deleteSoft(String id);

    BizStoreEntity updateServiceCost(String codeQR, int productPrice, int cancellationPrice, ServicePaymentEnum servicePayment);
}

