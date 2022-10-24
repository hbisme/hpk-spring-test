package event.observer;

import lombok.Data;

/**
 *
 * @desc  老鹰(被观察者)
 */
@Data
public class Eagle {

    public static String action;

    private String name;

    public Eagle(String name) {
        this.name = name;
    }
    public void move() {
        System.out.println(this.name + action);
    }

}
