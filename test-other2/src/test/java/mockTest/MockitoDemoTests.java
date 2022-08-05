package mockTest;

import com.hb.mockservice.User;
import com.hb.mockservice.UserMapper;
import com.hb.mockservice.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author hubin
 * @date 2022年08月04日 11:08
 */

@RunWith(MockitoJUnitRunner.class)
public class MockitoDemoTests {

    @Mock
    private List list;


    @Before
    public void before() {
        Mockito.when(list.get(Mockito.anyInt())).thenReturn(100);
    }


    @Test
    public void shouldDoSomething() {
        System.out.println(list.get(12));
    }



}
