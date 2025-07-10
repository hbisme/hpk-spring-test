package com.hb.example1.ddd.service.equipment;

import com.hb.example1.ddd.Weapon;
import com.hb.example1.ddd.WeaponType;
import com.hb.example1.ddd.player.Player;
import com.hb.example1.ddd.player.PlayerClass;

/**
 * @author hubin
 * @date 2024年04月29日 10:00
 */
// 策略案例
public class FighterEquipmentPolicy implements EquipmentPolicy {

    @Override
    public boolean canApply(Player player, Weapon weapon) {
        return player.getPlayerClass() == PlayerClass.Fighter;
    }

    /**
     * Fighter能装备Sword和Dagger
     */
    @Override
    public boolean canEquip(Player player, Weapon weapon) {
        return weapon.getWeaponType() == WeaponType.Sword
                || weapon.getWeaponType() == WeaponType.Dagger;
    }
}
