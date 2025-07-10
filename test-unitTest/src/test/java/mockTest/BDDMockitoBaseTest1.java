package mockTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

/**
 * Mockito的BDD风格的测试
 * @author hubin
 * @date 2024年02月05日 20:30
 */
public class BDDMockitoBaseTest1 {

    /**
     * mock返回结果为其他值
     */
    @Test
    public void test1() {
        MyService myService = mock(MyService.class);
        given(myService.doSomething()).willReturn("other Value");
        String result = myService.doSomething();

        System.out.println(result);

        // 验证doSomething方法被执行了1次
        then(myService).should(times(1)).doSomething();
        Assert.assertEquals("other Value", result);
    }

    @Test
            // (expected = HelloWorldException.class)
    public void test2() {
        MyService myService = mock(MyService.class);

        given(myService.doSomething()).willThrow(new HelloWorldException());
        myService.doSomething();

    }





    class MyService {
        public String doSomething() {
             return "hello BDDMock";
        }
    }

    class HelloWorldException extends Exception {
        public HelloWorldException() {
            super("Hello World Exception");
        }
    }
}
