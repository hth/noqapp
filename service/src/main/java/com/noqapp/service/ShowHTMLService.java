package com.noqapp.service;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;
import static com.noqapp.domain.types.BusinessSupportEnum.OD;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.helper.CommonHelper;
import com.noqapp.domain.json.JsonNameDatePair;
import com.noqapp.domain.json.JsonProfessionalProfile;
import com.noqapp.domain.json.JsonReview;
import com.noqapp.domain.types.BusinessSupportEnum;
import com.noqapp.domain.types.WalkInStateEnum;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.UserProfileManager;

import com.google.zxing.WriterException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.jetbrains.annotations.NotNull;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * Generate store HTML page at runtime.
 * User: hitender
 * Date: 1/16/17 9:24 AM
 */
@Service
public class ShowHTMLService {
    private static final Logger LOG = LoggerFactory.getLogger(ShowHTMLService.class);

    private String parentHost;
    private String domain;
    private String https;
    private String awsEndPoint;
    private String awsBucket;

    private BizService bizService;
    private FreemarkerService freemarkerService;
    private TokenQueueService tokenQueueService;
    private CodeQRGeneratorService codeQRGeneratorService;
    private StoreHourService storeHourService;
    private StoreProductService storeProductService;

    private UserProfileManager userProfileManager;
    private QueueManagerJDBC queueManagerJDBC;

    private static String showStoreBlank;
    private static String showBusinessBlank;

    @Autowired
    public ShowHTMLService(
        @Value("${parentHost}")
        String parentHost,

        @Value("${domain}")
        String domain,

        @Value("${https}")
        String https,

        @Value("${aws.s3.endpoint}")
        String awsEndPoint,

        @Value("${aws.s3.bucketName}")
        String awsBucket,

        BizService bizService,
        FreemarkerService freemarkerService,
        TokenQueueService tokenQueueService,
        CodeQRGeneratorService codeQRGeneratorService,
        StoreHourService storeHourService,
        StoreProductService storeProductService,

        UserProfileManager userProfileManager,
        QueueManagerJDBC queueManagerJDBC
    ) {
        this.parentHost = parentHost;
        this.domain = domain;
        this.https = https;
        this.awsEndPoint = awsEndPoint;
        this.awsBucket = awsBucket;

        this.bizService = bizService;
        this.freemarkerService = freemarkerService;
        this.tokenQueueService = tokenQueueService;
        this.codeQRGeneratorService = codeQRGeneratorService;
        this.storeHourService = storeHourService;
        this.storeProductService = storeProductService;

        this.userProfileManager = userProfileManager;
        this.queueManagerJDBC = queueManagerJDBC;

        try {
            Map<String, Object> rootMap = new HashMap<>();
            rootMap.put("parentHost", parentHost);
            rootMap.put("domain", domain);
            rootMap.put("https", https);
            showStoreBlank = freemarkerService.freemarkerToString("html/show-store-blank.ftl", rootMap);
            showBusinessBlank = freemarkerService.freemarkerToString("html/show-business-blank.ftl", rootMap);
        } catch (IOException | TemplateException e) {
            LOG.error("Failed generating html page for BLANK store reason={}", e.getLocalizedMessage(), e);
        }
    }

    public String showStoreByWebLocation(BizStoreEntity bizStore) {
        Map<String, Object> rootMap = new HashMap<>();
        try {
            if (null == bizStore) {
                LOG.warn("No such store found. Showing blank store.");
                return showStoreBlank;
            }

            if (populateStore(rootMap, bizStore)) {
                return freemarkerService.freemarkerToString("html/show-store.ftl", rootMap);
            } else {
                /* This can happen when the business is awaiting approval. */
                LOG.warn("Skipped creating store html bizStore={} bizName={} active={}",
                    bizStore.getId(),
                    bizStore.getBizName().getId(),
                    bizStore.isActive());
            }

            return showStoreBlank;
        } catch (IOException | TemplateException | NullPointerException e) {
            LOG.error("Failed generating html page for store reason={}", e.getLocalizedMessage(), e);
            return showStoreBlank;
        }
    }

    public String showBusinessByCodeQR(String codeQR) {
        if (Validate.isValidObjectId(codeQR)) {
            return showBusinessByWebLocation(bizService.findBizNameByCodeQR(codeQR));
        }
        return showBusinessByWebLocation(null);
    }

    private String showBusinessByWebLocation(BizNameEntity bizName) {
        Map<String, Object> rootMap = new HashMap<>();
        try {
            if (null == bizName) {
                LOG.warn("No such business found. Showing blank business.");
                return showBusinessBlank;
            }

            rootMap.put("parentHost", parentHost);
            rootMap.put("domain", domain);
            rootMap.put("https", https);
            rootMap.put("bizName", bizName.getBusinessName());
            rootMap.put("qrFileName", codeQRGeneratorService.createQRImage(bizName.getCodeQRInALink()));
            return freemarkerService.freemarkerToString("html/show-business.ftl", rootMap);
        } catch (IOException | TemplateException | NullPointerException e) {
            LOG.error("Failed generating html page for store reason={}", e.getLocalizedMessage(), e);
            return showBusinessBlank;
        } catch (WriterException e) {
            LOG.error("Failed creating QR Code on html page for business reason={}", e.getLocalizedMessage(), e);
            return showBusinessBlank;
        }
    }

    public boolean populateStore(Map<String, Object> rootMap, BizStoreEntity bizStore) {
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(bizStore.getCodeQR());

        if (null == tokenQueue) {
            /* This can happen when the business is awaiting approval. */
            LOG.warn("Could not find tokenQueue for codeQR={} active={}", bizStore.getCodeQR(), bizStore.isActive());
            return false;
        }

        bizStore.setStoreHours(storeHourService.findAllStoreHours(bizStore.getId()));
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());

        rootMap.put("parentHost", parentHost);
        rootMap.put("domain", domain);
        rootMap.put("https", https);

        rootMap.put("linkId", bizStore.getCodeQRInALink());
        rootMap.put("bizName", bizStore.getBizName().getBusinessName());
        rootMap.put("businessType", bizStore.getBusinessType().name());
        rootMap.put("businessTypeDescription", bizStore.getBusinessType().getDescription());

        rootMap.put("addressLocality", bizStore.getArea());
        if (StringUtils.isNotBlank(bizStore.getStateShortName())) {
            rootMap.put("addressRegion", bizStore.getStateShortName());
        }
        if (StringUtils.isNotBlank(bizStore.getPostalCode())) {
            rootMap.put("postalCode", bizStore.getPostalCode());
        }
        rootMap.put("addressCountry", bizStore.getCountry());
        rootMap.put("latitude", bizStore.getCoordinate()[1]);
        rootMap.put("longitude", bizStore.getCoordinate()[0]);

        String image = CommonHelper.getBannerImage(bizStore);
        if (StringUtils.isNotBlank(image)) {
            rootMap.put("image", awsEndPoint + awsBucket + "/" + "service/" + image);
        }

        if (StringUtils.isNotBlank(bizStore.getFamousFor())) {
            rootMap.put("famousFor", bizStore.getFamousFor());
        }

        String address;
        switch (bizStore.getBusinessType()) {
            case CD:
            case CDQ:
                address = "-";
                break;
            case RS:
            case RSQ:
            case FT:
            case FTQ:
            case BA:
            case BAQ:
            case ST:
            case STQ:
            case GS:
            case GSQ:
            case CF:
            case CFQ:
                switch (bizStore.getBusinessType().getBusinessSupport()) {
                    case OD:
                    case OQ:
                        List<StoreProductEntity> storeProducts = storeProductService.findAll(bizStore.getId());
                        rootMap.put("storeProducts", storeProducts);
                        break;
                    default:
                        //Do nothing
                }
                address = bizStore.getAddressWrappedFunky();
                break;
            default:
                address = bizStore.getAddressWrappedFunky();
        }
        rootMap.put("storeAddress", address);
        rootMap.put("phone", "N/A");
        rootMap.put("displayName", bizStore.getDisplayName());
        rootMap.put("categoryName", CommonHelper.findCategoryName(bizStore));
        rootMap.put("dayOfWeek", WordUtils.capitalizeFully(zonedDateTime.getDayOfWeek().name()));
        rootMap.put("tokenAvailableFrom", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getTokenAvailableFrom(zonedDateTime.getDayOfWeek())));
        rootMap.put("startHour", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getStartHour(zonedDateTime.getDayOfWeek())));
        rootMap.put("tokenNotAvailableFrom", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getTokenNotAvailableFrom(zonedDateTime.getDayOfWeek())));
        rootMap.put("endHour", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getEndHour(zonedDateTime.getDayOfWeek())));
        rootMap.put("rating", String.valueOf(bizStore.getRatingFormatted()));
        rootMap.put("reviewCount", String.valueOf(bizStore.getReviewCount()));
        rootMap.put("peopleInQueue", String.valueOf(tokenQueue.numberOfPeopleInQueue()));
        rootMap.put("codeQR", bizStore.getCodeQRInBase64());
        rootMap.put("walkIn", bizStore.getWalkInState() == null ? WalkInStateEnum.E.getName() : bizStore.getWalkInState().getName());
        rootMap.put("reviews", getStoreReviews(bizStore));
        switch (bizStore.getBusinessType().getBusinessSupport()) {
            case OD:
                rootMap.put("isOrderPlacingAllowed", true);
                break;
            case OQ:
            case QQ:
                rootMap.put("isOrderPlacingAllowed", false);
                break;
            case MP:
            default:
                LOG.warn("BizStore incorrectly set {} {}", bizStore.getId(), bizStore.getBusinessType().getBusinessSupport());
        }

        int i = zonedDateTime.getDayOfWeek().getValue();
        StoreHourEntity storeHour = bizStore.getStoreHours().get(i - 1);

        rootMap.put("claimed", bizStore.getBizName().isClaimed() ? "Yes" : "No");

        if (storeHour.isDayClosed() || storeHour.isTempDayClosed() || bizStore.getBizName().isDayClosed()) {
            rootMap.put("storeClosed", "Yes");
            rootMap.put("queueStatus", "Closed");
            rootMap.put("currentlyServing", "N/A");
        } else {
            rootMap.put("storeClosed", "No");
            switch (tokenQueue.getQueueStatus()) {
                case S:
                    rootMap.put("queueStatus", "Queue not yet started");
                    rootMap.put("currentlyServing", "0");
                    break;
                case R:
                    rootMap.put("currentlyServing", "Next to serve " + tokenQueue.getLastNumber());
                    computeQueueStatus(rootMap, zonedDateTime, storeHour);
                    break;
                case N:
                    rootMap.put("currentlyServing", "Serving Now " + tokenQueue.generateDisplayServingNow());
                    computeQueueStatus(rootMap, zonedDateTime, storeHour);
                    break;
                case D:
                    rootMap.put("currentlyServing", "Last served token " + tokenQueue.generateDisplayServingNow());
                    computeQueueStatus(rootMap, zonedDateTime, storeHour);
                    break;
                case C:
                    rootMap.put("queueStatus", "Closed Permanently");
                    rootMap.put("currentlyServing", "N/A");
                    break;
                default:
                    LOG.error("Reached unreachable condition {}", tokenQueue.getQueueStatus());
                    throw new UnsupportedOperationException("Reached unreachable condition");
            }
        }
        return true;
    }

    private List<JsonReview> getStoreReviews(BizStoreEntity bizStore) {
        List<JsonReview> jsonReviews = new LinkedList<>();

        List<QueueEntity> queues = queueManagerJDBC.findReviews(bizStore.getCodeQR(), 120);
        if (queues.size() == 0) {
            return jsonReviews;
        }

        for (QueueEntity queue : queues) {
            try {
                if (StringUtils.isNotBlank(queue.getReview())) {
                    UserProfileEntity userProfile = userProfileManager.findByQueueUserId(queue.getQueueUserId());
                    jsonReviews.add(new JsonReview(
                        queue.getId(),
                        queue.getRatingCount(),
                        queue.getReview(),
                        userProfile.getProfileImage(),
                        userProfile.getName(),
                        true,
                        DateUtil.convertDateToStringOf_DTF_MMMM_DD_YYYY(queue.getCreated())
                    ));
                }
            } catch (Exception e) {
                LOG.error("Failed populating review {} {} {} {} {}",
                    queue.getId(),
                    bizStore.getId(),
                    bizStore.getDisplayName(),
                    bizStore.getBizName().getBusinessName(),
                    e.getLocalizedMessage(),
                    e);
            }
        }
        return jsonReviews;
    }

    boolean populateMedicalProfile(
        Map<String, Map<String, Object>> rootMap,
        UserProfileEntity userProfile,
        JsonProfessionalProfile jsonProfessionalProfile,
        List<BizStoreEntity> bizStores
    ) {

        Map<String, Object> page = new HashMap<>();
        page.put("parentHost", parentHost);
        page.put("domain", domain);
        page.put("https", https);
        rootMap.put("page", page);

        Map<String, Object> profile = new HashMap<>();
        profile.put("name", userProfile.getName());
        profile.put("gender", userProfile.getGender().name());
        profile.put("experienceDuration", jsonProfessionalProfile.experienceDuration());
        profile.put("profileImage", StringUtils.isBlank(userProfile.getProfileImage())
            ? "/static/internal/img/profile-image-192x192.png"
            : awsEndPoint + awsBucket + "/profile/" + userProfile.getProfileImage());

        profile.put("awards", jsonProfessionalProfile.getAwards());
        profile.put("education", jsonProfessionalProfile.getEducation().stream()
            .map(JsonNameDatePair::getName)
            .collect(Collectors.joining(", ")));

        Map<String, Object> stores = new HashMap<>();
        for (BizStoreEntity bizStore : bizStores) {
            Map<String, Object> storeData = new HashMap<>();
            populateStore(storeData, bizStore);
            profile.put("categoryName", CommonHelper.findCategoryName(bizStore));
            stores.put(bizStore.getCodeQR(), storeData);
        }
        rootMap.put("stores", stores);
        rootMap.put("profile", profile);
        return true;
    }

    private void computeQueueStatus(Map<String, Object> rootMap, ZonedDateTime zonedDateTime, StoreHourEntity storeHour) {
        /*
         * Hour format is 0-23, example 1 for 12:01 AM and 2359 for 11:59 PM.
         * Hence matches ZonedDateTime Hour and Minutes
         * And, To make sure minute in time 11:06 AM is not represented as 116 but as 1106 hence string formatting.
         */
        int timeIn24HourFormat = CommonUtil.getTimeIn24HourFormat(zonedDateTime);
        if (storeHour.getTokenNotAvailableFrom() > timeIn24HourFormat) {
            LOG.debug("computeQueueStatus getTokenNotAvailableFrom={} > timeIn24HourFormat={}",
                storeHour.getTokenNotAvailableFrom(),
                timeIn24HourFormat);

            rootMap.put("queueStatus", "Open");
        } else if (storeHour.getEndHour() <= timeIn24HourFormat) {
            LOG.debug("computeQueueStatus getEndHour={} <= timeIn24HourFormat={}",
                storeHour.getEndHour(),
                timeIn24HourFormat);

            rootMap.put("queueStatus", "Closed");
        } else if (storeHour.getTokenNotAvailableFrom() <= timeIn24HourFormat && storeHour.getEndHour() > timeIn24HourFormat) {
            LOG.debug("computeQueueStatus getTokenNotAvailableFrom={} < timeIn24HourFormat={} & getEndHour={} > timeIn24HourFormat={}",
                storeHour.getTokenNotAvailableFrom(),
                timeIn24HourFormat,
                storeHour.getEndHour(),
                timeIn24HourFormat);

            rootMap.put("queueStatus", "Closing soon. No more token accepted.");
        } else {
            LOG.error("QueueStatus computed currentZoneTime={} bizStoreId={} storeHour={}",
                timeIn24HourFormat,
                storeHour.getBizStoreId(),
                storeHour);

            throw new UnsupportedOperationException("Reached unreachable condition");
        }
    }
}
