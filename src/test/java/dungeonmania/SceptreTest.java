package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import dungeonmania.util.Direction;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;

import static dungeonmania.TestUtils.getInventory;
import dungeonmania.exceptions.InvalidActionException;

public class SceptreTest {
    // INTEGRATION/SYSTEM TESTS

    @Test
    @DisplayName("Test build sceptre with wood, key, stone")
    public void testBuildWithWoodKeyStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_newBuildable", "c_newBuildable");

        // collect wood, key, sunstone
        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));

        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));

        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, res.getBuildables().size());
        res = dmc.getDungeonResponseModel();

        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "key").size());
        // sunstone doesnt get removed
        assertEquals(1, getInventory(res, "sun_stone").size());

        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, res.getBuildables().size());

    }

    @Test
    @DisplayName("Test build sceptre with wood, treasure, stone")
    public void testBuildWithWoodTreasureStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_newBuildable", "c_newBuildable");

        // collect wood, key, sunstone
        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, res.getBuildables().size());
        res = dmc.getDungeonResponseModel();

        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "treasure").size());
        // sunstone doesnt get removed
        assertEquals(1, getInventory(res, "sun_stone").size());

        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, res.getBuildables().size());

    }

    @Test
    @DisplayName("Test build sceptre with arrow, key, stone")
    public void testBuildWithArrowKeyStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_newBuildable", "c_newBuildable");

        // collect wood, key, sunstone
        res = dmc.tick(Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);

        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, res.getBuildables().size());
        res = dmc.getDungeonResponseModel();

        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(0, getInventory(res, "key").size());
        // sunstone doesnt get removed
        assertEquals(1, getInventory(res, "sun_stone").size());

        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, res.getBuildables().size());

    }

    @Test
    @DisplayName("Test build sceptre with arrow, treasure, stone")
    public void testBuildWithArrowTreasureStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_newBuildable", "c_newBuildable");

        res = dmc.tick(Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.LEFT);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.RIGHT);

        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, res.getBuildables().size());
        res = dmc.getDungeonResponseModel();

        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(0, getInventory(res, "treasure").size());
        // sunstone doesnt get removed
        assertEquals(1, getInventory(res, "sun_stone").size());

        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, res.getBuildables().size());

    }

    @Test
    @DisplayName("Test build sceptre with arrow, stone, stone")
    public void testBuildWithArrowStoneStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_newBuildablesSunStone", "c_newBuildable");

        res = dmc.tick(Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);

        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(2, getInventory(res, "sun_stone").size());
        assertEquals(1, res.getBuildables().size());
        res = dmc.getDungeonResponseModel();

        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "arrow").size());
        // sunstone doesnt get removed
        assertEquals(2, getInventory(res, "sun_stone").size());

        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, res.getBuildables().size());

    }

    @Test
    @DisplayName("Test build sceptre with wood, stone, stone")
    public void testBuildWithWoodStoneStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_newBuildablesSunStone", "c_newBuildable");

        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(2, getInventory(res, "sun_stone").size());
        assertEquals(1, res.getBuildables().size());
        res = dmc.getDungeonResponseModel();

        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "wood").size());
        // sunstone doesnt get removed
        assertEquals(2, getInventory(res, "sun_stone").size());

        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, res.getBuildables().size());

    }

    @Test
    @DisplayName("Test invalid exception 2 arrows , treasure")
    public void testExceptionArrowTreasure() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_newBuildable", "c_newBuildable");

        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "arrow").size());
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "treasure").size());
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        assertEquals(2, getInventory(res, "arrow").size());
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
    }

    @Test
    @DisplayName("Test player cannot build item with exception")
    public void TestSceptreInvalidArgumentException() {

        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_newBuildablesSunStone", "c_newBuildable");
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));

    }

    @Test
    @DisplayName("Test mind control mercenary")
    public void TestMindControlMerc() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_newBuildablesSunStone", "c_newBuildable");
        // collect wood, key, sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "wood").size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "sun_stone").size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, getInventory(res, "sun_stone").size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        res = dmc.getDungeonResponseModel();
        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "sceptre").size());

        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("mercenary")) {
                // bribe merc who is out of range
                assertDoesNotThrow(() -> dmc.interact(e.getId()));
            }
        }

        res = dmc.getDungeonResponseModel();

        // assert merc is an ally, can no longer be interactable
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("mercenary")) {
                assertEquals(false, e.isInteractable());

            }
        }

        // after four ticks, sceptre wears off (tick 1)
        res = dmc.tick(Direction.RIGHT);
        // assert merc is an ally, can no longer be interactable
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("mercenary")) {
                assertEquals(false, e.isInteractable());

            }
        }

        // tick 2
        res = dmc.tick(Direction.RIGHT);
        // assert merc is an ally, can no longer be interactable
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("mercenary")) {
                assertEquals(false, e.isInteractable());

            }
        }

        // tick 3 (mind control wears off at end of tick)
        // move player left onto following merc, no battle should occur
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, res.getBattles().size());
        // assert merc is an ally. can be interactable
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("mercenary")) {
                assertEquals(false, e.isInteractable());

            }
        }

        // tick 4, sceptre now wears off at end of this tick
        res = dmc.tick(Direction.RIGHT);
        // assert merc is not an ally
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("mercenary")) {
                assertEquals(true, e.isInteractable());

            }
        }

        // can battle merc
        res = dmc.tick(Direction.LEFT);

        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("mercenary")) {
                assertEquals(true, e.isInteractable());

            }
        }

        assertEquals(1, res.getBattles().size());

    }

    @Test
    @DisplayName("Test mind control assassin")
    public void TestMindControlAssassin() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreAssassin", "c_newBuildable");
        // collect wood, key, sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "wood").size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "sun_stone").size());

        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "sceptre").size());

        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("assassin")) {
                // bribe assassin who is out of range
                assertDoesNotThrow(() -> dmc.interact(e.getId()));
            }
        }

        res = dmc.getDungeonResponseModel();

        // assert merc is an ally, can no longer be interactable
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("assassin")) {
                assertEquals(false, e.isInteractable());

            }
        }

        // after four ticks, sceptre wears off (tick 1)
        res = dmc.tick(Direction.UP);
        // assert merc is an ally, can no longer be interactable
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("assassin")) {
                assertEquals(false, e.isInteractable());

            }
        }

        // tick 2
        res = dmc.tick(Direction.DOWN);
        // assert merc is an ally, can no longer be interactable
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("assassin")) {
                assertEquals(false, e.isInteractable());

            }
        }

        // tick 3 
        res = dmc.tick(Direction.UP);
        assertEquals(0, res.getBattles().size());
        // assert merc is an ally. can be interactable
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("assassin")) {
                assertEquals(false, e.isInteractable());

            }
        }

        // tick 4, sceptre now wears off at end of this tick
        res = dmc.tick(Direction.DOWN);
        // assert merc is not an ally
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("assassin")) {
                assertEquals(true, e.isInteractable());

            }
        }

        // battle should occur
        DungeonResponse playerBattleAssassin = dmc.tick(Direction.UP);

        DungeonResponse postBattleResponse;
        postBattleResponse = playerBattleAssassin;

        assertEquals(1, postBattleResponse.getBattles().size());

    }

    @Test
    @DisplayName("Test mind control assassin then battle while under control")
    public void TestMindControlAssassinBattle() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreAssassin", "c_newBuildable");
        // collect wood, key, sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "wood").size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "sun_stone").size());

        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "sceptre").size());

        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("assassin")) {
                // bribe assassin who is out of range
                assertDoesNotThrow(() -> dmc.interact(e.getId()));
            }
        }

        res = dmc.getDungeonResponseModel();

        DungeonResponse assassinBattlePlayer = dmc.tick(Direction.RIGHT);
        DungeonResponse playerBattleAssassin = dmc.tick(Direction.DOWN);

        DungeonResponse postBattleResponse;
        if (assassinBattlePlayer.getBattles().size() > 0) {
            postBattleResponse = assassinBattlePlayer;
        } else {
            postBattleResponse = playerBattleAssassin;
        }

        assertEquals(0, postBattleResponse.getBattles().size());

    }

}
