package com.mocentre.tehui.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类Token.java的实现描述：表格重复提交token
 * 
 * @author sz.gong 2016年4月1日 上午10:18:11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {

    boolean save() default false;

    boolean remove() default false;
}
