package com.noqapp.search.elastic.helper;

import java.util.Set;

/**
 * User: hitender
 * Date: 6/28/18 12:01 AM
 */
public class BusinessImageHolder {

    private String bannerImage;
    private Set<String> serviceImages;

    public String getBannerImage() {
        return bannerImage;
    }

    public BusinessImageHolder setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
        return this;
    }

    public Set<String> getServiceImages() {
        return serviceImages;
    }

    public BusinessImageHolder setServiceImages(Set<String> serviceImages) {
        this.serviceImages = serviceImages;
        return this;
    }
}
