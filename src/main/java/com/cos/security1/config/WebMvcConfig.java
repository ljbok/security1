package com.cos.security1.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    // dependecy 로 추가되어 디폴트 뷰리졸버로 설정되어 있는 .mustache 대신에 .html을 뷰리졸버로 사용하기 위한 설정
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MustacheViewResolver resolver = new MustacheViewResolver();
        resolver.setCharset("UTF-8");
        resolver.setContentType("text/html; charset=UTF-8");
        resolver.setPrefix("classpath:/templates/"); //classpath:/ 까지가 프로젝트 경로
        resolver.setSuffix(".html");

        registry.viewResolver(resolver);
    }
}
