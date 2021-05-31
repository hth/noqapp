package com.noqapp.domain;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.helper.NameDatePair;
import com.noqapp.domain.json.JsonNameDatePair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 11/18/16 3:16 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public abstract class BaseEntity implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(BaseEntity.class);

    @Id
    protected String id;

    @Version
    @Field ("V")
    private Integer version;

    @Field ("U")
    private Date updated = new Date();

    @Field ("C")
    private Date created = new Date();

    @Field ("A")
    private boolean active = true;

    @Field ("D")
    private boolean deleted = false;

    public BaseEntity() {
        super();
    }

    /**
     * http://thierrywasyl.wordpress.com/2011/05/12/get-annotations-fields-value-easily/
     *
     * @param classType
     * @param annotationType
     * @param attributeName
     * @return Collection Name
     */
    @SuppressWarnings ("rawtypes")
    public static String getClassAnnotationValue(Class<?> classType, Class annotationType, String attributeName) {
        String value = null;

        @SuppressWarnings ("unchecked")
        Annotation annotation = classType.getAnnotation(annotationType);
        if (null != annotation) {
            try {
                value = (String) annotation.annotationType().getMethod(attributeName).invoke(annotation);
            } catch (Exception annotationException) {
                LOG.error("Annotation reading error=\"{}\"", annotationException.getLocalizedMessage(), annotationException);
            }
        }

        return value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    private void setActive(boolean active) {
        this.active = active;
    }

    public void active() {
        setActive(true);
    }

    public void inActive() {
        setActive(false);
    }

    public boolean isDeleted() {
        return deleted;
    }

    private void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void markAsDeleted() {
        setDeleted(true);
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @DateTimeFormat (iso = DateTimeFormat.ISO.NONE)
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated() {
        this.updated = new Date();
    }

    @DateTimeFormat (iso = DateTimeFormat.ISO.NONE)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setCreateAndUpdate(Date created) {
        this.created = created;
        this.updated = created;
    }

    @Transient
    protected List<JsonNameDatePair> getJsonNameDatePairs(List<NameDatePair> nameDatePairs) {
        List<JsonNameDatePair> jsonNameDatePairs = new ArrayList<>();
        if (null != nameDatePairs) {
            for (NameDatePair nameDatePair : nameDatePairs) {
                jsonNameDatePairs.add(new JsonNameDatePair()
                    .setName(nameDatePair.getName())
                    .setMonthYear(nameDatePair.getMonthYear()));
            }
        }

        return jsonNameDatePairs;
    }

    /** Used displaying on web page. */
    @SuppressWarnings("unused")
    public String createdAsPerBusinessTimeZone(String timeZone) {
        return DateUtil.convertDateToStringOf_DTF_DD_MMM_YYYY_HH_MM_SS(this.getCreated(), timeZone);
    }
}
