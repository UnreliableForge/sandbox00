package com.unreliableforge.sandbox00.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class Backend00ApplicationTests {

	@Test
	void contextLoads() {
	}

}
