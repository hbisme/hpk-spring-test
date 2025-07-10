package com.hb.example1.ddd.service.combat;

import com.hb.example1.ddd.Weapon;
import com.hb.example1.ddd.monster.Monster;
import com.hb.example1.ddd.player.Player;

/**
 * @author hubin
 * @date 2024年04月29日 10:07
 */
public class CombatServiceImpl implements CombatService {
    // private WeaponRepository weaponRepository;
    private DamageManager damageManager;

    @Override
    public void performAttack(Player player, Monster monster) {
        // Weapon weapon = weaponRepository.find(player.getWeaponId());
        // int damage = damageManager.calculateDamage(player, weapon, monster);
        // if (damage > 0) {
            // monster.takeDamage(damage); // （Note 1）在领域服务里变更Monster
        // }
        // 省略掉Player和Weapon可能受到的影响
    }
}
