package dungeonmania;

import java.util.ArrayList;

import dungeonmania.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.enemy.Assassin;
import dungeonmania.enemy.Enemy;
import dungeonmania.enemy.Hydra;
import dungeonmania.enemy.Mercenary;
import dungeonmania.enemy.Spider;
import dungeonmania.enemy.ZombieToast;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.Door;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Exit;
import dungeonmania.entities.FloorSwitch;
import dungeonmania.entities.Player;
import dungeonmania.entities.Portal;
import dungeonmania.entities.Wall;
import dungeonmania.entities.TimeTravellingPortal;
import dungeonmania.entities.ZombieToastSpawner;
import dungeonmania.goals.AndGoal;
import dungeonmania.goals.BoulderGoal;
import dungeonmania.goals.EnemyGoal;
import dungeonmania.goals.ExitGoal;
import dungeonmania.goals.GoalsInterface;
import dungeonmania.goals.OrGoal;
import dungeonmania.goals.TreasureGoal;
import dungeonmania.items.Arrow;
import dungeonmania.items.Bomb;
import dungeonmania.items.InvinciblePotion;
import dungeonmania.items.InvisiblePotion;
import dungeonmania.items.ItemEntity;
import dungeonmania.items.Key;
import dungeonmania.items.TimeTurner;
import dungeonmania.items.SunStone;
import dungeonmania.items.Treasure;
import dungeonmania.items.Wood;
import dungeonmania.util.Position;
import dungeonmania.weapon.Shield;
import dungeonmania.weapon.Sword;
import dungeonmania.weapon.Bow;
import dungeonmania.weapon.MidnightArmour;
import dungeonmania.weapon.Sceptre;

import java.io.Serializable;

public class EntityFactory implements Serializable {
    
    public Entity createEntity(JSONObject entity, JSONObject configJson, String type, Position position) {

        switch (type) {
            case "player":
                int playerHealth = configJson.getInt("player_health");
                int attackDamage = configJson.getInt("player_attack");
                return new Player(position, attackDamage, playerHealth, type);
            case "wall":
                return new Wall(position, type);
            case "exit":
                return new Exit(position, type);
            case "zombie_toast_spawner":
                return new ZombieToastSpawner(position, type);
            case "boulder":
                return new Boulder(position, type);
            case "switch":
                return new FloorSwitch(position, type);
            case "door":
                int id = entity.getInt("key");
                return new Door(position, type, id);
            case "portal":
                String colour = entity.getString("colour");
                return new Portal(position, type, colour);
            case "time_travelling_portal":
                return new TimeTravellingPortal(position, type);
            case "swamp_tile":
                int movementFactor = entity.getInt("movement_factor");
                return new SwampTile(position, movementFactor, type);
            default:
                break;
        }
        return null;
    }

    public Enemy createEnemy(JSONObject configJson, String type, Position position) {
    
        int initialEnemyHealth;
        int attackDamage;
        switch (type) {
            case "spider":
                initialEnemyHealth = configJson.getInt("spider_health");
                attackDamage = configJson.getInt("spider_attack");
                return new Spider(position, initialEnemyHealth, attackDamage, type);
            case "zombie_toast":
                initialEnemyHealth = configJson.getInt("zombie_health");
                attackDamage = configJson.getInt("zombie_attack");
                return new ZombieToast(position, initialEnemyHealth, attackDamage, type);
            case "mercenary":
                initialEnemyHealth = configJson.getInt("mercenary_health");
                attackDamage = configJson.getInt("mercenary_attack");
                int bribeAmount = configJson.getInt("bribe_amount");
                int bribeRadius = configJson.getInt("bribe_radius");
                int allyAttack = configJson.getInt("ally_attack");
                int allyDefence = configJson.getInt("ally_defence");
                return new Mercenary(position, initialEnemyHealth, attackDamage, bribeAmount, bribeRadius, allyAttack, allyDefence, type);
            case "hydra":
                double hydraAttackDamage = configJson.getDouble("hydra_attack");
                double hydraHealth = configJson.getDouble("hydra_health");
                double hydraHealthIncreaseRate = configJson.getDouble("hydra_health_increase_rate");
                double hydraHealthIncreaseAmount = configJson.getDouble("hydra_health_increase_amount");
                return new Hydra(position, hydraHealth, hydraAttackDamage, hydraHealthIncreaseRate, hydraHealthIncreaseAmount, type);
            case "assassin":
                double assassinAttack = configJson.getDouble("assassin_attack");
                int assassinBribeAmount = configJson.getInt("assassin_bribe_amount");
                int bribeRadiusA = configJson.getInt("bribe_radius");
                double assassinBribeFailRate = configJson.getDouble("assassin_bribe_fail_rate");
                double assassinHealth = configJson.getDouble("assassin_health");
                int assassinRadius = configJson.getInt("assassin_recon_radius");
                int allyAttackA = configJson.getInt("ally_attack");
                int allyDefenceA = configJson.getInt("ally_defence");
               return new Assassin(position, assassinHealth, assassinAttack, assassinBribeAmount, bribeRadiusA, assassinBribeFailRate, assassinRadius, allyAttackA, allyDefenceA, type);
        }
        return null;
    }

    public ItemEntity createItems(JSONObject entity, JSONObject configJson, String type, Position position) {
        
        int durability;
        switch (type) {
            case "treasure":
                int treasureGoal = configJson.getInt("treasure_goal");
                return new Treasure(position, type, treasureGoal);
            case "sun_stone":
                int treasureSunStoneGoal = configJson.getInt("treasure_goal");
                return new SunStone(position, type, treasureSunStoneGoal);
            case "key":
                int id = entity.getInt("key");
                return new Key(position, type, id);
            case "invincibility_potion":
                durability = configJson.getInt("invincibility_potion_duration");
                return new InvinciblePotion(position, type, durability);
            case "invisibility_potion":
                durability = configJson.getInt("invisibility_potion_duration");
                return new InvisiblePotion(position, type, durability);
            case "wood":
                return new Wood(position, type);
            case "arrow":
                return new Arrow(position, type);
            case "bomb":
                int raidus = configJson.getInt("bomb_radius");
                return new Bomb(position, type, raidus);
            case "sword":
                durability = configJson.getInt("sword_durability");
                int attackDamage = configJson.getInt("sword_attack");
                return new Sword(position, attackDamage, durability, type);
            case "time_turner":
                return new TimeTurner(position, type);
        }
        return null;
    } 


    public GoalsInterface addGoal(JSONObject subgoalObject, Dungeon dungeon) {
        String operator = (String) subgoalObject.get("goal");
        JSONArray innerArray;
        switch(operator) {
            case "AND":
                AndGoal andGoal = new AndGoal(dungeon, new ArrayList<>());
                innerArray = subgoalObject.getJSONArray("subgoals");
                for (int j = 0; j < innerArray.length(); j++) {  
                    andGoal.setGoals(addGoal(innerArray.getJSONObject(j), dungeon));
                }
                return new AndGoal(dungeon, andGoal.getAndGoalList());
            case "OR":
                OrGoal orGoal = new OrGoal(dungeon, new ArrayList<>());
                innerArray = subgoalObject.getJSONArray("subgoals");
                for (int j = 0; j < innerArray.length(); j++) {  
                    orGoal.setGoals(addGoal(innerArray.getJSONObject(j), dungeon));
                }
                return new OrGoal(dungeon, orGoal.getOrGoalList());
            case "treasure":
                return new TreasureGoal(dungeon);
            case "boulders":
                return new BoulderGoal(dungeon);
            case "exit":
                return new ExitGoal(dungeon);
            case "enemies":
                return new EnemyGoal(dungeon);
        }
        return null;
    }
    
    public ItemEntity createBuildableEntity(String type, JSONObject configJson, Position position) {

        switch (type) {
            case "bow":
                int bow_durability = configJson.getInt("bow_durability");
                return new Bow(position, 2, bow_durability, type);
            case "shield":
                int shield_durability = configJson.getInt("shield_durability");
                int shield_defence = configJson.getInt("shield_defence");
                return new Shield(position, shield_defence, shield_durability, type);
            case "midnight_armour":
                int armour_attack = configJson.getInt("midnight_armour_attack");
                int armour_defence = configJson.getInt("midnight_armour_defence");
                int armour_durability = Integer.MAX_VALUE; // unlimited durability
                return new MidnightArmour(position, armour_attack, armour_defence, armour_durability, type);
            case "sceptre":
                int sceptre_duration = configJson.getInt("mind_control_duration");
                int sceptre_attack = 0;
                int sceptre_durability = Integer.MAX_VALUE; // unlimited durability
                return new Sceptre(position, sceptre_attack, sceptre_duration, sceptre_durability, type);
            default:
                break;
        }
        return null;
    }

}
