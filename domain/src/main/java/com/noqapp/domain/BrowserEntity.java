package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 11/19/16 7:14 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "BROWSER")
@CompoundIndexes(value = {
    @CompoundIndex(name = "browser_idx", def = "{'U': -1}", unique = true),
})
public class BrowserEntity extends BaseEntity {

    @Field("CK")
    private String cookieId;

    @Field("IP")
    private String ipAddress;

    @Field("CC")
    private String country;

    @Field("CT")
    private String city;

    @Field("UA")
    private String userAgent;

    @Field("BR")
    private String browserName;

    @Field("BRV")
    private String browserVersion;

    @Field("DV")
    private String device;

    @Field("DVB")
    private String deviceBrand;

    @Field("OS")
    private String operatingSystem;

    @Field("OSV")
    private String operatingSystemVersion;

    @SuppressWarnings("unused")
    public BrowserEntity() {
    }

    private BrowserEntity(
        String cookieId,
        String ipAddress,
        String country,
        String city,
        String userAgent,
        String browserName,
        String browserVersion,
        String device,
        String deviceBrand,
        String operatingSystem,
        String operatingSystemVersion
    ) {
        super();
        this.cookieId = cookieId;
        this.ipAddress = ipAddress;
        this.country = country;
        this.city = city;
        this.userAgent = userAgent;
        this.browserName = browserName;
        this.browserVersion = browserVersion;
        this.device = device;
        this.deviceBrand = deviceBrand;
        this.operatingSystem = operatingSystem;
        this.operatingSystemVersion = operatingSystemVersion;
    }

    public static BrowserEntity newInstance(
        String cookieId,
        String ip,
        String country,
        String city,
        String userAgent,
        String browserName,
        String browserVersion,
        String device,
        String deviceBrand,
        String operatingSystem,
        String operatingSystemVersion
    ) {
        return new BrowserEntity(
            cookieId,
            ip,
            country,
            city,
            userAgent,
            browserName,
            browserVersion,
            device,
            deviceBrand,
            operatingSystem,
            operatingSystemVersion);
    }
}
