package com.noqapp.view.form.emp;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.site.JsonBusiness;

import java.util.List;

/**
 * hitender
 * 2/6/18 12:13 AM
 */
public class AccountAccessForm {
    private ScrubbedInput action;
    private ScrubbedInput id;

    private List<JsonBusiness> jsonBusinesses;
    private List<JsonBusiness> jsonBusinessesMatchingSearch;

    public ScrubbedInput getAction() {
        return action;
    }

    public AccountAccessForm setAction(ScrubbedInput action) {
        this.action = action;
        return this;
    }

    public ScrubbedInput getId() {
        return id;
    }

    public AccountAccessForm setId(ScrubbedInput id) {
        this.id = id;
        return this;
    }

    public List<JsonBusiness> getJsonBusinesses() {
        return jsonBusinesses;
    }

    public AccountAccessForm setJsonBusinesses(List<JsonBusiness> jsonBusinesses) {
        this.jsonBusinesses = jsonBusinesses;
        return this;
    }

    public List<JsonBusiness> getJsonBusinessesMatchingSearch() {
        return jsonBusinessesMatchingSearch;
    }

    public AccountAccessForm setJsonBusinessesMatchingSearch(List<JsonBusiness> jsonBusinessesMatchingSearch) {
        this.jsonBusinessesMatchingSearch = jsonBusinessesMatchingSearch;
        return this;
    }
}
