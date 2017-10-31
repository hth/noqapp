package com.noqapp.view.form;

import com.noqapp.domain.BizNameEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 10/31/17 11:53 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class BusinessInviteForm {

    private List<BizNameEntity> bizNames;

    public List<BizNameEntity> getBizNames() {
        return bizNames;
    }

    public BusinessInviteForm setBizNames(List<BizNameEntity> bizNames) {
        this.bizNames = bizNames;
        return this;
    }
}
