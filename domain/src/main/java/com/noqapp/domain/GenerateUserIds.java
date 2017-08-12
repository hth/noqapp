package com.noqapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 11/19/16 12:32 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "GENERATE_USER_IDS")
public class GenerateUserIds {
    public static final long STARTING_USER_ID = 100_000_000_000L;

    @Id
    private final String className;

    @Field ("QID")
    private long autoGeneratedQueueUserId = STARTING_USER_ID;

    private GenerateUserIds(String className) {
        this.className = className;
    }

    public static GenerateUserIds newInstance() {
        return new GenerateUserIds(GenerateUserIds.class.getName());
    }

    public long getAutoGeneratedQueueUserId() {
        return autoGeneratedQueueUserId;
    }
}
