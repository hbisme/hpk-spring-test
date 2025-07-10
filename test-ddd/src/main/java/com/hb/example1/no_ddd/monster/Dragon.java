package com.hb.example1.no_ddd.monster;

import com.hb.example1.no_ddd.player.Dragoon;
import com.hb.example1.no_ddd.player.Player;
import com.hb.example1.no_ddd.weapon.Weapon;

/**
 * @author hubin
 * @date 2024年04月28日 15:34
 */
public class Dragon extends Monster {


    public Dragon(String name, Long health) {
        super(name, health);
    }

    @Override
    public void receiveDamageBy(Weapon weapon, Player player) {
        if (player instanceof Dragoon) {
            this.setHealth(this.getHealth() - weapon.getDamage() * 2); // 龙骑伤害规则
        }
    }
}