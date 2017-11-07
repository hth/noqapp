package com.noqapp.health.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 11/07/17 10:13 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "HEALTH_SERVICE")
@CompoundIndexes({
        @CompoundIndex(name = "user_account_role_idx", def = "{'UID': 1, 'RE': 1}", unique = true),
        @CompoundIndex (name = "user_account_qid_idx", def = "{'QID': 1}", unique = true),
        @CompoundIndex (name = "user_account_uid_idx", def = "{'UID': 1}", unique = true)
})
public class ApiHealthEntity {
}
