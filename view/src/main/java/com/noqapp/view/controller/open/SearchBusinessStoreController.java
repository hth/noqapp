package com.noqapp.view.controller.open;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.search.elastic.helper.GeoIP;
import com.noqapp.search.elastic.json.ElasticBizStoreSource;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.search.elastic.service.GeoIPLocationService;
import com.noqapp.view.form.SearchForm;
import com.noqapp.view.form.business.CategoryLandingForm;
import com.noqapp.view.util.HttpRequestResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * hitender
 * 2/19/18 11:04 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/open/search")
public class SearchBusinessStoreController {
    private static final Logger LOG = LoggerFactory.getLogger(SearchBusinessStoreController.class);

    private BizStoreElasticService bizStoreElasticService;
    private GeoIPLocationService geoIPLocationService;

    private String nextPage;

    @Autowired
    public SearchBusinessStoreController(
            @Value("${nextPage:/search}")
            String nextPage,

            BizStoreElasticService bizStoreElasticService,
            GeoIPLocationService geoIPLocationService
    ) {
        this.nextPage = nextPage;

        this.bizStoreElasticService = bizStoreElasticService;
        this.geoIPLocationService = geoIPLocationService;
    }

    @GetMapping
    public String search(
            @ModelAttribute("searchForm")
            SearchForm searchForm,

            HttpServletRequest request
    ) {
        String ipAddress = HttpRequestResponseParser.getClientIpAddress(request);
        searchForm.setGeoIP(geoIPLocationService.getLocation(ipAddress));
        return nextPage;
    }

    @PostMapping(produces = "application/json")
    @ResponseBody
    public List<ElasticBizStoreSource> search(
            @ModelAttribute("searchForm")
            SearchForm searchForm
    ) {
        return bizStoreElasticService.createBizStoreSearchDSLQuery(searchForm.getSearch().getText(), searchForm.getGeoIP().getGeoHash());
    }
}
