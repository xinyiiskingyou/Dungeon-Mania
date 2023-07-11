package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.items.Arrow;
import dungeonmania.items.InvisiblePotion;
import dungeonmania.items.ItemEntity;
import dungeonmania.items.Key;
import dungeonmania.util.Position;
import dungeonmania.util.Direction;

import dungeonmania.weapon.Sword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import dungeonmania.enemy.Enemy;
import dungeonmania.enemy.Spider;
import dungeonmania.enemy.ZombieToast;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;
import dungeonmania.entities.FloorSwitch;
import dungeonmania.entities.Player;
import dungeonmania.entities.Wall;
import dungeonmania.entities.ZombieToastSpawner;

public class ZombieSpawnerTest {
    
    // test correct number of zombies are spawned
    @Test
    @DisplayName("Test correct number of zombies spawned for the spawn rate") 
    public void testZombieSpawnRate(){
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        // spawn zombie every second tick
        dungeon.setZombieSpawnRate(2);
        dungeon.setSpiderSpawner(new SpiderSpawner(0));
        dungeon.non_player_acions();
        assertEquals(0, dungeon.getEnemies().size());
        
        dungeon.non_player_acions();
        assertEquals(1, dungeon.getEnemies().size());
        assertEquals("zombie_toast", dungeon.getEnemies().get(0).getType());
    }

    // test correct open square is found
    /**
    *          wall
    *   wall  spawner  open
    *          wall
    */
    @Test
    @DisplayName("Test found open square") 
    public void testZombieSpawnSurrondThreeWalls(){
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        // add obstacles
        Entity wall = new Wall(position.translateBy(Direction.LEFT), "wall");
        dungeon.addEntity(wall);
        Entity wall2 = new Wall(position.translateBy(Direction.DOWN), "wall");
        dungeon.addEntity(wall2);
        Entity wall3 = new Wall(position.translateBy(Direction.UP), "wall");
        dungeon.addEntity(wall3);
        dungeon.setZombieSpawnRate(1);
        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        spawner.spawn(dungeon);
        
        List <Enemy> enemies = dungeon.getEnemies();
        Enemy enemy = enemies.get(0);
        assertTrue(enemy instanceof ZombieToast);
        // zombies should only spawn on the free square
        assertEquals(position.translateBy(Direction.RIGHT), enemy.getPosition());
    }

    // test cannot spawn if surrounded by walls
    /**
    *          wall
    *   wall  spawner  wall
    *          wall
    */
    @Test
    @DisplayName("Test no open square") 
    public void testZombieCannotSpawnOnWall(){
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        // add obstacles
        Entity wall = new Wall(position.translateBy(Direction.LEFT), "wall");
        dungeon.addEntity(wall);
        Entity wall2 = new Wall(position.translateBy(Direction.DOWN), "wall");
        dungeon.addEntity(wall2);
        Entity wall3 = new Wall(position.translateBy(Direction.UP), "wall");
        dungeon.addEntity(wall3);
        Entity wall4 = new Wall(position.translateBy(Direction.RIGHT), "wall");
        dungeon.addEntity(wall4);

        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        spawner.spawn(dungeon);
        
        List <Enemy> enemies = dungeon.getEnemies();
        assertEquals(0, enemies.size());
    }

    // test cannot spawn if surrounded by walls and boulders
    /**
    *         boulder
    *   wall  spawner  boulder
    *          wall
    */
    @Test
    @DisplayName("Test no open square boulders and walls") 
    public void testZombieCannotSpawnOnWallBoulder(){
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        // add obstacles
        Entity wall = new Wall(position.translateBy(Direction.LEFT), "wall");
        dungeon.addEntity(wall);
        Entity wall2 = new Wall(position.translateBy(Direction.DOWN), "wall");
        dungeon.addEntity(wall2);
        Entity boulder = new Boulder(position.translateBy(Direction.UP), "boulder");
        dungeon.addEntity(boulder);
        Entity boulder1 = new Boulder(position.translateBy(Direction.RIGHT), "boulder");
        dungeon.addEntity(boulder1);

        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        spawner.spawn(dungeon);

        List <Enemy> enemies = dungeon.getEnemies();
        assertEquals(0, enemies.size());
    }

    // test can spawn if surrounded by walls and boulder after a boulder is moved
    /**                                   
    *         boulder                       boulder
    *   wall  spawner  boulder  -->   wall  spawner  open   boulder         
    *          wall                         wall  
    */
    @Test
    @DisplayName("Test pushing boulder can spawn now") 
    public void testZombieCanSpawnAfterMoveBoulder() {
        Position position = new Position(1,1);
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        Dungeon dungeon = new Dungeon();
        dungeon.setPlayer(player);
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        // add obstacles
        Entity wall = new Wall(position.translateBy(Direction.LEFT), "wall");
        dungeon.addEntity(wall);
        Entity wall2 = new Wall(position.translateBy(Direction.DOWN), "wall");
        dungeon.addEntity(wall2);
        Entity boulder = new Boulder(position.translateBy(Direction.UP), "boulder");
        dungeon.addEntity(boulder);
        Entity boulder1 = new Boulder(position.translateBy(Direction.RIGHT), "boulder");
        dungeon.addEntity(boulder1);
        spawner.spawn(dungeon);
        assertEquals(0, dungeon.getEnemies().size());

        ((Boulder) boulder1).pushBoulder(Direction.RIGHT, dungeon);

        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        spawner.spawn(dungeon);
        assertEquals(1, dungeon.getEnemies().size());
        Enemy enemy = dungeon.getEnemies().get(0);
        assertTrue(enemy instanceof ZombieToast);
        assertEquals(position.translateBy(Direction.RIGHT), enemy.getPosition());
    }

    @Test
    @DisplayName("Test if player is not cardinally adjacent to the spawner when trying to destroy it")
    public void testNotCardinallyAdjacentSpawner() {
        Position spawnerPosition = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        ZombieToastSpawner spawner = new ZombieToastSpawner(spawnerPosition , "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        Position initialPosPlayer = new Position(0, 5);
        Player player = new Player(initialPosPlayer, 0, 2, "player");
        dungeon.setPlayer(player);

        // Player does not have weapon and is not in range
        assertThrows(InvalidActionException.class, () -> spawner.destroy(dungeon));
    }

    @Test
    @DisplayName("Test if player does not have a weapon but is within range and attempts to destroy a spawner")
    public void testNoWeaponSpawner() {
        Position spawnerPosition = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        ZombieToastSpawner spawner = new ZombieToastSpawner(spawnerPosition , "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");
        dungeon.setPlayer(player);

        // Player does not have weapon and is in range
        assertThrows(InvalidActionException.class, () -> spawner.destroy(dungeon));
    }

    @Test
    @DisplayName("Test player can destroy spawner when cardinally adjacent and has a weapon")
    public void testDestroySpawner() {
        Position spawnerPosition = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        ZombieToastSpawner spawner = new ZombieToastSpawner(spawnerPosition , "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");
        dungeon.setPlayer(player);

        // Give player weapon and destroy spawner
        Sword sword = new Sword(initialPosPlayer, 10, 5, "sword");
        player.addWeapon((sword));
        assertDoesNotThrow(() -> spawner.destroy(dungeon));

        // Check spawner is removed from dungeon
        assertTrue(dungeon.getEntities().size() == 1);
    }

    
    @Test
    @DisplayName("Test zombie cannot spawn on wall with spider on it") 
    public void testZombieNotSpawnSpiderWall() {
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        // add spider before every wall
        Enemy spider = new Spider(position.translateBy(Direction.LEFT), 1.0, 1.0, "spider");
        Entity wall = new Wall(position.translateBy(Direction.LEFT), "wall");
        dungeon.addEnemy(spider);
        dungeon.addEntity(wall);
        Enemy spider2 = new Spider(position.translateBy(Direction.DOWN), 1.0, 1.0, "spider");
        Entity wall2 = new Wall(position.translateBy(Direction.DOWN), "wall");
        dungeon.addEnemy(spider2);
        dungeon.addEntity(wall2);
        Enemy spider3 = new Spider(position.translateBy(Direction.UP), 1.0, 1.0, "spider");
        Entity wall3 = new Wall(position.translateBy(Direction.UP), "wall");
        dungeon.addEnemy(spider3);
        dungeon.addEntity(wall3);
        Enemy spider4 = new Spider(position.translateBy(Direction.RIGHT), 1.0, 1.0, "spider");
        Entity wall4 = new Wall(position.translateBy(Direction.RIGHT), "wall");
        dungeon.addEnemy(spider4);
        dungeon.addEntity(wall4);

        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        spawner.spawn(dungeon);
        
        List <Enemy> enemies = dungeon.getEnemies();
        assertEquals(4, enemies.size());
 
    }

    @Test
    @DisplayName("Test zombie cannot spawn on boulder on top of switch") 
    public void testZombieNotSpawnSwitchBoulder() {
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        // add switch under every boulder 
        Entity floorSwitch = new FloorSwitch(position.translateBy(Direction.LEFT), "floor_switch");
        Entity boulder = new Boulder(position.translateBy(Direction.LEFT), "boulder");
        dungeon.addEntity(floorSwitch);
        dungeon.addEntity(boulder);
        Entity floorSwitch2 = new FloorSwitch(position.translateBy(Direction.LEFT), "floor_switch");
        Entity boulder2 = new Boulder(position.translateBy(Direction.DOWN), "boulder");
        dungeon.addEntity(floorSwitch2);
        dungeon.addEntity(boulder2);
        Entity floorSwitch3 = new FloorSwitch(position.translateBy(Direction.LEFT), "floor_switch");
        Entity boulder3 = new Boulder(position.translateBy(Direction.UP), "boulder");
        dungeon.addEntity(floorSwitch3);
        dungeon.addEntity(boulder3);
        Entity floorSwitch4 = new FloorSwitch(position.translateBy(Direction.LEFT), "floor_switch");
        Entity boulder4 = new Boulder(position.translateBy(Direction.RIGHT), "boulder");
        dungeon.addEntity(floorSwitch4);
        dungeon.addEntity(boulder4);

        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        spawner.spawn(dungeon);
        
        List <Enemy> enemies = dungeon.getEnemies();
        assertEquals(0, enemies.size());
 
    }

    @Test
    @DisplayName("Test zombie cannot spawn on boulder on top of item") 
    public void testZombieNotSpawnItemBoulder() {
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        // add items under every boulder 
        ItemEntity key = new Key(position.translateBy(Direction.LEFT), "key", 1);
        Entity boulder = new Boulder(position.translateBy(Direction.LEFT), "boulder");
        dungeon.addItem(key);
        dungeon.addEntity(boulder);
        ItemEntity sword = new Sword(position.translateBy(Direction.LEFT), 10.0, 10, "sword");
        Entity boulder2 = new Boulder(position.translateBy(Direction.DOWN), "boulder");
        dungeon.addItem(sword);
        dungeon.addEntity(boulder2);
        ItemEntity potion = new InvisiblePotion(position.translateBy(Direction.LEFT), "invicibility_potion", 15);
        Entity boulder3 = new Boulder(position.translateBy(Direction.UP), "boulder");
        dungeon.addItem(potion);
        dungeon.addEntity(boulder3);
        ItemEntity arrow = new Arrow(position.translateBy(Direction.LEFT), "arrow");
        Entity boulder4 = new Boulder(position.translateBy(Direction.RIGHT), "boulder");
        dungeon.addItem(arrow);
        dungeon.addEntity(boulder4);

        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        spawner.spawn(dungeon);
        
        List <Enemy> enemies = dungeon.getEnemies();
        assertEquals(0, enemies.size());
    }


    @Test
    @DisplayName("Test zombie cannot spawn on switch after boulder moved") 
    public void testZombieNotSpawnBoulder() {
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);

        // add switch under every boulder 
        Entity floorSwitch = new FloorSwitch(position.translateBy(Direction.LEFT), "floor_switch");
        Entity boulder = new Boulder(position.translateBy(Direction.LEFT), "boulder");
        dungeon.addEntity(floorSwitch);
        dungeon.addEntity(boulder);
        Entity floorSwitch2 = new FloorSwitch(position.translateBy(Direction.LEFT), "floor_switch");
        Entity boulder2 = new Boulder(position.translateBy(Direction.DOWN), "boulder");
        dungeon.addEntity(floorSwitch2);
        dungeon.addEntity(boulder2);
        Entity floorSwitch3 = new FloorSwitch(position.translateBy(Direction.LEFT), "floor_switch");
        Entity boulder3 = new Boulder(position.translateBy(Direction.UP), "boulder");
        dungeon.addEntity(floorSwitch3);
        dungeon.addEntity(boulder3);
        Entity floorSwitch4 = new FloorSwitch(position.translateBy(Direction.LEFT), "floor_switch");
        Entity boulder4 = new Boulder(position.translateBy(Direction.RIGHT), "boulder");
        dungeon.addEntity(floorSwitch4);
        dungeon.addEntity(boulder4);

        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        spawner.spawn(dungeon);
        
        List <Enemy> enemies = dungeon.getEnemies();
        assertEquals(0, enemies.size());

        // move boulder
        ((Boulder) boulder4).pushBoulder(Direction.RIGHT, dungeon);

        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        spawner.spawn(dungeon);
        assertEquals(1, dungeon.getEnemies().size());
        Enemy enemy = dungeon.getEnemies().get(0);
        assertTrue(enemy instanceof ZombieToast);
        assertEquals(position.translateBy(Direction.RIGHT), enemy.getPosition());
 
    }

    
}
