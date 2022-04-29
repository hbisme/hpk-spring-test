import com.test.mybatis.dao.entity.TRedpillTag;
import com.test.mybatis.dao.mappers.TRedpillTagMapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.test.mybatis.utils.MyBatisUtil.getSession;

/**
 * @author hubin
 * @date 2022年03月08日 10:58 上午
 */
public class AppTest {

    @Test
    public void test1() throws IOException {

        final SqlSession sqlSession = getSession();

        try {
            TRedpillTagMapper mapper = sqlSession.getMapper(TRedpillTagMapper.class);
            TRedpillTag tRedpillTag = mapper.getById(3);
            System.out.println(mapper.getClass());
            System.out.println(tRedpillTag);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test2() {
        final SqlSession sqlSession = getSession();
        final TRedpillTagMapper mapper = sqlSession.getMapper(TRedpillTagMapper.class);
        final List<TRedpillTag> all = mapper.getAll();
        System.out.println(all);


    }
}
