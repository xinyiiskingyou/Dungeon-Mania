package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getGoals;
import static dungeonmania.TestUtils.getInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;

public class NewGameTest {

    @Test
    @DisplayName("Test the game can start normally")
    public void testStartGame() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 1), false);
        assertEquals(expectedPlayer, initPlayer);

        EntityResponse exit = getEntities(initDungonRes, "exit").get(0);
        EntityResponse expected = new EntityResponse(exit.getId(), exit.getType(), new Position(1, 3), false);
        assertEquals(expected, exit);

        String goal = getGoals(initDungonRes);
        assertNotNull(goal);
    }

    @Test
    @DisplayName("Non-exist dungeon / config file")
    public void testThrowException() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc.newGame("123.json", "c_movementTest_testMovementDown"));
        assertThrows(IllegalArgumentException.class, () -> dmc.newGame("d_movementTest_testMovementDown", "123.json"));
        assertThrows(IllegalArgumentException.class, () -> dmc.newGame("d_movementTest", "123"));
    }   

    @Test
    @DisplayName("Test the game can start normally")
    public void testStartGameWithInventory() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_useKeyWalkThroughOpenDoor", "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");
        
        int keyAmount = getInventory(res, "key").size();
        assertEquals(0, keyAmount);

        EntityResponse door = getEntities(res, "door").get(0);

        EntityResponse expectedPlayer = new EntityResponse(door.getId(), door.getType(), door.getPosition(), false);
        assertEquals(expectedPlayer, door);
    }

}
