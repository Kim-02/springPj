package com.dasolsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.dasolsystem.core.elasticrepository")
@EnableJpaRepositories(basePackages = "com.dasolsystem.core.jparepository")
public class dasolsystemApplication {
    public static void main(String[] args) {

        SpringApplication.run(dasolsystemApplication.class, args);
    }
}
