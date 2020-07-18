package event.observer;

import java.util.Random;

public class Test {
    private static final String[] actions = {"向左移动一步", "向左移动两步", "向左移动三步", "飞走了"};

    public static void main(String[] args) {

        Eagle eagle = new Eagle("老鹰");
        Hen hen = new Hen( "母鸡", eagle);
        Chick chick = new Chick("小鸡");
        Random random = new Random();

        int ran;
        for (int i = 0; i < 10; i++) {
            ran = random.nextInt(actions.length);
            Eagle.action = actions[ran];
            eagle.move();
            hen.move();
            chick.move();
        }

    }
}
