package dungeonmania;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
public class TimeTravelViaPortalTest {

    @Test
    @DisplayName("Test initial game state")
    public void TestInitialGameState() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("time_travelling_portal", "c_movementTest_testMovementDown");
        Position pos = getEntities(res, "player").get(0).getPosition(); 
        // collect 3 treasures
        res = controller.tick(Direction.RIGHT);
        Position pos1 = getEntities(res, "player").get(0).getPosition(); 
        res = controller.tick(Direction.RIGHT);
        Position pos2 = getEntities(res, "player").get(0).getPosition(); 
        res = controller.tick(Direction.RIGHT);
        Position pos3 = getEntities(res, "player").get(0).getPosition(); 
        res = controller.tick(Direction.RIGHT);
        Position pos4 = getEntities(res, "player").get(0).getPosition(); 
        assertEquals(3, getInventory(res, "treasure").size());
        // teleports to time travelling portal
        res = controller.tick(Direction.RIGHT);
        Position finalPos = getEntities(res, "player").get(0).getPosition(); 
        // test inventory persists
        assertEquals(3, getInventory(res, "treasure").size());

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        // tets player position won't change
        assertEquals(finalPos, getEntities(res, "player").get(0).getPosition());
        // test older_player position at initial game state
        assertEquals(pos, getEntities(res, "older_player").get(0).getPosition());

        // test we are at initial game state
        assertEquals(3, getEntities(res, "treasure").size());

        // test older_player take the same path as before
        res = controller.tick(Direction.LEFT);
        assertEquals(pos1, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.LEFT);
        assertEquals(pos2, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.LEFT);
        assertEquals(1, getEntities(res, "older_player").size());
        assertEquals(pos3, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.LEFT);
        assertEquals(pos4, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.LEFT);

        // test older player disappear immediately
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(0, getEntities(res, "older_player").size());
    }

    @Test
    @DisplayName("test time travel after 30 ticks")
    public void TestTimeTravelAfter30Ticks() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("time_travel_portal_only", "c_movementTest_testMovementDown");

        // record the first 10 positions
        res = controller.tick(Direction.UP); // (1,0)
        Position pos1 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.RIGHT); // (2,0)
        Position pos2 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.RIGHT); // (3,0)
        Position pos3 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.RIGHT); // (4,0)
        Position pos4 = getEntities(res, "player").get(0).getPosition();
        // pick one treasure
        res = controller.tick(Direction.DOWN); // (4,1)
        Position pos5 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.DOWN); // (4,2)
        Position pos6 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.LEFT); // (3,2)
        Position pos7 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.LEFT); // (2,2)
        Position pos8 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.LEFT); // (1,2)
        Position pos9 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.DOWN); // (1,3)
        Position pos10 = getEntities(res, "player").get(0).getPosition();
        res = controller.tick(Direction.DOWN); // (1,4)
        res = controller.tick(Direction.RIGHT); // (2,4)
        res = controller.tick(Direction.RIGHT); // (3,4)
        res = controller.tick(Direction.RIGHT); // (4,4)
        // pick 2 arrows
        res = controller.tick(Direction.DOWN); // (4,5)
        res = controller.tick(Direction.LEFT); // (3,5)
        // pick one wood
        res = controller.tick(Direction.LEFT); // (2,5)
        res = controller.tick(Direction.LEFT); // (1,5)
        res = controller.tick(Direction.DOWN); // (1,6)
        res = controller.tick(Direction.RIGHT); // (2,6)
        res = controller.tick(Direction.RIGHT); // (3,6)
        // pick one arrow
        res = controller.tick(Direction.RIGHT); // (4,6)
        res = controller.tick(Direction.UP); // (4,5)
        res = controller.tick(Direction.UP); // (4,4)
        res = controller.tick(Direction.UP); // (4,3)
        res = controller.tick(Direction.UP); // (4,2)
        res = controller.tick(Direction.UP); // (4,1)
        // pick one treasure
        res = controller.tick(Direction.LEFT); // (3,1)
        res = controller.tick(Direction.RIGHT); // (4,1)
        res = controller.tick(Direction.RIGHT); // (5,1)
        assertEquals(2, getInventory(res, "treasure").size());
        assertEquals(3, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "wood").size());
        // time travel through time travelling portal
        // 31st tick time travel to -> 1st tick
        res = controller.tick(Direction.RIGHT); // (6,1)
        Position finalPos = getEntities(res, "player").get(0).getPosition();

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        // test players in correct positions
        assertEquals(finalPos, getEntities(res, "player").get(0).getPosition());
        // older_player pos after 1st tick
        assertEquals(pos1, getEntities(res, "older_player").get(0).getPosition());

        // test player's inventory persists
        assertEquals(2, getInventory(res, "treasure").size());
        assertEquals(3, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "wood").size());

        // test older_player take the same path as before
        res = controller.tick(Direction.DOWN);
        // test players in correct positions
        assertEquals(finalPos.translateBy(Direction.DOWN), getEntities(res, "player").get(0).getPosition());
        assertEquals(pos2, getEntities(res, "older_player").get(0).getPosition());

        res = controller.tick(Direction.LEFT);
        assertEquals(pos3, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.LEFT);
        assertEquals(pos4, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.LEFT);
        assertEquals(pos5, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.LEFT);
        assertEquals(pos6, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.LEFT);
        assertEquals(pos7, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.DOWN);
        assertEquals(pos8, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.RIGHT);
        assertEquals(pos9, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.RIGHT);
        assertEquals(pos10, getEntities(res, "older_player").get(0).getPosition());
    }

    @Test
    @DisplayName("OlderPlayerMovesIntoPortal")
    public void TestOlderPlayerIntoPortal() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("time_travelling_portal", "c_movementTest_testMovementDown");
        Position pos0 = getEntities(res, "player").get(0).getPosition();

        // travel through a portal
        res = controller.tick(Direction.DOWN); // (1,2)
        Position pos1 = getEntities(res, "player").get(0).getPosition();

        res = controller.tick(Direction.DOWN); // (1,3) -> (7,5)
        Position pos2 = getEntities(res, "player").get(0).getPosition();

        res = controller.tick(Direction.LEFT); // (6,5)
        Position pos3 = getEntities(res, "player").get(0).getPosition();

        res = controller.tick(Direction.UP); // (6,4)
        Position pos4 = getEntities(res, "player").get(0).getPosition();

        res = controller.tick(Direction.UP); // (6,3)
        Position pos5 = getEntities(res, "player").get(0).getPosition();

        res = controller.tick(Direction.UP); // (6,2)
        Position pos6 = getEntities(res, "player").get(0).getPosition();


        res = controller.tick(Direction.UP); // (6,1)
        Position pos7 = getEntities(res, "player").get(0).getPosition();
        // expect now back to initial game state (1,1)

        // test player and older_player both exist
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        // test players in correct positions
        assertEquals(pos7, getEntities(res, "player").get(0).getPosition());
        // older_player pos after 1st tick
        assertEquals(pos0, getEntities(res, "older_player").get(0).getPosition());
        res = controller.tick(Direction.LEFT); // (5,1)
        assertEquals(pos7.translateBy(Direction.LEFT), getEntities(res, "player").get(0).getPosition());
        assertEquals(pos1, getEntities(res, "older_player").get(0).getPosition());

        res = controller.tick(Direction.LEFT); // (4,1)
        // expect older_player moves into portal and disaappears
        assertEquals(0, getEntities(res, "older_player").size());
    }

    @Test
    @DisplayName("TestOlderPlayerDisappearsImmediately")
    public void TestOlderPlayerDisappearsImmediately() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("one_time_travel_portal", "c_movementTest_testMovementDown");
        Position pos0 = getEntities(res, "player").get(0).getPosition();

        res = controller.tick(Direction.DOWN); // (1,2)
        Position pos1 = getEntities(res, "player").get(0).getPosition();

        // time travel through portal
        res = controller.tick(Direction.RIGHT); // (2,2)
        Position pos2 = getEntities(res, "player").get(0).getPosition();

        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());

        res = controller.tick(Direction.RIGHT); // (3,2)
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(1, getEntities(res, "older_player").size());
        assertEquals(pos1, getEntities(res, "older_player").get(0).getPosition());

        // older_player disappear immediately
        res = controller.tick(Direction.RIGHT); // (4,2)
        assertEquals(1, getEntities(res, "player").size());
        assertEquals(0, getEntities(res, "older_player").size());
        
    }

}
