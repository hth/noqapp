package com.noqapp.view.flow.access;

import com.noqapp.domain.market.PropertyEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.market.PropertyManager;
import com.noqapp.view.form.PostOnMarketplaceForm;
import com.noqapp.view.util.HttpRequestResponseParser;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

/**
 * hitender
 * 1/10/21 11:12 AM
 */
@Component
public class PostOnMarketplaceFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(PostOnMarketplaceFlowActions.class);

    private Environment environment;
    private DatabaseReader databaseReader;
    private PropertyManager propertyManager;
    private UserProfileManager userProfileManager;

    @Autowired
    public PostOnMarketplaceFlowActions(
        Environment environment,
        DatabaseReader databaseReader,
        PropertyManager propertyManager,
        UserProfileManager userProfileManager
    ) {
        this.environment = environment;
        this.databaseReader = databaseReader;
        this.propertyManager = propertyManager;
        this.userProfileManager = userProfileManager;
    }

    @SuppressWarnings("unused")
    public PostOnMarketplaceForm startOfNewPost(String postId, String businessTypeAsString, boolean postingAllowed, ExternalContext externalContext) {
        HttpServletRequest httpServletRequest = (HttpServletRequest)externalContext.getNativeRequest();
        String ip = HttpRequestResponseParser.getClientIpAddress(httpServletRequest);

        String countryCode = "IN";
        String city = "";
        Location location = null;
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = databaseReader.city(ipAddress);
            countryCode = StringUtils.isEmpty(response.getCountry().getIsoCode()) ? "" : response.getCountry().getIsoCode();
            city = StringUtils.isEmpty(response.getCity().getName()) ? "" : response.getCity().getName();
            location = response.getLocation();
            LOG.info("Accuracy radius {} {} {}", location.getAccuracyRadius(), city, countryCode);
        } catch (AddressNotFoundException e) {
            LOG.warn("Failed finding ip={} reason={}", ip, e.getLocalizedMessage());
        } catch (GeoIp2Exception e) {
            LOG.error("Failed geoIp reason={}", e.getLocalizedMessage(), e);
        } catch (UnknownHostException e) {
            LOG.error("Failed host reason={}", e.getLocalizedMessage(), e);
        } catch (IOException e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }

        PostOnMarketplaceForm postOnMarketplaceForm = new PostOnMarketplaceForm()
            .setIp(ip)
            .setCountryCode(countryCode)
            .setCity(city)
            .setPostingAllowed(postingAllowed);

        if (null != location) {
            postOnMarketplaceForm.setCoordinate(new double[]{location.getLongitude(), location.getLatitude()});
        }

        if (StringUtils.isNotBlank(postId)) {
            switch (BusinessTypeEnum.valueOf(businessTypeAsString)) {
                case PR:
                    PropertyEntity property = propertyManager.findOneById(postId);
                    postOnMarketplaceForm.setMarketplace(property)
                        .setBusinessType(property.getBusinessType())
                        .setCity(property.getCity());
                    break;
                default:
                    LOG.error("Reached unreachable condition, businessType={}", postOnMarketplaceForm.getBusinessType());
                    throw new IllegalStateException("Condition set is not defined");
            }
        }

        return postOnMarketplaceForm;
    }

    /** After business type is selected set the entity. */
    @SuppressWarnings("unused")
    public void afterPostTypeHasBeenSelected(PostOnMarketplaceForm postOnMarketplaceForm) {
        switch (postOnMarketplaceForm.getBusinessType()) {
            case PR:
                if (null == postOnMarketplaceForm.getMarketplace()) {
                    postOnMarketplaceForm.setMarketplace(
                        new PropertyEntity()
                            .setCity(postOnMarketplaceForm.getCity())
                            .setCountryShortName(postOnMarketplaceForm.getCountryCode()));
                } else {
                    postOnMarketplaceForm.getMarketplace()
                        .setCity(postOnMarketplaceForm.getCity())
                        .setCountryShortName(postOnMarketplaceForm.getCountryCode());
                }
                break;
            default:
                LOG.error("Reached unreachable condition, businessType={}", postOnMarketplaceForm.getBusinessType());
                throw new IllegalStateException("Condition set is not defined");
        }
    }

    @SuppressWarnings("unused")
    public String completeNewPost(PostOnMarketplaceForm postOnMarketplaceForm) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Ready to post {}", postOnMarketplaceForm);
        switch (postOnMarketplaceForm.getBusinessType()) {
            case PR:
                PropertyEntity property = (PropertyEntity) postOnMarketplaceForm.getMarketplace();
                property.setBusinessType(postOnMarketplaceForm.getBusinessType())
                    .setQueueUserId(queueUser.getQueueUserId())
                    .setValidateByQid(
                        environment.getProperty("build.env").equalsIgnoreCase("prod")
                            ? userProfileManager.findOneByMail("beta@noqapp.com").getQueueUserId()
                            : "100000000002")
                    .setIpAddress(postOnMarketplaceForm.getIp());
                propertyManager.save(property);
                return "success";
            default:
                LOG.error("Reached unreachable condition, businessType={}", postOnMarketplaceForm.getBusinessType());
                throw new IllegalStateException("Condition set is not defined");
        }
    }
}
