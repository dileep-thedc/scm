package com.sylphcorps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SylphcorpsApplication {
	public static void main(String[] args) {
		SpringApplication.run(SylphcorpsApplication.class, args);
	}
}
