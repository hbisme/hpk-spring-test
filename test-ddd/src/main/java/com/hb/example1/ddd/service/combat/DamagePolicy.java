package com.hb.example1.ddd.service.combat;

import com.hb.example1.ddd.Weapon;
import com.hb.example1.ddd.monster.Monster;
import com.hb.example1.ddd.player.Player;

/**
 * @author hubin
 * @date 2024年04月29日 10:17
 */
public interface DamagePolicy {
    int calculateDamage(Player player, Weapon weapon, Monster monster);

    boolean canApply(Player player, Weapon weapon, Monster monster);
}
