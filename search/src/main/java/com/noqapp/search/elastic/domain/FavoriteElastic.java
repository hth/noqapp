package com.noqapp.search.elastic.domain;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.ActionTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Not stored in elasticsearch. Sent across as json for display on client.
 * <p>
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
public class FavoriteElastic extends AbstractDomain {

    @JsonProperty("fs")
    private List<BizStoreElastic> favoriteSuggested = new ArrayList<>();

    @JsonProperty("ft")
    private List<BizStoreElastic> favoriteTagged = new ArrayList<>();

    @JsonProperty("fsb")
    private Set<String> favoriteSuggestedBizNameIds = new HashSet<>();

    @JsonProperty("ftb")
    private Set<String> favoriteTaggedBizNameIds = new HashSet<>();

    @JsonProperty("at")
    private ActionTypeEnum actionType;

    @JsonProperty("qr")
    private String codeQR;

    public List<BizStoreElastic> getFavoriteSuggested() {
        return favoriteSuggested;
    }

    public FavoriteElastic addFavoriteSuggested(BizStoreElastic favoriteSuggested) {
        this.favoriteSuggested.add(favoriteSuggested);
        return this;
    }

    public List<BizStoreElastic> getFavoriteTagged() {
        return favoriteTagged;
    }

    public FavoriteElastic addFavoriteTagged(BizStoreElastic favoriteTagged) {
        this.favoriteTagged.add(favoriteTagged);
        return this;
    }

    public Set<String> getFavoriteSuggestedBizNameIds() {
        return favoriteSuggestedBizNameIds;
    }

    public FavoriteElastic setFavoriteSuggestedBizNameIds(Set<String> favoriteSuggestedBizNameIds) {
        this.favoriteSuggestedBizNameIds = favoriteSuggestedBizNameIds;
        return this;
    }

    public FavoriteElastic addFavoriteSuggestedBizNameId(String favoriteSuggestedBizNameId) {
        this.favoriteSuggestedBizNameIds.add(favoriteSuggestedBizNameId);
        return this;
    }

    public Set<String> getFavoriteTaggedBizNameIds() {
        return favoriteTaggedBizNameIds;
    }

    public FavoriteElastic setFavoriteTaggedBizNameIds(Set<String> favoriteTaggedBizNameIds) {
        this.favoriteTaggedBizNameIds = favoriteTaggedBizNameIds;
        return this;
    }

    public FavoriteElastic addFavoriteTaggedBizNameId(String favoriteTaggedBizNameId) {
        this.favoriteTaggedBizNameIds.add(favoriteTaggedBizNameId);
        return this;
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public FavoriteElastic setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public FavoriteElastic setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }
}
