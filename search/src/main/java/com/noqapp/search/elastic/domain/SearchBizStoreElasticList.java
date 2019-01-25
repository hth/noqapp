package com.noqapp.search.elastic.domain;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.json.JsonCategory;
import com.noqapp.search.elastic.json.SearchElasticBizStoreSource;

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
public class SearchBizStoreElasticList extends AbstractDomain {
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
    private Collection<SearchBizStoreElastic> searchBizStoreElastics = new ArrayList<>();

    public String getScrollId() {
        return scrollId;
    }

    public SearchBizStoreElasticList setScrollId(String scrollId) {
        this.scrollId = scrollId;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public SearchBizStoreElasticList setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public List<JsonCategory> getJsonCategories() {
        return jsonCategories;
    }

    public SearchBizStoreElasticList setJsonCategories(List<JsonCategory> jsonCategories) {
        this.jsonCategories = jsonCategories;
        return this;
    }

    public SearchBizStoreElasticList addJsonCategory(JsonCategory jsonCategory) {
        this.jsonCategories.add(jsonCategory);
        return this;
    }

    public Collection<SearchBizStoreElastic> getSearchBizStoreElastics() {
        return searchBizStoreElastics;
    }

    public SearchBizStoreElasticList setSearchBizStoreElastics(Collection<SearchBizStoreElastic> searchBizStoreElastics) {
        this.searchBizStoreElastics = searchBizStoreElastics;
        return this;
    }

    public SearchBizStoreElasticList addSearchBizStoreElastic(SearchBizStoreElastic searchBizStoreElastic) {
        this.searchBizStoreElastics.add(searchBizStoreElastic);
        return this;
    }

    @Transient
    public SearchBizStoreElasticList populateBizStoreElasticSet(List<SearchElasticBizStoreSource> searchElasticBizStoreSources) {
        LOG.info("Before count={}", searchElasticBizStoreSources.size());

        if (!searchElasticBizStoreSources.isEmpty()) {
            searchBizStoreElastics = new HashSet<>();
        }

        for (SearchElasticBizStoreSource searchElasticBizStoreSource : searchElasticBizStoreSources) {
            SearchBizStoreElastic elastic = searchElasticBizStoreSource.getSearchBizStoreElastic();
            LOG.debug("{}, {}, hashCode={} {}", elastic.getDisplayName(), elastic.getBusinessName(), elastic.hashCode(), elastic);
            searchBizStoreElastics.add(elastic);
        }
        LOG.info("After count={}", searchBizStoreElastics.size());
        return this;
    }

    @Override
    public String toString() {
        return "SearchBizStoreElasticList{" +
            "scrollId='" + scrollId + '\'' +
            ", cityName='" + cityName + '\'' +
            ", jsonCategories=" + jsonCategories +
            ", searchBizStoreElastics=" + searchBizStoreElastics +
            '}';
    }
}
