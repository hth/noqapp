package com.noqapp.view.controller.open;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.search.elastic.json.ElasticBizStoreSearchSource;
import com.noqapp.search.elastic.json.ElasticMarketplaceSearchSource;
import com.noqapp.search.elastic.service.BizStoreSearchElasticService;
import com.noqapp.search.elastic.service.GeoIPLocationService;
import com.noqapp.search.elastic.service.MarketplaceSearchElasticService;
import com.noqapp.view.form.SearchForm;
import com.noqapp.view.util.HttpRequestResponseParser;
import com.noqapp.view.validator.SearchValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * hitender
 * 2/19/18 11:04 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/open/search")
public class SearchBusinessStoreController {
    private static final Logger LOG = LoggerFactory.getLogger(SearchBusinessStoreController.class);

    private BizStoreSearchElasticService bizStoreSearchElasticService;
    private MarketplaceSearchElasticService marketplaceSearchElasticService;
    private GeoIPLocationService geoIPLocationService;
    private SearchValidator searchValidator;

    private String nextPage;

    @Autowired
    public SearchBusinessStoreController(
        @Value("${nextPage:/search}")
        String nextPage,

        BizStoreSearchElasticService bizStoreSearchElasticService,
        MarketplaceSearchElasticService marketplaceSearchElasticService,
        GeoIPLocationService geoIPLocationService,
        SearchValidator searchValidator
    ) {
        this.nextPage = nextPage;

        this.bizStoreSearchElasticService = bizStoreSearchElasticService;
        this.marketplaceSearchElasticService = marketplaceSearchElasticService;
        this.geoIPLocationService = geoIPLocationService;
        this.searchValidator = searchValidator;
    }

    @GetMapping
    public String search(
        @ModelAttribute("searchForm")
        SearchForm searchForm,

        Model model,
        HttpServletRequest request
    ) {
        String ipAddress = HttpRequestResponseParser.getClientIpAddress(request);
        searchForm.setGeoIP(geoIPLocationService.getLocation(ipAddress));

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.searchForm", model.asMap().get("result"));
            searchForm.setSearch((ScrubbedInput) model.asMap().get("search"));
        } else {
            if (model.asMap().containsKey("search")) {
                searchForm.setSearch(((SearchForm) model.asMap().get("search")).getSearch());
                model.addAttribute(
                    "bizStoreSearchResult",
                    bizStoreSearchElasticService.createBizStoreSearchDSLQuery(
                        searchForm.getSearch().getText(),
                        searchForm.getGeoIP().getGeoHash()));

                model.addAttribute(
                    "marketplaceSearchResult",
                    marketplaceSearchElasticService.createMarketplaceSearchDSLQuery(
                        searchForm.getSearch().getText(),
                        searchForm.getGeoIP().getGeoHash()));
            }
        }

        LOG.info("ip={} cityName={}", ipAddress, searchForm.getGeoIP().getCityName());
        return nextPage;
    }

    @PostMapping
    public String search(
        @ModelAttribute("searchForm")
        SearchForm searchForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        searchValidator.validate(searchForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:/open/search.htm";
        }
        redirectAttrs.addFlashAttribute("search", searchForm);
        return "redirect:/open/search.htm";
    }
}
