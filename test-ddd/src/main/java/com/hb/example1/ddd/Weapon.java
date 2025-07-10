package com.hb.example1.ddd;

import lombok.Data;

/**
 * @author hubin
 * @date 2024年04月28日 17:37
 */
@Data
public class Weapon {
    private WeaponId id;
    private String name;
    private WeaponType weaponType; // enum
    private int damage;
    private int damageType; // 0 - physical, 1 - fire, 2 - ice
}
