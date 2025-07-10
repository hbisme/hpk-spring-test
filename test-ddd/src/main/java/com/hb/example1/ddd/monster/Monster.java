package com.hb.example1.ddd.monster;

import com.hb.example1.ddd.Movable;
import com.hb.example1.ddd.Transform;
import com.hb.example1.ddd.Vector;

import lombok.Data;

/**
 * @author hubin
 * @date 2024年04月28日 17:37
 */
@Data
public class Monster implements Movable {
    private MonsterId id;
    private MonsterClass monsterClass; // enum
    private Health health;
    private Transform position = Transform.ORIGIN;
    private Vector velocity = Vector.ZERO;

    @Override
    public Transform getPosition() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }

    @Override
    public void moveTo(long x, long y) {

    }

    @Override
    public void startMove(long velX, long velY) {

    }

    @Override
    public void stopMove() {

    }

    @Override
    public boolean isMoving() {
        return false;
    }
}
