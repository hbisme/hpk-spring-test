package com.hb.example1.no_ddd.monster;

import com.hb.example1.no_ddd.player.Player;
import com.hb.example1.no_ddd.weapon.Weapon;
import lombok.Data;

/**
 * @author hubin
 * @date 2024年04月28日 15:33
 */
@Data
public abstract class Monster {
    String name;

    Long health;

    public Monster(String name, Long health) {
        this.name = name;
        this.health = health;
    }

    public void receiveDamageBy(Weapon weapon, Player player) {
        this.health -= weapon.getDamage(); // 基础规则
    }

}