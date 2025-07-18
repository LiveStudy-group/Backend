package org.livestudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LivestudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LivestudyApplication.class, args);
    }

}
