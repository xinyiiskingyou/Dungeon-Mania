package dungeonmania;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.entities.*;
import dungeonmania.items.*;
import dungeonmania.battles.FightStrategy;
import dungeonmania.battles.InvincibleRunStrategy;
import dungeonmania.battles.InvisibleAvoidStrategy;
import dungeonmania.enemy.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerAndEnemyTest {

    @Test
    @DisplayName("Test player can move into enemy in unlocked doorway")
    public void enemyInDoorWayMovement() {
        Dungeon dungeon = new Dungeon();
        Position position = new Position(1,1);
        Player player = new Player(position, 10, 10, "player");
        
        // collect one key
        ItemEntity key = new Key(new Position(0, 1), "key", 1);
        dungeon.addItem(key);
        player.move(dungeon, Direction.LEFT);

        // unlock the door
        Direction movementDirection = Direction.RIGHT;
        Entity door = new Door(player.getPosition().translateBy(movementDirection), "key", 1);
        dungeon.addEntity(door);
        player.move(dungeon, movementDirection);

        // move out of doorway
        player.move(dungeon, Direction.RIGHT);

        // move into zombie in open doorway
        Entity zombie = new ZombieToast(player.getPosition().translateBy(movementDirection),10.0, 10.0, "zombie_toast");
        dungeon.addEntity(zombie);
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }

    
    // ENEMIES
    // one enemy 
    @Test
    @DisplayName("Test player can move into an enemy and collect item")
    public void playerCollectOnEnemy() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(1,1);
        Player player = new Player(position, 10, 10, "player");
        
        Direction movementDirection = Direction.RIGHT;
        Entity zombieToast = new ZombieToast(position.translateBy(movementDirection), 10.0, 10.0, "zombie_toast");
        dungeon.addEntity(zombieToast);
        ItemEntity wood = new Wood(position.translateBy(movementDirection), "wood");
        dungeon.addItem(wood);
        
        player.move(dungeon, movementDirection);
        assertEquals(wood, player.getInventory().get(0));
        assertEquals(1, player.getInventory().size());
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }

    @Test
    @DisplayName("Test player can move into an enemy and collect item reverse")
    public void playerCollectOnEnemReverse() {

        Dungeon dungeon = new Dungeon();

        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");

        Direction movementDirection = Direction.RIGHT;
        Entity zombieToast = new ZombieToast(position.translateBy(movementDirection), 10.0, 10.0, "zombie_toast");
        dungeon.addEntity(zombieToast);

        ItemEntity wood = new Wood(position.translateBy(movementDirection), "wood");
        dungeon.addItem(wood);
        
        player.move(dungeon, movementDirection);
        assertEquals(wood, player.getInventory().get(0));
        assertEquals(1, player.getInventory().size());
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }
    
    // multiple enemies
    @Test
    @DisplayName("Test player can move into mutiple enemies and collect item")
    public void playerCollectOnEnemies() {
        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");
        
        Direction movementDirection = Direction.RIGHT;
        Entity zombieToast = new ZombieToast(position.translateBy(movementDirection), 10.0, 10.0, "zombie_toast");
        Entity zombieToast2 = new ZombieToast(position.translateBy(movementDirection), 10.0, 10.0, "zombie_toast");
        Entity zombieToast3 = new ZombieToast(position.translateBy(movementDirection), 10.0, 10.0, "zombie_toast");
        dungeon.addEntity(zombieToast);
        dungeon.addEntity(zombieToast2);
        dungeon.addEntity(zombieToast3);

        ItemEntity wood = new Wood(position.translateBy(movementDirection), "wood");
        dungeon.addItem(wood);
        
        player.move(dungeon, movementDirection);
        assertEquals(wood, player.getInventory().get(0));
        assertEquals(1, player.getInventory().size());
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }

    @Test
    @DisplayName("Test player can drink potion")
    public void playerCanDrinkPotion() {
        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 0, 0, "player");
        // spawn zombie, spider and mercenary
        ZombieToast zombie = new ZombieToast(position, 0.0, 0.0, "zombie_toast");
        Spider spider = new Spider(position, 0.0, 0.0, "spider");
        Mercenary mercenary = new Mercenary(position, 0.0, 0.0, 1, 1,0,0, "mercenary");
        // check battle strategies
        assertTrue( mercenary.getBattleStrategy() instanceof FightStrategy);
        assertTrue(spider.getBattleStrategy() instanceof FightStrategy);
        assertTrue(zombie.getBattleStrategy() instanceof FightStrategy);

        // add as subscribers
        player.subscribe(mercenary);
        player.subscribe(spider);
        player.subscribe(zombie);
        Direction movementDirection = Direction.LEFT;

        ItemEntity potion = new InvisiblePotion(position.translateBy(movementDirection), "invisibility_potion", 4);

        dungeon.addItem(potion);
        player.move(dungeon, movementDirection);
        // check battle strategies - collection should not change strategy
        assertTrue(mercenary.getBattleStrategy() instanceof FightStrategy);
        assertTrue(spider.getBattleStrategy() instanceof FightStrategy);
        assertTrue(zombie.getBattleStrategy() instanceof FightStrategy);
        player.consumePotion((Potion) potion);
         // check battle strategies - should change after consumption
         assertTrue(mercenary.getBattleStrategy() instanceof InvisibleAvoidStrategy);
         assertTrue(spider.getBattleStrategy() instanceof InvisibleAvoidStrategy);
         assertTrue(zombie.getBattleStrategy() instanceof InvisibleAvoidStrategy);
        assertEquals(0, player.getInventory().size());
        assertEquals(potion, player.getActivePotion());
        assertEquals(potion, player.getPotionQueue().get(0));
    }


    @Test
    @DisplayName("Test potion effect wears off")
    public void potionEffectWearsOff() {
        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 0, 0, "player");
        Direction movementDirection = Direction.LEFT;

        // spawn zombie, spider and mercenary
        ZombieToast zombie = new ZombieToast(position, 0.0, 0.0, "zombie_toast");
        Spider spider = new Spider(position, 0.0, 0.0, "spider");
        Mercenary mercenary = new Mercenary(position, 0.0, 0.0, 1, 1,0,0, "mercenary");

        // add as subscribers
        player.subscribe(mercenary);
        player.subscribe(spider);
        player.subscribe(zombie);

        ItemEntity potion = new InvisiblePotion(position.translateBy(movementDirection), "invisibility_potion", 3);
        dungeon.addItem(potion);
        player.move(dungeon, movementDirection); // potion duration 3

        // potion tick 0
        player.consumePotion((Potion) potion); // active duration 2
        player.tickPotions();
        assertEquals(2, ((Potion) potion).getDuration());
        // spawn zombie after potion consumed
        ZombieToast zombie2 = new ZombieToast(position, 0.0, 0.0, "zombie_toast");
        player.subscribe(zombie2);
        // check battle strategies
        assertTrue(mercenary.getBattleStrategy() instanceof InvisibleAvoidStrategy);
        assertTrue(spider.getBattleStrategy() instanceof InvisibleAvoidStrategy);
        assertTrue(zombie.getBattleStrategy() instanceof InvisibleAvoidStrategy);
        assertTrue(zombie2.getBattleStrategy() instanceof InvisibleAvoidStrategy);

        // poton tick 1
        player.tickPotions(); // duration 1
        assertEquals(potion, player.getActivePotion());
        assertEquals(1, player.getPotionQueue().size());
        // check battle strategies after decreasing potion duration
        assertTrue(mercenary.getBattleStrategy() instanceof InvisibleAvoidStrategy);
        assertTrue(spider.getBattleStrategy() instanceof InvisibleAvoidStrategy);
        assertTrue(zombie.getBattleStrategy() instanceof InvisibleAvoidStrategy);
        assertTrue(zombie2.getBattleStrategy() instanceof InvisibleAvoidStrategy);

        

        // potion tick 3
        // wears off go back to fight
        player.tickPotions();
        assertEquals(0, ((Potion) potion).getDuration());

        assertEquals(0, player.getInventory().size());
        assertEquals(0, player.getPotionQueue().size());
        assertEquals(null, player.getActivePotion());
    
        // test battle strategies after potion wears off
        assertTrue(mercenary.getBattleStrategy() instanceof FightStrategy);
        assertTrue(spider.getBattleStrategy() instanceof FightStrategy);
        assertTrue(zombie.getBattleStrategy() instanceof FightStrategy);
    }

    @Test
    @DisplayName("Test potion effects can be queued")
    public void potionEffectCanBeQueued() {
        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 0, 0, "player");
        Direction movementDirection = Direction.LEFT;

        // spawn zombie, spider, mercenary
        ZombieToast zombie = new ZombieToast(position.translateBy(Direction.RIGHT), 0.0, 0.0, "zombie_toast");
        Spider spider = new Spider(position, 0.0, 0.0, "spider");
        Mercenary mercenary = new Mercenary(position.translateBy(Direction.RIGHT), 0.0, 0.0, 1, 1,0,0, "mercenary");

        // add as subscribers
        player.subscribe(mercenary);
        player.subscribe(spider);
        player.subscribe(zombie);

        ItemEntity potion = new InvisiblePotion(position.translateBy(movementDirection), "invisibility_potion", 3);
        dungeon.addItem(potion);
        player.move(dungeon, movementDirection);

        // potion tick - 0 ; active duration 2
        player.consumePotion((Potion) potion);
        player.tickPotions();
        assertEquals(2, ((Potion) potion).getDuration());
        assertEquals(0, player.getInventory().size());
        assertEquals(1, player.getPotionQueue().size());
        assertEquals(potion, player.getActivePotion());

        // add second potion
        ItemEntity potion2 = new InvinciblePotion(position, "invincibility_potion", 2);
        dungeon.addItem(potion2);
        // move to collect potion
        player.move(dungeon, Direction.RIGHT);
        assertEquals(1, player.getInventory().size());
        player.consumePotion((Potion) potion2);
        // potion tick 1 - active duraction 1
        player.tickPotions();
        // removed from inventory here
        assertEquals(0, player.getInventory().size());
        assertEquals(2, player.getPotionQueue().size());


        // assert battle strategies are still for invisible after player consumes second potion
        assertTrue(mercenary.getBattleStrategy() instanceof InvisibleAvoidStrategy);
        assertTrue(spider.getBattleStrategy() instanceof InvisibleAvoidStrategy);
        assertTrue(zombie.getBattleStrategy() instanceof InvisibleAvoidStrategy);

        // try to swap to queued potion efect
        // potion tick 2 - active = 3, shoudl wear off and swap potion
        player.tickPotions();
        // asert battle strategies have update for new active player potion
        assertTrue(mercenary.getBattleStrategy() instanceof InvincibleRunStrategy);
        assertTrue(spider.getBattleStrategy() instanceof InvincibleRunStrategy);
        assertTrue(zombie.getBattleStrategy() instanceof InvincibleRunStrategy);
        assertEquals(1, player.getPotionQueue().size());
        assertEquals(potion2, player.getActivePotion());
        assertEquals(0, player.getInventory().size()); // removed from inventory once effects take place

        // assert potion2 lasts for its full duration
        player.tickPotions();
        assertEquals(1, player.getPotionQueue().size());
        assertEquals(potion2, player.getActivePotion());

        player.tickPotions();
        assertEquals(0, player.getPotionQueue().size());
        assertEquals(null, player.getActivePotion());

    }

    
}
