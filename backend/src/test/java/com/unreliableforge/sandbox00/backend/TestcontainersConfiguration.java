package com.unreliableforge.sandbox00.backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	MySQLContainer mysqlContainer() {
		DockerImageName mysqlImage = DockerImageName.parse("public.ecr.aws/docker/library/mysql:8.4")
				.asCompatibleSubstituteFor("mysql");
		return new MySQLContainer(mysqlImage);
	}

}
