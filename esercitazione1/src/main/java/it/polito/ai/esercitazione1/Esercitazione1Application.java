package it.polito.ai.esercitazione1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.HashMap;

@SpringBootApplication
public class Esercitazione1Application {

    public static void main(String[] args) {
        SpringApplication.run(Esercitazione1Application.class, args);
    }

    @Bean
    public ConcurrentMap<String, User> createMap() {
        return new ConcurrentHashMap<>();
    }

}
