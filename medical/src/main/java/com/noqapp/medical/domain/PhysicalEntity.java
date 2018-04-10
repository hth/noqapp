package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 4/4/18 9:34 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "PHYSICAL")
@CompoundIndexes(value = {
        @CompoundIndex(name = "physical_idx", def = "{'NA' : 1}", unique = true),
})
public class PhysicalEntity extends BaseEntity {

    @Field("NA")
    private String name;

    @Field("NR")
    private String[] normalRange = new String[2];

    @Field("DC")
    private String description;

    public String getName() {
        return name;
    }

    public PhysicalEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String[] getNormalRange() {
        return normalRange;
    }

    public PhysicalEntity setNormalRange(String[] normalRange) {
        this.normalRange = normalRange;
        return this;
    }

    public PhysicalEntity addNormalRange(String min, String max) {
        this.normalRange[0] = min;
        this.normalRange[1] = max;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PhysicalEntity setDescription(String description) {
        this.description = description;
        return this;
    }
}
