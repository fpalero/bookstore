package com.app.bookstore.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    // This class is a placeholder for the configuration of the mapper in the
    // bookstore application.
    // It can be used to define mapping rules and configurations for converting
    // between different object types.

    @Bean
    public ModelMapper bookStoreModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Add any custom configurations or mappings here if needed
        return modelMapper;
    }

}