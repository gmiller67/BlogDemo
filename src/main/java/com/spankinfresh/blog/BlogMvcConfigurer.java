package com.spankinfresh.blog;

import com.spankinfresh.blog.util.LocalDateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BlogMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addFormatter(localDateTimeFormatter());
    }

    @Bean
    public LocalDateTimeFormatter localDateTimeFormatter() {
        return new LocalDateTimeFormatter();
    }
}
