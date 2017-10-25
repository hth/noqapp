package com.noqapp.domain.annotation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * Add checked exception in RDBS transaction rollback.
 * User: hitender
 * Date: 6/6/17 8:39 PM
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Transactional(rollbackFor = Exception.class)
@Documented
public @interface CustomTransactional {
}
