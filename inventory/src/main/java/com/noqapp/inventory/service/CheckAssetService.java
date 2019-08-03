package com.noqapp.inventory.service;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.RandomString;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.inventory.domain.CheckAssetEntity;
import com.noqapp.inventory.domain.json.JsonCheckAsset;
import com.noqapp.inventory.domain.json.JsonCheckAssetList;
import com.noqapp.inventory.repository.CheckAssetManager;
import com.noqapp.service.FileService;
import com.noqapp.service.exceptions.CSVParsingException;
import com.noqapp.service.exceptions.CSVProcessingException;
import com.noqapp.service.exceptions.FailedTransactionException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-29 23:34
 */
@Service
public class CheckAssetService {
    private static final Logger LOG = LoggerFactory.getLogger(CheckAssetService.class);

    private CheckAssetManager checkAssetManager;
    private AssetFileService assetFileService;
    private FileService fileService;

    @Autowired
    public CheckAssetService(
        CheckAssetManager checkAssetManager,
        AssetFileService assetFileService,
        FileService fileService
    ) {
        this.checkAssetManager = checkAssetManager;
        this.assetFileService = assetFileService;
        this.fileService = fileService;
    }

    public void save(String bizNameId, String floor, String roomNumber, String assetName) {
        checkAssetManager.save(new CheckAssetEntity()
            .setAssetName(assetName)
            .setRoomNumber(roomNumber)
            .setFloor(floor)
            .setBizNameId(bizNameId)
        );
    }

    public void save(CheckAssetEntity checkAsset) {
        checkAssetManager.save(checkAsset);
    }

    private void insertOrUpdate(CheckAssetEntity checkAsset) {
        checkAssetManager.insertOrUpdate(checkAsset);
    }

    public void remove(String id) {
        CheckAssetEntity checkAsset = checkAssetManager.findById(id);
        checkAssetManager.deleteHard(checkAsset);
    }

    private List<String> findDistinctFloors(String bizNameId) {
        return checkAssetManager.findDistinctFloors(bizNameId);
    }

    private List<String> findDistinctRoomsOnFloor(String bizNameId, String floor) {
        return checkAssetManager.findDistinctRoomsOnFloor(bizNameId, floor);
    }

    private List<CheckAssetEntity> findAssetInRoom(String bizNameId, String floor, String roomNumber) {
        return checkAssetManager.findAssetInRoom(bizNameId, floor, roomNumber);
    }

    @Mobile
    public JsonCheckAssetList findDistinctFloorsAsJson(String bizNameId) {
        JsonCheckAssetList jsonCheckAssetList = new JsonCheckAssetList();
        List<String> floors = findDistinctFloors(bizNameId);
        for (String floor : floors) {
            jsonCheckAssetList.addJsonCheckAsset(new JsonCheckAsset().setFloor(floor));
        }

        return jsonCheckAssetList;
    }

    @Mobile
    public JsonCheckAssetList findDistinctRoomsOnFloorAsJson(String bizNameId, String floor) {
        JsonCheckAssetList jsonCheckAssetList = new JsonCheckAssetList();
        List<String> roomNumbers = findDistinctRoomsOnFloor(bizNameId, floor);
        for (String roomNumber : roomNumbers) {
            jsonCheckAssetList.addJsonCheckAsset(new JsonCheckAsset().setRoomNumber(roomNumber));
        }

        return jsonCheckAssetList;
    }

    @Mobile
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

    private List<CheckAssetEntity> findAllByBizNameId(String bizNameId) {
        return checkAssetManager.findAllByBizNameId(bizNameId);
    }

    public int bulkUpdateAsset(InputStream in, String bizNameId) {
        try {
            List<CheckAssetEntity> checkAssets = assetFileService.processUploadedForMasterProductCSVFile(in, bizNameId);
            if (!checkAssets.isEmpty()) {
                for (CheckAssetEntity checkAsset : checkAssets) {
                    insertOrUpdate(checkAsset);
                }
            }
            in.close();
            return checkAssets.size();
        } catch (CSVParsingException e) {
            LOG.warn("Failed parsing CSV file reason={}", e.getLocalizedMessage());
            throw e;
        } catch (CSVProcessingException e) {
            LOG.warn("Failed processing CSV file reason={}", e.getLocalizedMessage());
            throw e;
        } catch (FailedTransactionException e) {
            LOG.warn("Failed transaction reason={}", e.getLocalizedMessage());
            throw e;
        } catch (IOException e) {
            LOG.error("Error reason={}", e.getLocalizedMessage(), e);
        }

        return 0;
    }

    public FileObject getAssetsInFile(DefaultFileSystemManager manager, String bizNameId) throws IOException {
        File csv = null;

        try {
            List<CheckAssetEntity> storeProducts = findAllByBizNameId(bizNameId);
            csv = FileUtil.createTempFile("asset", "csv");
            csv.deleteOnExit();
            Path pathOfCSV = Paths.get(csv.toURI());
            List<String> strings = new ArrayList<>();
            String header = Arrays.toString(AssetFileService.ASSET_HEADERS).replaceAll("\\[|]|\\s", "");
            strings.add(header);
            for (CheckAssetEntity checkAsset : storeProducts) {
                strings.add(checkAsset.toCommaSeparatedString());
            }
            Files.write(pathOfCSV, strings, StandardCharsets.UTF_8);

            String fileName = "asset" + "_" + RandomString.newInstance(10).nextString() + "_" + DateUtil.dateToString(new Date());
            File tar = new File(FileUtil.getTmpDir(), fileName + ".tar.gz");
            fileService.createTarGZ(pathOfCSV.toFile(), tar, fileName);
            return manager.resolveFile(tar.getAbsolutePath());
        } catch (Exception e) {
            LOG.error("Failed to create asset file, reason={}", e.getLocalizedMessage(), e);
            throw e;
        } finally {
            /* Delete temp file no matter what. */
            if (csv != null) {
                csv.delete();
            }
        }
    }
}
