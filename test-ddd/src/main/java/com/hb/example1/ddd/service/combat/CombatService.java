package com.hb.example1.ddd.service.combat;

import com.hb.example1.ddd.monster.Monster;
import com.hb.example1.ddd.player.Player;

public interface CombatService {
    void performAttack(Player player, Monster monster);

}
