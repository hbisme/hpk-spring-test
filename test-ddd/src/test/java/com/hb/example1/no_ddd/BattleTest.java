package com.hb.example1.no_ddd;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import com.hb.example1.no_ddd.monster.Dragon;
import com.hb.example1.no_ddd.monster.Orc;
import com.hb.example1.no_ddd.player.Dragoon;
import com.hb.example1.no_ddd.player.Fighter;
import com.hb.example1.no_ddd.player.Mage;
import com.hb.example1.no_ddd.weapon.Staff;
import com.hb.example1.no_ddd.weapon.Sword;

/**
 * @author hubin
 * @date 2024年04月28日 15:54
 */
public class BattleTest {

    @Test
    @DisplayName("Dragon is immune to attacks")
    public void testDragonImmunity() {
        // Given
        Fighter fighter = new Fighter("Hero");
        Sword sword = new Sword("Excalibur", 10L);
        fighter.setWeapon(sword);
        Dragon dragon = new Dragon("Dragon", 100L);

        // When
        fighter.attack(dragon);

        Long health = dragon.getHealth();
        System.out.println("战士装备神剑攻击恶龙,恶龙还剩余" + health + "点血.(恶龙不受伤)");

    }


    @Test
    @DisplayName("Dragoon attack dragon doubles damage")
    public void testDragoonSpecial() {
        // Given
        Dragoon dragoon = new Dragoon("Dragoon");
        Sword sword = new Sword("Excalibur", 10L);
        dragoon.setWeapon(sword);
        Dragon dragon = new Dragon("Dragon", 100L);


        // Then
        dragoon.attack(dragon);

        Long health = dragon.getHealth();
        System.out.println("龙骑士装备神剑攻击恶龙,恶龙还剩余" + health + "点血.(双倍伤害)");    }


    @Test
    @DisplayName("Orc should receive half damage from physical weapons")
    public void testFighterOrc() {
        // Given
        Fighter fighter = new Fighter("Hero");
        Sword sword = new Sword("Excalibur", 10L);
        fighter.setWeapon(sword);
        Orc orc = new Orc("Orc", 100L);

        // When
        fighter.attack(orc);
        Assert.assertEquals(Long.valueOf(100 - 10 / 2), orc.getHealth());
    }


    @Test
    @DisplayName("Orc receive full damage from magic attacks")
    public void testMageOrc() {
        // Given
        Mage mage = new Mage("Mage");
        Staff staff = new Staff("Fire Staff", 10L);
        mage.setWeapon(staff);
        Orc orc = new Orc("Orc", 100L);

        // When
        mage.attack(orc);

        // Then
        Assert.assertEquals(Long.valueOf(100 - 10), orc.getHealth());

    }



    @Test
    public void testEquip() {
        Fighter fighter = new Fighter("Hero");

        Sword sword = new Sword("Sword", 10L);
        fighter.setWeapon(sword);

        Staff staff = new Staff("Staff", 10L);
        fighter.setWeapon(staff);

        System.out.println((fighter.getWeapon()));



    }



}
