package com.hb.example1.ddd.service.combat;

import com.hb.example1.ddd.Weapon;
import com.hb.example1.ddd.monster.Monster;
import com.hb.example1.ddd.monster.MonsterClass;
import com.hb.example1.ddd.player.Player;
import com.hb.example1.ddd.player.PlayerClass;

/**
 * @author hubin
 * @date 2024年04月29日 10:18
 */

// 策略案例
public class DragoonPolicy implements DamagePolicy {
    public int calculateDamage(Player player, Weapon weapon, Monster monster) {
        return weapon.getDamage() * 2;
    }

    @Override
    public boolean canApply(Player player, Weapon weapon, Monster monster) {
        return player.getPlayerClass() == PlayerClass.Dragoon &&
                monster.getMonsterClass() == MonsterClass.Dragon;
    }
}


