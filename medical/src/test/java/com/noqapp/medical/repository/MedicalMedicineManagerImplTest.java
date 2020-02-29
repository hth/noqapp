package com.noqapp.medical.repository;

import static org.mockito.Mockito.when;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.medical.domain.MedicalMedicineEntity;

import org.springframework.data.mongodb.core.MongoTemplate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 7/3/18 5:03 PM
 */
@DisplayName("Medical Medicine Repo")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("Repository")
class MedicalMedicineManagerImplTest {
    @Mock private MongoTemplate mongoTemplate;
    @Mock private MedicalMedicineEntity medicalMedicine1;
    @Mock private MedicalMedicineEntity medicalMedicine2;

    private MedicalMedicineManager medicalMedicineManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.medicalMedicineManager = new MedicalMedicineManagerImpl(mongoTemplate);

        when(medicalMedicine1.getName()).thenReturn("Medicine1");
        when(medicalMedicine2.getName()).thenReturn("Medicine2");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findByIds() {
        List<String> ids = new ArrayList<String>(){{
            add(CommonUtil.generateHexFromObjectId());
            add(CommonUtil.generateHexFromObjectId());
        }};

        for(String id : ids) {
            MedicalMedicineEntity medicalMedicine = new MedicalMedicineEntity();
            medicalMedicine.setId(id);
            medicalMedicineManager.save(medicalMedicine);
        }

        List<MedicalMedicineEntity> found = medicalMedicineManager.findByIds(ids);
        Assertions.assertEquals(2, found.size());
    }
}