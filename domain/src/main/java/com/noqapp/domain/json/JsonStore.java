package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 3/23/18 1:52 AM
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
public class JsonStore extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonStore.class);

    @JsonProperty("queue")
    private JsonQueue jsonQueue;

    @JsonProperty("hours")
    private List<JsonHour> jsonHours = new LinkedList<>();

    @JsonProperty("categories")
    private List<JsonStoreCategory> jsonStoreCategories = new LinkedList<>();

    @JsonProperty("products")
    private List<JsonStoreProduct> jsonStoreProducts = new LinkedList<>();

    public JsonQueue getJsonQueue() {
        return jsonQueue;
    }

    public JsonStore setJsonQueue(JsonQueue jsonQueue) {
        this.jsonQueue = jsonQueue;
        return this;
    }

    public List<JsonHour> getJsonHours() {
        return jsonHours;
    }

    public JsonStore setJsonHours(List<JsonHour> jsonHours) {
        this.jsonHours = jsonHours;
        return this;
    }

    public JsonStore addJsonHour(JsonHour jsonHour) {
        this.jsonHours.add(jsonHour);
        return this;
    }

    public List<JsonStoreCategory> getJsonStoreCategories() {
        return jsonStoreCategories;
    }

    public JsonStore addJsonStoreCategory(JsonStoreCategory jsonStoreCategory) {
        this.jsonStoreCategories.add(jsonStoreCategory);
        return this;
    }

    public List<JsonStoreProduct> getJsonStoreProducts() {
        return jsonStoreProducts;
    }

    public JsonStore addJsonStoreProduct(JsonStoreProduct jsonStoreProduct) {
        this.jsonStoreProducts.add(jsonStoreProduct);
        return this;
    }
}
