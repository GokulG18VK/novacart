package com.novacart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NovaCart Backend — Main Spring Boot Application Entry Point.
 * Run this class to start the embedded Tomcat server on port 8080.
 *
 * Uses Spring JDBC (JdbcTemplate) to connect to MySQL — no JPA/Hibernate.
 */
@SpringBootApplication
public class NovaCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(NovaCartApplication.class, args);
        System.out.println("=========================================");
        System.out.println(" NovaCart Backend running on port 8080");
        System.out.println(" API Base URL: http://localhost:8080/api");
        System.out.println("=========================================");
    }
}
