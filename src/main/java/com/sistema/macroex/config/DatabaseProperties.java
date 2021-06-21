package com.sistema.macroex.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Setter
@Getter
@ConfigurationProperties(prefix = "macroex")
public class DatabaseProperties {
	
    private String email;
    private String senha;;

}
