package com.goormthon.hero_home;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HeroHomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeroHomeApplication.class, args);
    }

}
