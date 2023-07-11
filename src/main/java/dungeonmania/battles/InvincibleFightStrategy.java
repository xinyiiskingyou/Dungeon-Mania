package dungeonmania.battles;

import dungeonmania.entities.Player;
import dungeonmania.Dungeon;
import dungeonmania.enemy.Enemy;

public class InvincibleFightStrategy implements BattleStrategy{
    /*
     * Applies to Spiders - will fight Players, with player immediately winning
     */
    @Override
    public Battle battleResponse(Player player, Enemy enemy, Battle battle, Dungeon dungeon) {
        //  player wins in one round
        Round round = new Round();
        //  make player take 0 damage since they are invincible
        round.setDeltaPlayerHealth(0);
        //  immediately kill enemy
        Double enemyHealth = enemy.getHealth();
        round.setDeltaEnemyHealth(-enemyHealth);
        // remove enemy from dungeon
        dungeon.removeEnemy(enemy);
        // unsubscribe enemy
        player.unsubscribe(enemy);
        //  add to stats
        round.addToWeaponsUsed(player.getActivePotion());
        battle.addToRounds(round);
        dungeon.incrementEnemiesKill();
        return battle;
    }
}