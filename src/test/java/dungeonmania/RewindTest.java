package dungeonmania;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

public class RewindTest {
    @Test
    @DisplayName("TestRewindTickLessThanOne")
    public void TestTickLessThanOne() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse init = controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        assertThrows(IllegalArgumentException.class, () -> controller.rewind(0));
        assertThrows(IllegalArgumentException.class, () -> controller.rewind(-1));
    }

    @Test
    @DisplayName("TestTickNotOccurred")
    public void TestTickNotOccurred() {
        DungeonManiaController controller = new DungeonManiaController();
        controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        assertThrows(IllegalArgumentException.class, () -> controller.rewind(1));
    }

    
    @Test
    @DisplayName("TestTickNotOccurredWith5Ticks")
    public void TestTickNotOccurred5Ticks() {
        DungeonManiaController controller = new DungeonManiaController();
        controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        controller.tick(Direction.RIGHT);
        controller.tick(Direction.RIGHT);
        controller.tick(Direction.RIGHT);
        controller.tick(Direction.RIGHT);
        assertThrows(IllegalArgumentException.class, () -> controller.rewind(5));
    }
    @Test
    @DisplayName("TestTickOccurred")
    public void TestTickOccurred() {
        DungeonManiaController controller = new DungeonManiaController();
        controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        controller.tick(Direction.RIGHT);
        assertDoesNotThrow(() -> controller.rewind(1));
    }

    @Test
    @DisplayName("TestInventoryPersists")
    public void TestInventoryPresist() {
        DungeonManiaController controller = new DungeonManiaController();
        controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        // collect treasure
        controller.tick(Direction.DOWN);
        // collect bomb
        controller.tick(Direction.RIGHT);
        // collect time turner
        controller.tick(Direction.UP);
        DungeonResponse res = controller.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "time_turner").size());
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(0, getEntities(res, "time_turner").size());

        assertDoesNotThrow(() -> controller.rewind(3));
        res = controller.getDungeonResponseModel();
        // test inventory presists
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "time_turner").size());
        // test entities available in the map
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "time_turner").size());
    }

    @Test
    @DisplayName("TestOldPlayerExistsWhenTimeTravelled")
    public void TestOlderPlayer() {
        DungeonManiaController controller = new DungeonManiaController();
        controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        // collect treasure
        controller.tick(Direction.DOWN);
        // collect bomb
        controller.tick(Direction.RIGHT);
        // collect time turner
        controller.tick(Direction.UP);
        DungeonResponse res = controller.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(0, getEntities(res, "time_turner").size());
        assertDoesNotThrow(() -> controller.rewind(3));
        res = controller.getDungeonResponseModel();
        // test inventory presists
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "treasure").size());
        
        // test entities available in the map
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "treasure").size());

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());
    }

    @Test
    @DisplayName("Test Time travel takes the player to initial game state")
    public void TestTimeTravelInitialState() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        Position startPos = getEntities(res, "player").get(0).getPosition();
        // collect treasure
        res = controller.tick(Direction.DOWN);
        Position position1 = getEntities(res, "player").get(0).getPosition();
        // collect bomb
        res = controller.tick(Direction.RIGHT);
        Position position2 = getEntities(res, "player").get(0).getPosition();
        // collect time turner
        res = controller.tick(Direction.UP);
        Position position3 = getEntities(res, "player").get(0).getPosition();

        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        assertDoesNotThrow(() -> controller.rewind(3));
        res = controller.getDungeonResponseModel();
        // test inventory presists
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "treasure").size());

        // test entities available in the map
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "treasure").size());

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        // test player and older_player position
        // player expectes same position before it rewinds
        assertEquals(position3, getEntities(res, "player").get(0).getPosition());
        // older player is epxected to at the position before 3 ticks occurred
        assertEquals(startPos, getEntities(res, "older_player").get(0).getPosition());
    }

    @Test
    @DisplayName("Test Time travel with one tick")
    public void TestTimeTravelOneTick() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        Position startPos = getEntities(res, "player").get(0).getPosition();
        // collect treasure
        res = controller.tick(Direction.DOWN);
        Position position1 = getEntities(res, "player").get(0).getPosition();
        // collect bomb
        res = controller.tick(Direction.RIGHT);
        Position position2 = getEntities(res, "player").get(0).getPosition();
        // collect time turner
        res = controller.tick(Direction.UP);
        Position position3 = getEntities(res, "player").get(0).getPosition();

        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        assertDoesNotThrow(() -> controller.rewind(1));
        res = controller.getDungeonResponseModel();
        // test inventory presists
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "treasure").size());

        // test entities available in the map
        assertEquals(0, getEntities(res, "bomb").size());
        // treasure is collected after tick 1
        assertEquals(0, getEntities(res, "treasure").size());

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        // test player and older_player position
        // player expectes same position before it rewinds
        assertEquals(position3, getEntities(res, "player").get(0).getPosition());
        // older player is epxected to at the position before 3 ticks occurred
        assertEquals(position2, getEntities(res, "older_player").get(0).getPosition());
    }
    @Test
    @DisplayName("Test Time travel takes the player to after the tick takes place")
    public void TestTimeTravelAfterTickOccurs() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        Position startPos = getEntities(res, "player").get(0).getPosition();
        // collect treasure
        res = controller.tick(Direction.DOWN);
        Position position1 = getEntities(res, "player").get(0).getPosition();
        // collect bomb
        res = controller.tick(Direction.RIGHT);
        Position position2 = getEntities(res, "player").get(0).getPosition();
        // collect time turner
        res = controller.tick(Direction.UP);
        Position position3 = getEntities(res, "player").get(0).getPosition();

        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        assertDoesNotThrow(() -> controller.rewind(2));
        res = controller.getDungeonResponseModel();
        // test inventory presists
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "treasure").size());

        // test entities available in the map
        assertEquals(1, getEntities(res, "bomb").size());
        // treasure is collected after tick 1
        assertEquals(0, getEntities(res, "treasure").size());

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        // test player and older_player position
        // player expectes same position before it rewinds
        assertEquals(position3, getEntities(res, "player").get(0).getPosition());
        // older player is epxected to at the position before 3 ticks occurred
        assertEquals(position1, getEntities(res, "older_player").get(0).getPosition());
    }

    @Test
    @DisplayName("TestOldPlayerHaveSamePathAsBefore")
    public void TestOldPlayerSamePath() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        // collect treasure
        Position position1 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.DOWN);

        // collect bomb
        Position position2 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.RIGHT);
        // collect time turner
        Position position3 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.UP);
        Position position4 = getEntities(res, "player").get(0).getPosition();
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        assertDoesNotThrow(() -> controller.rewind(3));
        res = controller.getDungeonResponseModel();
        // test inventory presists
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "treasure").size());

        // test entities available in the map
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "treasure").size());

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        // test player and older_player position
        assertEquals(position1, getEntities(res, "older_player").get(0).getPosition());
        // player expectes same position before it rewinds
        assertEquals(position4, getEntities(res, "player").get(0).getPosition());

        // test older_player takes same path as before
        res = controller.tick(Direction.UP);
        assertEquals(position2, getEntities(res, "older_player").get(0).getPosition());
    
        res = controller.tick(Direction.RIGHT);
        assertEquals(position3, getEntities(res, "older_player").get(0).getPosition());
    }

    @Test
    @DisplayName("TestOldPlayerReachesNumTicks")
    public void TestOldPlayerRemovedFromMap() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        // collect treasure
        Position position1 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.DOWN);

        // collect bomb
        Position position2 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.RIGHT);
        // collect time turner
        Position position3 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.UP);
        Position position4 = getEntities(res, "player").get(0).getPosition();
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        assertDoesNotThrow(() -> controller.rewind(3));
        res = controller.getDungeonResponseModel();
        // test inventory presists
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "treasure").size());

        // test entities available in the map
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "treasure").size());

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        // test player and older_player position
        assertEquals(position1, getEntities(res, "older_player").get(0).getPosition());
        // player expectes same position before it rewinds
        assertEquals(position4, getEntities(res, "player").get(0).getPosition());

        // test older_player takes same path as before
        res = controller.tick(Direction.UP);
        assertEquals(position2, getEntities(res, "older_player").get(0).getPosition());
    
        res = controller.tick(Direction.RIGHT);
        assertEquals(position3, getEntities(res, "older_player").get(0).getPosition());

        res = controller.tick(Direction.RIGHT);
        // older player is expected removed from the map as reaches ticks during which they time travelled
        assertEquals(1, getEntities(res, "older_player").size());

        res = controller.tick(Direction.RIGHT);
        assertEquals(0, getEntities(res, "older_player").size());
    }

    @Test
    @DisplayName("test older older removed from map at correct tick")
    public void TestOlderPlayerDisapperaAtRightTIme() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        Position position0 = getEntities(res, "player").get(0).getPosition();

        // collect time turner
        res = controller.tick(Direction.RIGHT);
        Position position1 = getEntities(res, "player").get(0).getPosition();

        res = controller.tick(Direction.RIGHT);
        Position position2 = getEntities(res, "player").get(0).getPosition();

        assertDoesNotThrow(() -> controller.rewind(1));
        res = controller.getDungeonResponseModel();

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        // test player and older_player position
        assertEquals(position1, getEntities(res, "older_player").get(0).getPosition());
        // player expectes same position before it rewinds
        assertEquals(position2, getEntities(res, "player").get(0).getPosition());

        // move one tick
        res = controller.tick(Direction.UP);
        // expect it still exists
        assertEquals(1, getEntities(res, "older_player").size());
        assertEquals(position2, getEntities(res, "older_player").get(0).getPosition());

        // expect older player disappear
        res = controller.tick(Direction.UP);
        assertEquals(0, getEntities(res, "older_player").size());
    }
    @Test
    @DisplayName("TesDoorGameState")
    public void TestDoorKeyGameState() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        Position position1 = getEntities(res, "player").get(0).getPosition();
        // collect time turner
        res = controller.tick(Direction.RIGHT);
        Position position2 = getEntities(res, "player").get(0).getPosition();
        assertEquals(1, getInventory(res, "time_turner").size());

        // collect key
        res = controller.tick(Direction.RIGHT);
        Position position3 = getEntities(res, "player").get(0).getPosition();

        assertEquals(0, getEntities(res, "key").size());
        assertEquals(1, getInventory(res, "key").size());
        

        // open door
        res = controller.tick(Direction.RIGHT);
        Position position4 = getEntities(res, "player").get(0).getPosition();

        // test key has been used
        assertEquals(0, getInventory(res, "key").size());
        assertEquals(0, getEntities(res, "key").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(0, getInventory(res, "bomb").size());
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        assertDoesNotThrow(() -> controller.rewind(1));
        res = controller.getDungeonResponseModel();
        // test inventory presists
        assertEquals(0, getInventory(res, "key").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(0, getInventory(res, "bomb").size());

        // test entities available in the map
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "treasure").size());
        // key already been collected by player
        assertEquals(0, getEntities(res, "key").size());

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        // test player and older_player position
        assertEquals(position3, getEntities(res, "older_player").get(0).getPosition());
        // player expectes same position before it rewinds
        assertEquals(position4, getEntities(res, "player").get(0).getPosition());
    }
    // TO BE TESTED:
    // system test with bomb
    @Test
    @DisplayName("TestBomb")
    public void TestBomb() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_timeturner", "c_movementTest_testMovementDown");
        Position position1 = getEntities(res, "player").get(0).getPosition();

        // collect time turner
        res = controller.tick(Direction.RIGHT); //(4,1)
        res = controller.tick(Direction.LEFT); //(3,1)
        res = controller.tick(Direction.LEFT); //(2,1)
        Position position2 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.DOWN); //(2,2)
        res = controller.tick(Direction.DOWN); //(2,3)
        // push boulder
        res = controller.tick(Direction.RIGHT); //(3,3)
        res = controller.tick(Direction.UP); //(3,2)
        // collect bomb
        res = controller.tick(Direction.RIGHT); //(4,2)
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getEntities(res, "switch").size());
        assertEquals(1, getInventory(res, "bomb").size());
        String bomb = getInventory(res, "bomb").get(0).getId();
        assertDoesNotThrow(() -> controller.tick(bomb));

        // expect they are all destroyed
        res = controller.getDungeonResponseModel();
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(0, getInventory(res, "bomb").size());

        res = controller.rewind(1);
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getEntities(res, "switch").size());
        // expect inventory consistent
        assertEquals(0, getInventory(res, "bomb").size());
    }

}
