package com.noqapp.inventory.service;

import com.noqapp.inventory.domain.CheckAssetEntity;
import com.noqapp.inventory.domain.json.JsonCheckAsset;
import com.noqapp.inventory.domain.json.JsonCheckAssetList;
import com.noqapp.inventory.repository.CheckAssetManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-29 23:34
 */
@Service
public class CheckAssetService {

    private CheckAssetManager checkAssetManager;

    @Autowired
    public CheckAssetService(CheckAssetManager checkAssetManager) {
        this.checkAssetManager = checkAssetManager;
    }

    public List<String> findDistinctFloors(String bizNameId) {
        return checkAssetManager.findDistinctFloors(bizNameId);
    }

    public List<String> findDistinctRoomsOnFloor(String bizNameId, String floor) {
        return checkAssetManager.findDistinctRoomsOnFloor(bizNameId, floor);
    }

    public List<CheckAssetEntity> findAssetInRoom(String bizNameId, String floor, String roomNumber) {
        return checkAssetManager.findAssetInRoom(bizNameId, floor, roomNumber);
    }

    public JsonCheckAssetList findDistinctFloorsAsJson(String bizNameId) {
        JsonCheckAssetList jsonCheckAssetList = new JsonCheckAssetList();
        List<String> floors = findDistinctFloors(bizNameId);
        for (String floor : floors) {
            jsonCheckAssetList.addJsonCheckAsset(new JsonCheckAsset().setFloor(floor));
        }

        return jsonCheckAssetList;
    }

    public JsonCheckAssetList findDistinctRoomsOnFloorAsJson(String bizNameId, String floor) {
        JsonCheckAssetList jsonCheckAssetList = new JsonCheckAssetList();
        List<String> roomNumbers = findDistinctRoomsOnFloor(bizNameId, floor);
        for (String roomNumber : roomNumbers) {
            jsonCheckAssetList.addJsonCheckAsset(new JsonCheckAsset().setRoomNumber(roomNumber));
        }

        return jsonCheckAssetList;
    }

    public JsonCheckAssetList findAssetInRoomAsJson(String bizNameId, String floor, String roomNumber) {
        JsonCheckAssetList jsonCheckAssetList = new JsonCheckAssetList();
        List<CheckAssetEntity> checkAssets = findAssetInRoom(bizNameId, floor, roomNumber);
        for (CheckAssetEntity checkAsset : checkAssets) {
            jsonCheckAssetList.addJsonCheckAsset(
                new JsonCheckAsset()
                    .setId(checkAsset.getId())
                    .setFloor(checkAsset.getFloor())
                    .setRoomNumber(checkAsset.getRoomNumber())
                    .setAssetName(checkAsset.getAssetName()));
        }

        return jsonCheckAssetList;
    }
}
