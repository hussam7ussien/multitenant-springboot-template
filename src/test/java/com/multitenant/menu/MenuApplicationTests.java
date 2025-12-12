package com.multitenant.menu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = MenuApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
	"spring.jpa.hibernate.ddl-auto=validate",
	"spring.flyway.enabled=false"
})
class MenuApplicationTests {

	@Test
	void contextLoads() {
	}

}
