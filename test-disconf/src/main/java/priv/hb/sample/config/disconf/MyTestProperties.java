package priv.hb.sample.config.disconf;


import com.hipac.disconf.client.common.annotations.DisconfFile;
import com.hipac.disconf.client.common.annotations.DisconfFileItem;

import org.springframework.stereotype.Service;

@Service
@DisconfFile(filename = "myTest.properties")   // disconf key的名称,在disconf上创建时选"新建配置文件",要以properties结尾
public class MyTestProperties {
    private String name;
    private Integer age;

    @DisconfFileItem(name ="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DisconfFileItem(name ="age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "MyTestProperties{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
