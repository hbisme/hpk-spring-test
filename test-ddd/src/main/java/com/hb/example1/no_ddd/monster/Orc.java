package com.hb.example1.no_ddd.monster;

import com.hb.example1.no_ddd.player.Player;
import com.hb.example1.no_ddd.weapon.Weapon;

/**
 * @author hubin
 * @date 2024年04月28日 15:33
 */
public class Orc extends Monster {
    public Orc(String name, Long health) {
        super(name, health);
    }

    @Override
    public void receiveDamageBy(Weapon weapon, Player player) {
        if (weapon.getDamageType() == 0) {
            this.setHealth(this.getHealth() - weapon.getDamage() / 2); // Orc的物理防御规则
        } else {
            super.receiveDamageBy(weapon, player);
        }
    }

}
