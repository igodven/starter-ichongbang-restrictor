package com.ichong.exception;

/**
 * ClassName: RedissonRateLimitException
 * Description:
 *
 * @author 陈高文
 * @date 2023/5/5 11:30
 */
public class RedissonRateLimitException extends RuntimeException {

    public RedissonRateLimitException() {
        super();
    }

    public RedissonRateLimitException(String message) {
        super(message);
    }

    public RedissonRateLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedissonRateLimitException(Throwable cause) {
        super(cause);
    }

    protected RedissonRateLimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
