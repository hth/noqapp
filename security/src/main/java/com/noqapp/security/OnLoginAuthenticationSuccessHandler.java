package com.noqapp.security;

import com.noqapp.domain.types.RoleEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 11/18/16 3:02 PM
 */
public class OnLoginAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OnLoginAuthenticationSuccessHandler.class);

    /** For users. */
    @Value ("${accessLanding:/access/landing.htm}")
    private String accessLanding;

    /** For supers. */
    @Value ("${empSupervisorLanding:/emp/landing.htm}")
    private String empSupervisorLanding;

    /** For NoQueue techs for data related. TODO(hth) still */
    @Value ("${empTechLanding:/emp/noqueue/landing.htm}")
    private String empTechLanding;

    @Value ("${empMedicalTechLanding:/emp/medical/landing.htm}")
    private String empMedicalTechLanding;

    @Value ("${adminLanding:/admin/landing.htm}")
    private String adminLanding;

    @Value ("${displayLanding:/display/landing.htm}")
    private String displayLanding;

    @Value ("${supervisorLanding:/business/store/sup/landing.htm}")
    private String supervisorLanding;

    @Value ("${storeManagerLanding:/business/store/landing.htm}")
    private String storeManagerLanding;

    @Value ("${payoutLanding:/business/payout/landing.htm}")
    private String payoutLanding;

    @Value ("${businessAdminLanding:/business/landing.htm}")
    private String businessAdminLanding;

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws IOException {
        if (request.getHeader("cookie") != null) {
            handle(request, response, authentication);
            clearAuthenticationAttributes(request);
        }

        /*
         * Refer: http://www.baeldung.com/securing-a-restful-web-service-with-spring-security
         *
         * To execute:
         * curl -i -X POST
         *      -d emailId=some@mail.com
         *      -d password=realPassword
         * http://localhost:8080/login
         *
         * curl  -X "POST" "https://sandbox.noqapp.com/login" \
         *       -H "Cookie: JSESSIONID=A2ED915AA76A33010FC677F47BC624D6" \
         *       -H "X-CSRF-TOKEN: 5595db91-2bdb-4f64-8c13-4e35d7aaa1fd" \
         *       -H "Content-Type: application/json; charset=utf-8" \
         *       -d $'{
         *           "emailId": "dytvfGjc2nVShaVj6bnITulJSzx2",
         *           "password": "testtest"
         *       }'
         *
         */
        final SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (null == savedRequest) {
            clearAuthenticationAttributes(request);
            return;
        }
        final String targetUrlParameter = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || null != targetUrlParameter && StringUtils.hasText(request.getParameter(targetUrlParameter))) {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }

        clearAuthenticationAttributes(request);
    }

    protected void handle(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException {
        String targetUrl = determineTargetUrl(auth);

        if (res.isCommitted()) {
            LOG.debug("Response has already been committed. Unable to redirect endpoint={}", targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(req, res, targetUrl);
    }

    /**
     * Builds the landing URL according to the user role when they log in.
     * Refer: http://www.baeldung.com/spring_redirect_after_login
     */
    public String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        switch (getHighestRoleEnum(authorities)) {
            case ROLE_CLIENT:
                return accessLanding;
            case ROLE_Q_SUPERVISOR:
                return supervisorLanding;
            case ROLE_S_MANAGER:
                return storeManagerLanding;
            case ROLE_M_ACCOUNTANT:
                return payoutLanding;
            case ROLE_M_ADMIN:
                return businessAdminLanding;

            /* All the above ROLES are for Client and Merchant Landing. */
            case ROLE_ANALYSIS:
                return displayLanding;
            case ROLE_TECHNICIAN:
                return empTechLanding;
            case ROLE_MEDICAL_TECHNICIAN:
                return empMedicalTechLanding;
            case ROLE_SUPERVISOR:
                return empSupervisorLanding;
            case ROLE_ADMIN:
                return adminLanding;
            default:
                LOG.error("Role set is not defined");
                throw new IllegalStateException("Role set is not defined");
        }
    }

    /**
     * Finds the highest available role for landing page.
     *
     * @param authorities
     * @return
     */
    private RoleEnum getHighestRoleEnum(Collection<? extends GrantedAuthority> authorities) {
        RoleEnum roleEnum = null;
        for (GrantedAuthority grantedAuthority : authorities) {
            if (null == roleEnum || roleEnum.ordinal() < RoleEnum.valueOf(grantedAuthority.getAuthority()).ordinal()) {
                roleEnum = RoleEnum.valueOf(grantedAuthority.getAuthority());
            }
        }

        return roleEnum;
    }
}
