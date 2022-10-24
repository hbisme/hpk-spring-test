package priv.hb.sample.mockservice;

/**
 * @author hubin
 * @date 2022年08月04日 11:03
 */
public interface UserMapper
{
    User selectOne(Integer id);

    void print();
}
