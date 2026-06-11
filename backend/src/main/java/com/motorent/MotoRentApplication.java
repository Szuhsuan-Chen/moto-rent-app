package com.motorent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
@SpringBootApplication
public class MotoRentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotoRentApplication.class, args);
    }
}
