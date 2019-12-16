package com.noqapp.loader;

import com.noqapp.common.config.TextToSpeechConfiguration;
import com.noqapp.health.repository.ApiHealthNowManager;
import com.noqapp.health.repository.ApiHealthNowManagerImpl;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.medical.repository.MasterLabManager;
import com.noqapp.medical.repository.MasterLabManagerImpl;
import com.noqapp.medical.repository.MedicalPathologyManager;
import com.noqapp.medical.repository.MedicalPathologyManagerImpl;
import com.noqapp.medical.repository.MedicalRadiologyManager;
import com.noqapp.medical.repository.MedicalRadiologyManagerImpl;
import com.noqapp.medical.repository.MedicalRecordManager;
import com.noqapp.medical.repository.MedicalRecordManagerImpl;
import com.noqapp.medical.repository.UserMedicalProfileHistoryManager;
import com.noqapp.medical.repository.UserMedicalProfileHistoryManagerImpl;
import com.noqapp.medical.repository.UserMedicalProfileManager;
import com.noqapp.medical.repository.UserMedicalProfileManagerImpl;
import com.noqapp.medical.service.MedicalFileService;
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
import com.noqapp.repository.BusinessUserManager;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.BusinessUserStoreManagerImpl;
import com.noqapp.repository.CouponManager;
import com.noqapp.repository.CouponManagerImpl;
import com.noqapp.repository.CustomTextToSpeechManager;
import com.noqapp.repository.CustomTextToSpeechManagerImpl;
import com.noqapp.repository.EmailValidateManager;
import com.noqapp.repository.EmailValidateManagerImpl;
import com.noqapp.repository.ForgotRecoverManager;
import com.noqapp.repository.ForgotRecoverManagerImpl;
import com.noqapp.repository.GenerateUserIdManager;
import com.noqapp.repository.GenerateUserIdManagerImpl;
import com.noqapp.repository.InviteManager;
import com.noqapp.repository.InviteManagerImpl;
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
import com.noqapp.repository.UserAuthenticationManager;
import com.noqapp.repository.UserAuthenticationManagerImpl;
import com.noqapp.repository.UserPreferenceManager;
import com.noqapp.repository.UserPreferenceManagerImpl;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.UserProfileManagerImpl;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessCustomerService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.CouponService;
import com.noqapp.service.CustomTextToSpeechService;
import com.noqapp.service.EmailValidateService;
import com.noqapp.service.FileService;
import com.noqapp.service.FirebaseMessageService;
import com.noqapp.service.FtpService;
import com.noqapp.service.GenerateUserIdService;
import com.noqapp.service.InviteService;
import com.noqapp.service.MailService;
import com.noqapp.service.PreferredBusinessService;
import com.noqapp.service.PurchaseOrderProductService;
import com.noqapp.service.QueueService;
import com.noqapp.service.StatsCronService;
import com.noqapp.service.StoreCategoryService;
import com.noqapp.service.TextToSpeechService;
import com.noqapp.service.TokenQueueService;

import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.OkHttpClient;

/**
 * hitender
 * 11/18/18 1:55 PM
 */
public class ITest extends RealMongoForITest {

    protected AccountService accountService;
    protected InviteService inviteService;
    protected FileService fileService;
    protected BizService bizService;
    protected StoreCategoryService storeCategoryService;
    protected UserMedicalProfileService userMedicalProfileService;
    protected TokenQueueService tokenQueueService;
    protected BusinessCustomerService businessCustomerService;
    protected ApiHealthService apiHealthService;
    protected FirebaseMessageService firebaseMessageService;
    protected BusinessUserService businessUserService;
    protected PreferredBusinessService preferredBusinessService;
    protected GenerateUserIdService generateUserIdService;
    protected EmailValidateService emailValidateService;
    protected QueueService queueService;
    protected MedicalFileService medicalFileService;
    protected MedicalTransactionService medicalTransactionService;
    protected StatsCronService statsCronService;
    protected PurchaseOrderProductService purchaseOrderProductService;
    protected CouponService couponService;
    protected TextToSpeechService textToSpeechService;
    protected CustomTextToSpeechService customTextToSpeechService;

    protected TokenQueueManager tokenQueueManager;
    protected QueueManager queueManager;
    protected UserAccountManager userAccountManager;
    protected UserAuthenticationManager userAuthenticationManager;
    protected UserPreferenceManager userPreferenceManager;
    protected UserProfileManager userProfileManager;
    protected EmailValidateManager emailValidateManager;
    protected ForgotRecoverManager forgotRecoverManager;
    protected InviteManager inviteManager;
    protected RegisteredDeviceManager registeredDeviceManager;
    protected BizNameManager bizNameManager;
    protected BizStoreManager bizStoreManager;
    protected StoreHourManager storeHourManager;
    protected BusinessUserStoreManager businessUserStoreManager;
    protected BusinessUserManager businessUserManager;
    protected ProfessionalProfileManager professionalProfileManager;
    protected UserMedicalProfileManager userMedicalProfileManager;
    protected UserMedicalProfileHistoryManager userMedicalProfileHistoryManager;
    protected StoreCategoryManager storeCategoryManager;
    protected PreferredBusinessManager preferredBusinessManager;
    protected ScheduledTaskManager scheduledTaskManager;
    protected GenerateUserIdManager generateUserIdManager;
    protected BusinessCustomerManager businessCustomerManager;
    protected ApiHealthNowManager apiHealthNowManager;
    protected MasterLabManager masterLabManager;
    protected PublishArticleManager publishArticleManager;
    protected MedicalRecordManager medicalRecordManager;
    protected MedicalRadiologyManager medicalRadiologyManager;
    protected MedicalPathologyManager medicalPathologyManager;
    protected StatsBizStoreDailyManager statsBizStoreDailyManager;
    protected StatsCronManager statsCronManager;
    protected PurchaseOrderManager purchaseOrderManager;
    protected PurchaseOrderProductManager purchaseOrderProductManager;
    protected AdvertisementManager advertisementManager;
    protected CouponManager couponManager;
    protected CustomTextToSpeechManager customTextToSpeechManager;

    protected S3FileManager s3FileManager;
    protected StoreProductManager storeProductManager;

    @Mock protected FtpService ftpService;
    @Mock protected MailService mailService;
    @Mock protected OkHttpClient okHttpClient;
    @Mock protected QueueManagerJDBC queueManagerJDBC;
    @Mock protected PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    @Mock protected PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC;
    @Mock protected TextToSpeechConfiguration textToSpeechConfiguration;

    @BeforeAll
    public void globalISetup() {
        MockitoAnnotations.initMocks(this);

        userAccountManager = new UserAccountManagerImpl(getMongoTemplate());
        userAuthenticationManager = new UserAuthenticationManagerImpl(getMongoTemplate());
        userPreferenceManager = new UserPreferenceManagerImpl(getMongoTemplate());
        userProfileManager = new UserProfileManagerImpl(getMongoTemplate());
        generateUserIdManager = new GenerateUserIdManagerImpl(getMongoTemplate());
        emailValidateManager = new EmailValidateManagerImpl(getMongoTemplate());
        inviteManager = new InviteManagerImpl(getMongoTemplate());
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
        medicalRecordManager = new MedicalRecordManagerImpl(getMongoTemplate());
        medicalRadiologyManager = new MedicalRadiologyManagerImpl(getMongoTemplate());
        medicalPathologyManager = new MedicalPathologyManagerImpl(getMongoTemplate());
        statsBizStoreDailyManager = new StatsBizStoreDailyManagerImpl(getMongoTemplate());
        statsCronManager = new StatsCronManagerImpl(getMongoTemplate());
        couponManager = new CouponManagerImpl(getMongoTemplate());
        customTextToSpeechManager = new CustomTextToSpeechManagerImpl(getMongoTemplate());

        userMedicalProfileService = new UserMedicalProfileService(userMedicalProfileManager, userMedicalProfileHistoryManager);
        firebaseMessageService = new FirebaseMessageService("", okHttpClient);
        inviteService = new InviteService(inviteManager);
        emailValidateService = new EmailValidateService(emailValidateManager);
        generateUserIdService = new GenerateUserIdService(generateUserIdManager);
        storeCategoryService = new StoreCategoryService(storeCategoryManager, storeProductManager);
        businessCustomerService = new BusinessCustomerService(
            businessCustomerManager,
            userProfileManager,
            queueManager
        );
        apiHealthService = new ApiHealthService(apiHealthNowManager);
        couponService = new CouponService(couponManager, bizStoreManager, userProfileManager);
        purchaseOrderProductService = new PurchaseOrderProductService(couponService, purchaseOrderProductManager, purchaseOrderProductManagerJDBC);

        accountService = new AccountService(
            5,
            userAccountManager,
            userAuthenticationManager,
            userPreferenceManager,
            userProfileManager,
            generateUserIdService,
            emailValidateService,
            inviteService,
            forgotRecoverManager
        );

        customTextToSpeechService = new CustomTextToSpeechService(customTextToSpeechManager);
        textToSpeechService = new TextToSpeechService(
            bizStoreManager,
            textToSpeechConfiguration,
            customTextToSpeechService
        );

        tokenQueueService = new TokenQueueService(
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
            apiHealthService
        );

        queueService = new QueueService(
            30,
            userProfileManager,
            businessCustomerService,
            bizStoreManager,
            queueManager,
            queueManagerJDBC,
            tokenQueueService,
            businessUserStoreManager,
            statsBizStoreDailyManager,
            purchaseOrderManager,
            purchaseOrderManagerJDBC,
            purchaseOrderProductService
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
            scheduledTaskManager
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
            bizService,
            storeCategoryService
        );

        medicalFileService = new MedicalFileService(medicalRecordManager, medicalPathologyManager, medicalRadiologyManager, s3FileManager, fileService ,ftpService);
        medicalTransactionService = new MedicalTransactionService(
            getMongoTemplate(),
            transactionManager(),
            getMongoTemplate(),
            masterLabManager
        );

        statsCronService = new StatsCronService(statsCronManager);
    }
}
