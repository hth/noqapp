package com.noqapp.service.cache;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * hitender
 * 7/4/21 1:38 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Cacheable(cacheNames = "flexAppointment", key = "{#bizStore.codeQR, #scheduleDate}")
public @interface FlexAppointmentCache {
}
