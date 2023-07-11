package dungeonmania.battles;

import dungeonmania.entities.Player;
import dungeonmania.items.ItemEntity;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Dungeon;
import dungeonmania.enemy.Assassin;
import dungeonmania.enemy.Enemy;
import dungeonmania.enemy.Hydra;
import dungeonmania.enemy.Mercenary;
import dungeonmania.weapon.Bow;
import dungeonmania.weapon.MidnightArmour;
import dungeonmania.weapon.Shield;
import dungeonmania.weapon.Sword;
import dungeonmania.weapon.Weapon;


public class FightStrategy implements BattleStrategy {

    @Override
    public Battle battleResponse(Player player, Enemy enemy, Battle battle, Dungeon dungeon) {
        //  set new round
        Round round = new Round();

        double bowDamage = 0;
        double swordDamage = 0;
        double shieldDefence = 0;
        double midnightDefence = 0;
        double midnightAttack = 0;
        boolean hasBow = false;
        double allyAttack = 0;
        double allyDefence = 0;

        for (Enemy e: dungeon.getAlliesList()) {
            if (e instanceof Mercenary) {
                allyAttack += ((Mercenary)e).getAllyAttack();
                allyDefence += ((Mercenary)e).getAllyDefence();
            }

            if (e instanceof Assassin) {
                allyAttack += ((Assassin)e).getAllyAttack();
                allyDefence += ((Assassin)e).getAllyDefence();
            }
        }
      
        List<ItemEntity> weaponsList = player.getWeapons();
        for (ItemEntity w : weaponsList) {
            if (w instanceof Sword) {
                swordDamage += ((Sword)w).getDamage();
                round.addToWeaponsUsed(w);
            }
            if (w instanceof Bow) {
                hasBow = true;
                bowDamage += ((Bow)w).getDamage();
                round.addToWeaponsUsed(w);
            }
            if (w instanceof Shield) {
                Shield s = (Shield) w;
                shieldDefence += s.getDefence();
                round.addToWeaponsUsed(w);
            }
            if (w instanceof MidnightArmour) {
                MidnightArmour m = (MidnightArmour) w;
                midnightDefence += m.getDefence();
                midnightAttack += m.getDamage();
                round.addToWeaponsUsed(w);
            }
        }

        if (!hasBow) {
            bowDamage = 1;
        }
        // start battle round
        // check hydra increase chance
        boolean healed = false;
        if (enemy instanceof Hydra) {
            Hydra h = (Hydra) enemy;
            
            if (h.getHealthIncreaseRate() == 0) {
                // do nothing
            } else if (h.getHealthIncreaseRate() == 1) {
                // heal
                h.updateHealth(h.getHealthIncreaseAmount());
                round.setDeltaEnemyHealth(h.getHealthIncreaseAmount());
                healed = true;
            } else if (Math.random() < h.getHealthIncreaseRate()) {
                h.updateHealth(h.getHealthIncreaseAmount());
                healed = true;
                round.setDeltaEnemyHealth(h.getHealthIncreaseAmount());
            }  
        }
        if (!healed) {
            double deltaEnemyHealth = -((bowDamage * (player.getAttackDamage() + swordDamage + allyAttack + midnightAttack)) / 5.0);
            enemy.updateHealth(deltaEnemyHealth);
            round.setDeltaEnemyHealth(deltaEnemyHealth);
        }

        // player health
        double deltaPlayerHealth = -((enemy.getAttackDamage() - shieldDefence - allyDefence - midnightDefence) / 10.0);
        player.updateHealth(deltaPlayerHealth);
        round.setDeltaPlayerHealth(deltaPlayerHealth);

        List<ItemEntity> itemsToRemove = new ArrayList<>();
        for (ItemEntity w: weaponsList) {
            if (w instanceof Weapon && !(w instanceof MidnightArmour)) {
                ((Weapon)w).decreaseDurability();
                if ( ((Weapon)w).getDurability() == 0) {
                    itemsToRemove.add(w);
                }
            }

        }

        for (ItemEntity r : itemsToRemove) {
            player.removeWeapon(r);
            player.removeItem((ItemEntity)r);
        }

        //add round stats to battle
        battle.addToRounds(round);
        
        // if player health <= 0
        if (player.getHealth() <= 0) {
            // remove player from game and end it
            dungeon.removePlayer(player);
            return battle;
        } else if (enemy.getHealth() <= 0) {
            // remove enemy from game
            dungeon.incrementEnemiesKill();
            dungeon.removeEnemy(enemy);
            // unsubscribe enemy
            player.unsubscribe(enemy);
            return battle;
        } else {
            // neither player or enemy dead, play another round
            return battleResponse(player, enemy, battle, dungeon);
        }

    }

}