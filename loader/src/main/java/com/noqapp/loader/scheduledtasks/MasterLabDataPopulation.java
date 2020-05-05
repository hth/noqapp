package com.noqapp.loader.scheduledtasks;

import static com.noqapp.domain.types.catgeory.HealthCareServiceEnum.MRI;
import static com.noqapp.domain.types.catgeory.HealthCareServiceEnum.SCAN;

import com.noqapp.medical.domain.MasterLabEntity;
import com.noqapp.medical.repository.MasterLabManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 3/18/20 1:44 PM
 */
public class MasterLabDataPopulation {
    private static final Logger LOG = LoggerFactory.getLogger(MasterLabDataPopulation.class);

    private String oneTimeStatusSwitch;
    private MasterLabManager masterLabManager;

    @Autowired
    public MasterLabDataPopulation(
        @Value("${oneTimeStatusSwitch:ON}")
        String oneTimeStatusSwitch,

        MasterLabManager masterLabManager
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.masterLabManager = masterLabManager;
    }

    /**
     * Runs any requested task underneath.
     * Make sure there are proper locks, limits and or conditions to prevent re-run.
     */
    @SuppressWarnings("all")
    @Scheduled(fixedDelayString = "${loader.MailProcess.sendMail}")
    public void populateMasterLab() {
        if ("OFF".equalsIgnoreCase(oneTimeStatusSwitch)) {
            return;
        }

        oneTimeStatusSwitch = "OFF";
        LOG.info("PopulateMasterLab Started");

        /* Write your method after here. Un-comment @Scheduled. */
        masterLabManager.deleteMatching(MRI);
        masterLabManager.deleteMatching(SCAN);

        List<MasterLabEntity> masterRadiologies = new ArrayList<MasterLabEntity>() {{
            add(new MasterLabEntity().setProductName("Abdomen & Pelvis").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Angio Brain").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Ankle").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Arm").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain & Angiography").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain & Orbit").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain With CP Angle").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain With CV Junction").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain With IAM").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain With Venogram").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain - Pitutary P+C").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain + DTI").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain + Spectro").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Breast Mammography").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Chest").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Elbow").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Elbow - Both").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Face").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Fistulogram").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Foot").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Forearm").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Forearm - Both").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Hand").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Humerus").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Heel").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Hip").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Knee").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Leg").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("LS Spine With Whole Spine Screening").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("MRCP").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("PELVIS").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("PNS").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening Brain").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening Cervical").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening DL Spine").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening Hip Joints").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening Lumbar Spine").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening SI Joint").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening Shoulder Joint").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening TM Joint").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening Whole Spine").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Shoulder").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Spectro Brain").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("DTI Brain").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("SI Joints").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Spine - Cervical").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Spine - Dorsal").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Spine - Lumbar Sacral").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Spine - Cervicodorsal").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Spine - Dorsolumbar").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Sternoclavicular Joint").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("T M Joint").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Thigh").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("VenoBrain").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Wrist").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Whole Body MRI").setHealthCareService(MRI));

            add(new MasterLabEntity().setProductName("3D CT Calcaneum").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Hip").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Ankle ").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Elbow").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Foot").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Knee").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Shoulder").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Face").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Face + Contrast").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT SI Joint").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Wrist").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT (One Region)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT D/L Spine").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT L S Spine").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Pelvis").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT TM JT").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen & Pelvis Plain").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen & Pelvis (Double Contrast)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen & Pelvis (Plain)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen & Pelvis 3 Phase(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen KUB").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Anesthesist").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Angio").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Biopsy").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Brain").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Brain(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Head + Neck").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Head + Neck(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Chest").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Chest(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Contrast").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Chest & Abdomen & Pelvis").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("HRCT Chest").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("HRCT PNS Ltd").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("HRCT PNS Ltd(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("HRCT Temportal Bone(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("HRCT Temportal Bone").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("KUB").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("KUB + Contrast").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Neck & Chest(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Neck & Chest Abdomen & Pelvis").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Pelvis").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Pelvis(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("PNS - HRCT").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("PNS - HRCT(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Pulmonary Angio").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Thorax").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Thorax(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("TM Jt").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("TM Jt(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Upper Abdomen").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Upper Abdomen(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Dental Mandible").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Dental Both").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Angio Lower Limb").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Dental Maxilla").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Angio of Abdomen").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Face").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Bronchoscopy With Chest").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Bronchoscopy With Chest(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Brain With Pituitary Fossa").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Brain With Pituitary Fossa(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Aorta Angio").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen & Pelvis").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Orbit").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Orbit(P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Whole Body").setHealthCareService(SCAN));
        }};

        for (MasterLabEntity masterRadiology : masterRadiologies) {
            masterLabManager.save(masterRadiology);
        }
    }
}
