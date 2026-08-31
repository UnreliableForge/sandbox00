package com.unreliableforge.sandbox00.backend;

import org.springframework.boot.SpringApplication;

public class TestBackend00Application {

	public static void main(String[] args) {
		SpringApplication.from(BackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
