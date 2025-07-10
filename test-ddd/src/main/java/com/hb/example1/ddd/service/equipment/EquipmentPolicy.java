package com.hb.example1.ddd.service.equipment;

import com.hb.example1.ddd.Weapon;
import com.hb.example1.ddd.player.Player;

public interface EquipmentPolicy {

    boolean canApply(Player player, Weapon weapon);


    /**
     * Fighter能装备Sword和Dagger
     */

    boolean canEquip(Player player, Weapon weapon);


}
