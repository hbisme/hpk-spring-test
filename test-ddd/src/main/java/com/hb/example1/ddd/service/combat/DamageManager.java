package com.hb.example1.ddd.service.combat;

import java.util.ArrayList;
import java.util.List;

import com.hb.example1.ddd.Weapon;
import com.hb.example1.ddd.monster.Monster;
import com.hb.example1.ddd.player.Player;

/**
 * @author hubin
 * @date 2024年04月29日 10:16
 */
// 策略优先级管理
public class DamageManager {
    private static final List<DamagePolicy> POLICIES = new ArrayList<>();

    static {
        POLICIES.add(new DragoonPolicy());
        // POLICIES.add(new DragonImmunityPolicy());
        // POLICIES.add(new OrcResistancePolicy());
        // POLICIES.add(new ElfResistancePolicy());
        // POLICIES.add(new PhysicalDamagePolicy());
        // POLICIES.add(new DefaultDamagePolicy());
    }

    public int calculateDamage(Player player, Weapon weapon, Monster monster) {
        for (DamagePolicy policy : POLICIES) {
            if (!policy.canApply(player, weapon, monster)) {
                continue;
            }
            return policy.calculateDamage(player, weapon, monster);
        }
        return 0;
    }
}
