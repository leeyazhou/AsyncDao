package com.tg.async.springsupport.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.tg.async.springsupport.config.AsyncDaoAutoConfiguration;
import com.tg.async.springsupport.mapper.AutoConfiguredMapperScannerRegistrar;

/**
 * Created by twogoods on 2018/8/27.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ AsyncDaoAutoConfiguration.class, AutoConfiguredMapperScannerRegistrar.class })
@Documented
public @interface EnableAsyncDao {
}
