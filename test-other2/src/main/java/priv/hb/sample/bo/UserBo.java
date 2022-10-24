package priv.hb.sample.bo;

/**
 * @author hubin
 * @date 2022年03月10日 10:35 上午
 */
public class UserBo {
    private Integer id;
    private String name;
    private String password;

    public UserBo() {
    }

    public UserBo(Integer id, String username, String password) {
        this.id = id;
        this.name = username;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "UserBo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
