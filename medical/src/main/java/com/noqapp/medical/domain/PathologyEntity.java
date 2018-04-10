package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 4/4/18 3:33 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "PATHOLOGY")
@CompoundIndexes(value = {
        @CompoundIndex(name = "pathology_idx", def = "{'NA' : 1}", unique = true),
})
public class PathologyEntity extends BaseEntity {

    @Field("NA")
    private String name;

    @Field("CA")
    private String category;

    @Field("DC")
    private String description;

    public String getName() {
        return name;
    }

    public PathologyEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public PathologyEntity setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PathologyEntity setDescription(String description) {
        this.description = description;
        return this;
    }
}
