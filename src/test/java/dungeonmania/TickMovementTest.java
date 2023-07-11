package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;



public class TickMovementTest {
    
    @Test
    @DisplayName("Test player's basic movement")
    public void testTickBasicMovement() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_playerBasicMovement", "c_movementTest_testMovementDown");
        
        // initial position at [1, 1]
        EntityResponse initPlayer = getPlayer(res).get();

        // test player can move right
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 1), false);

        // currPos [2, 1]
        DungeonResponse actualDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse actualPlayer = getPlayer(actualDungonRes).get();
        assertEquals(expectedPlayer, actualPlayer);

        // back to [1, 1]
        Position expectedLeft = new Position(1, 1);
        DungeonResponse actualLeft = dmc.tick(Direction.LEFT);
        assertEquals(expectedLeft, getPlayer(actualLeft).get().getPosition());

        // test player can move down (y - 1)
        // currPos [1, 2]
        Position expectedDown = new Position(1, 2);
        DungeonResponse actualDown = dmc.tick(Direction.DOWN);
        assertEquals(expectedDown, getPlayer(actualDown).get().getPosition());

        // back to [1, 1]
        Position expectedUp = new Position(1, 1);
        DungeonResponse actualUp = dmc.tick(Direction.UP);
        assertEquals(expectedUp, getPlayer(actualUp).get().getPosition());

    }

    @Test
    @DisplayName("Test player can not open the door with wrong key")
    public void testTickOpenDoorWithWrongKey() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_playerBasicMovement", "c_movementTest_testMovementDown");
        
        // initial position at [1, 1] 
        // move right to collect the key
        res = dmc.tick(Direction.RIGHT);
        // current position [2, 1]
        Position pos = getEntities(res, "player").get(0).getPosition();
        assertEquals(1, getInventory(res, "key").size());

        // cannot move right as the key cannot open the door
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(pos, getEntities(res, "player").get(0).getPosition());
    }  

    @Test
    @DisplayName("Test player blocked by a wall")
    public void testTickBlockByWall() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_playerBasicMovement", "c_movementTest_testMovementDown");
        Position initialPosition = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.UP);
        Position pos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPosition, pos);
    }

    @Test
    @DisplayName("Test player can collect item but cannot collect 2 keys")
    public void testTickBasicCollectItems() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_playerBasicMovement", "c_movementTest_testMovementDown");
        Position initialPosition = getEntities(res, "player").get(0).getPosition();
        
        // move down to collect the first key
        DungeonResponse key = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(key, "key").size());

        // move down for the 2nd key but cannot collect
        DungeonResponse key1 = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(key1, "key").size());

        // move down to collect the arrow
        DungeonResponse actual = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(actual, "arrow").size());

        // position 
        Position finalPosition = getEntities(actual, "player").get(0).getPosition();
        assertNotEquals(initialPosition, finalPosition);

        Position currPosition = new Position(2, 3);
        assertEquals(currPosition, finalPosition);
    }

    @Test
    @DisplayName("Test player can push boulder") 
    public void testTickMovementPushBoulder() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_andAll", "c_bombTest_placeBombRadius2");

        // player at [1, 1]
        // boulder at [3, 1]

        // player at [2, 1] now
        res = dmc.tick(Direction.RIGHT);
        Position boulderPosition = getEntities(res, "boulder").get(0).getPosition();

        // push boulder to [4, 1]
        // player at [3, 1] now
        DungeonResponse boulder = dmc.tick(Direction.RIGHT);
        Position playerPosition = getEntities(boulder, "player").get(0).getPosition();
        Position currBoulder = getEntities(boulder, "boulder").get(0).getPosition();
        assertEquals(boulderPosition, playerPosition);
        assertEquals(new Position(4, 1), currBoulder);
        
        // push boulder to the right -> [5, 1]
        DungeonResponse boulderLeft = dmc.tick(Direction.RIGHT);
        Position playerPosition1 = getEntities(boulderLeft, "player").get(0).getPosition();
        assertEquals(currBoulder, playerPosition1);
        assertEquals(new Position(5, 1), getEntities(boulderLeft, "boulder").get(0).getPosition());
    }
}
