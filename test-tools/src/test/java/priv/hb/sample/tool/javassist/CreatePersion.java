package priv.hb.sample.tool.javassist;

import java.lang.reflect.Method;

import javassist.*;

/**
 * javassist来生成类
 * @author hubin
 * @date 2022年11月24日 09:21
 */
public class CreatePersion {

    public static Object createPerson() throws Exception {
        ClassPool pool = ClassPool.getDefault();

        // 1. 创建一个空类
        CtClass cc = pool.makeClass("priv.hb.sample.tool.javassist.Person");

        // 2. 新增一个字段 private String name;
        // 字段名为name
        CtField param = new CtField(pool.get("java.lang.String"), "name", cc);

        param.setModifiers(Modifier.PRIVATE);

        // 字段 初始值是 "xiaoming"
        cc.addField(param, CtField.Initializer.constant("xiaoming"));

        // 3. 生成 getter、setter 方法
        cc.addMethod(CtNewMethod.setter("setName", param));
        cc.addMethod(CtNewMethod.setter("getName", param));

        // 4. 添加无参的构造函数
        CtConstructor cons = new CtConstructor(new CtClass[]{}, cc);
        cons.setBody("{name = \"xiaohong\";}");
        cc.addConstructor(cons);

        // 5. 添加有参的构造函数
        cons = new CtConstructor(new CtClass[]{pool.get("java.lang.String")}, cc);
        // $0=this / $1,$2,$3... 代表方法参数
        cons.setBody("{$0.name = $1;}");
        cc.addConstructor(cons);

        // 6. 创建一个名为printName方法，无参数，无返回值，输出name值
        CtMethod ctMethod = new CtMethod(CtClass.voidType, "printName", new CtClass[]{}, cc);
        ctMethod.setModifiers(Modifier.PUBLIC);
        ctMethod.setBody("{System.out.println(name);}");
        cc.addMethod(ctMethod);


        // 这里不写入文件，直接实例化
        Object person = cc.toClass().newInstance();

        return person;
    }

    // public static void invoke() throws Exception {
    //     Object person = createPerson();
    //
    //     Method printName = person.getClass().getMethod("printName");
    //     printName.invoke(person);
    //
    // }
    //
    // public static void main(String[] args) throws Exception {
    //     invoke();
    // }
}
