package dungeonmania.battles;

import dungeonmania.entities.Player;
import dungeonmania.Dungeon;
import dungeonmania.enemy.Enemy;
import java.io.Serializable;

public interface BattleStrategy extends Serializable {

    public Battle battleResponse(Player player, Enemy enemy, Battle battle, Dungeon dungeon);
}
