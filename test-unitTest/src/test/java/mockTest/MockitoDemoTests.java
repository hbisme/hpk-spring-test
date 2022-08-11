package mockTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

/**
 * @author hubin
 * @date 2022年08月04日 11:08
 */

@RunWith(MockitoJUnitRunner.class)
public class MockitoDemoTests {

    @Mock
    private List list;


    /**
     * 使用前要先设置,不然会null
     */
    @Before
    public void before() {
        Mockito.when(list.get(Mockito.anyInt())).thenReturn(100);
    }


    @Test
    public void shouldDoSomething() {
        System.out.println(list.get(12));
    }



}
