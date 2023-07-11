package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dungeonmania.TestUtils.getInventory;

public class BuildTest {

    @Test
    @DisplayName("Test player cannot build item with exception")
    public void TestBuildIllegalArgumentException() {

        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_buildMovement", "c_movementTest_testMovementDown");
        assertThrows(IllegalArgumentException.class, () -> dmc.build("key"));
        assertThrows(IllegalArgumentException.class, () -> dmc.build("treasure"));
    }

    @Test
    @DisplayName("Test player cannot build item with exception")
    public void TestBuildInvalidActionException() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildMovement", "c_movementTest_testMovementDown");
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "wood").size());

        res = dmc.tick(Direction.DOWN);
        assertEquals(2, getInventory(res, "wood").size());

        assertThrows(InvalidActionException.class, () -> dmc.build("bow"));
        assertThrows(InvalidActionException.class, () -> dmc.build("shield"));

    }

    @Test
    @DisplayName("Test player successfully builds shield with 2 woods + 1 treasure")
    public void TestBuildValidShieldWithTreasure() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildMovement", "c_movementTest_testMovementDown");

        // collect two woods
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "wood").size());

        res = dmc.tick(Direction.DOWN);
        assertEquals(2, getInventory(res, "wood").size());

        // collect one treasure
        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, getInventory(res, "treasure").size());

        
        assertDoesNotThrow( () -> dmc.build("shield"));

        res = dmc.getDungeonResponseModel();
        // Once the shield built, the items should be removed
        assertEquals(1, getInventory(res, "shield").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "treasure").size());

        // shield is added to inventory
        assertEquals(1, getInventory(res, "shield").size());
        // player can now no longer build anything
        assertEquals(0, res.getBuildables().size());
        assertThrows(InvalidActionException.class, () -> dmc.build("bow"));
    }

    @Test
    @DisplayName("Test player successfully builds shield with 2 woods + 1 key")
    public void TestBuildValidShieldWithKey() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildMovement", "c_movementTest_testMovementDown");
        
        // collect two woods
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "wood").size());

        res = dmc.tick(Direction.DOWN);
        assertEquals(2, getInventory(res, "wood").size());

        // collect one treasure
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        
        assertDoesNotThrow( () -> dmc.build("shield"));
        res = dmc.getDungeonResponseModel();
        // Once the shield built, the items should be removed
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "key").size());

        // shield is added to inventory
        assertEquals(1, getInventory(res, "shield").size());

        // cannot build two shields anymore
        assertEquals(0, res.getBuildables().size());
        assertThrows(InvalidActionException.class, () -> dmc.build("bow"));
    }

    @Test
    @DisplayName("Test player successfully builds bow with 3 arrows + 1 wood")
    public void TestBuildValidBow() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildMovement", "c_movementTest_testMovementDown");
        
        // collect one wood
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "wood").size());

        // collect three arrows
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "arrow").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, getInventory(res, "arrow").size());

        res = dmc.tick(Direction.UP);
        assertEquals(3, getInventory(res, "arrow").size());
        
        assertDoesNotThrow( () -> dmc.build("bow"));
        res = dmc.getDungeonResponseModel();
        // Once the bow is built, the items should be removed
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(0, getInventory(res, "wood").size());
        

        // shield is added to inventory
        assertEquals(1, getInventory(res, "bow").size());
        assertEquals(0, res.getBuildables().size());
        assertThrows(InvalidActionException.class, () -> dmc.build("shield"));
    }

    @Test
    @DisplayName("Test player successfully builds 2 shield")
    public void TestBuildTwoValidShields() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildMovement", "c_movementTest_testMovementDown");
        
        // collect 2 woods
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(4, getInventory(res, "wood").size());

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);

        // collect 6 arrows
        res = dmc.tick(Direction.UP);
        assertEquals(1, getInventory(res, "treasure").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(2, res.getBuildables().size());

        assertDoesNotThrow( () -> dmc.build("shield"));
        // check only one built
        assertEquals(1, res.getBuildables().size());
        assertDoesNotThrow( () -> dmc.build("shield"));

        res = dmc.getDungeonResponseModel();
        // Once the bows are built, the items should be removed
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "arrow").size());

        // bows are added to inventory
        assertEquals(2, getInventory(res, "shield").size());
        assertEquals(0, res.getBuildables().size());
    }
}