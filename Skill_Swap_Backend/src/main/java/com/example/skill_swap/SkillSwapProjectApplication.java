package com.example.skill_swap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.skill_swap")
public class SkillSwapProjectApplication {
	public static void main(String[] args) {
		SpringApplication.run(SkillSwapProjectApplication.class, args);
	}
}
