package com.solactive;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
public class IndexingApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndexingApplication.class, args);
    }

}
