package com.rupeng.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在类上，控制这个类的方法是否可使用@RupengUseCache 或@RupengClearCache
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RupengCacheable {

    int expire() default 600;//缓存过期时间，单位秒，默认60
}
