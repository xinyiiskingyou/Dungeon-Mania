package dungeonmania.battles;

import dungeonmania.entities.Player;
import dungeonmania.Dungeon;
import dungeonmania.enemy.Enemy;

public class InvincibleRunStrategy implements BattleStrategy{
    /*
    * Applies to Mercenaries and Zombies - runs away when player is invincible
    */
    @Override
    public Battle battleResponse(Player player, Enemy enemy, Battle battle, Dungeon dungeon) {
        //  run away
        return null;
    }
}