package com.mocentre.tehui.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类Log.java的实现描述：系统日志记录注解，该注解用在需要记录操作日志的action中，使用Spring AOP结合该注解完成操作日志记录
 * 
 * @author sz.gong 2016年3月11日 下午3:57:04
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    String title() default "";//名称

    String value() default "";//操作值

    String description() default "";//描述

}
