package com.campus.lostfound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class LostFoundApplication {

    public static void main(String[] args) {

        // Load .env file
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(e -> 
            System.setProperty(e.getKey(), e.getValue())
        );

        SpringApplication.run(LostFoundApplication.class, args);
    }
}