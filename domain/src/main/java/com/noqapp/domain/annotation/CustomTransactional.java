package com.noqapp.domain.annotation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add checked exception in RDBS transaction rollback.
 * User: hitender
 * Date: 6/6/17 8:39 PM
 */
@Target ({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention (RetentionPolicy.RUNTIME)
@Transactional (rollbackFor = Exception.class)
@Documented
public @interface CustomTransactional {
}