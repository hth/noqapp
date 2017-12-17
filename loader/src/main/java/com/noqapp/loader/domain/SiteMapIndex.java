package com.noqapp.loader.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.noqapp.common.utils.AbstractDomain;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 12/17/17 8:34 PM
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
@JacksonXmlRootElement(localName = "sitemapindex", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
public class SiteMapIndex extends AbstractDomain {

    @JacksonXmlProperty(localName = "sitemap")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SiteMap> siteMaps = new ArrayList<>();

    public List<SiteMap> getSiteMaps() {
        return siteMaps;
    }

    public SiteMapIndex addSiteMaps(SiteMap siteMap) {
        this.siteMaps.add(siteMap);
        return this;
    }
}
