package com.flowbot.application.configs;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@AllArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("HEAD,GET,POST,PUT,DELETE,PATCH,OPTIONS".split(","))
                .allowedHeaders(("Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,"
                        + "Access-Control-Request-Headers,Authorization,"
                        + "Filter-Encoded").split(","));
    }
}

