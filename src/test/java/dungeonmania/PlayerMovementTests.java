package dungeonmania;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.entities.*;
import dungeonmania.items.*;
import dungeonmania.enemy.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerMovementTests {

    @Test
    @DisplayName("Test player movement to an empty square")
    public void basicPositionChange() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");
        // test all directions
        // down movement empty square
        Direction movementDirection = Direction.DOWN;
        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());

        // left movement empty square
        position = player.getPosition();
        movementDirection = Direction.LEFT;
        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());

        // right movement empty square
        position = player.getPosition();
        movementDirection = Direction.RIGHT;
        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());

        // Up movement empty square
        position = player.getPosition();
        movementDirection = Direction.UP;
        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }

    @Test
    @DisplayName("Test player blocked by a wall")
    public void basicwallBlock() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");
        
        Direction movementDirection = Direction.LEFT;
        Entity wall = new Wall(position.translateBy(movementDirection), "wall");
        dungeon.addEntity(wall);

        player.move(dungeon, movementDirection);
        assertEquals(position, player.getPosition());

    }

    @Test
    @DisplayName("Test player blocked by a spawner")
    public void basicSpawnerBlock() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");
        
        Direction movementDirection = Direction.RIGHT;
        Entity spawner = new ZombieToastSpawner(position.translateBy(movementDirection), "spawner");
        dungeon.addEntity(spawner);

        player.move(dungeon, movementDirection);
        assertEquals(position, player.getPosition());

    }

    @Test
    @DisplayName("Test player can move onto a floor switch")
    public void playerOnSwitch() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");
        
        Direction movementDirection = Direction.LEFT;
        Entity floorSwitch = new FloorSwitch(position.translateBy(movementDirection), "switch");
        dungeon.addEntity(floorSwitch);

        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());

    }

    @Test
    @DisplayName("Test player can use an exit, with no goals")
    public void playerCanExit() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");
        
        Direction movementDirection = Direction.UP;
        Entity exit = new Exit(position.translateBy(movementDirection), "exit");
        dungeon.addEntity(exit);

        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }

    // ITEMS
    // move onto item
    @Test
    @DisplayName("Test player can move on to an item")
    public void playerCanMoveOntoItem() {
        
        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 0, "player");
        Direction movementDirection = Direction.LEFT;

        ItemEntity treasure = new Treasure(position.translateBy(movementDirection), "treasure", 1);
        dungeon.addItem(treasure);

        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }

    // item and switch
    @Test
    @DisplayName("Test player can move on to an item over a switch")
    public void playerCanMoveOntoItemOverSwitch() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");
        
        Direction movementDirection = Direction.LEFT;
        ItemEntity treasure = new Treasure(position.translateBy(movementDirection), "treasure", 1);
        dungeon.addItem(treasure);
        Entity floorSwitch = new FloorSwitch(position.translateBy(movementDirection),"switch");
        dungeon.addEntity(floorSwitch);

        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }

    // switch and item
    @Test
    @DisplayName("Test player can move onto an item over a switch")
    public void playerCanMoveOntoItemOverSwitchReverseOrder() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");
        
        Direction movementDirection = Direction.LEFT;
        ItemEntity treasure = new Treasure(position.translateBy(movementDirection), "treasure", 1);
        dungeon.addItem(treasure);
        Entity floorSwitch = new FloorSwitch(position.translateBy(movementDirection),"switch");
        dungeon.addEntity(floorSwitch);

        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }
    
    // ENEMIES
    // one enemy 
    @Test
    @DisplayName("Test player can move into an enemy")
    public void playerCanMoveIntoEnemy() {
        
        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");

        Direction movementDirection = Direction.RIGHT;
        Entity zombieToast = new ZombieToast(position.translateBy(movementDirection), 10.0, 10.0, "zombie_toast");
        dungeon.addEntity(zombieToast);
        
        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }
    
    // multiple enemies
    @Test
    @DisplayName("Test player can move into mutiple enemies")
    public void playerCanMoveIntoEnemies() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");
        
        Direction movementDirection = Direction.RIGHT;
        Entity zombieToast1 = new ZombieToast(position.translateBy(movementDirection), 10.0, 10.0, "zombie_toast");
        dungeon.addEntity(zombieToast1);

        Entity zombieToast2 = new ZombieToast(position.translateBy(movementDirection), 10.0, 10.0, "zombie_toast");
        dungeon.addEntity(zombieToast2);
        
        player.move(dungeon, movementDirection);
        assertEquals(position.translateBy(movementDirection), player.getPosition());
    }

}

