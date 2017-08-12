package com.noqapp.view.controller.open;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.security.OnLoginAuthenticationSuccessHandler;
import com.noqapp.service.AccountService;
import com.noqapp.service.LoginService;
import com.noqapp.social.service.CustomUserDetailsService;
import com.noqapp.view.cache.CachedUserAgentStringParser;
import com.noqapp.view.form.UserLoginForm;
import com.noqapp.view.form.UserLoginPhoneForm;
import com.noqapp.view.util.HttpRequestResponseParser;
import net.pieroxy.ua.detection.UserAgentDetectionResult;

import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * User: hitender
 * Date: 11/19/16 7:08 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/login")
public class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
    
    @Value ("${loginPage:login5}")
    private String loginPage;

    private final CachedUserAgentStringParser parser;

    private LoginService loginService;
    private OnLoginAuthenticationSuccessHandler onLoginAuthenticationSuccessHandler;
    private AccountService accountService;
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public LoginController(
            LoginService loginService,
            OnLoginAuthenticationSuccessHandler onLoginAuthenticationSuccessHandler,
            AccountService accountService,
            CustomUserDetailsService customUserDetailsService) {
        this.parser = CachedUserAgentStringParser.getInstance();

        this.loginService = loginService;
        this.onLoginAuthenticationSuccessHandler = onLoginAuthenticationSuccessHandler;
        this.accountService = accountService;
        this.customUserDetailsService = customUserDetailsService;
    }

    // TODO(hth) add later to my answer http://stackoverflow.com/questions/3457134/how-to-display-a-formatted-datetime-in-spring-mvc-3-0

    /**
     * isEnabled() false exists when properties registration.turned.on is false and user is trying to gain access
     * or signup through one of the provider. This is last line of defense for user signing in through social provider.
     * <p>
     * During application start up a call is made to show index page. Hence this method and only this controller
     * contains support for request type HEAD.
     * <p>
     * We have added support for HEAD request in filter to prevent failing on HEAD request. As of now there is no valid
     * reason why filter contains this HEAD request as everything is secure after login and there are no bots or
     * crawlers when a valid user has logged in.
     * <p>
     *
     * @param locale
     * @param request
     * @return
     * @see <a href="http://axelfontaine.com/blog/http-head.html">http://axelfontaine.com/blog/http-head.html</a>
     */
    @RequestMapping (method = {RequestMethod.GET, RequestMethod.HEAD})
    public String loadForm(
            @RequestHeader ("User-Agent")
            String userAgent,

            @ModelAttribute ("userLoginForm")
            UserLoginForm userLoginForm,

            @ModelAttribute ("userLoginPhoneForm")
            UserLoginPhoneForm userLoginPhoneForm,

            Locale locale,
            HttpServletRequest request
    ) {
        LOG.info("Locale Type={}", locale);

        UserAgentDetectionResult res = parser.parse(userAgent);
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            Cookie cookie = cookies[0];
            String cookieId = cookie.getValue();
            String ip = HttpRequestResponseParser.getClientIpAddress(request);

            String browser = res.getBrowser().getDescription();
            String browserVersion = res.getBrowser().getVersion();

            String device = res.getDevice().getDeviceType().getLabel();
            String deviceBrand = res.getDevice().getBrand().getLabel();

            String operatingSystem = res.getOperatingSystem().getFamily().getLabel();
            String operatingSystemVersion = res.getOperatingSystem().getVersion();

            LOG.info("cookie={}, ip={}, user-agent={}", cookieId, ip, userAgent);
            loginService.saveUpdateBrowserInfo(cookieId, ip, userAgent, browser, browserVersion, device, deviceBrand, operatingSystem, operatingSystemVersion);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOG.info("Auth {}", authentication.getPrincipal().toString());
        if (authentication instanceof AnonymousAuthenticationToken) {
            return loginPage;
        }

        return "redirect:" + onLoginAuthenticationSuccessHandler.determineTargetUrl(authentication);
    }

    /**
     * Login user after successful registration.
     *
     * @param qid
     * @return
     */
    String continueLoginAfterRegistration(String qid) {
        UserAccountEntity userAccount = accountService.findByQueueUserId(qid);

        if (null == userAccount) {
            LOG.error("No user found with qid={}", qid);
            throw new UsernameNotFoundException("User Not found");
        }

        UserProfileEntity userProfile = accountService.findProfileByReceiptUserId(qid);
        return determineTargetUrlAfterLogin(userAccount, userProfile);
    }

    String determineTargetUrlAfterLogin(UserAccountEntity userAccount, UserProfileEntity userProfile) {
        Collection<? extends GrantedAuthority> authorities = customUserDetailsService.getAuthorities(userAccount.getRoles());
        UserDetails userDetails = new QueueUser(
                userProfile.getEmail(),
                "",
                authorities,
                userProfile.getQueueUserId(),
                userProfile.getLevel(),
                customUserDetailsService.isUserActive(userAccount),
                userAccount.isAccountValidated(),
                userProfile.getCountryShortName()
        );

        /*
         * Blank password as Authentication object from thread is still being processed.
         * For other QueueUser pass the password for BCryptPasswordEncoder.
         */
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return onLoginAuthenticationSuccessHandler.determineTargetUrl(authentication);
    }
}
