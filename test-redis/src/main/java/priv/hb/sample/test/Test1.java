package priv.hb.sample.test;

import priv.hb.sample.dto.Persion;


import io.vavr.control.Option;

public class Test1 {
    public static void main(String[] args) {
        Persion p = new Persion();
        // int age = p.getAge();

        int age = Option.of(p.getAge()).getOrElse(0);




        if (age > 18) {
            System.out.println(">18");
        } else {
            System.out.println(age);
        }

        Integer i = 1270;
        Integer j = 1271;
        System.out.println(i < j);


    }

}
