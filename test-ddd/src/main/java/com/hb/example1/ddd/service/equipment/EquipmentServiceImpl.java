package com.hb.example1.ddd.service.equipment;

import com.hb.example1.ddd.Weapon;
import com.hb.example1.ddd.player.Player;

/**
 * @author hubin
 * @date 2024年04月29日 09:56
 */
public class EquipmentServiceImpl implements EquipmentService {
    private EquipmentManager equipmentManager;

    @Override
    public boolean canEquip(Player player, Weapon weapon) {
        return equipmentManager.canEquip(player, weapon);
    }
}



