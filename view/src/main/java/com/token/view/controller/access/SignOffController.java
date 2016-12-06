package com.token.view.controller.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.token.domain.site.TokenUser;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 11/19/16 6:46 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/signoff")
@Qualifier ("signOff")
public class SignOffController extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SignOffController.class);

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String receiptUserId = "Not Available";
        if (authentication.getPrincipal() != null) {
            TokenUser receiptUser = (TokenUser) authentication.getPrincipal();
            receiptUserId = receiptUser.getRid();
        }

        LOG.info("Logout user={} from={}", receiptUserId, request.getServletPath());
        super.onLogoutSuccess(request, response, authentication);
    }
}