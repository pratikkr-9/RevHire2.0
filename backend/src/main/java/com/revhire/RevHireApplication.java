package com.revhire;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RevHireApplication {

    private static final Logger logger = LogManager.getLogger(RevHireApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RevHireApplication.class, args);
        logger.info("RevHire Application started successfully!");
        logger.info("API available at: http://localhost:8080/api");
    }
}
