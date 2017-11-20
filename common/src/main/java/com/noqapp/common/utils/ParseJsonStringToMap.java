package com.noqapp.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 11/18/16 6:56 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ParseJsonStringToMap {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ParseJsonStringToMap() {
    }

    public static Map<String, ScrubbedInput> jsonStringToMap(String ids) throws IOException {
        return OBJECT_MAPPER.readValue(ids, new TypeReference<HashMap<String, ScrubbedInput>>() {
            //Blank
        });
    }
}

