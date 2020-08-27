package com.seepine.auth.interceptor;


import com.seepine.auth.annotation.Expose;
import com.seepine.auth.annotation.Login;
import com.seepine.auth.annotation.NotExpose;
import com.seepine.auth.entity.AuthProperties;
import com.seepine.auth.exception.AuthException;
import com.seepine.auth.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author seepine
 */
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    AuthProperties authProperties;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (hasAnnotation(handler, Login.class)) {
            return true;
        }
        String token = httpServletRequest.getHeader(authProperties.header());
        Object user = null;
        if (token != null && !"".equals(token)) {
            user = AuthUtil.getUserByToken(token);
        }
        //没有保护并且有暴露,不拦截
        if (!hasAnnotation(handler, NotExpose.class) && hasAnnotation(handler, Expose.class)) {
            return true;
        }
        //有保护,或者没有暴露,需要拦截
        else if (token == null || "".equals(token)) {
            throw new AuthException("invalid token");
        } else if (user == null) {
            throw new AuthException("not auth");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }

    /**
     * 方法上有注解或类上有注解
     *
     * @param handler
     * @param annotationClass
     * @return
     */
    private boolean hasAnnotation(Object handler, Class<? extends Annotation> annotationClass) {
        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            return method.isAnnotationPresent(annotationClass) || method.getDeclaringClass().isAnnotationPresent(annotationClass);
        } catch (Exception ignored) {
            return false;
        }
    }
}
