package com.noqapp.service;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

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

    private FreeMarkerConfigurationFactory freemarkerConfiguration;

    @Autowired
    public FreemarkerService(
            @SuppressWarnings ("SpringJavaAutowiringInspection")
            FreeMarkerConfigurationFactory freemarkerConfiguration
    ) {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    String freemarkerToString(String ftl, Map<String, Object> rootMap) throws IOException, TemplateException {
        Configuration cfg = freemarkerConfiguration.createConfiguration();
        Template template = cfg.getTemplate(ftl);
        return processTemplateIntoString(template, rootMap);
    }

    String freemarkerToStringComplex(String ftl, Map<String, Map<String, Object>> rootMap) throws IOException, TemplateException {
        Configuration cfg = freemarkerConfiguration.createConfiguration();
        Template template = cfg.getTemplate(ftl);
        return processTemplateIntoString(template, rootMap);
    }
}
