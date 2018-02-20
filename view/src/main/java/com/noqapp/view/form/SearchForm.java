package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.search.elastic.helper.GeoIP;

/**
 * hitender
 * 2/7/18 11:58 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class SearchForm {
    private ScrubbedInput search;
    private GeoIP geoIP;

    public ScrubbedInput getSearch() {
        return search;
    }

    public SearchForm setSearch(ScrubbedInput search) {
        this.search = search;
        return this;
    }

    public GeoIP getGeoIP() {
        return geoIP;
    }

    public SearchForm setGeoIP(GeoIP geoIP) {
        this.geoIP = geoIP;
        return this;
    }
}
