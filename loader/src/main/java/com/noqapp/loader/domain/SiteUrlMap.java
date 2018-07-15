package com.noqapp.loader.domain;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 12/17/17 1:17 PM
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
@JacksonXmlRootElement(localName = "urlset", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
public class SiteUrlMap extends AbstractDomain {

    @JacksonXmlProperty(localName = "url", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SiteUrl> siteUrls = new ArrayList<>();

    public List<SiteUrl> getSiteUrls() {
        return siteUrls;
    }

    public SiteUrlMap addSiteUrl(SiteUrl siteUrl) {
        this.siteUrls.add(siteUrl);
        return this;
    }
}
