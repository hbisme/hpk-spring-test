package com.hb.example1.no_ddd.weapon;

/**
 * @author hubin
 * @date 2024年04月28日 15:14
 */
public abstract class Weapon {
    String name;
    Long damage;
    int damageType; // 0 - physical, 1 - fire, 2 - ice etc.

    public Weapon(String name, Long damage) {
        this.name = name;
        this.damage = damage;
    }

    public Long getDamage() {
        return damage;
    }

    public int getDamageType() {
        return damageType;
    }
}