package dungeonmania.battles;

import dungeonmania.entities.Player;
import dungeonmania.Dungeon;
import dungeonmania.enemy.Enemy;

public class InvisibleAvoidStrategy implements BattleStrategy{
    @Override
    public Battle battleResponse(Player player, Enemy enemy, Battle battle, Dungeon dungeon) {
        //  player is invisible, do not battle and simply return
        return null;
    }
}