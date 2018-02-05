package com.noqapp.loader.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.noqapp.common.utils.AbstractDomain;

/**
 * hitender
 * 12/17/17 1:14 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteUrl extends AbstractDomain {
    @JacksonXmlProperty(localName = "loc", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    private String location;

    @JacksonXmlProperty(localName = "lastmod", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    private String lastModified;

    @JacksonXmlProperty(localName = "changefreq", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    private String changeFrequency;

    @JacksonXmlProperty(localName = "priority", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    private String priority;

    public String getLocation() {
        return location;
    }

    public SiteUrl setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getLastModified() {
        return lastModified;
    }

    public SiteUrl setLastModified(String lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getChangeFrequency() {
        return changeFrequency;
    }

    public SiteUrl setChangeFrequency(String changeFrequency) {
        this.changeFrequency = changeFrequency;
        return this;
    }

    public String getPriority() {
        return priority;
    }

    public SiteUrl setPriority(String priority) {
        this.priority = priority;
        return this;
    }
}
