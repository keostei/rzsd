package com.rzsd.wechat.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.rzsd.wechat.interceptor.LoginInterceptor;

@Configuration
public class RzWebAppConfigurer extends WebMvcConfigurerAdapter {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// excludePathPatterns 用户排除拦截
		registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/login");
		super.addInterceptors(registry);
	}
}
