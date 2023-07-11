package dungeonmania;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;


public class BombTest {
    @Test
    @DisplayName("Test given id not in player's inventory")
    public void TestBombInvalidException() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_bombTest_placeBombRadius2");
        String bombId = getEntities(res, "bomb").get(0).getType();
        assertThrows(InvalidActionException.class, () -> dmc.tick(bombId));
        //assertThrows(IllegalArgumentException.class, () -> dmc.build("treasure"));
    }

    @Test
    @DisplayName("Test given id not correspond a bomb")
    public void TestBombIllegalArgument() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_bombTest_placeBombRadius2");

        // pick up treasure
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(1, getInventory(res, "treasure").size());
        String id = getInventory(res, "treasure").get(0).getId();
        assertThrows(IllegalArgumentException.class, () -> dmc.tick(id));
    }

    @Test
    @DisplayName("Test bomb successfully explode with radius 3")
    public void TestBombExplodewithRadius3() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius3", "c_bombTest_placeBombRadius3");

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT); //(3,2)
        res = dmc.tick(Direction.DOWN); //(3,3)

        // activate switch
        res = dmc.tick(Direction.RIGHT); //(4,3)
        res = dmc.tick(Direction.DOWN); //(4,4)
        res = dmc.tick(Direction.RIGHT); //(5,4)
        res = dmc.tick(Direction.RIGHT); //(6,4)
        res = dmc.tick(Direction.RIGHT); //(7,4)
        res = dmc.tick(Direction.DOWN); //(7,5)

        // push boulder
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getInventory(res, "bomb").size());
        String id = getInventory(res, "bomb").get(0).getId();
        assertEquals(0, getInventory(res, "treasure").size());
        // position at (6,5)
        assertDoesNotThrow(() -> dmc.tick(id));

        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(0, getEntities(res, "wall").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }

    @Test
    @DisplayName("Test explode inactive switch")
    public void TestInactiveSwitch() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius3", "c_bombTest_placeBombRadius3");

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        // move next to switch but dont activate
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, getInventory(res, "bomb").size());
        String id = getInventory(res, "bomb").get(0).getId();
        assertEquals(0, getInventory(res, "treasure").size());
        // place bomb at (5,6)
        assertDoesNotThrow(() -> dmc.tick(id));

        res = dmc.getDungeonResponseModel();
        // nothing gets destroyed
        assertEquals(0, getInventory(res, "bomb").size());
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getEntities(res, "switch").size());
        assertEquals(2, getEntities(res, "wall").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        // push boulder to activate

        res = dmc.tick(Direction.RIGHT); // (6,6)
        res = dmc.tick(Direction.RIGHT); // (7,6)
        res = dmc.tick(Direction.UP); // (7,5)
        res = dmc.tick(Direction.LEFT); // (6,5)

        // expect now bomb explode
        assertEquals(0, getInventory(res, "bomb").size());
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(2, getEntities(res, "wall").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }
    @Test
    @DisplayName("Test pick up treasure and bomb successfully explode with radius 3 ")
    public void TestInventory() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius3", "c_bombTest_placeBombRadius3");

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT); //(3,2)
        res = dmc.tick(Direction.DOWN); //(3,3)

        // activate switch
        res = dmc.tick(Direction.RIGHT); //(4,3)
        res = dmc.tick(Direction.RIGHT); //(5,3)
        res = dmc.tick(Direction.DOWN); //(5,4)
        res = dmc.tick(Direction.RIGHT); //(6,4)
        res = dmc.tick(Direction.RIGHT); //(7,4)
        res = dmc.tick(Direction.DOWN); //(7,5)

        // push boulder
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "treasure").size());
        String id = getInventory(res, "bomb").get(0).getId();
        // position at (6,5)
        assertDoesNotThrow(() -> dmc.tick(id));

        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(0, getEntities(res, "wall").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        // item in inventory won't get affected
        assertEquals(1, getInventory(res, "treasure").size());
    }

    @Test
    @DisplayName("Test bomb successfully explode with radius 0")
    public void TestBombExplodewithRadius0() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius0", "c_bombTest_placeBombRadius0");

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        // push boulder
        
        res = dmc.tick(Direction.DOWN); // (3,4)
        res = dmc.tick(Direction.DOWN); // (3,5)
        res = dmc.tick(Direction.RIGHT); // (4,5)

        res = dmc.tick(Direction.DOWN); // (4,6)
        res = dmc.tick(Direction.RIGHT); // (5,6)
        res = dmc.tick(Direction.RIGHT); //(6,6)
        res = dmc.tick(Direction.UP); // (6,5)

        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "treasure").size());
        String id = getInventory(res, "bomb").get(0).getId();
        // position at (6,5)
        assertDoesNotThrow(() -> dmc.tick(id));

        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "bomb").size());
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getEntities(res, "switch").size());
        assertEquals(2, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }

    @Test
    @DisplayName("Test bomb successfully explode with radius 1")
    public void TestBombExplodewithRadius1() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius0", "c_bombTest_placeBombRadius1");

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        // push boulder
        
        res = dmc.tick(Direction.DOWN); // (3,4)
        res = dmc.tick(Direction.DOWN); // (3,5)
        res = dmc.tick(Direction.RIGHT); // (4,5)

        res = dmc.tick(Direction.DOWN); // (4,6)
        res = dmc.tick(Direction.RIGHT); // (5,6)
        res = dmc.tick(Direction.RIGHT); //(6,6)
        res = dmc.tick(Direction.UP); // (6,5)

        assertEquals(1, getInventory(res, "bomb").size());
        String id = getInventory(res, "bomb").get(0).getId();
        // position at (6,5)
        assertDoesNotThrow(() -> dmc.tick(id));

        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(1, getEntities(res, "wall").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }


    @Test
    @DisplayName("Test bomb placed diagonally adjacent to an active switch")
    public void TestBombDiagonallyAdjacent() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_bombTest_placeBombRadius2");

        // Push the boulder
        res = dmc.tick(Direction.RIGHT); //(3,2)
        res = dmc.tick(Direction.DOWN); //(3,3)

        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getInventory(res, "bomb").size());
        String id = getInventory(res, "bomb").get(0).getId();
        assertEquals(0, getInventory(res, "treasure").size());
        // position at (3,3)
        assertDoesNotThrow(() -> dmc.tick(id));

        // expect bomb is not exploded
        res = dmc.getDungeonResponseModel();
        // the bomb is placed on the map
        assertEquals(0, getInventory(res, "bomb").size());
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getEntities(res, "switch").size());
        assertEquals(2, getEntities(res, "wall").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }
}