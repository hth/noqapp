package com.noqapp.search.elastic.domain;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.json.JsonCategory;
import com.noqapp.search.elastic.json.ElasticBizStoreSearchSource;

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
import java.util.List;

/**
 * hitender
 * 2019-01-24 18:14
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
public class BizStoreSearchElasticList extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreElasticList.class);

    @Transient
    @JsonProperty("si")
    private String scrollId;

    @JsonProperty("cityName")
    private String cityName;

    @JsonProperty("categories")
    private List<JsonCategory> jsonCategories = new ArrayList<>();

    /** Do not make it a Set. Intentionally using List here */
    @JsonProperty("result")
    private Collection<BizStoreSearchElastic> bizStoreSearchElastics = new ArrayList<>();

    public String getScrollId() {
        return scrollId;
    }

    public BizStoreSearchElasticList setScrollId(String scrollId) {
        this.scrollId = scrollId;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public BizStoreSearchElasticList setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public List<JsonCategory> getJsonCategories() {
        return jsonCategories;
    }

    public BizStoreSearchElasticList setJsonCategories(List<JsonCategory> jsonCategories) {
        this.jsonCategories = jsonCategories;
        return this;
    }

    public BizStoreSearchElasticList addJsonCategory(JsonCategory jsonCategory) {
        this.jsonCategories.add(jsonCategory);
        return this;
    }

    public Collection<BizStoreSearchElastic> getBizStoreSearchElastics() {
        return bizStoreSearchElastics;
    }

    public BizStoreSearchElasticList setBizStoreSearchElastics(Collection<BizStoreSearchElastic> bizStoreSearchElastics) {
        this.bizStoreSearchElastics = bizStoreSearchElastics;
        return this;
    }

    public BizStoreSearchElasticList addSearchBizStoreElastic(BizStoreSearchElastic bizStoreSearchElastic) {
        this.bizStoreSearchElastics.add(bizStoreSearchElastic);
        return this;
    }

    @Transient
    public BizStoreSearchElasticList populateSearchBizStoreElasticArray(List<ElasticBizStoreSearchSource> elasticBizStoreSearchSources) {
        LOG.info("Before count={}", elasticBizStoreSearchSources.size());

        for (ElasticBizStoreSearchSource elasticBizStoreSearchSource : elasticBizStoreSearchSources) {
            BizStoreSearchElastic elastic = elasticBizStoreSearchSource.getBizStoreSearchElastic();
            switch (elastic.getBusinessType()) {
                case CD:
                case CDQ:
                    elastic.setAddress(FileUtil.DASH);
                    elastic.setArea(FileUtil.DASH);
                    elastic.setTown(FileUtil.DASH);
                default:
                    //Do nothing
            }
            LOG.debug("{}, {}, hashCode={} {}", elastic.getDisplayName(), elastic.getBusinessName(), elastic.hashCode(), elastic);
            bizStoreSearchElastics.add(elastic);
        }
        LOG.info("After count={}", bizStoreSearchElastics.size());
        return this;
    }

    @Override
    public String toString() {
        return "SearchBizStoreElasticList{" +
            "scrollId='" + scrollId + '\'' +
            ", cityName='" + cityName + '\'' +
            ", jsonCategories=" + jsonCategories +
            ", searchBizStoreElastics=" + bizStoreSearchElastics +
            '}';
    }
}
