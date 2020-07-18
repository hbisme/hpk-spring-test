package event.observer;

/**
 * 小鸡
 */
public class Chick {
    private String name;

    public Chick(String name) {
        this.name = name;
    }

    public void move() {
        if (Eagle.action.equals("飞走了")) {
            System.out.println(this.name + "唧唧叫");
        } else {
            System.out.println(this.name + Eagle.action);
        }
    }
}


