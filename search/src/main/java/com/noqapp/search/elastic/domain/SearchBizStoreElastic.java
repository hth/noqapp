package com.noqapp.search.elastic.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Search to uniquely identify each document and be made available during search.
 * 
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
public class SearchBizStoreElastic extends BizStoreElastic {
    private static final Logger LOG = LoggerFactory.getLogger(SearchBizStoreElastic.class);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchBizStoreElastic that = (SearchBizStoreElastic) o;

        switch (businessType) {
            case DO:
            case HS:
                return Objects.equals(bizNameId, that.bizNameId) &&
                    Objects.equals(bizCategoryId, that.bizCategoryId);
            case BK:
                return Objects.equals(bizNameId, that.bizNameId) &&
                    Objects.equals(placeId, that.placeId);
            default:
                return Objects.equals(bizNameId, that.bizNameId) &&
                    Objects.equals(bizCategoryId, that.bizCategoryId) &&
                    Objects.equals(address, that.address);
        }
    }

    @Override
    public int hashCode() {
        switch (businessType) {
            case DO:
            case HS:
                return Objects.hash(bizNameId, bizCategoryId);
            case BK:
                return Objects.hash(bizNameId, placeId);
            default:
                return Objects.hash(bizNameId, bizCategoryId, address);
        }
    }
}
