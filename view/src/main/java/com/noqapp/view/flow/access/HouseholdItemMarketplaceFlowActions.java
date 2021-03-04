package com.noqapp.view.flow.access;

import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.domain.shared.Geocode;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.market.HouseholdItemManager;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.service.MarketplaceElasticService;
import com.noqapp.service.ExternalService;
import com.noqapp.view.form.marketplace.HouseholdItemMarketplaceForm;
import com.noqapp.view.form.marketplace.MarketplaceForm;
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
 * 2/24/21 4:05 PM
 */
@Component
public class HouseholdItemMarketplaceFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(HouseholdItemMarketplaceFlowActions.class);

    private Environment environment;
    private DatabaseReader databaseReader;
    private HouseholdItemManager householdItemManager;
    private UserProfileManager userProfileManager;
    private MarketplaceElasticService marketplaceElasticService;
    private ExternalService externalService;

    @Autowired
    public HouseholdItemMarketplaceFlowActions(
        Environment environment,
        DatabaseReader databaseReader,
        HouseholdItemManager householdItemManager,
        UserProfileManager userProfileManager,
        MarketplaceElasticService marketplaceElasticService,
        ExternalService externalService
    ) {
        this.environment = environment;
        this.databaseReader = databaseReader;
        this.householdItemManager = householdItemManager;
        this.userProfileManager = userProfileManager;
        this.marketplaceElasticService = marketplaceElasticService;
        this.externalService = externalService;
    }

    @SuppressWarnings("unused")
    public MarketplaceForm startOfNewPost(String postId, String businessTypeAsString, boolean postingAllowed, ExternalContext externalContext) {
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
            LOG.info("Accuracy radius {} {} {} {}", ip, location.getAccuracyRadius(), city, countryCode);
        } catch (AddressNotFoundException e) {
            LOG.warn("Failed finding ip={} reason={}", ip, e.getLocalizedMessage());
        } catch (GeoIp2Exception e) {
            LOG.error("Failed geoIp reason={}", e.getLocalizedMessage(), e);
        } catch (UnknownHostException e) {
            LOG.error("Failed host reason={}", e.getLocalizedMessage(), e);
        } catch (IOException e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }

        MarketplaceForm marketplaceForm = new HouseholdItemMarketplaceForm()
            .setIp(ip)
            .setCountryCode(countryCode)
            .setCity(city)
            .setPostingAllowed(postingAllowed);

        if (null != location) {
            marketplaceForm.setCoordinate(new double[]{location.getLongitude(), location.getLatitude()});
        }

        if (StringUtils.isNotBlank(postId)) {
            switch (BusinessTypeEnum.valueOf(businessTypeAsString)) {
                case HI:
                    HouseholdItemEntity property = householdItemManager.findOneById(postId);
                    marketplaceForm.setMarketplace(property)
                        .setBusinessType(property.getBusinessType())
                        .setCity(property.getCity())
                        .setCoordinate(marketplaceForm.getCoordinate());
                    break;
                default:
                    LOG.error("Reached unreachable condition, businessType={}", marketplaceForm.getBusinessType());
                    throw new IllegalStateException("Condition set is not defined");
            }
        } else {
            marketplaceForm.setBusinessType(BusinessTypeEnum.valueOf(businessTypeAsString));
        }

        return marketplaceForm;
    }

    /** After business type is selected set the entity. */
    @SuppressWarnings("unused")
    public void afterPostTypeHasBeenSelected(HouseholdItemMarketplaceForm marketplaceForm) {
        switch (marketplaceForm.getBusinessType()) {
            case HI:
                if (null == marketplaceForm.getMarketplace()) {
                    marketplaceForm.setMarketplace(
                        new HouseholdItemEntity()
                            .setCity(marketplaceForm.getCity())
                            .setCountryShortName(marketplaceForm.getCountryCode()));
                } else {
                    marketplaceForm.getMarketplace()
                        .setCity(marketplaceForm.getCity())
                        .setCountryShortName(marketplaceForm.getCountryCode());
                }
                break;
            default:
                LOG.error("Reached unreachable condition, businessType={}", marketplaceForm.getBusinessType());
                throw new IllegalStateException("Condition set is not defined");
        }
    }

    @SuppressWarnings("unused")
    public String completeNewPost(HouseholdItemMarketplaceForm marketplaceForm) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Ready to post {}", marketplaceForm);
        switch (marketplaceForm.getBusinessType()) {
            case HI:
                HouseholdItemEntity marketplace = (HouseholdItemEntity) marketplaceForm.getMarketplace();
                boolean isUserProfileVerified = userProfileManager.isProfileVerified(queueUser.getQueueUserId());
                marketplace.setBusinessType(marketplaceForm.getBusinessType())
                    .setQueueUserId(queueUser.getQueueUserId())
                    .setIpAddress(marketplaceForm.getIp());

                if (userProfileManager.isProfileVerified(queueUser.getQueueUserId())) {
                    marketplace.setValidateByQid(
                        environment.getProperty("build.env").equalsIgnoreCase("prod")
                            ? userProfileManager.findOneByMail("beta@noqapp.com").getQueueUserId()
                            : "100000000002");
                }

                Geocode geocode;
                if (StringUtils.isNotBlank(marketplace.getAddress())) {
                    geocode = Geocode.newInstance(externalService.getGeocodingResults(marketplace.getAddress()), marketplace.getAddress());
                } else {
                    geocode = Geocode.newInstance(externalService.getGeocodingResults(marketplace.getTown() + " " + marketplace.getCity()), marketplace.getTown() + " " + marketplace.getCity());
                }
                DecodedAddress decodedAddress = DecodedAddress.newInstance(geocode.getResults(), 0);
                marketplace.setCoordinate(decodedAddress.getCoordinate())
                    .setCountryShortName(decodedAddress.getCountryShortName());

                householdItemManager.save(marketplace);
                marketplaceElasticService.save(DomainConversion.getAsMarketplaceElastic(marketplace));
                return "success";
            default:
                LOG.error("Reached unreachable condition, businessType={}", marketplaceForm.getBusinessType());
                throw new IllegalStateException("Condition set is not defined");
        }
    }
}
