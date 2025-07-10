package junit5;

import org.junit.jupiter.api.*;


/**
 * @author hubin
 * @date 2024年09月08日 15:33
 */
@DisplayName("JUnit 5 示例测试类")
public class ExampleTest {
    @BeforeAll
    static void initAll() {
        System.out.println("初始化...");
    }

    @BeforeEach
    void init() {
        System.out.println("开始测试...");
    }

    @Test
    @DisplayName("第一个简单测试")
    void simpleTest() {
        Assertions.assertTrue(true);
    }

    @AfterEach
    void tearDown() {
        System.out.println("测试结束...");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("清理...");
    }
}
