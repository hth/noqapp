package com.token.security;

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

import com.token.domain.types.RoleEnum;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
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

    /** For receipt techs. */
    @Value ("${empTechLanding:/emp/receipt/landing.htm}")
    private String empTechLanding;

    /** For supers. */
    @Value ("${empLanding:/emp/landing.htm}")
    private String empLanding;

    @Value ("${adminLanding:/admin/landing.htm}")
    private String adminLanding;

    @Value ("${displayLanding:/display/landing.htm}")
    private String displayLanding;

    @Value ("${businessManagerLanding:/business/bm/landing.htm}")
    private String businessManagerLanding;

    @Value ("${businessAdminLanding:/business/landing.htm}")
    private String businessAdminLanding;

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws ServletException, IOException {
        if (request.getHeader("cookie") != null) {
            handle(request, response, authentication);
            clearAuthenticationAttributes(request);
        }

        /**
         * Refer: http://www.baeldung.com/2011/10/31/securing-a-restful-web-service-with-spring-security-3-1-part-3/
         * To execute:
         * curl -i -X POST
         * -d emailId=some@mail.com
         * -d password=realPassword
         * http://localhost:8080/receipt/login
         */
        final SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (null == savedRequest) {
            clearAuthenticationAttributes(request);
            return;
        }
        final String targetUrlParameter = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || targetUrlParameter != null &&
                StringUtils.hasText(request.getParameter(targetUrlParameter))) {
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
            case ROLE_ANALYSIS:
                return displayLanding;
            case ROLE_MER_MANAGER:
                return businessManagerLanding;
            case ROLE_MER_ADMIN:
                return businessAdminLanding;
            case ROLE_TECHNICIAN:
                return empTechLanding;
            case ROLE_SUPERVISOR:
                return empLanding;
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
