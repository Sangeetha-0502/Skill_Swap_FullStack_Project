package com.example.skill_swap.skill_swap_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.skill_swap.skill_swap_project")
public class SkillSwapProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillSwapProjectApplication.class, args);
	}

}
