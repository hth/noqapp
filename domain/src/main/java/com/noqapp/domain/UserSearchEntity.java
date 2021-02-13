package com.noqapp.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 2/12/21 6:21 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "USER_SEARCH")
public class UserSearchEntity extends BaseEntity {

    @Field("QY")
    private String query;

    @Field ("QID")
    private String qid;

    @Field ("DID")
    private String did;

    @Field("CT")
    private String cityName;

    @Field ("GH")
    private String geoHash;

    @Field ("RC")
    private int resultCount;

    public String getQuery() {
        return query;
    }

    public UserSearchEntity setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getQid() {
        return qid;
    }

    public UserSearchEntity setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public String getDid() {
        return did;
    }

    public UserSearchEntity setDid(String did) {
        this.did = did;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public UserSearchEntity setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public UserSearchEntity setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public int getResultCount() {
        return resultCount;
    }

    public UserSearchEntity setResultCount(int resultCount) {
        this.resultCount = resultCount;
        return this;
    }
}
