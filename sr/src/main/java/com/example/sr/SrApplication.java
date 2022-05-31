package com.example.sr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.sr.repo.UserRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class SrApplication {

	public static void main(String[] args) {
		SpringApplication.run(SrApplication.class, args);
	}

}
