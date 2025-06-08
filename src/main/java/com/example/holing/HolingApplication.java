package com.example.holing;

import com.example.holing.base.config.GoogleProperties;
import com.example.holing.bounded_context.auth.config.KakaoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties({KakaoProperties.class, GoogleProperties.class})
public class HolingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolingApplication.class, args);
    }

}
