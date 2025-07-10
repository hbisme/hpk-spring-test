package com.hb.example1.ddd.service.equipment;

import com.hb.example1.ddd.Weapon;
import com.hb.example1.ddd.player.Player;

/**
 * @author hubin
 * @date 2024年04月29日 09:52
 */
public interface EquipmentService {
    boolean canEquip(Player player, Weapon weapon);
}