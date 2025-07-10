package mockTest;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author hubin
 * @date 2022年08月04日 13:35
 */
@ExtendWith(MockitoExtension.class)
public class ListMockTest {
    @Test
    public void mockList0() {
        List mockedList  = mock(List.class);
        mockedList.add("one");
        mockedList.clear();

        // 验证mockedList上是否调用了add方法，并且传递的参数是字符串"one"。
        verify(mockedList).add("one");

        // 验证mockedList上是否调用了clear方法。
        verify(mockedList).clear();
    }


    @Test
    public void mockList() {
        List mockedList = mock(List.class);

        when(mockedList.get(anyInt())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                System.out.println("哈哈哈，被我逮到了吧");
                Object[] arguments = invocationOnMock.getArguments();
                System.out.println("参数为:" + Arrays.toString(arguments));
                Method method = invocationOnMock.getMethod();
                System.out.println("方法名为:" + method.getName());

                return "结果由我决定";
            }
        });

        System.out.println(mockedList.get(0));
    }



}
