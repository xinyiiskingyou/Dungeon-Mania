package dungeonmania;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getPlayer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class GenerateDungeonTest {

    @Test
    @DisplayName("Test IllegalArgumentException with not exist config file")
    public void testInvalidGenerate() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc.generateDungeon(1, 5, 10, 10, "notExist"));
        assertThrows(IllegalArgumentException.class, () -> dmc.generateDungeon(1, 5, 10, 10, "randomFile"));
    }
    
    @Test
    @DisplayName("Test the basic game generation")
    public void testSimpleGenerateGame() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(1, 1, 6, 6, "c_movementTest_testMovementDown");

        // player at [1, 1]
        assertEquals(new Position(1, 1), getPlayer(res).get().getPosition());
        // exit is the bottom right corner
        assertEquals(new Position(6, 6), getEntities(res, "exit").get(0).getPosition());

        // walls exist
        assertNotEquals(0, getEntities(res, "wall").size());
    }

    @Test
    @DisplayName("Test the map can be generated with negative coordinate")
    public void testGenerateWithNegativeCoordinate() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(-50, -50, -45, -45, "c_movementTest_testMovementDown");

        // player at [-50, -50]
        assertEquals(new Position(-50, -50), getPlayer(res).get().getPosition());
        // exit is the bottom right corner
        assertEquals(new Position(-45, -45), getEntities(res, "exit").get(0).getPosition());

        // walls exist
        assertNotEquals(0, getEntities(res, "wall").size());
    }

    @Test
    @DisplayName("Test all non-wall grids are reachable")
    public void TestingNonWallGridsReachable() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(-1, -3, 4, 3, "c_movementTest_testMovementDown");

        Position originalPos = getPlayer(res).get().getPosition();
        assertEquals(new Position(-1, -3), originalPos);
        assertEquals(new Position(4, 3), getEntities(res, "exit").get(0).getPosition());

        dmc.saveGame("GenerateDungeon2");

        // reload the game
        DungeonResponse reload = dmc.loadGame("GenerateDungeon2");
        
        // moving around on the map
        reload = dmc.tick(Direction.DOWN);
        reload = dmc.tick(Direction.DOWN);
        reload = dmc.tick(Direction.DOWN);
        reload = dmc.tick(Direction.DOWN);
        reload = dmc.tick(Direction.RIGHT);
        reload = dmc.tick(Direction.RIGHT);
        reload = dmc.tick(Direction.UP);

        // player changed position
        assertNotEquals(originalPos, getPlayer(reload).get().getPosition());
    }

    @Test
    @DisplayName("Test all wall grids (boundary) are not reachable")
    public void TestingWallGridsNotReachable() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(15, 20, 25, 30, "c_movementTest_testMovementDown");

        Position originalPos = getPlayer(res).get().getPosition();
        assertEquals(new Position(15, 20), originalPos);
        assertEquals(new Position(25, 30), getEntities(res, "exit").get(0).getPosition());

        dmc.saveGame("GenerateDungeon3");

        // reload the game
        DungeonResponse reload = dmc.loadGame("GenerateDungeon3");
        
        // cannot move left / up as they are boundaries -> player still at the same position
        reload = dmc.tick(Direction.LEFT);
        reload = dmc.tick(Direction.UP);
        assertEquals(originalPos, getPlayer(reload).get().getPosition());
    }
    
}
