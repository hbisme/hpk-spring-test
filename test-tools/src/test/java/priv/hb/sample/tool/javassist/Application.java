package priv.hb.sample.tool.javassist;

import java.lang.reflect.Method;

import static priv.hb.sample.tool.javassist.CreatePersion.createPerson;

/**
 * 通过反射来调用 javassist生成的类
 *
 * @author hubin
 * @date 2022年11月24日 09:49
 */
public class Application {

    public static void invoke() throws Exception {
        Object person = createPerson();

        Method printName = person.getClass().getMethod("printName");
        printName.invoke(person);

    }

    public static void main(String[] args) throws Exception {
        invoke();
    }
}
