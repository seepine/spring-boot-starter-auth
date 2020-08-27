package com.seepine.auth.config;

import com.seepine.auth.entity.AuthProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * auth配置类
 * <p>
 * 如想自定义各种参数，自己注入authProperties的bean，
 * 类上加@AutoConfigureBefore(AuthAutoConfigurer.class)
 * <p>
 *
 * @author seepine
 */
@Configuration
public class AuthAutoConfigurer {

    @Bean
    @ConditionalOnMissingBean(AuthProperties.class)
    public AuthProperties authProperties() {
        return new AuthProperties();
    }
}
