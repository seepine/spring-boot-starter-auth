package com.seepine.auth.util;

import cn.hutool.core.util.IdUtil;
import com.seepine.auth.entity.AuthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author seepine
 */
@Component
@DependsOn({"redisTemplate", "authProperties"})
public class AuthUtil {
    private static AuthUtil authUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private AuthProperties authProperties;

    private final ThreadLocal<Object> THREAD_LOCAL_TENANT = new ThreadLocal<>();
    private final ThreadLocal<String> THREAD_LOCAL_TOKEN = new ThreadLocal<>();


    private AuthUtil() {
    }

    @PostConstruct
    public void init() {
        authUtil = this;
        authUtil.redisTemplate = this.redisTemplate;
        authUtil.authProperties = this.authProperties;

    }

    /**
     * 在controller/service中使用，直接获取当前登录者用户信息
     *
     * @param <T> 范型
     * @return user
     */
    public static <T> T getUser() {
        return (T) authUtil.THREAD_LOCAL_TENANT.get();
    }

    /**
     * 登录成功后设置用户信息，并返回token
     *
     * @param user user
     * @return token
     */
    public static String loginSuccess(Object user) {
        String token = IdUtil.fastSimpleUUID();
        putIntoCache(token, user);
        // 设置过期时间
        authUtil.redisTemplate.expire(authUtil.authProperties.cacheKey() + token,
                authUtil.authProperties.timeout(), authUtil.authProperties.unit());
        return token;
    }

    /**
     * 通过token获取用户信息
     *
     * @param token token
     * @param <T> 范型
     * @return user
     */
    public static <T> T getUserByToken(String token) {
        Object user = authUtil.redisTemplate.opsForHash().get(authUtil.authProperties.cacheKey() + token, token);
        authUtil.THREAD_LOCAL_TENANT.set(user);
        authUtil.THREAD_LOCAL_TOKEN.set(token);
        return (T) user;
    }

    /**
     * 刷新用户信息,当进行了更新等操作
     *
     * @param user user
     * @return boolean
     */
    public static boolean refreshUser(Object user) {
        String token = authUtil.THREAD_LOCAL_TOKEN.get();
        if (token == null || "".equals(token)) {
            return false;
        }
        putIntoCache(token, user);
        return true;
    }

    private static void putIntoCache(String token, Object user) {
        authUtil.THREAD_LOCAL_TENANT.set(user);
        authUtil.redisTemplate.opsForHash().put(authUtil.authProperties.cacheKey() + token, token, user);
    }
}
