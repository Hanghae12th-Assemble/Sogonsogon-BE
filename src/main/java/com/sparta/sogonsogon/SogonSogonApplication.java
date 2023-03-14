package com.sparta.sogonsogon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SogonSogonApplication  {

    public static void main(String[] args) {
        SpringApplication.run(SogonSogonApplication.class, args);
    }

}
