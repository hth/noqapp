package com.noqapp.view.flow.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.webflow.context.ExternalContext;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * User: hitender
 * Date: 7/14/17 8:17 PM
 */
@Component
public final class WebFlowUtils {

    public Object getFlashAttribute(ExternalContext context, String attributeName) {
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap((HttpServletRequest) context.getNativeRequest());
        return flashMap != null ? flashMap.get(attributeName) : null;
    }
}
