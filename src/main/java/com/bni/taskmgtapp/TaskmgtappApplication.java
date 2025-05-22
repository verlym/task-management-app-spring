package com.bni.taskmgtapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.bni.taskmgtapp")
public class TaskmgtappApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskmgtappApplication.class, args);
	}

}
