package com.noqapp.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Converts domain object to string.
 * User: hitender
 * Date: 1/1/17 7:25 AM
 */
public abstract class AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDomain.class);

    /* ISO date format 8601. */
    public static final String ISO8601_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * @deprecated (This adds tons of accept charset.)
     * Converts this object to JSON representation;
     * do not use annotation as this breaks and content length is set to -1
     */
    public String asJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Writer writer = new StringWriter();
            mapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            LOG.error("Failed JSON transforming object error={}", e.getLocalizedMessage(), e);
            return "{}";
        }
    }

    public String asXML() {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper
                .configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
                .enable(SerializationFeature.INDENT_OUTPUT);
            return xmlMapper.writeValueAsString(this);
        } catch (IOException e) {
            LOG.error("Failed XML transforming object error={}", e.getLocalizedMessage(), e);
            return "";
        }
    }
}
