package pl.miquido.java.recruitmenttask;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;

@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=true",
        "sw.character.height.minimum=160"
})
public class BaseIntegrationTests {

    private static MySQLContainer container = new MySQLContainer<>("mysql:8")
            .withReuse(true);

    @BeforeAll
    static void initialSetup() {
        container.start();

        Thread stopTestContainer = new Thread(() -> container.close());
        Runtime.getRuntime().addShutdownHook(stopTestContainer);
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

}
