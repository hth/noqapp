package com.noqapp.search.elastic.domain;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.json.JsonCategory;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.search.elastic.json.ElasticBizStoreSource;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * hitender
 * 3/20/18 6:36 PM
 */
@SuppressWarnings({
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

    @Transient
    @JsonProperty("si")
    private String scrollId;

    @JsonProperty("cityName")
    private String cityName;

    @JsonProperty("categories")
    private List<JsonCategory> jsonCategories = new ArrayList<>();

    /** Do not make it a Set datatype. Intentionally using List here. When changed to use Set, level up stops working and shows just one store. */
    @JsonProperty("result")
    private Collection<BizStoreElastic> bizStoreElastics = new ArrayList<>();

    @JsonProperty("bt")
    private BusinessTypeEnum searchedOnBusinessType;

    public String getScrollId() {
        return scrollId;
    }

    public BizStoreElasticList setScrollId(String scrollId) {
        this.scrollId = scrollId;
        return this;
    }

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

    public BizStoreElasticList setBizStoreElastics(Collection<BizStoreElastic> bizStoreElastics) {
        this.bizStoreElastics = bizStoreElastics;
        return this;
    }

    public BizStoreElasticList addBizStoreElastic(BizStoreElastic bizStoreElastic) {
        this.bizStoreElastics.add(bizStoreElastic);
        return this;
    }

    public BusinessTypeEnum getSearchedOnBusinessType() {
        return searchedOnBusinessType;
    }

    public BizStoreElasticList setSearchedOnBusinessType(BusinessTypeEnum searchedOnBusinessType) {
        this.searchedOnBusinessType = searchedOnBusinessType;
        return this;
    }

    @Transient
    public BizStoreElasticList populateBizStoreElasticSet(List<ElasticBizStoreSource> elasticBizStoreSources) {
        LOG.info("Before count={}", elasticBizStoreSources.size());

        if (!elasticBizStoreSources.isEmpty()) {
            bizStoreElastics = new HashSet<>();
        }

        for (ElasticBizStoreSource elasticBizStoreSource : elasticBizStoreSources) {
            BizStoreElastic elastic = elasticBizStoreSource.getBizStoreElastic();
            LOG.debug("{}, {}, hashCode={} {}", elastic.getDisplayName(), elastic.getBusinessName(), elastic.hashCode(), elastic);
            bizStoreElastics.add(elastic);
        }
        LOG.info("After count={}", bizStoreElastics.size());
        return this;
    }

    @Override
    public String toString() {
        return "BizStoreElasticList{" +
            "scrollId='" + scrollId + '\'' +
            ", cityName='" + cityName + '\'' +
            ", jsonCategories=" + jsonCategories +
            ", bizStoreElastics=" + bizStoreElastics +
            '}';
    }
}
