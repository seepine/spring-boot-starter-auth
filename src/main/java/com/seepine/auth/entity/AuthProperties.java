package com.seepine.auth.entity;

import java.util.concurrent.TimeUnit;

/**
 * auth参数类
 *
 * @author seepine
 */
public class AuthProperties {
    String header = "token";
    String cacheKey = "sm_auth:";
    long timeout = 5;
    TimeUnit unit = TimeUnit.DAYS;

    public AuthProperties header(String header) {
        this.header = header;
        return this;
    }

    public AuthProperties cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    public AuthProperties timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public AuthProperties timeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    public String header() {
        return header;
    }

    public String cacheKey() {
        return cacheKey;
    }

    public long timeout() {
        return timeout;
    }

    public TimeUnit unit() {
        return unit;
    }

    @Override
    public String toString() {
        return "AuthProperties{" +
                "header='" + header + '\'' +
                ", cacheKey='" + cacheKey + '\'' +
                ", timeout=" + timeout +
                ", unit=" + unit +
                '}';
    }
}
