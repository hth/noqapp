package com.noqapp.search.elastic.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.search.elastic.domain.BizStoreElastic;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 12/24/20 3:48 PM
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
public class ElasticFavorite extends AbstractDomain {

    @JsonProperty("fs")
    private List<BizStoreElastic> favoriteSuggested = new ArrayList<>();

    @JsonProperty("ft")
    private List<BizStoreElastic> favoriteTagged = new ArrayList<>();

    public List<BizStoreElastic> getFavoriteSuggested() {
        return favoriteSuggested;
    }

    public ElasticFavorite addFavoriteSuggested(BizStoreElastic favoriteSuggested) {
        this.favoriteSuggested.add(favoriteSuggested);
        return this;
    }

    public List<BizStoreElastic> getFavoriteTagged() {
        return favoriteTagged;
    }

    public ElasticFavorite addFavoriteTagged(BizStoreElastic favoriteTagged) {
        this.favoriteTagged.add(favoriteTagged);
        return this;
    }
}
