package com.token.repository;

import com.token.domain.BizNameEntity;
import com.token.domain.BizStoreEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 11/23/16 4:44 PM
 */
public interface BizStoreManager extends RepositoryManager<BizStoreEntity> {

    BizStoreEntity getById(String id);

    BizStoreEntity noStore();

    BizStoreEntity findMatchingStore(String address, String phone);

    /**
     * Search for specific Biz, Address or Phone. Limited to 10.
     *
     * @param bizAddress
     * @param bizPhone
     * @param bizNameEntity
     * @return
     */
    List<BizStoreEntity> findAllWithStartingAddressStartingPhone(String bizAddress, String bizPhone, BizNameEntity bizNameEntity);

    List<BizStoreEntity> findAllWithAnyAddressAnyPhone(String bizAddress, String bizPhone, BizNameEntity bizNameEntity);

    /**
     * Used for Ajax. Populates BizStoreEntity with just fieldName.
     *
     * @param bizPhone
     * @param bizAddress
     * @param bizId
     * @param fieldName
     * @return
     */
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


    /**
     * Find just one store with matching BizName Id.
     *
     * @param bizNameId
     * @return
     */
    BizStoreEntity findOne(String bizNameId);


    /**
     * This is mostly being used when data is corrupted, like missing addresses or lat or lng.
     *
     * @return
     */
    List<BizStoreEntity> getAll(int skip, int limit);

    /**
     * Gets all the data where the addresses have not been validated using external api.
     *
     * @return
     */
    List<BizStoreEntity> getAllWhereNotValidatedUsingExternalAPI(int validationCountTry, int skip, int limit);


    /**
     * Get count of all the stores for business.
     *
     * @param bizNameId
     * @return
     */
    long getCountOfStore(String bizNameId);

    /**
     * Get all the stores for business.
     *
     * @param bizNameId
     * @return
     */
    List<BizStoreEntity> getAllBizStores(String bizNameId);

    BizStoreEntity findByCodeQR(String codeQR);

    boolean isValidCodeQR(String codeQR);
}

