package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.domain.AbstractDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Response shown when application is working.
 * User: hitender
 * Date: 11/07/17 12:28 PM
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
public class JsonHealthCheck extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueue.class);

    @JsonProperty("health")
    private int health;

    @JsonProperty("sw")
    private String web;

    @JsonProperty("sm")
    private String mongo;

    @JsonProperty("sr")
    private String mysql;

    public int getHealth() {
        return health;
    }

    public JsonHealthCheck setHealth(int health) {
        this.health = health;
        return this;
    }

    public void increaseHealth() {
        health ++;
    }

    public String getWeb() {
        return web;
    }

    public JsonHealthCheck setWeb(String web) {
        this.web = web;
        return this;
    }

    public String getMongo() {
        return mongo;
    }

    public JsonHealthCheck setMongo(String mongo) {
        this.mongo = mongo;
        return this;
    }

    public String getMysql() {
        return mysql;
    }

    public JsonHealthCheck setMysql(String mysql) {
        this.mysql = mysql;
        return this;
    }
}
