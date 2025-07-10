package com.hb.example1.no_ddd.player;

import com.hb.example1.no_ddd.monster.Monster;
import com.hb.example1.no_ddd.weapon.Sword;
import com.hb.example1.no_ddd.weapon.Weapon;

import lombok.Data;

/**
 * @author hubin
 * @date 2024年04月28日 15:14
 */
@Data
public abstract class Player {

    String name;
    Weapon weapon;


    public Player(String name) {
        this.name = name;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public void attack(Monster monster) {
        monster.receiveDamageBy(weapon, this);
    }

}