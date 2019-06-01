package com.rupeng.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用此注解的方法会使用缓存（前提是此方法所属的类标注了@RupengCacheable）
 * 在执行方法内代码之前，会先从缓存中尝试获取数据，如果获取到了，直接返回获取的数据，
 * 如果没有获取到数据，则执行方法进行查询，然后再把查询到的数据放入缓存
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface RupengUseCache {

}
