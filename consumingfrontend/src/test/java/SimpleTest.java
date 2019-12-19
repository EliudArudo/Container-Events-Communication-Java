import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleTest {
    @DisplayName("Test Simple class.get()")
    @Test
    void testGet() {
        assertEquals("Hello JUnit 5", Simple.get());
    }
}
