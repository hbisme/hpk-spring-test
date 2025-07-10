package com.hb.example1.ddd.service.equipment;

import java.util.ArrayList;
import java.util.List;

import com.hb.example1.ddd.Weapon;
import com.hb.example1.ddd.player.Player;

/**
 * @author hubin
 * @date 2024年04月29日 09:57
 */

// 策略优先级管理
public class EquipmentManager {
    private static final List<EquipmentPolicy> POLICIES = new ArrayList<>();
    static {
        POLICIES.add(new FighterEquipmentPolicy());
        // POLICIES.add(new MageEquipmentPolicy());
        // POLICIES.add(new DragoonEquipmentPolicy());
        // POLICIES.add(new DefaultEquipmentPolicy());
    }

    public boolean canEquip(Player player, Weapon weapon) {
        for (EquipmentPolicy policy : POLICIES) {
            if (!policy.canApply(player, weapon)) {
                continue;
            }
            return policy.canEquip(player, weapon);
        }
        return false;
    }
}



