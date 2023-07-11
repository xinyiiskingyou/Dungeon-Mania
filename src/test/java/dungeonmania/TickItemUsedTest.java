package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

public class TickItemUsedTest {
    @Test
    @DisplayName("Test can consume a potion")
    public void testCanUsePotion() {
        // consume potion
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potions_dungeon", "c_potions_test_config");
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        String potion = getEntities(res, "invincibility_potion").get(0).getId();

        // move player into potion
        dmc.tick(Direction.UP);
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());

        // consume potion
        assertDoesNotThrow(() -> dmc.tick(potion));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        // player has consumed the potion - remove from inventory
        assertEquals(0, getInventory(res, "invincibility_potion").size());
    }

    @Test
    @DisplayName("Test can consume multiple potions")
    public void testCanUseMultiplePotion() {
        // consume potion
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potions_dungeon", "c_potions_test_config");
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        String potion = getEntities(res, "invincibility_potion").get(0).getId();

        // move player into potion
        dmc.tick(Direction.UP);
        res = dmc.getDungeonResponseModel();

        // move out of dungeon and into player inventory
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());

        // consume potion
        assertDoesNotThrow(() -> dmc.tick(potion));
        // player has consumed the potion - deleted from their inventory
        // LASTS FOUR TICKS
        // call this potion tick 0 - active duration = 3
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "invincibility_potion").size());

        String potion2 = getEntities(res, "invisibility_potion").get(0).getId();
        // pick up another potion
        // potion tick 1 - active duraction = 2
        dmc.tick(Direction.RIGHT);
        // move check invis still in inventory until effect swap over
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "invisibility_potion").size());
        
        // potion tick 2 - active duration 1 
        assertDoesNotThrow(() -> dmc.tick(potion2));
        res = dmc.getDungeonResponseModel();
        dmc.tick(Direction.RIGHT);
        res = dmc.getDungeonResponseModel();
        // invis removed from inventory
        assertEquals(0, getInventory(res, "invisibility_potion").size());

        // tick active duraction 0 - should swap at the end of this tick
        dmc.tick(Direction.RIGHT);
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "invisibility_potion").size());

    }

    @Test
    @DisplayName("Test potion lasts one tick and is then changed")
    public void potionLastsOneTick() {
        // consume potion
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potions_dungeon", "c_potions_small_tick_config");
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        String potion = getEntities(res, "invincibility_potion").get(0).getId();

        // move player into potions
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        res = dmc.getDungeonResponseModel();

        // move out of dungeon and into player inventory
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());

        // consume potion
        assertDoesNotThrow(() -> dmc.tick(potion));
        // player has consumed the potion - deleted from their inventory
        // LASTS ONE TICK
        // call this potion tick 0 - active duration = 0
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "invincibility_potion").size());

        String potion2 = getInventory(res, "invisibility_potion").get(0).getId();
        // tick 1 -new potion should take effect as previous one was only active for the previous tick
        assertDoesNotThrow(() -> dmc.tick(potion2));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "invisibility_potion").size());

    }

    @Test
    @DisplayName("Test not potoin or bomb")
    public void testNotBombPotion() {
        // consume potion
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_weapon", "c_potions_test_config");
        String sword = getEntities(res, "sword").get(0).getId();

        // move player into potion
        dmc.tick(Direction.UP);

        // consume potion
        assertThrows(InvalidActionException.class, () -> dmc.tick(sword));
    }

    @Test
    @DisplayName("Test not in dungeon")
    public void testNotInDungeon() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_weapon", "c_potions_test_config");
        // consume potion
        assertThrows(InvalidActionException.class, () -> dmc.tick("null"));
    }

    @Test
    @DisplayName("Test IllegalArgumentException")
    public void testIllegalArgumentException() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicORgoalsTest", "c_potions_small_tick_config");

        // move up to collect treasure
        res = dmc.tick(Direction.UP);
        String treasure = getInventory(res, "treasure").get(0).getId();

        assertThrows(IllegalArgumentException.class, () -> dmc.tick(treasure));
    }
}
