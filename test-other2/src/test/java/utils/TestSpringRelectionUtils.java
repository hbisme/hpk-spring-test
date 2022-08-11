package utils;

import com.hb.dto.JobDto;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;


/**
 * 测试spring自带的反射根据类,还算比较好用.
 * @author hubin
 * @date 2022年05月27日 10:52
 */

public class TestSpringRelectionUtils {

    @Data
    // @AllArgsConstructor
    class Student {
        private int id;
        private String name;

        public Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDouble() {
            return name + "_" + name;
        }

    }

    @Test
    public void test0() throws Exception {
        Student student = new Student(100, "hb");
        Class<? extends Student> aClass = student.getClass();

        Field id = aClass.getDeclaredField("id");
        id.setAccessible(true);

        int i = (int) id.get(student);
        System.out.println(i);

        Method getDouble = aClass.getDeclaredMethod("getDouble", null);
        String invoke = (String) getDouble.invoke(student, null);
        System.out.println(invoke);


    }


    @SneakyThrows
    @Test
    public void testGetField() {
        Student student = new Student(100, "hb");
        Class<? extends Student> aClass = student.getClass();
        final Field id = ReflectionUtils.findField(aClass, "id");
        ReflectionUtils.makeAccessible(id);

        int field = (int) ReflectionUtils.getField(id, student);
        System.out.println(field);
    }

    @Test
    public void testSetField() {
        Student student = new Student(100, "hb");
        Class<? extends Student> aClass = student.getClass();
        final Field id = ReflectionUtils.findField(aClass, "id");
        ReflectionUtils.makeAccessible(id);

        ReflectionUtils.setField(id, student, 101);
        System.out.println(student.getId());
    }

    @SneakyThrows
    @Test
    public void testMethod() {
        Student student = new Student(100, "hb");
        Class<? extends Student> aClass = student.getClass();
        Method getDouble = ReflectionUtils.findMethod(aClass, "getDouble");

        ReflectionUtils.makeAccessible(getDouble);
        String o = (String)ReflectionUtils.invokeMethod(getDouble, student);
        System.out.println(o);
    }




}
