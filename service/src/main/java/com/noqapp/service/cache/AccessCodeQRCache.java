package com.noqapp.service.cache;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * hitender
 * 7/5/21 6:00 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Cacheable(cacheNames = "access-codeQR")
public @interface AccessCodeQRCache {
}
