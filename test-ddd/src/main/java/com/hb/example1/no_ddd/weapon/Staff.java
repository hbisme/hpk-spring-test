package com.hb.example1.no_ddd.weapon;

/**
 * @author hubin
 * @date 2024年04月28日 15:36
 */
public class Staff extends Weapon {
    public Staff(String name, Long damage) {
        super(name, damage);

        super.damageType = 1;
    }
}
