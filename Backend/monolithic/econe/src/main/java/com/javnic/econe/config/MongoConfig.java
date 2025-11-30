package com.javnic.econe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.javnic.econe.repository")
@EnableMongoAuditing
public class MongoConfig {
    // MongoDB configuration is handled by application.yml
}