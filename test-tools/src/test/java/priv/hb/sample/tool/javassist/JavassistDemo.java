package priv.hb.sample.tool.javassist;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

/**
 * @author hubin
 * @date 2022年11月24日 10:09
 */
public class JavassistDemo {


    /**
     * 对已有的student类中的sayHello方法，当调用时，控制台会输出: Hello, My name is 张三(name=张三)
     * <p>
     * 需求：通过动态修改sayHello方法，当调用sayHello时，除了输出已经的内容外，再输出当前学生的age信息
     *
     * @throws Exception
     */
    public static void editMethod() throws Exception {

        // 类库池, jvm中所加载的class
        ClassPool pool = ClassPool.getDefault();

        // 获取指定的Student类
        CtClass ctClass = pool.get("priv.hb.sample.tool.javassist.Student");

        // 获取sayHello方法
        CtMethod ctMethod = ctClass.getDeclaredMethod("sayHello");

        // 在方法的代码后追加 一段代码
        ctMethod.insertAfter("System.out.println(\"I'm \" + this.age + \" years old.\");");

        // 使用当前的ClassLoader加载被修改后的类
        Class<?> newClass = ctClass.toClass();


        Student stu = (Student) newClass.newInstance();
        stu.setName("张三");
        stu.setAge(18);
        stu.sayHello();
    }

    /**
     * 动态添加方法
     * @throws Exception
     */
    @Test
    public void addMethod() throws Exception {

        ClassPool pool = ClassPool.getDefault();

        CtClass ctClass = pool.get("priv.hb.sample.tool.javassist.Student");

        // 创建calc方法, 带两个参数，参数的类型都为int类型,返回int类型
        CtMethod ctMethod = new CtMethod(CtClass.intType, "calc", new CtClass[]{CtClass.intType, CtClass.intType}, ctClass);

        ctMethod.setModifiers(Modifier.PUBLIC);

        // 设置方法体代码
        ctMethod.setBody("return $1 + $2;");

        // 添加新建的方法到原有的类中
        ctClass.addMethod(ctMethod);

        // 加载修改后的类
        ctClass.toClass();


        // 开始调用
        Student stu = new Student();

        // calc方法
        Method dMethod = Student.class.getDeclaredMethod("calc", new Class[]{int.class, int.class});

        // 反射调用 方法
        Object result = dMethod.invoke(stu, 10, 20);


        // 打印结果
        System.out.println(String.format("调用calc方法，传入参数：%d,%d", 10, 20));
        System.out.println("返回结果：" + (int) result);

    }


    public static void main(String[] args) throws Exception {
        // new Student().sayHello();

        editMethod();
    }

}
