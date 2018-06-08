package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;

import java.util.HashMap;
import java.util.Map;

/**
 * hitender
 * 1/29/18 4:47 AM
 */
public class WebJoinQueueForm {
    private ScrubbedInput codeQR;
    private Map<String, Object> rootMap = new HashMap<>();
    private ScrubbedInput uid;
    private ScrubbedInput phone;

    public ScrubbedInput getCodeQR() {
        return codeQR;
    }

    public WebJoinQueueForm setCodeQR(ScrubbedInput codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public Map<String, Object> getRootMap() {
        return rootMap;
    }

    public WebJoinQueueForm setRootMap(Map<String, Object> rootMap) {
        this.rootMap = rootMap;
        return this;
    }

    public ScrubbedInput getUid() {
        return uid;
    }

    public WebJoinQueueForm setUid(ScrubbedInput uid) {
        this.uid = uid;
        return this;
    }

    public ScrubbedInput getPhone() {
        return phone;
    }

    public WebJoinQueueForm setPhone(ScrubbedInput phone) {
        this.phone = phone;
        return this;
    }
}
