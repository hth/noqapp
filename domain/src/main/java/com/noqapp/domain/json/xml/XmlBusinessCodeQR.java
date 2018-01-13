package com.noqapp.domain.json.xml;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.noqapp.common.utils.AbstractDomain;

import java.net.URI;

/**
 * hitender
 * 1/12/18 3:33 AM
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
@JacksonXmlRootElement(localName = "business")
public class XmlBusinessCodeQR extends AbstractDomain {

    private String businessName;
    private URI imageLocationCodeQR;

    public String getBusinessName() {
        return businessName;
    }

    public XmlBusinessCodeQR setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public URI getImageLocationCodeQR() {
        return imageLocationCodeQR;
    }

    public XmlBusinessCodeQR setImageLocationCodeQR(URI imageLocationCodeQR) {
        this.imageLocationCodeQR = imageLocationCodeQR;
        return this;
    }
}
