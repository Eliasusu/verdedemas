package com.eliasit.verdedemas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // Habilita @CreatedDate y @LastModifiedDate
}