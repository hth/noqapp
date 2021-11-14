package com.noqapp.medical;

import com.noqapp.common.config.FirebaseConfig;
import com.noqapp.common.config.TextToSpeechConfiguration;
import com.noqapp.health.repository.ApiHealthNowManager;
import com.noqapp.health.repository.ApiHealthNowManagerImpl;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.medical.repository.MasterLabManager;
import com.noqapp.medical.repository.MasterLabManagerImpl;
import com.noqapp.medical.repository.MedicalMedicationManager;
import com.noqapp.medical.repository.MedicalMedicineManager;
import com.noqapp.medical.repository.MedicalPathologyManager;
import com.noqapp.medical.repository.MedicalPathologyManagerImpl;
import com.noqapp.medical.repository.MedicalPathologyTestManager;
import com.noqapp.medical.repository.MedicalPhysicalManager;
import com.noqapp.medical.repository.MedicalRadiologyManager;
import com.noqapp.medical.repository.MedicalRadiologyManagerImpl;
import com.noqapp.medical.repository.MedicalRadiologyTestManager;
import com.noqapp.medical.repository.MedicalRecordManager;
import com.noqapp.medical.repository.MedicalRecordManagerImpl;
import com.noqapp.medical.repository.UserMedicalProfileHistoryManager;
import com.noqapp.medical.repository.UserMedicalProfileHistoryManagerImpl;
import com.noqapp.medical.repository.UserMedicalProfileManager;
import com.noqapp.medical.repository.UserMedicalProfileManagerImpl;
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
import com.noqapp.repository.ProfessionalProfileManager;
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
import com.noqapp.repository.ScheduledTaskManager;
import com.noqapp.repository.StatsBizStoreDailyManager;
import com.noqapp.repository.StatsBizStoreDailyManagerImpl;
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
import com.noqapp.service.BusinessCustomerService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.CouponService;
import com.noqapp.service.CustomTextToSpeechService;
import com.noqapp.service.EmailValidateService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.FileService;
import com.noqapp.service.FirebaseMessageService;
import com.noqapp.service.FirebaseService;
import com.noqapp.service.FtpService;
import com.noqapp.service.GenerateUserIdService;
import com.noqapp.service.JMSProducerService;
import com.noqapp.service.LanguageTranslationService;
import com.noqapp.service.MailService;
import com.noqapp.service.MessageCustomerService;
import com.noqapp.service.PreferredBusinessService;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.service.PurchaseOrderProductService;
import com.noqapp.service.PurchaseOrderService;
import com.noqapp.service.QueueService;
import com.noqapp.service.ReviewService;
import com.noqapp.service.StoreCategoryService;
import com.noqapp.service.StoreHourService;
import com.noqapp.service.StoreProductService;
import com.noqapp.service.SubscribeTopicService;
import com.noqapp.service.TextToSpeechService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.service.UserAddressService;
import com.noqapp.service.UserProfilePreferenceService;
import com.noqapp.service.graph.GraphDetailOfPerson;
import com.noqapp.service.nlp.NLPService;
import com.noqapp.service.payment.CashfreeService;
import com.noqapp.service.transaction.TransactionService;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.env.MockEnvironment;

import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import okhttp3.OkHttpClient;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 11/18/18 1:55 PM
 */
public class ITest extends RealMongoForITest {

    protected AccountService accountService;
    protected UserProfilePreferenceService userProfilePreferenceService;
    protected TokenQueueService tokenQueueService;
    protected BizService bizService;
    protected QueueService queueService;
    protected BusinessUserStoreService businessUserStoreService;
    protected ProfessionalProfileService professionalProfileService;

    protected UserAddressManager userAddressManager;
    protected UserAddressService userAddressService;
    protected StoreProductManager storeProductManager;
    protected StoreProductService storeProductService;
    protected PurchaseOrderManager purchaseOrderManager;
    protected PurchaseOrderProductManager purchaseOrderProductManager;
    protected PurchaseOrderService purchaseOrderService;
    protected FileService fileService;
    protected S3FileManager s3FileManager;
    protected ReviewService reviewService;
    protected StoreHourService storeHourService;
    protected CouponService couponService;
    protected TextToSpeechService textToSpeechService;
    protected CustomTextToSpeechService customTextToSpeechService;
    protected MessageCustomerService messageCustomerService;

    protected MedicalRecordManager medicalRecordManager;
    protected MedicalPhysicalManager medicalPhysicalManager;
    protected MedicalMedicationManager medicalMedicationManager;
    protected MedicalMedicineManager medicalMedicineManager;
    protected MedicalPathologyManager medicalPathologyManager;
    protected MedicalPathologyTestManager medicalPathologyTestManager;
    protected MedicalRadiologyManager medicalRadiologyManager;
    protected MedicalRadiologyTestManager medicalRadiologyTestManager;
    protected MedicalRecordService medicalRecordService;
    protected MedicalFileService medicalFileService;
    protected MedicalTransactionService medicalTransactionService;

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
    protected MasterLabManager masterLabManager;
    protected AdvertisementManager advertisementManager;
    protected PropertyRentalManager propertyRentalManager;
    protected HouseholdItemManager householdItemManager;
    protected CouponManager couponManager;
    protected CustomTextToSpeechManager customTextToSpeechManager;
    protected NotificationMessageManager notificationMessageManager;

    protected BusinessCustomerManager businessCustomerManager;
    protected BusinessCustomerService businessCustomerService;
    protected UserMedicalProfileService userMedicalProfileService;
    protected StoreCategoryService storeCategoryService;
    protected TransactionService transactionService;
    protected NLPService nlpService;
    protected PurchaseOrderProductService purchaseOrderProductService;

    protected ApiHealthService apiHealthService;
    protected ApiHealthNowManager apiHealthNowManager;
    protected StatsBizStoreDailyManager statsBizStoreDailyManager;

    protected GenerateUserIdManager generateUserIdManager;

    private MockEnvironment mockEnvironment;

    private StanfordCoreNLP stanfordCoreNLP;
    @Mock protected ExternalService externalService;
    @Mock protected QueueManagerJDBC queueManagerJDBC;
    @Mock protected PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    @Mock protected PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC;
    @Mock protected HttpServletResponse httpServletResponse;
    @Mock protected FtpService ftpService;
    @Mock protected MailService mailService;
    @Mock protected OkHttpClient okHttpClient;
    @Mock protected CashfreeService cashfreeService;
    @Mock protected FirebaseConfig firebaseConfig;
    @Mock protected TextToSpeechConfiguration textToSpeechConfiguration;
    @Mock protected LanguageTranslationService languageTranslationService;
    @Mock protected GraphDetailOfPerson graphDetailOfPerson;
    @Mock protected NotificationN4jManager notificationN4jManager;
    @Mock protected StringRedisTemplate stringRedisTemplate;
    @Mock protected JMSProducerService jmsProducerService;
    @Mock protected SubscribeTopicService subscribeTopicService;

    @BeforeAll
    public void globalISetup() {
        MockitoAnnotations.openMocks(this);

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
        userMedicalProfileHistoryManager = new UserMedicalProfileHistoryManagerImpl(getMongoTemplate());
        purchaseOrderManager = new PurchaseOrderManagerImpl(getMongoTemplate());
        purchaseOrderProductManager = new PurchaseOrderProductManagerImpl(getMongoTemplate());
        s3FileManager = new S3FileManagerImpl(getMongoTemplate());
        bizNameManager = new BizNameManagerImpl(getMongoTemplate());
        bizStoreManager = new BizStoreManagerImpl(getMongoTemplate());
        masterLabManager = new MasterLabManagerImpl(getMongoTemplate());
        storeProductManager = new StoreProductManagerImpl(getMongoTemplate());
        storeCategoryManager = new StoreCategoryManagerImpl(getMongoTemplate());
        tokenQueueManager = new TokenQueueManagerImpl(getMongoTemplate());
        queueManager = new QueueManagerImpl(getMongoTemplate());
        storeHourManager = new StoreHourManagerImpl(getMongoTemplate());
        businessCustomerManager = new BusinessCustomerManagerImpl(getMongoTemplate());
        apiHealthNowManager = new ApiHealthNowManagerImpl(getMongoTemplate());
        businessUserStoreManager = new BusinessUserStoreManagerImpl(getMongoTemplate());
        publishArticleManager = new PublishArticleManagerImpl(getMongoTemplate());
        advertisementManager = new AdvertisementManagerImpl(getMongoTemplate());
        propertyRentalManager = new PropertyRentalManagerImpl(getMongoTemplate());
        householdItemManager = new HouseholdItemManagerImpl(getMongoTemplate());

        medicalRecordManager = new MedicalRecordManagerImpl(getMongoTemplate());
        medicalRadiologyManager = new MedicalRadiologyManagerImpl(getMongoTemplate());
        medicalPathologyManager = new MedicalPathologyManagerImpl(getMongoTemplate());
        statsBizStoreDailyManager = new StatsBizStoreDailyManagerImpl(getMongoTemplate());
        couponManager = new CouponManagerImpl(getMongoTemplate());
        businessCustomerPriorityManager = new BusinessCustomerPriorityManagerImpl(getMongoTemplate());
        userAddressManager = new UserAddressManagerImpl(5, getMongoTemplate());

        userMedicalProfileService = new UserMedicalProfileService(userMedicalProfileManager, userMedicalProfileHistoryManager);
        firebaseMessageService = new FirebaseMessageService("", okHttpClient);
        emailValidateService = new EmailValidateService(emailValidateManager);
        generateUserIdService = new GenerateUserIdService(generateUserIdManager);
        storeCategoryService = new StoreCategoryService(storeCategoryManager, storeProductManager);
        firebaseService = new FirebaseService(firebaseConfig, userProfileManager);
        apiHealthService = new ApiHealthService(apiHealthNowManager);
        couponService = new CouponService(couponManager, bizStoreManager, userProfileManager);
        userAddressService = new UserAddressService(5, userAddressManager);
        purchaseOrderProductService = new PurchaseOrderProductService(couponService, purchaseOrderProductManager, purchaseOrderProductManagerJDBC, userAddressService);

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

        medicalFileService = new MedicalFileService(medicalRecordManager, medicalPathologyManager, medicalRadiologyManager, s3FileManager, fileService ,ftpService);
        medicalTransactionService = new MedicalTransactionService(
            getMongoTemplate(),
            transactionManager(),
            masterLabManager,
            mongoHosts()
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
    }
}
