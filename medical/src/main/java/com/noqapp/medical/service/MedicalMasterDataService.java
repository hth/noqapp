package com.noqapp.medical.service;

import com.noqapp.medical.domain.PathologyEntity;
import com.noqapp.medical.domain.PharmacyEntity;
import com.noqapp.medical.domain.PhysicalEntity;
import com.noqapp.medical.domain.RadiologyEntity;
import com.noqapp.medical.repository.PathologyManager;
import com.noqapp.medical.repository.PharmacyManager;
import com.noqapp.medical.repository.PhysicalManager;
import com.noqapp.medical.repository.RadiologyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 4/7/18 10:18 PM
 */
@Service
public class MedicalMasterDataService {

    private PathologyManager pathologyManager;
    private PharmacyManager pharmacyManager;
    private PhysicalManager physicalManager;
    private RadiologyManager radiologyManager;

    @Autowired
    public MedicalMasterDataService(
            PathologyManager pathologyManager,
            PharmacyManager pharmacyManager,
            PhysicalManager physicalManager,
            RadiologyManager radiologyManager
    ) {
        this.pathologyManager = pathologyManager;
        this.pharmacyManager = pharmacyManager;
        this.physicalManager = physicalManager;
        this.radiologyManager = radiologyManager;
    }

    public List<PathologyEntity> findAllPathologies() {
        return pathologyManager.findAll();
    }

    public long countPathology() {
        return pathologyManager.totalNumberOfRecords();
    }

    public List<PharmacyEntity> findAllPharmacies() {
        return pharmacyManager.findAll();
    }

    public long countPharmacy() {
        return pharmacyManager.totalNumberOfRecords();
    }

    public List<PhysicalEntity> findAllPhysicals() {
        return physicalManager.findAll();
    }

    public long countPhysical() {
        return physicalManager.totalNumberOfRecords();
    }

    public List<RadiologyEntity> findAllRadiologies() {
        return radiologyManager.findAll();
    }

    public long countRadiology() {
        return radiologyManager.totalNumberOfRecords();
    }

    public void savePathology(PathologyEntity pathology) {
        pathologyManager.save(pathology);
    }

    public void savePharmacy(PharmacyEntity pharmacy) {
        pharmacyManager.save(pharmacy);
    }

    public void savePhysical(PhysicalEntity physical) {
        physicalManager.save(physical);
    }

    public void saveRadiology(RadiologyEntity radiology) {
        radiologyManager.save(radiology);
    }

    public boolean existsPathology(String name) {
        return pathologyManager.existsName(name);
    }

    public boolean existsPharmacy(String name) {
        return pharmacyManager.existsName(name);
    }

    public boolean existsPhysical(String name) {
        return physicalManager.existsName(name);
    }

    public boolean existsRadiology(String name) {
        return radiologyManager.existsName(name);
    }
}
