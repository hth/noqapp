package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.BrowserEntity;
import com.token.domain.UserAccountEntity;
import com.token.domain.UserAuthenticationEntity;
import com.token.repository.BrowserManager;
import com.token.repository.UserAuthenticationManager;

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
            BrowserManager browserManager) {
        this.userAuthenticationManager = userAuthenticationManager;
        this.browserManager = browserManager;
    }

    private UserAuthenticationEntity loadAuthenticationEntity(UserAccountEntity userAccount) {
        return userAuthenticationManager.getById(userAccount.getUserAuthentication().getId());
    }

    public void saveUpdateBrowserInfo(
            String cookieId,
            String ip,
            String userAgent,
            String browser,
            String browserVersion,
            String device,
            String deviceBrand,
            String operatingSystem,
            String operatingSystemVersion
    ) {
        try {
            BrowserEntity browserEntity = browserManager.getByCookie(cookieId);
            if (null == browserEntity) {
                browserEntity = BrowserEntity.newInstance(cookieId, ip, userAgent, browser, browserVersion, device, deviceBrand, operatingSystem, operatingSystemVersion);
                browserManager.save(browserEntity);
            } else {
                browserEntity.setUpdated();
                browserManager.save(browserEntity);
            }
        } catch (Exception e) {
            LOG.error("Moving on. Omitting this error={}", e.getLocalizedMessage(), e);
        }
    }
}
