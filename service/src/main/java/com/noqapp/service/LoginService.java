package com.noqapp.service;

import com.noqapp.domain.BrowserEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserAuthenticationEntity;
import com.noqapp.repository.BrowserManager;
import com.noqapp.repository.UserAuthenticationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 11/19/16 7:14 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class LoginService {
    private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);

    private UserAuthenticationManager userAuthenticationManager;
    private BrowserManager browserManager;

    @Autowired
    public LoginService(
            UserAuthenticationManager userAuthenticationManager,
            BrowserManager browserManager
    ) {
        this.userAuthenticationManager = userAuthenticationManager;
        this.browserManager = browserManager;
    }

    private UserAuthenticationEntity loadAuthenticationEntity(UserAccountEntity userAccount) {
        return userAuthenticationManager.getById(userAccount.getUserAuthentication().getId());
    }

    public void saveUpdateBrowserInfo(
            String cookieId,
            String ip,
            String countryCode,
            String city,
            String userAgent,
            String browserName,
            String browserVersion,
            String device,
            String deviceBrand,
            String operatingSystem,
            String operatingSystemVersion
    ) {
        try {
            BrowserEntity browser = browserManager.getByCookie(cookieId);
            if (null == browser) {
                browser = BrowserEntity.newInstance(
                    cookieId,
                    ip,
                    countryCode,
                    city,
                    userAgent,
                    browserName,
                    browserVersion,
                    device,
                    deviceBrand,
                    operatingSystem,
                    operatingSystemVersion);
                browserManager.save(browser);
            } else {
                browserManager.update(browser.getId());
            }
        } catch (Exception e) {
            LOG.error("Moving on. Omitting this error={}", e.getLocalizedMessage(), e);
        }
    }
}
