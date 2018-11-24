package com.noqapp.loader.scheduledtasks;


import static com.noqapp.domain.types.catgeory.HealthCareServiceEnum.MRI;
import static com.noqapp.domain.types.catgeory.HealthCareServiceEnum.SCAN;

import com.noqapp.medical.domain.MasterLabEntity;
import com.noqapp.medical.repository.MasterLabManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mostly used one time to update, modify any data.
 * hitender
 * 1/13/18 6:17 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class AnyTask {
    private static final Logger LOG = LoggerFactory.getLogger(AnyTask.class);

    private String oneTimeStatusSwitch;

    private Environment environment;
    private MasterLabManager masterLabManager;

    @Autowired
    public AnyTask(
        @Value("${oneTimeStatusSwitch:ON}")
        String oneTimeStatusSwitch,

        Environment environment,
        MasterLabManager masterLabManager
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.masterLabManager = masterLabManager;
        this.environment = environment;
        LOG.info("AnyTask environment={}", this.environment.getProperty("build.env"));
    }

    /**
     * Runs any requested task underneath.
     * Make sure there are proper locks, limits and or conditions to prevent re-run.
     */
    @SuppressWarnings("all")
    @Scheduled(fixedDelayString = "${loader.MailProcess.sendMail}")
    public void someTask() {
        if ("OFF".equalsIgnoreCase(oneTimeStatusSwitch)) {
            return;
        }

        oneTimeStatusSwitch = "OFF";
        LOG.info("Run someTask in AnyTask");

        /* Write your method after here. Un-comment @Scheduled. */
        masterLabManager.deleteAll();

        List<MasterLabEntity> masterRadiologies = new ArrayList<MasterLabEntity>() {{
            add(new MasterLabEntity().setProductName("Abdomen and Pelvis").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Angio Brain").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Ankle").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Arm").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain & Angiography").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain & Orbit").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain with CP angle").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain with CV junction ").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain with IAM ").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain with venogram").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain – Pitutary P+C").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain + DTI").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Brain + spectro").setHealthCareService(MRI));
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
            add(new MasterLabEntity().setProductName("humerus").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Heel").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Hip").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Knee").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Leg").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("LS Spine with Whole spine screening ").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("MRCP").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("PELVIS").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("PNS").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening of brain").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening of cervical").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening of DL spine").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening of Hip joints").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening of Lumbar Spine").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening of S I Joint").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening of Shoulder Joint").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening of TM Joint").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Screening of Whole Spine").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Shoulder ").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("Spectro brain").setHealthCareService(MRI));
            add(new MasterLabEntity().setProductName("DTI BRAIN").setHealthCareService(MRI));
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
            add(new MasterLabEntity().setProductName("whole body MRI").setHealthCareService(MRI));

            add(new MasterLabEntity().setProductName("3 D Ct Calcaneum").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3 D CT Hip").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3 D CT Ankle ").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3 D C T Elbow").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3 D C T Foot").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3 D C T Knee").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3 D C T Shoulder").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3 D C T Face").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3 D C T Face + contrast").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3 D CT SI Joint").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3D CT Wrist").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3DCT ").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3DCT (One Region)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3DCT D/L Spine").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3DCT L S Spine").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3DCT Pelvis").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("3DCT TM JT").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen & Pelvis PLAIN").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen & Pelvis (Double contrast)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen & Pelvis (Plain)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen & Pelvis 3 Phase (P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen KUB").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Anesthesist").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Angio").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Biopsy").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Brain").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("brain (p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("head + neck").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("head + neck (p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Chest").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("chest(p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("contrast").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Chest & Abdomen & Pelvis").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("HRCT Chest").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("HRCT PNS Ltd").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("HRCT PNS Ltd (p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("HRCT Temportal Bone (p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("HRCT Temportal Bone").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("KUB").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("KUB + contrast").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Neck & Chest (P+C)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Neck & Chest Abdomen & Pelvis").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Pelvis").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Pelvis (p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("PNS - HRCT").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("PNS - HRCT(p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Pulmonary Angio").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Thorax").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Thorax(p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("TM Jt").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("TM Jt(p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Upper Abdomen").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Upper Abdomen(p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("dental mandible").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("dental both").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Angio lower limb").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("dental maxilla").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Angio of abdomen").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("face").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("bronchoscopy with chest ").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("bronchoscopy with chest (p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("brain with pituitary fossa").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("brain with pituitary fossa(p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Aorta Angio").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("Abdomen & pelvis ").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("orbit").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("orbit(p+c)").setHealthCareService(SCAN));
            add(new MasterLabEntity().setProductName("whole body").setHealthCareService(SCAN));
        }};

        for (MasterLabEntity masterRadiology : masterRadiologies) {
            masterLabManager.save(masterRadiology);
        }
    }
}
