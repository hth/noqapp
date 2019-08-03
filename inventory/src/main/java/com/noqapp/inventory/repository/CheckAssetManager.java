package com.noqapp.inventory.repository;

import com.noqapp.inventory.domain.CheckAssetEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-29 23:26
 */
public interface CheckAssetManager extends RepositoryManager<CheckAssetEntity> {

    void insertOrUpdate(CheckAssetEntity object);

    List<String> findDistinctFloors(String bizNameId);

    List<String> findDistinctRoomsOnFloor(String bizNameId, String floor);

    List<CheckAssetEntity> findAssetInRoom(String bizNameId, String floor, String room);

    CheckAssetEntity findById(String id);

    List<CheckAssetEntity> findAllByBizNameId(String bizNameId);
}
