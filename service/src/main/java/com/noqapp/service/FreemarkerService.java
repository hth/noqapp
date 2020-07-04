package com.noqapp.service;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

/**
 * User: hitender
 * Date: 1/16/17 9:32 AM
 */
@Service
public class FreemarkerService {
    private static final Logger LOG = LoggerFactory.getLogger(FreemarkerService.class);

    private FreeMarkerConfigurationFactory freemarkerConfiguration;

    @Autowired
    public FreemarkerService(
        @SuppressWarnings ("SpringJavaAutowiringInspection")
        FreeMarkerConfigurationFactory freemarkerConfiguration
    ) {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    String freemarkerToString(String ftl, Map<String, Object> rootMap) throws IOException, TemplateException {
        try {
            Configuration cfg = freemarkerConfiguration.createConfiguration();
            Template template = cfg.getTemplate(ftl);
            return processTemplateIntoString(template, rootMap);
        } catch (Exception e) {
            LOG.error("Failed processing ftl={} reason={} {}", ftl, e.getLocalizedMessage(), rootMap, e);
            throw e;
        }
    }

    String freemarkerToStringComplex(String ftl, Map<String, Map<String, Object>> rootMap) throws IOException, TemplateException {
        try {
            Configuration cfg = freemarkerConfiguration.createConfiguration();
            Template template = cfg.getTemplate(ftl);
            return processTemplateIntoString(template, rootMap);
        } catch (Exception e) {
            LOG.error("Failed processing ftl={} {} {}", ftl, e.getLocalizedMessage(), rootMap, e);
            throw e;
        }
    }
}
