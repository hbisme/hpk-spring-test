package com.hb.example1.ddd.player;

import com.hb.example1.ddd.Movable;
import com.hb.example1.ddd.Transform;
import com.hb.example1.ddd.Vector;
import com.hb.example1.ddd.Weapon;
import com.hb.example1.ddd.WeaponId;
import com.hb.example1.ddd.service.equipment.EquipmentService;

import lombok.Data;

/**
 * @author hubin
 * @date 2024年04月28日 17:34
 */
@Data
public class Player implements Movable {

    private PlayerId id;
    private String name;
    private PlayerClass playerClass; // enum
    private WeaponId weaponId; // （Note 1）
    private Transform position = Transform.ORIGIN;
    private Vector velocity = Vector.ZERO;


    public void equip(Weapon weapon, EquipmentService equipmentService) {
        if (equipmentService.canEquip(this, weapon)) {
            this.weaponId = weapon.getId();
        } else {
            throw new IllegalArgumentException("Cannot Equip: " + weapon);
        }
    }





    public void moveTo(long x, long y) {
        this.position = new Transform(x, y);
    }

    public void startMove(long velocityX, long velocityY) {
        this.velocity = new Vector(velocityX, velocityY);
    }

    public void stopMove() {
        this.velocity = Vector.ZERO;
    }

    @Override
    public boolean isMoving() {
        return this.velocity.getX() != 0 || this.velocity.getY() != 0;
    }
}
