package com.ichong.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.ichong.annotation.RedissonRateLimit;
import com.ichong.exception.RedissonRateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: RedissonRateLimitAspect
 * Description:
 *
 * @author 陈高文
 * @date 2023/5/5 9:56
 */
@Slf4j
@Aspect
public class RedissonRateLimitAspect {

    @Autowired
    private RedissonClient redissonClient;


    @Before("@annotation(redissonRateLimit)")
    public void redissonRateLimitHandle(RedissonRateLimit redissonRateLimit) {

        if (ObjectUtil.isEmpty(redissonRateLimit) || ObjectUtil.isEmpty(redissonClient) || !redissonRateLimit.isEnable()) {
            //放行，不进行限流
            return;
        }
        log.info("限流器开始工作");
        //获取限流次数/限流时间/限流的key
        String interfaceName = redissonRateLimit.interfaceName();
        long count = redissonRateLimit.count();
        long timeout = redissonRateLimit.timeout();
        String key = redissonRateLimit.key();
        log.info("获取到的限流接口名:{},限流时间:{},限流次数:{},限流的主键key:{}", interfaceName,
                timeout, count, key);

        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //如果限流的key不存在，设置限流的key，限流时间，限流次数
        if (!rateLimiter.isExists()) {
            rateLimiter.trySetRate(RateType.OVERALL, count, timeout, RateIntervalUnit.MILLISECONDS);
            return;
        }
        //获取限流的配置信息，防止服务重启，限流配置更新
        RateLimiterConfig rateLimiterConfig = rateLimiter.getConfig();
        //之前配置的限流时间
        Long rateInterval = rateLimiterConfig.getRateInterval();
        //之前配置的限流次数
        Long rate = rateLimiterConfig.getRate();
        if (timeout != rateInterval || count != rate) {
            //删除之前的限流配置
            rateLimiter.delete();
            //重新配置新的限流配置
            rateLimiter.trySetRate(RateType.OVERALL, count, timeout, RateIntervalUnit.MILLISECONDS);
        }
        //判断限流是否触发
        // 为true则通过 ,未false则拦截
        boolean flag = rateLimiter.tryAcquire();
        if (!flag) {
            throw new RedissonRateLimitException("当前请求次数过多，请在" + ((timeout / 1000 / 60) < 1 ? 1 :
                    (timeout / 1000 / 60 + 1)) + "分钟后尝试！");
        }
    }
}

