package com.noqapp.search.elastic.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.json.JsonCategory;
import com.noqapp.search.elastic.json.ElasticBizStoreSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 3/20/18 6:36 PM
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
public class BizStoreElasticList extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreElasticList.class);

    @JsonProperty("cityName")
    private String cityName;

    @JsonProperty("categories")
    private List<JsonCategory> jsonCategories = new ArrayList<>();

    @JsonProperty("result")
    private Collection<BizStoreElastic> bizStoreElastics = new LinkedList<>();

    public String getCityName() {
        return cityName;
    }

    public BizStoreElasticList setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public List<JsonCategory> getJsonCategories() {
        return jsonCategories;
    }

    public BizStoreElasticList setJsonCategories(List<JsonCategory> jsonCategories) {
        this.jsonCategories = jsonCategories;
        return this;
    }

    public BizStoreElasticList addJsonCategory(JsonCategory jsonCategory) {
        this.jsonCategories.add(jsonCategory);
        return this;
    }

    public Collection<BizStoreElastic> getBizStoreElastics() {
        return bizStoreElastics;
    }

    public BizStoreElasticList setBizStoreElastics(List<BizStoreElastic> bizStoreElastics) {
        this.bizStoreElastics = bizStoreElastics;
        return this;
    }

    public BizStoreElasticList addBizStoreElastic(BizStoreElastic bizStoreElastic) {
        this.bizStoreElastics.add(bizStoreElastic);
        return this;
    }

    @Transient
    public void uniqueSet() {
        bizStoreElastics = new LinkedHashSet<>(bizStoreElastics);
    }

    @Transient
    public BizStoreElasticList populateBizStoreElasticList(List<ElasticBizStoreSource> elasticBizStoreSources) {
        for (ElasticBizStoreSource elasticBizStoreSource : elasticBizStoreSources) {
            BizStoreElastic bizStoreElastic = elasticBizStoreSource.getBizStoreElastic();
            //TODO(hth) remove this call, currently it populates the images
            bizStoreElastic.getDisplayImage();
            LOG.info("{}, {}, hashCode={} {}",
                    bizStoreElastic.getDisplayName(),
                    bizStoreElastic.getBusinessName(),
                    bizStoreElastic.hashCode(),
                    bizStoreElastic);
            bizStoreElastics.add(bizStoreElastic);
        }

        return this;
    }
}
