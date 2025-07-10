package mockTest;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lombok.Data;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author hubin
 * @date 2022年08月02日 17:26
 */
public class MockitoBaseTest1 {

    /**
     * 测试mock参数匹配
     */
    @Test
    public void testArguments() {
        B b = Mockito.mock(B.class);
        Mockito.when(b.getSex(1)).thenReturn("男");
        Mockito.when(b.getSex(2)).thenReturn("女");
        Assert.assertEquals("男", b.getSex(1));
        Assert.assertEquals("男", b.getSex(1));
    }

    /**
     * 测试匹配任意参数
     */
    @Test
    public void test5() {
        List list = Mockito.mock(List.class);
        Mockito.when(list.get(Mockito.anyInt())).thenReturn(1);
        Assert.assertEquals(1, list.get(100));
        Assert.assertEquals(1, list.get(101));
    }

    /**
     * 测试mock 多次触发返回不同值
     */
    @Test
    public void test2() {
        //mock一个Iterator类
        Iterator<String> iterator = Mockito.mock(Iterator.class);

        //预设当iterator调用next()时第一次返回hello，第n次都返回world
        Mockito.when(iterator.next()).thenReturn("hello")
                .thenReturn("world");

        //使用mock的对象
        String result = iterator.next() + " " + iterator.next() + " " + iterator.next();
        System.out.println(result);
        Assert.assertEquals("hello world world", result);
    }


    /**
     * 模拟抛出异常
     *
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void test3() throws IOException {
        OutputStream mock = Mockito.mock(OutputStream.class);
        //预设当流关闭时抛出异常
        Mockito.doThrow(new IOException()).when(mock).close();
        mock.close();
    }


    /**
     * 测试mock嵌套对象
     */
    @Test
    public void deepstubsTest2() {
        A a = Mockito.mock(A.class);
        B b = Mockito.mock(B.class);
        Mockito.when(a.getB()).thenReturn(b);
        Mockito.when(b.getName()).thenReturn("Beijing");
        Assert.assertEquals("Beijing", a.getB().getName());
    }



    @Test
    public void test6() {
        LinkedList mockedList = mock(LinkedList.class);

        //stubbing
        // 测试桩
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());

        //following prints "first"
        // 输出“first”
        System.out.println(mockedList.get(0));

        //following throws runtime exception
        // 抛出异常
        System.out.println(mockedList.get(1));

    }



    @Data
    class A {
        private B b;
    }

    @Data
    class B {
        private String name;

        public String getSex(Integer sex) {
            if (sex == 1) {
                return "man";
            } else {
                return "woman";
            }
        }
    }


}
