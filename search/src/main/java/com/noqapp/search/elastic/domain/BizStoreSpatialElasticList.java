package com.noqapp.search.elastic.domain;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.json.JsonCategory;
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
 * User: hitender
 * Date: 11/27/19 7:06 AM
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
public class BizStoreSpatialElasticList extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreSpatialElasticList.class);

    @Transient
    @JsonProperty("si")
    private String scrollId;

    @Transient
    @JsonProperty("from")
    private int from;

    @Transient
    @JsonProperty("size")
    private int size;

    @JsonProperty("cityName")
    private String cityName;

    @JsonProperty("categories")
    private List<JsonCategory> jsonCategories = new ArrayList<>();

    /** Do not make it a Set. Intentionally using List here. When Set, level up stops working and shows just one store. */
    @JsonProperty("result")
    private Collection<BizStoreElastic> bizStoreElastics = new ArrayList<>();

    public String getScrollId() {
        return scrollId;
    }

    public BizStoreSpatialElasticList setScrollId(String scrollId) {
        this.scrollId = scrollId;
        return this;
    }

    public int getFrom() {
        return from;
    }

    public BizStoreSpatialElasticList setFrom(int from) {
        this.from = from;
        return this;
    }

    public int getSize() {
        return size;
    }

    public BizStoreSpatialElasticList setSize(int size) {
        this.size = size;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public BizStoreSpatialElasticList setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public List<JsonCategory> getJsonCategories() {
        return jsonCategories;
    }

    public BizStoreSpatialElasticList setJsonCategories(List<JsonCategory> jsonCategories) {
        this.jsonCategories = jsonCategories;
        return this;
    }

    public BizStoreSpatialElasticList addJsonCategory(JsonCategory jsonCategory) {
        this.jsonCategories.add(jsonCategory);
        return this;
    }

    public Collection<BizStoreElastic> getBizStoreElastics() {
        return bizStoreElastics;
    }

    public BizStoreSpatialElasticList setBizStoreElastics(Collection<BizStoreElastic> bizStoreElastics) {
        this.bizStoreElastics = bizStoreElastics;
        return this;
    }

    public BizStoreSpatialElasticList addBizStoreElastic(BizStoreElastic bizStoreElastic) {
        this.bizStoreElastics.add(bizStoreElastic);
        return this;
    }

    @Transient
    public BizStoreSpatialElasticList populateBizStoreElasticSet(List<ElasticBizStoreSource> elasticBizStoreSources) {
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
        return "BizStoreSpatialElasticList{" +
            "scrollId='" + scrollId + '\'' +
            ", cityName='" + cityName + '\'' +
            ", jsonCategories=" + jsonCategories +
            ", bizStoreElastics=" + bizStoreElastics +
            '}';
    }
}
