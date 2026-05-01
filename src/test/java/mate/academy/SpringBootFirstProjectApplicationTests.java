package mate.academy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = "spring.profiles.active=test")
@Testcontainers
class SpringBootFirstProjectApplicationTests {

    @Test
    void contextLoads() {
    }

}
