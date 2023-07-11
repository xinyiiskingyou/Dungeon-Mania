package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;



import org.junit.jupiter.api.Test;

import dungeonmania.entities.Door;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.items.Key;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class DoorsAndKeysTest {

    @Test
    public void testCollectKey() {
        // Test player cannot have 2 keys at the same time
        Dungeon dungeon = new Dungeon();

        Player player = new Player(new Position(1, 1), 10, 10, "player");
    
        Key key = new Key(new Position(1, 2), "key", 1);
        dungeon.addItem(key);
        
        player.move(dungeon, Direction.DOWN);
        
        assertEquals(1, player.getInventory().size());
        
        Key key1 = new Key(new Position(1, 3), "key", 2);
        dungeon.addItem(key1);
        
        player.move(dungeon, Direction.DOWN);
        assertEquals(1, player.getInventory().size());
        assertEquals(1, dungeon.getInventory().size());
    }
    
    @Test
    public void testKeyOpenDoor() {
        // Test the player can open the door with correct key
        Dungeon dungeon = new Dungeon();

        Player player = new Player(new Position(1, 1), 10, 10, "player");
        dungeon.addEntity(player);

        Key key = new Key(new Position(1, 2), "key", 1);
        dungeon.addItem(key);
    
        player.move(dungeon, Direction.DOWN);
        assertEquals(1, player.getInventory().size());
        
        Door door = new Door(new Position(1, 3), "door", 1);
        dungeon.addEntity(door);

        player.move(dungeon, Direction.DOWN);
        assertEquals(true, door.isOpen());
        assertEquals(0, player.getInventory().size());

        // player will move up
        Position expected = new Position(1, 3);
        assertEquals(expected, player.getPosition());
    }

    @Test
    public void testKeyCannotOpenDoor() {
        // Test the player cannot open the door with wrong key
        Dungeon dungeon = new Dungeon();

        Player player = new Player(new Position(1, 1), 10, 10, "player");
        Key key = new Key(new Position(1, 2), "key", 1);
        dungeon.addEntity(player);
        dungeon.addItem(key);
    
        player.move(dungeon, Direction.DOWN);
        assertEquals(1, player.getInventory().size());
        
        Door door = new Door(new Position(1, 3), "door", 2);
        dungeon.addEntity(door);
    
        player.move(dungeon, Direction.DOWN);
        assertEquals(false, door.isOpen());
        assertEquals(1, player.getInventory().size());

        // player will stay in the original position
        Position expected = new Position(1, 2);
        assertEquals(expected, player.getPosition());
    } 

    // DOOR
    // blocked by locked door with no key
    @Test
    public void playerBlockedByLockedDoor() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 1, 10, "player");
        
        Direction movementDirection = Direction.RIGHT;
        Entity door = new Door(position.translateBy(movementDirection), "door", 1);
        dungeon.addEntity(door);

        player.move(dungeon, movementDirection);
        assertEquals(position, player.getPosition());
    }
}

