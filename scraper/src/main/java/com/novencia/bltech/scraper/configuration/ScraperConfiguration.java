package com.novencia.bltech.scraper.configuration;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.novencia.bltech.api.dto.RedditMediaPostDto;
import com.novencia.bltech.scraper.batch.reader.deserializer.MediaPostDtoDeserializer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class ScraperConfiguration {
    @Bean
    public ObjectMapper configureObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        SimpleModule module = new SimpleModule("MediaPostDtoDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(RedditMediaPostDto.class, new MediaPostDtoDeserializer());
        objectMapper.registerModule(module);

        InjectableValues.Std injectables = new InjectableValues.Std();
        injectables.addValue(ObjectMapper.class, objectMapper);
        objectMapper.setInjectableValues(injectables);
        return objectMapper;
    }

    @Bean
    Encoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(HttpMessageConverters::new));
    }

    @Bean
    Decoder feignFormDecoder() {
        return new SpringDecoder(HttpMessageConverters::new);
    }
}
