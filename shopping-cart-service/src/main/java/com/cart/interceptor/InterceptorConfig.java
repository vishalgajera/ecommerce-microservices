package com.cart.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
/**
 * The Class InterceptorConfig.
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    /**
     * Adds the interceptors.
     *
     * @param registry the registry
     */
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor()).addPathPatterns("/**");
    }

    /**
     * Request interceptor.
     *
     * @return the request interceptor
     */
    @Bean
    public EntryInterceptor requestInterceptor() {
        return new EntryInterceptor();
    }
}
