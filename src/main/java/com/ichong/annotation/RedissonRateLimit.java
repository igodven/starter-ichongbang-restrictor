package com.ichong.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: RedissonRateLimit
 * Description:
 *
 * @author 陈高文
 * @date 2023/5/5 9:36
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonRateLimit {

    //限流时存放在redis中key值
    String key();

    //限流的时间，单位毫秒
    long timeout();

    //在限流的时间内，允许通过的次数
    long count();

    boolean isEnable() default false;

    //限流接口名
    String interfaceName();

}
