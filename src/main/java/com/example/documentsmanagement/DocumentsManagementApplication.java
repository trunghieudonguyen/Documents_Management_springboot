package com.example.documentsmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DocumentsManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocumentsManagementApplication.class, args);
    }
}
    