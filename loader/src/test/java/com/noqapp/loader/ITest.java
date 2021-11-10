package com.noqapp.loader;

import com.noqapp.common.config.FirebaseConfig;
import com.noqapp.common.config.TextToSpeechConfiguration;
import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Constants;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.domain.types.OnOffEnum;
import com.noqapp.domain.types.RoleEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.domain.types.catgeory.CanteenStoreDepartmentEnum;
import com.noqapp.health.repository.ApiHealthNowManager;
import com.noqapp.health.repository.ApiHealthNowManagerImpl;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.loader.scheduledtasks.FileOperationOnS3;
import com.noqapp.loader.service.ComputeNextRunService;
import com.noqapp.loader.service.FlexAppointmentToTokenService;
import com.noqapp.medical.repository.HospitalVisitScheduleManager;
import com.noqapp.medical.repository.HospitalVisitScheduleManagerImpl;
import com.noqapp.medical.repository.MasterLabManager;
import com.noqapp.medical.repository.MasterLabManagerImpl;
import com.noqapp.medical.repository.MedicalMedicationManager;
import com.noqapp.medical.repository.MedicalMedicationManagerImpl;
import com.noqapp.medical.repository.MedicalMedicineManager;
import com.noqapp.medical.repository.MedicalMedicineManagerImpl;
import com.noqapp.medical.repository.MedicalPathologyManager;
import com.noqapp.medical.repository.MedicalPathologyManagerImpl;
import com.noqapp.medical.repository.MedicalPathologyTestManager;
import com.noqapp.medical.repository.MedicalPathologyTestManagerImpl;
import com.noqapp.medical.repository.MedicalPhysicalManager;
import com.noqapp.medical.repository.MedicalPhysicalManagerImpl;
import com.noqapp.medical.repository.MedicalRadiologyManager;
import com.noqapp.medical.repository.MedicalRadiologyManagerImpl;
import com.noqapp.medical.repository.MedicalRadiologyTestManager;
import com.noqapp.medical.repository.MedicalRadiologyTestManagerImpl;
import com.noqapp.medical.repository.MedicalRecordManager;
import com.noqapp.medical.repository.MedicalRecordManagerImpl;
import com.noqapp.medical.repository.UserMedicalProfileHistoryManager;
import com.noqapp.medical.repository.UserMedicalProfileManager;
import com.noqapp.medical.repository.UserMedicalProfileManagerImpl;
import com.noqapp.medical.service.HospitalVisitScheduleService;
import com.noqapp.medical.service.MedicalFileService;
import com.noqapp.medical.service.MedicalRecordService;
import com.noqapp.medical.service.UserMedicalProfileService;
import com.noqapp.medical.transaction.MedicalTransactionService;
import com.noqapp.repository.AdvertisementManager;
import com.noqapp.repository.AdvertisementManagerImpl;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizNameManagerImpl;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BizStoreManagerImpl;
import com.noqapp.repository.BusinessCustomerManager;
import com.noqapp.repository.BusinessCustomerManagerImpl;
import com.noqapp.repository.BusinessCustomerPriorityManager;
import com.noqapp.repository.BusinessCustomerPriorityManagerImpl;
import com.noqapp.repository.BusinessUserManager;
import com.noqapp.repository.BusinessUserManagerImpl;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.BusinessUserStoreManagerImpl;
import com.noqapp.repository.CouponManager;
import com.noqapp.repository.CouponManagerImpl;
import com.noqapp.repository.CustomTextToSpeechManager;
import com.noqapp.repository.EmailValidateManager;
import com.noqapp.repository.EmailValidateManagerImpl;
import com.noqapp.repository.ForgotRecoverManager;
import com.noqapp.repository.ForgotRecoverManagerImpl;
import com.noqapp.repository.GenerateUserIdManager;
import com.noqapp.repository.GenerateUserIdManagerImpl;
import com.noqapp.repository.NotificationMessageManager;
import com.noqapp.repository.NotificationMessageManagerImpl;
import com.noqapp.repository.PointEarnedManager;
import com.noqapp.repository.PointEarnedManagerImpl;
import com.noqapp.repository.PreferredBusinessManager;
import com.noqapp.repository.PreferredBusinessManagerImpl;
import com.noqapp.repository.ProfessionalProfileManager;
import com.noqapp.repository.ProfessionalProfileManagerImpl;
import com.noqapp.repository.PublishArticleManager;
import com.noqapp.repository.PublishArticleManagerImpl;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerImpl;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.repository.PurchaseOrderProductManagerImpl;
import com.noqapp.repository.PurchaseOrderProductManagerJDBC;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerImpl;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.RegisteredDeviceManagerImpl;
import com.noqapp.repository.S3FileManager;
import com.noqapp.repository.S3FileManagerImpl;
import com.noqapp.repository.ScheduleAppointmentManager;
import com.noqapp.repository.ScheduleAppointmentManagerImpl;
import com.noqapp.repository.ScheduledTaskManager;
import com.noqapp.repository.ScheduledTaskManagerImpl;
import com.noqapp.repository.StatsBizStoreDailyManager;
import com.noqapp.repository.StatsBizStoreDailyManagerImpl;
import com.noqapp.repository.StatsCronManager;
import com.noqapp.repository.StatsCronManagerImpl;
import com.noqapp.repository.StoreCategoryManager;
import com.noqapp.repository.StoreCategoryManagerImpl;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.StoreHourManagerImpl;
import com.noqapp.repository.StoreProductManager;
import com.noqapp.repository.StoreProductManagerImpl;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.repository.TokenQueueManagerImpl;
import com.noqapp.repository.UserAccountManager;
import com.noqapp.repository.UserAccountManagerImpl;
import com.noqapp.repository.UserAddressManager;
import com.noqapp.repository.UserAddressManagerImpl;
import com.noqapp.repository.UserAuthenticationManager;
import com.noqapp.repository.UserAuthenticationManagerImpl;
import com.noqapp.repository.UserPreferenceManager;
import com.noqapp.repository.UserPreferenceManagerImpl;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.UserProfileManagerImpl;
import com.noqapp.repository.market.HouseholdItemManager;
import com.noqapp.repository.market.HouseholdItemManagerImpl;
import com.noqapp.repository.market.PropertyRentalManager;
import com.noqapp.repository.market.PropertyRentalManagerImpl;
import com.noqapp.repository.neo4j.NotificationN4jManager;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessCustomerPriorityService;
import com.noqapp.service.BusinessCustomerService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.CouponService;
import com.noqapp.service.CustomTextToSpeechService;
import com.noqapp.service.DeviceService;
import com.noqapp.service.EmailValidateService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.FileService;
import com.noqapp.service.FirebaseMessageService;
import com.noqapp.service.FirebaseService;
import com.noqapp.service.FtpService;
import com.noqapp.service.GenerateUserIdService;
import com.noqapp.service.JMSProducerService;
import com.noqapp.service.JoinAbortService;
import com.noqapp.service.LanguageTranslationService;
import com.noqapp.service.MailService;
import com.noqapp.service.MessageCustomerService;
import com.noqapp.service.NotifyMobileService;
import com.noqapp.service.PreferredBusinessService;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.service.PurchaseOrderProductService;
import com.noqapp.service.PurchaseOrderService;
import com.noqapp.service.QueueService;
import com.noqapp.service.ReviewService;
import com.noqapp.service.ScheduleAppointmentService;
import com.noqapp.service.SmsService;
import com.noqapp.service.StatsCronService;
import com.noqapp.service.StoreCategoryService;
import com.noqapp.service.StoreHourService;
import com.noqapp.service.StoreProductService;
import com.noqapp.service.SubscribeTopicService;
import com.noqapp.service.SystemNotifyOnGettingTokenService;
import com.noqapp.service.TextToSpeechService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.service.UserAddressService;
import com.noqapp.service.UserProfilePreferenceService;
import com.noqapp.service.graph.GraphDetailOfPerson;
import com.noqapp.service.nlp.NLPService;
import com.noqapp.service.payment.CashfreeService;
import com.noqapp.service.transaction.TransactionService;

import org.bson.types.ObjectId;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import okhttp3.OkHttpClient;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

/**
 * hitender
 * 11/18/18 1:55 PM
 */
public class ITest extends RealMongoForITest {

    protected String did;
    protected String didClient1;
    protected String didClient2;
    protected String didQueueSupervisor;
    protected String fcmToken;
    protected String model;
    protected String osVersion;
    protected String appVersion;
    protected String deviceType;

    /** Additional */
    protected MedicalTransactionService medicalTransactionService;
    protected StatsCronService statsCronService;
    protected ComputeNextRunService computeNextRunService;
    protected NotifyMobileService notifyMobileService;

    protected StatsCronManager statsCronManager;
    protected MasterLabManager masterLabManager;
    /** Additional End. */

    protected AccountService accountService;
    protected UserProfilePreferenceService userProfilePreferenceService;
    protected TokenQueueService tokenQueueService;
    protected BizService bizService;
    protected QueueService queueService;
    protected BusinessUserStoreService businessUserStoreService;
    protected ProfessionalProfileService professionalProfileService;
    protected ScheduleAppointmentService scheduleAppointmentService;

    protected UserAddressManager userAddressManager;
    protected UserAddressService userAddressService;
    protected StoreProductManager storeProductManager;
    protected StoreProductService storeProductService;
    protected PurchaseOrderManager purchaseOrderManager;
    protected PurchaseOrderProductManager purchaseOrderProductManager;
    protected PurchaseOrderService purchaseOrderService;
    protected PurchaseOrderProductService purchaseOrderProductService;
    protected FileService fileService;
    protected S3FileManager s3FileManager;
    protected ReviewService reviewService;
    protected CouponService couponService;
    protected JoinAbortService joinAbortService;
    protected DeviceService deviceService;
    protected TextToSpeechService textToSpeechService;
    protected CustomTextToSpeechService customTextToSpeechService;
    protected StoreHourService storeHourService;
    protected MessageCustomerService messageCustomerService;

    protected MedicalRecordManager medicalRecordManager;
    protected MedicalPhysicalManager medicalPhysicalManager;
    protected MedicalMedicationManager medicalMedicationManager;
    protected MedicalMedicineManager medicalMedicineManager;
    protected MedicalPathologyManager medicalPathologyManager;
    protected MedicalPathologyTestManager medicalPathologyTestManager;
    protected MedicalRadiologyManager medicalRadiologyManager;
    protected MedicalRadiologyTestManager medicalRadiologyTestManager;
    protected HospitalVisitScheduleManager hospitalVisitScheduleManager;
    protected MedicalRecordService medicalRecordService;
    protected MedicalFileService medicalFileService;

    protected TokenQueueManager tokenQueueManager;
    protected FirebaseMessageService firebaseMessageService;
    protected FirebaseService firebaseService;
    protected QueueManager queueManager;

    protected UserAccountManager userAccountManager;
    protected UserAuthenticationManager userAuthenticationManager;
    protected UserPreferenceManager userPreferenceManager;
    protected UserProfileManager userProfileManager;
    protected PointEarnedManager pointEarnedManager;
    protected GenerateUserIdService generateUserIdService;
    protected EmailValidateManager emailValidateManager;
    protected EmailValidateService emailValidateService;
    protected ForgotRecoverManager forgotRecoverManager;
    protected RegisteredDeviceManager registeredDeviceManager;
    protected BizNameManager bizNameManager;
    protected BusinessCustomerPriorityManager businessCustomerPriorityManager;
    protected BizStoreManager bizStoreManager;
    protected StoreHourManager storeHourManager;
    protected BusinessUserStoreManager businessUserStoreManager;
    protected BusinessUserService businessUserService;
    protected BusinessUserManager businessUserManager;
    protected ProfessionalProfileManager professionalProfileManager;
    protected UserMedicalProfileManager userMedicalProfileManager;
    protected UserMedicalProfileHistoryManager userMedicalProfileHistoryManager;
    protected StoreCategoryManager storeCategoryManager;
    protected PreferredBusinessManager preferredBusinessManager;
    protected PreferredBusinessService preferredBusinessService;
    protected ScheduledTaskManager scheduledTaskManager;
    protected PublishArticleManager publishArticleManager;
    protected AdvertisementManager advertisementManager;
    protected PropertyRentalManager propertyRentalManager;
    protected HouseholdItemManager householdItemManager;
    protected ScheduleAppointmentManager scheduleAppointmentManager;
    protected CouponManager couponManager;
    protected CustomTextToSpeechManager customTextToSpeechManager;
    protected NotificationMessageManager notificationMessageManager;

    protected BusinessCustomerManager businessCustomerManager;
    protected BusinessCustomerService businessCustomerService;
    protected UserMedicalProfileService userMedicalProfileService;
    protected StoreCategoryService storeCategoryService;
    protected TransactionService transactionService;
    protected HospitalVisitScheduleService hospitalVisitScheduleService;
    protected NLPService nlpService;
    protected BusinessCustomerPriorityService businessCustomerPriorityService;
    protected FlexAppointmentToTokenService flexAppointmentToTokenService;

    protected ApiHealthService apiHealthService;
    protected ApiHealthNowManager apiHealthNowManager;
    protected StatsBizStoreDailyManager statsBizStoreDailyManager;

    protected GenerateUserIdManager generateUserIdManager;

    private StanfordCoreNLP stanfordCoreNLP;
    private MaxentTagger maxentTagger;
    protected ExternalService externalService;
    @Mock protected QueueManagerJDBC queueManagerJDBC;
    @Mock protected PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    @Mock protected PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC;
    @Mock protected FtpService ftpService;
    @Mock protected MailService mailService;
    @Mock protected OkHttpClient okHttpClient;
    @Mock protected CashfreeService cashfreeService;
    @Mock protected FirebaseConfig firebaseConfig;
    @Mock protected TextToSpeechConfiguration textToSpeechConfiguration;
    @Mock protected SmsService smsService;
    @Mock protected LanguageTranslationService languageTranslationService;
    @Mock protected GraphDetailOfPerson graphDetailOfPerson;
    @Mock protected NotificationN4jManager notificationN4jManager;
    @Mock protected StringRedisTemplate stringRedisTemplate;
    @Mock protected JMSProducerService jmsProducerService;
    @Mock protected SubscribeTopicService subscribeTopicService;
    @Mock protected SystemNotifyOnGettingTokenService systemNotifyOnGettingTokenService;
    @Mock protected FileOperationOnS3 fileOperationOnS3;

    private MockEnvironment mockEnvironment;

    @BeforeAll
    public void globalISetup() {
        MockitoAnnotations.openMocks(this);

        did = UUID.randomUUID().toString();
        didClient1 = UUID.randomUUID().toString();
        didClient2 = UUID.randomUUID().toString();
        didQueueSupervisor = UUID.randomUUID().toString();
        mockEnvironment = new MockEnvironment();
        mockEnvironment.setProperty("build.env", "sandbox");

        Properties nlpProperties = new Properties();
        nlpProperties.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        stanfordCoreNLP = new StanfordCoreNLP(nlpProperties);

        fcmToken = UUID.randomUUID().toString();
        deviceType = DeviceTypeEnum.A.getName();
        model = "Model";
        osVersion = "OS-Version";
        appVersion = "1.3.150";

        userAccountManager = new UserAccountManagerImpl(getMongoTemplate());
        userAuthenticationManager = new UserAuthenticationManagerImpl(getMongoTemplate());
        userPreferenceManager = new UserPreferenceManagerImpl(getMongoTemplate());
        userProfileManager = new UserProfileManagerImpl(getMongoTemplate());
        pointEarnedManager = new PointEarnedManagerImpl(getMongoTemplate());
        generateUserIdManager = new GenerateUserIdManagerImpl(getMongoTemplate());
        emailValidateManager = new EmailValidateManagerImpl(getMongoTemplate());
        forgotRecoverManager = new ForgotRecoverManagerImpl(getMongoTemplate());
        registeredDeviceManager = new RegisteredDeviceManagerImpl(getMongoTemplate());
        userMedicalProfileManager = new UserMedicalProfileManagerImpl(getMongoTemplate());
        purchaseOrderManager = new PurchaseOrderManagerImpl(getMongoTemplate());
        purchaseOrderProductManager = new PurchaseOrderProductManagerImpl(getMongoTemplate());
        storeProductManager = new StoreProductManagerImpl(getMongoTemplate());
        scheduledTaskManager = new ScheduledTaskManagerImpl(getMongoTemplate());
        bizNameManager = new BizNameManagerImpl(getMongoTemplate());
        businessCustomerPriorityManager = new BusinessCustomerPriorityManagerImpl(getMongoTemplate());
        businessUserStoreManager = new BusinessUserStoreManagerImpl(getMongoTemplate());
        businessUserManager = new BusinessUserManagerImpl(getMongoTemplate());
        queueManager = new QueueManagerImpl(getMongoTemplate());
        bizStoreManager = new BizStoreManagerImpl(getMongoTemplate());
        statsBizStoreDailyManager = new StatsBizStoreDailyManagerImpl(getMongoTemplate());
        scheduleAppointmentManager = new ScheduleAppointmentManagerImpl(getMongoTemplate());
        couponManager = new CouponManagerImpl(getMongoTemplate());
        apiHealthNowManager = new ApiHealthNowManagerImpl(getMongoTemplate());
        userAddressManager = new UserAddressManagerImpl(5, getMongoTemplate());

        generateUserIdService = new GenerateUserIdService(generateUserIdManager);
        emailValidateService = new EmailValidateService(emailValidateManager);
        userMedicalProfileService = new UserMedicalProfileService(userMedicalProfileManager, userMedicalProfileHistoryManager);
        nlpService = new NLPService(stanfordCoreNLP, maxentTagger);
        businessCustomerPriorityService = new BusinessCustomerPriorityService(businessCustomerPriorityManager, bizNameManager);
        userAddressService = new UserAddressService(5, userAddressManager);

        accountService = new AccountService(
            userAccountManager,
            userAuthenticationManager,
            userPreferenceManager,
            userProfileManager,
            pointEarnedManager,
            generateUserIdService,
            emailValidateService,
            forgotRecoverManager,
            userAddressService,
            stringRedisTemplate
        );

        reviewService = new ReviewService(
            180,
            queueManager,
            queueManagerJDBC,
            purchaseOrderManager,
            purchaseOrderManagerJDBC,
            userProfileManager
        );

        professionalProfileManager = new ProfessionalProfileManagerImpl(getMongoTemplate());
        professionalProfileService = new ProfessionalProfileService(
            reviewService,
            professionalProfileManager,
            userProfileManager,
            businessUserStoreManager,
            bizStoreManager);

        externalService = new ExternalService("AIzaSyDUM3yIIrwrx3ciwZ57O9YamC4uISWAlAk", 0, bizStoreManager);
        userProfilePreferenceService = new UserProfilePreferenceService(
            userProfileManager,
            userPreferenceManager,
            userAddressManager
        );

        deviceService = new DeviceService(registeredDeviceManager, userProfileManager);
        apiHealthService = new ApiHealthService(apiHealthNowManager);
        tokenQueueManager = new TokenQueueManagerImpl(getMongoTemplate());
        storeHourManager = new StoreHourManagerImpl(getMongoTemplate());
        businessCustomerManager = new BusinessCustomerManagerImpl(getMongoTemplate());
        s3FileManager = new S3FileManagerImpl(getMongoTemplate());
        firebaseMessageService = new FirebaseMessageService("", okHttpClient);
        firebaseService = new FirebaseService(firebaseConfig, userProfileManager);

        couponService = new CouponService(couponManager, bizStoreManager, userProfileManager);
        purchaseOrderProductService = new PurchaseOrderProductService(couponService, purchaseOrderProductManager, purchaseOrderProductManagerJDBC, userAddressService);

        customTextToSpeechService = new CustomTextToSpeechService(customTextToSpeechManager);
        textToSpeechService = new TextToSpeechService(
            queueManager,
            bizStoreManager,
            textToSpeechConfiguration,
            customTextToSpeechService
        );

        notificationMessageManager = new NotificationMessageManagerImpl(getMongoTemplate());
        messageCustomerService = new MessageCustomerService(
            1,
            notificationMessageManager,
            registeredDeviceManager,
            bizStoreManager,
            bizNameManager,
            queueManagerJDBC,
            tokenQueueManager,
            userProfileManager,
            firebaseService,
            firebaseMessageService,
            languageTranslationService,
            graphDetailOfPerson,
            notificationN4jManager
        );

        businessCustomerService = new BusinessCustomerService(
            businessCustomerManager,
            userProfileManager,
            queueManager,
            messageCustomerService
        );

        tokenQueueService = new TokenQueueService(
            1,
            tokenQueueManager,
            firebaseMessageService,
            queueManager,
            accountService,
            registeredDeviceManager,
            queueManagerJDBC,
            storeHourManager,
            bizStoreManager,
            businessCustomerService,
            textToSpeechService,
            messageCustomerService,
            jmsProducerService,
            subscribeTopicService,
            apiHealthService
        );

        transactionService = new TransactionService(
            2,
            getMongoTemplate(),
            transactionManager(),
            purchaseOrderManager,
            purchaseOrderProductManager,
            storeProductManager,
            cashfreeService,
            mongoHosts()
        );

        storeHourService = new StoreHourService(storeHourManager);
        queueService = new QueueService(
            userProfileManager,
            bizStoreManager,
            queueManager,
            queueManagerJDBC,
            businessUserStoreManager,
            statsBizStoreDailyManager,
            purchaseOrderManager,
            purchaseOrderManagerJDBC,
            businessCustomerService,
            tokenQueueService,
            purchaseOrderProductService,
            storeHourService,
            couponService
        );

        bizService = new BizService(
            69.172,
            111.321,
            bizNameManager,
            bizStoreManager,
            storeHourManager,
            tokenQueueService,
            queueService,
            businessUserManager,
            businessUserStoreManager,
            mailService,
            userProfileManager,
            userPreferenceManager,
            scheduledTaskManager,
            storeHourService
        );

        storeCategoryManager = new StoreCategoryManagerImpl(getMongoTemplate());
        storeCategoryService = new StoreCategoryService(storeCategoryManager, storeProductManager);
        publishArticleManager = new PublishArticleManagerImpl(getMongoTemplate());
        advertisementManager = new AdvertisementManagerImpl(getMongoTemplate());
        propertyRentalManager = new PropertyRentalManagerImpl(getMongoTemplate());
        householdItemManager = new HouseholdItemManagerImpl(getMongoTemplate());

        fileService = new FileService(
            192, 192, 300, 150,
            accountService,
            ftpService,
            s3FileManager,
            bizNameManager,
            bizStoreManager,
            storeProductManager,
            publishArticleManager,
            advertisementManager,
            propertyRentalManager,
            householdItemManager,
            bizService,
            storeCategoryService,
            mailService
        );

        storeProductService = new StoreProductService(storeProductManager, bizStoreManager, fileService, transactionService);
        purchaseOrderService = new PurchaseOrderService(
            5,
            bizStoreManager,
            businessUserManager,
            storeHourManager,
            purchaseOrderManager,
            purchaseOrderManagerJDBC,
            purchaseOrderProductManager,
            purchaseOrderProductManagerJDBC,
            queueManager,
            queueManagerJDBC,
            pointEarnedManager,
            couponService,
            userAddressService,
            firebaseMessageService,
            registeredDeviceManager,
            tokenQueueManager,
            storeProductService,
            tokenQueueService,
            accountService,
            transactionService,
            nlpService,
            mailService,
            cashfreeService,
            purchaseOrderProductService,
            subscribeTopicService
        );

        joinAbortService = new JoinAbortService(
            2,
            deviceService,
            tokenQueueService,
            purchaseOrderService,
            queueManager,
            purchaseOrderProductService,
            bizService,
            businessCustomerService,
            firebaseMessageService,
            storeHourService);

        scheduleAppointmentService = new ScheduleAppointmentService(
            60,
            2,
            24,
            "no-reply@noqapp.com",
            "NoQueue",
            scheduleAppointmentManager,
            storeHourManager,
            userProfileManager,
            userAccountManager,
            userPreferenceManager,
            registeredDeviceManager,
            tokenQueueManager,
            scheduledTaskManager,
            bizService,
            firebaseMessageService,
            mailService,
            storeHourService
        );

        hospitalVisitScheduleManager = new HospitalVisitScheduleManagerImpl(getMongoTemplate());
        hospitalVisitScheduleService = new HospitalVisitScheduleService(hospitalVisitScheduleManager, userProfileManager);
        preferredBusinessManager = new PreferredBusinessManagerImpl(getMongoTemplate());
        preferredBusinessService = new PreferredBusinessService(preferredBusinessManager, bizStoreManager);

        businessUserService = new BusinessUserService(businessUserManager);
        businessUserStoreService = new BusinessUserStoreService(
            10,
            businessUserStoreManager,
            preferredBusinessService,
            businessUserService,
            tokenQueueService,
            accountService,
            bizService,
            professionalProfileService,
            storeHourService,
            userAddressService
        );

        medicalRecordManager = new MedicalRecordManagerImpl(getMongoTemplate());
        medicalPhysicalManager = new MedicalPhysicalManagerImpl(getMongoTemplate());
        medicalMedicationManager = new MedicalMedicationManagerImpl(getMongoTemplate());
        medicalMedicineManager = new MedicalMedicineManagerImpl(getMongoTemplate());
        medicalPathologyManager = new MedicalPathologyManagerImpl(getMongoTemplate());
        medicalPathologyTestManager = new MedicalPathologyTestManagerImpl(getMongoTemplate());
        medicalRadiologyManager = new MedicalRadiologyManagerImpl(getMongoTemplate());
        medicalRadiologyTestManager = new MedicalRadiologyTestManagerImpl(getMongoTemplate());
        medicalRecordService = new MedicalRecordService(
            10,
            medicalRecordManager,
            medicalPhysicalManager,
            medicalMedicationManager,
            medicalMedicineManager,
            medicalPathologyManager,
            medicalPathologyTestManager,
            medicalRadiologyManager,
            medicalRadiologyTestManager,
            userProfileManager,
            bizStoreManager,
            queueManager,
            registeredDeviceManager,
            businessUserStoreService,
            purchaseOrderService,
            userMedicalProfileService,
            userAddressManager
        );

        medicalFileService = new MedicalFileService(
            medicalRecordManager,
            medicalPathologyManager,
            medicalRadiologyManager,
            s3FileManager,
            fileService,
            ftpService
        );

        masterLabManager = new MasterLabManagerImpl(getMongoTemplate());
        medicalTransactionService = new MedicalTransactionService(
            getMongoTemplate(),
            transactionManager(),
            masterLabManager,
            mongoHosts()
        );

        statsCronManager = new StatsCronManagerImpl(getMongoTemplate());
        statsCronService = new StatsCronService(statsCronManager);
        computeNextRunService = new ComputeNextRunService(scheduledTaskManager, bizService);
        deviceService = new DeviceService(registeredDeviceManager, userProfileManager);
        notifyMobileService = new NotifyMobileService(
            purchaseOrderService,
            firebaseMessageService,
            firebaseService,
            tokenQueueManager,
            queueService
        );

        flexAppointmentToTokenService = new FlexAppointmentToTokenService(
            systemNotifyOnGettingTokenService,
            tokenQueueService,
            scheduleAppointmentManager,
            bizStoreManager,
            registeredDeviceManager
        );

        registerUser();
        addSecurityContextHolder("9118000001100");
        createBusinessCSD("9118000001100");
    }

    private void registerUser() {
        /* System Users. Like Admin, Supervisor. */
        addSystemUsers();

        /* Clients. */
        addClients();
        addStoreUsersToCSD();
    }

    private void addClients() {
        accountService.createNewAccount(
            "+9118000000001",
            "ROCKET",
            "Docket",
            "rocketd@r.com",
            "2000-12-12",
            GenderEnum.M,
            "IN",
            "Asia/Calcutta",
            "password",
            "",
            true,
            false
        );

        accountService.createNewAccount(
            "+9118000000002",
            "Pintoa D",
            "mAni",
            "pintod@r.com",
            "2000-12-12",
            GenderEnum.M,
            "IN",
            "Asia/Calcutta",
            "password",
            "",
            true,
            false
        );

        accountService.createNewAccount(
            "+9118000001111",
            "Damuscus",
            null,
            "damuscus@r.com",
            "2000-12-12",
            GenderEnum.M,
            "IN",
            "Asia/Calcutta",
            "password",
            "",
            true,
            false
        );

        accountService.createNewAccount(
            "+9118000001112",
            "Sita",
            null,
            "sita@r.com",
            "2000-12-12",
            GenderEnum.F,
            "IN",
            "Asia/Calcutta",
            "password",
            "",
            true,
            false
        );
    }

    private void addSystemUsers() {
        accountService.createNewAccount(
            "+9118000000101",
            "Admin",
            "Admin",
            "admin@r.com",
            "2000-12-12",
            GenderEnum.M,
            "IN",
            "Asia/Calcutta",
            "password",
            "",
            true,
            false
        );

        UserProfileEntity adminUserProfile = accountService.checkUserExistsByPhone("9118000000101");
        adminUserProfile.setLevel(UserLevelEnum.ADMIN);
        accountService.save(adminUserProfile);
        UserAccountEntity adminUserAccount = accountService.changeAccountRolesToMatchUserLevel(
            adminUserProfile.getQueueUserId(),
            adminUserProfile.getLevel());
        accountService.save(adminUserAccount);

        accountService.createNewAccount(
            "+9118000000102",
            "Supervisor",
            "Supervisor",
            "super@r.com",
            "2000-12-12",
            GenderEnum.M,
            "IN",
            "Asia/Calcutta",
            "password",
            "",
            true,
            false
        );

        UserProfileEntity supervisorUserProfile = accountService.checkUserExistsByPhone("9118000000102");
        supervisorUserProfile.setLevel(UserLevelEnum.SUPERVISOR);
        accountService.save(supervisorUserProfile);
        UserAccountEntity supervisorUserAccount = accountService.changeAccountRolesToMatchUserLevel(
            supervisorUserProfile.getQueueUserId(),
            supervisorUserProfile.getLevel());
        accountService.save(supervisorUserAccount);
    }

    private void addStoreUsersToCSD() {
        accountService.createNewAccount(
            "+9118000001100",
            "CSD",
            "Gurugram",
            "csd_business@r.com",
            "2000-12-12",
            GenderEnum.M,
            "IN",
            "Asia/Calcutta",
            "password",
            "",
            true,
            false
        );

        UserProfileEntity merchantUserProfile = accountService.checkUserExistsByPhone("9118000001100");
        merchantUserProfile.setLevel(UserLevelEnum.M_ADMIN);
        accountService.save(merchantUserProfile);
        UserAccountEntity merchantUserAccount = accountService.changeAccountRolesToMatchUserLevel(
            merchantUserProfile.getQueueUserId(),
            merchantUserProfile.getLevel());
        accountService.save(merchantUserAccount);

        accountService.createNewAccount(
            "+9118000001101",
            "CSD",
            "Gurugram",
            "csd_store_supervisor@r.com",
            "2000-12-12",
            GenderEnum.M,
            "IN",
            "Asia/Calcutta",
            "password",
            "",
            true,
            false
        );

        UserProfileEntity queueSupervisorUserProfile = accountService.checkUserExistsByPhone("9118000001101");
        queueSupervisorUserProfile.setLevel(UserLevelEnum.Q_SUPERVISOR);
        accountService.save(queueSupervisorUserProfile);
        UserAccountEntity queueSupervisorUserAccount = accountService.changeAccountRolesToMatchUserLevel(
            queueSupervisorUserProfile.getQueueUserId(),
            queueSupervisorUserProfile.getLevel());
        accountService.save(queueSupervisorUserAccount);

        accountService.createNewAccount(
            "+9118000001102",
            "Manager of CSD",
            "Gurugram",
            "manager_csd@r.com",
            "2000-12-12",
            GenderEnum.F,
            "IN",
            "Asia/Calcutta",
            "password",
            "",
            true,
            false
        );

        UserProfileEntity storeManagerUserProfile = accountService.checkUserExistsByPhone("9118000001102");
        storeManagerUserProfile.setLevel(UserLevelEnum.S_MANAGER);
        accountService.save(storeManagerUserProfile);
        UserAccountEntity storeManagerUserAccount = accountService.changeAccountRolesToMatchUserLevel(
            storeManagerUserProfile.getQueueUserId(),
            storeManagerUserProfile.getLevel());
        accountService.save(storeManagerUserAccount);
    }

    private void createBusinessCSD(String phone) {
        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(phone);

        BizNameEntity bizName = BizNameEntity.newInstance(CommonUtil.generateCodeQR(Objects.requireNonNull(mockEnvironment.getProperty("build.env"))))
            .setBusinessName("CSD Business")
            .setBusinessType(BusinessTypeEnum.CDQ)
            .setPhone("9118000000041")
            .setPhoneRaw("18000000041")
            .setAddress("Shop No 10 Plot No 102 Sector 29, Vashi, Navi Mumbai, Maharashtra 400703")
            .setTown("Vashi")
            .setStateShortName("MH")
            .setTimeZone("Asia/Calcutta")
            .setInviteeCode(userProfile.getInviteCode())
            .setAddressOrigin(AddressOriginEnum.G)
            .setCountryShortName("IN")
            .setCoordinate(new double[]{71.022498, 18.0244723})
            .setPriorityAccess(OnOffEnum.O);
        String webLocation = bizService.buildWebLocationForBiz(
            bizName.getTown(),
            bizName.getStateShortName(),
            bizName.getCountryShortName(),
            bizName.getBusinessName(),
            bizName.getId());

        bizName.setWebLocation(webLocation);
        bizName.setCodeQR(CommonUtil.generateCodeQR(Objects.requireNonNull(mockEnvironment.getProperty("build.env"))));
        bizService.saveName(bizName);

        BizStoreEntity bizStore = BizStoreEntity.newInstance()
            .setBizName(bizName)
            .setDisplayName("CSD Grocery for Ex-Servicemen")
            .setBusinessType(bizName.getBusinessType())
            .setBizCategoryId(CanteenStoreDepartmentEnum.EG.getName())
            .setPhone("9118000000042")
            .setPhoneRaw("18000000042")
            .setAddress("Shop No 10 Plot No 102 Sector 29, Vashi, Navi Mumbai, Maharashtra 400703")
            .setTimeZone("Asia/Calcutta")
            .setCodeQR(ObjectId.get().toString())
            .setAddressOrigin(AddressOriginEnum.G)
            .setRemoteJoin(true)
            .setAllowLoggedInUser(false)
            .setAvailableTokenCount(0)
            .setAverageServiceTime(50000)
            .setCountryShortName("IN")
            .setCoordinate(new double[]{73.022498, 19.0244723});
        webLocation = bizService.buildWebLocationForBiz(
            bizStore.getTown(),
            bizStore.getStateShortName(),
            bizStore.getCountryShortName(),
            bizStore.getDisplayName(),
            bizStore.getId());
        bizStore.setWebLocation(webLocation);
        bizStore.setCodeQR(CommonUtil.generateCodeQR(Objects.requireNonNull(mockEnvironment.getProperty("build.env"))));
        bizService.saveStore(bizStore, "Created New Store");

        List<StoreHourEntity> storeHours = new LinkedList<>();
        for (int i = 1; i <= 7; i++) {
            StoreHourEntity storeHour = new StoreHourEntity(bizStore.getId(), DayOfWeek.of(i).getValue());
            storeHour.setStartHour(1)
                .setTokenAvailableFrom(1)
                .setTokenNotAvailableFrom(2359)
                .setEndHour(2359);

            storeHours.add(storeHour);
        }

        /* Add store hours. */
        bizService.insertAll(storeHours);

        /* Create Queue. */
        tokenQueueService.createUpdate(bizStore, Constants.appendPrefix);

        /* Add Queue Admin, Queue Supervisor, Queue Manager to Business and Store. */
        BusinessUserEntity businessUser = BusinessUserEntity.newInstance(
            userProfile.getQueueUserId(),
            UserLevelEnum.M_ADMIN
        );
        businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.V)
            .setValidateByQid(accountService.checkUserExistsByPhone("9118000001100").getQueueUserId())
            .setBizName(bizName);
        businessUserService.save(businessUser);

        UserProfileEntity queueSupervisorUserProfile = accountService.checkUserExistsByPhone("9118000001101");
        BusinessUserStoreEntity businessUserStore = new BusinessUserStoreEntity(
            queueSupervisorUserProfile.getQueueUserId(),
            bizStore.getId(),
            bizName.getId(),
            bizStore.getCodeQR(),
            queueSupervisorUserProfile.getLevel());
        businessUserStoreService.save(businessUserStore);

        UserProfileEntity queueManagerUserProfile = accountService.checkUserExistsByPhone("9118000001102");
        businessUserStore = new BusinessUserStoreEntity(
            queueManagerUserProfile.getQueueUserId(),
            bizStore.getId(),
            bizName.getId(),
            bizStore.getCodeQR(),
            queueManagerUserProfile.getLevel());
        businessUserStoreService.save(businessUserStore);

        bizStore = BizStoreEntity.newInstance()
            .setBizName(bizName)
            .setDisplayName("CSD Liquor for Ex-Servicemen")
            .setBusinessType(bizName.getBusinessType())
            .setBizCategoryId(CanteenStoreDepartmentEnum.EL.getName())
            .setPhone("9118000000042")
            .setPhoneRaw("18000000042")
            .setAddress("Shop No 10 Plot No 102 Sector 29, Vashi, Navi Mumbai, Maharashtra 400703")
            .setTimeZone("Asia/Calcutta")
            .setCodeQR(ObjectId.get().toString())
            .setAddressOrigin(AddressOriginEnum.G)
            .setRemoteJoin(true)
            .setAllowLoggedInUser(false)
            .setAvailableTokenCount(0)
            .setAverageServiceTime(50000)
            .setCountryShortName("IN")
            .setCoordinate(new double[]{73.022498, 19.0244723});
        webLocation = bizService.buildWebLocationForBiz(
            bizStore.getTown(),
            bizStore.getStateShortName(),
            bizStore.getCountryShortName(),
            bizStore.getDisplayName(),
            bizStore.getId());
        bizStore.setWebLocation(webLocation);
        bizStore.setCodeQR(CommonUtil.generateCodeQR(Objects.requireNonNull(mockEnvironment.getProperty("build.env"))));
        bizService.saveStore(bizStore, "Created New Store");

        storeHours = new LinkedList<>();
        for (int i = 1; i <= 7; i++) {
            StoreHourEntity storeHour = new StoreHourEntity(bizStore.getId(), DayOfWeek.of(i).getValue());
            storeHour.setStartHour(1)
                .setTokenAvailableFrom(1)
                .setTokenNotAvailableFrom(2359)
                .setEndHour(2359);

            storeHours.add(storeHour);
        }

        /* Add store hours. */
        bizService.insertAll(storeHours);

        /* Create Queue. */
        tokenQueueService.createUpdate(bizStore, Constants.appendPrefix);

        queueSupervisorUserProfile = accountService.checkUserExistsByPhone("9118000001101");
        businessUserStore = new BusinessUserStoreEntity(
            queueSupervisorUserProfile.getQueueUserId(),
            bizStore.getId(),
            bizName.getId(),
            bizStore.getCodeQR(),
            queueSupervisorUserProfile.getLevel());
        businessUserStoreService.save(businessUserStore);

        queueManagerUserProfile = accountService.checkUserExistsByPhone("9118000001102");
        businessUserStore = new BusinessUserStoreEntity(
            queueManagerUserProfile.getQueueUserId(),
            bizStore.getId(),
            bizName.getId(),
            bizStore.getCodeQR(),
            queueManagerUserProfile.getLevel());
        businessUserStoreService.save(businessUserStore);
    }

    public void addSecurityContextHolder(String phone) {
        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
        updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_CLIENT.name()));
        updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_Q_SUPERVISOR.name()));
        updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_S_MANAGER.name()));
        updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_M_ADMIN.name()));

        UserProfileEntity merchantUserProfile = accountService.checkUserExistsByPhone(phone);
        QueueUser queueUser = new QueueUser(merchantUserProfile.getEmail(), "XXX", updatedAuthorities, merchantUserProfile.getQueueUserId(), merchantUserProfile.getLevel(), true, true, merchantUserProfile.getCountryShortName(), "XYZ");
        Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn(queueUser);
        SecurityContextHolder.setContext(securityContext);
    }
}
