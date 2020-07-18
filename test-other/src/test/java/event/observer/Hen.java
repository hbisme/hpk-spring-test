package event.observer;


/**
 * 母鸡(观察者)
 */
public class Hen {
    private String name;
    private Eagle eagle;

    public Hen(String name, Eagle eagle) {
        this.name = name;
        this.eagle = eagle;
    }

    public void move() {
        if (Eagle.action.equals("飞走了")) {
            System.out.println(this.name + "呱呱叫");
        } else {
            System.out.println(this.name + Eagle.action);
        }
    }
}
