package dungeonmania;

import dungeonmania.entities.*;
import dungeonmania.items.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoulderTest {

    @Test
    @DisplayName("Test player can push the boulder")
    public void testPushBoulderNormalMovement() {
        Dungeon dungeon = new Dungeon();

        Position position = new Position(2,1);
        Player player = new Player(position, 1, 10, "player");
        
        Direction movementDirection = Direction.LEFT;
        Entity boulder = new Boulder(position.translateBy(movementDirection), "boulder");
        Position boulderPos = boulder.getPosition();
        dungeon.addEntity(boulder);

        // push the boulder
        player.move(dungeon, movementDirection);
        assertEquals(boulderPos, player.getPosition());
        Position boulderPos2 = boulder.getPosition(); 
        assertNotEquals(boulderPos2, boulderPos);

        // continue pushing left
        player.move(dungeon, movementDirection);
        assertEquals(boulderPos2, player.getPosition());
        assertNotEquals(boulder.getPosition(), boulderPos);
        assertNotEquals(boulder.getPosition(), boulderPos2);
    }

    @Test
    @DisplayName("Test player can push the boulder onto item")
    public void testPushBoulderOntoItem() {
        Dungeon dungeon = new Dungeon();

        Position position = new Position(1,1);
        Player player = new Player(position, 1, 10, "player");

        Position boluderPos = new Position(1, 2);
        Entity boulder = new Boulder(boluderPos, "boulder");
        dungeon.addEntity(boulder);
        
        Position treasurePos = new Position(1, 3);
        ItemEntity treasure = new Treasure(treasurePos, "treasure", 1);
        dungeon.addItem(treasure);

        // push the boulder onto item
        player.move(dungeon, Direction.DOWN);
        assertEquals(boluderPos, player.getPosition());
        assertEquals(treasurePos, boulder.getPosition());
        assertEquals(0, player.getInventory().size());

        // push the boulder away and collect the item
        player.move(dungeon, Direction.DOWN);
        assertEquals(treasurePos, player.getPosition());
        assertEquals(1, player.getInventory().size());
        assertNotEquals(treasurePos, boulder.getPosition());        
    }

    @Test
    @DisplayName("Test player can push the boulder onto switch")
    public void testPushBoulderOntoSwitch() {
        Dungeon dungeon = new Dungeon();

        Position position = new Position(1,1);
        Player player = new Player(position, 0, 10, "player");

        Position boluderPos = new Position(1, 2);
        Entity boulder = new Boulder(boluderPos, "boulder");
        dungeon.addEntity(boulder);

        Position switchPos = new Position(1, 3);
        FloorSwitch floorSwitch = new FloorSwitch(switchPos, "switch");
        dungeon.addEntity(floorSwitch);

        // push the boluder onto switch will trigger the switch
        player.move(dungeon, Direction.DOWN);
        assertEquals(boluderPos, player.getPosition());
        assertEquals(switchPos, boulder.getPosition());
        assertEquals(true, floorSwitch.isOn());

        // push the boulder away from the switch will untrigger the switch
        player.move(dungeon, Direction.DOWN);
        assertEquals(switchPos, player.getPosition());
        assertEquals(new Position(1, 4), boulder.getPosition());
        assertEquals(false, floorSwitch.isOn());
    }

    @Test
    @DisplayName("Test player can push only one boulder at a time")
    public void testPushBoulderOnlyOne() {
        Dungeon dungeon = new Dungeon();

        Position position = new Position(1,1);
        Player player = new Player(position, 0, 10, "player");

        Position boluderPos = new Position(1, 2);
        Entity boulder = new Boulder(boluderPos, "boulder");
        dungeon.addEntity(boulder);

        Position boluderPos2 = new Position(1, 3);
        Entity boulder2 = new Boulder(boluderPos2, "boulder");
        dungeon.addEntity(boulder2);

        // player tried to move down
        player.move(dungeon, Direction.DOWN);
        assertNotEquals(boluderPos, player.getPosition());
        assertNotEquals(boluderPos2, boluderPos);
    }

    @Test
    @DisplayName("Test player cannot push the boulder to the wall")
    public void testPushBouldertoTheWall() {
        Dungeon dungeon = new Dungeon();

        Position position = new Position(1,1);
        Player player = new Player(position, 0, 10, "player");

        Position boluderPos = new Position(1, 2);
        Entity boulder = new Boulder(boluderPos, "boulder");
        dungeon.addEntity(boulder);

        Position wallPos = new Position(1, 3);
        Wall wall = new Wall(wallPos, "wall");
        dungeon.addEntity(wall);

        // player tried to move down
        player.move(dungeon, Direction.DOWN);
        assertNotEquals(boluderPos, player.getPosition());
        assertNotEquals(wallPos, boluderPos);
    }

    @Test
    @DisplayName("Test player cannot push the boulder to the locked door")
    public void testPushBouldertoLockedDoor() {
        Dungeon dungeon = new Dungeon();

        Position position = new Position(1,1);
        Player player = new Player(position, 0, 10, "player");

        Position boluderPos = new Position(1, 2);
        Entity boulder = new Boulder(boluderPos, "boulder");
        dungeon.addEntity(boulder);

        Position doorPos = new Position(1, 3);
        Door door = new Door(doorPos, "door", 1);
        dungeon.addEntity(door);

        // player tried to move down
        player.move(dungeon, Direction.DOWN);
        assertNotEquals(boluderPos, player.getPosition());
        assertNotEquals(doorPos, boluderPos);
    }

    @Test
    @DisplayName("Test player cannot push the boulder to the portal")
    public void testPushBouldertoPortal() {
        Dungeon dungeon = new Dungeon();

        Position position = new Position(1,1);
        Player player = new Player(position, 0, 10, "player");

        Position boluderPos = new Position(1, 2);
        Entity boulder = new Boulder(boluderPos, "boulder");
        dungeon.addEntity(boulder);

        Position portalPos = new Position(1, 3);
        Portal portal = new Portal(portalPos, "portal", "blue");
        dungeon.addEntity(portal);

        // player tried to move down
        player.move(dungeon, Direction.DOWN);
        assertNotEquals(boluderPos, player.getPosition());
        assertNotEquals(portal, boluderPos);
    }

    @Test
    public void testPushingDifferentBoulders() {
        Dungeon dungeon = new Dungeon();

        Position position = new Position(1,1);
        Player player = new Player(position, 0, 10, "player");

        Position boluderPos = new Position(1, 2);
        Entity boulder = new Boulder(boluderPos, "boulder");
        dungeon.addEntity(boulder);
        
        Position boulder1Pos = new Position(2, 3);
        Entity boulder1 = new Boulder(boulder1Pos, "boulder");
        dungeon.addEntity(boulder1);

        Position switchPos = new Position(1, 3);
        FloorSwitch floorSwitch = new FloorSwitch(switchPos, "switch");
        dungeon.addEntity(floorSwitch);

        // player tried to move down to push one boulder
        // [1,2]
        player.move(dungeon, Direction.DOWN);
        assertEquals(boluderPos, player.getPosition());
        assertTrue(floorSwitch.isOn());

        // push the boulder away -> [1, 3]
        player.move(dungeon, Direction.DOWN);
        assertEquals(switchPos, player.getPosition());
        assertFalse(floorSwitch.isOn());
        
        player.move(dungeon, Direction.DOWN);
        player.move(dungeon, Direction.RIGHT);
        player.move(dungeon, Direction.RIGHT);
        player.move(dungeon, Direction.UP);
        player.move(dungeon, Direction.LEFT);

        assertTrue(floorSwitch.isOn());
    }

    @Test
    public void testPushingDifferentBouldersUntriggers() {
        Dungeon dungeon = new Dungeon();

        Position position = new Position(1,1);
        Player player = new Player(position, 0, 10, "player");

        // create 2 boulders
        Position boluderPos = new Position(1, 2);
        Entity boulder = new Boulder(boluderPos, "boulder");
        dungeon.addEntity(boulder);
        
        Position boulder1Pos = new Position(2, 3);
        Entity boulder1 = new Boulder(boulder1Pos, "boulder");
        dungeon.addEntity(boulder1);

        // create 2 switches
        Position switchPos = new Position(1, 3);
        FloorSwitch floorSwitch = new FloorSwitch(switchPos, "switch");
        dungeon.addEntity(floorSwitch);

        Position switch1Pos = new Position(3, 3);
        FloorSwitch floorSwitch1 = new FloorSwitch(switch1Pos, "switch");
        dungeon.addEntity(floorSwitch1);

        // switch triggered by boulder
        player.move(dungeon, Direction.DOWN);
        assertEquals(boluderPos, player.getPosition());
        assertTrue(floorSwitch.isOn());

        // player at [1, 3] pushes boulder away
        player.move(dungeon, Direction.DOWN);
        assertEquals(switchPos, player.getPosition());
        assertFalse(floorSwitch.isOn());

        // player at [1, 3] pushes boulder1 to switch1 
        player.move(dungeon, Direction.RIGHT);
        assertEquals(boulder1Pos, player.getPosition());
        assertTrue(floorSwitch1.isOn());
        assertFalse(floorSwitch.isOn());
    }

}
