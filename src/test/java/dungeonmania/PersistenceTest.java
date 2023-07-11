package dungeonmania;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getGoals;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class PersistenceTest {

    public void clearDir() {
        String path = getClass().getClassLoader().getResource(".").getPath();
        File saveDir = new File(path, "saves");
        if (! saveDir.exists()) {
            return;
        }
        for(File file: saveDir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

    @Test
    @DisplayName("Test can save game")
    public void testSimpleSaveGame() {
        clearDir();
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
        assertDoesNotThrow(() -> dmc.saveGame("d_movementTest_testMovementDown"));   

        // test can save the same map again
        DungeonManiaController dmc1 = new DungeonManiaController();
        dmc1.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
        assertDoesNotThrow(() -> dmc1.saveGame("d_movementTest_testMovementDown2"));   

        DungeonManiaController dmc2 = new DungeonManiaController();
        dmc1.newGame("d_basicORgoalsTest", "c_movementTest_testMovementDown");
        assertDoesNotThrow(() -> dmc2.saveGame("d_basicORgoalsTest"));   

        assertNotEquals(0, dmc2.allGames().size());
    } 

    @Test
    @DisplayName("Test LoadGame with exception")
    public void testLoadNonExistGame() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc.loadGame("notExist"));
        assertThrows(IllegalArgumentException.class, () -> dmc.loadGame("randomGame"));

        String fileName = dmc.allGames().get(0);
        assertDoesNotThrow(() -> dmc.loadGame(fileName));
    }

    @Test
    @DisplayName("Test saving and loading a simple maze map")
    public void testSaveAndLoadSimpleGame() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_useKeyWalkThroughOpenDoor", "c_movementTest_testMovementDown");
        Position expectedPlayerPos = getPlayer(res).get().getPosition();
        Position expectedKeyPos = getEntities(res, "key").get(0).getPosition();
        Position expectedDoorPos = getEntities(res, "door").get(0).getPosition();
        Position expectedExitPos = getEntities(res, "exit").get(0).getPosition();
        String expectedGoals = getGoals(res);

        // save game
        assertDoesNotThrow(() -> dmc.saveGame("d_DoorsKeysTest_useKeyWalkThroughOpenDoor"));   
        
        // load game
        DungeonResponse reload = assertDoesNotThrow(() -> dmc.loadGame("d_DoorsKeysTest_useKeyWalkThroughOpenDoor"));
        assertEquals(expectedPlayerPos, getPlayer(reload).get().getPosition());
        assertEquals(expectedKeyPos, getEntities(reload, "key").get(0).getPosition());
        assertEquals(expectedDoorPos, getEntities(reload, "door").get(0).getPosition());
        assertEquals(expectedExitPos, getEntities(reload, "exit").get(0).getPosition());
        assertTrue(getGoals(reload).contains(":exit"));
        assertEquals(expectedGoals,  getGoals(reload));        
    }

    @Test
    @DisplayName("Test saving and loading a map with boulders")
    public void testSaveAndLoadWithBoulders() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_simpleAndGoalTest", "c_movementTest_testMovementDown");
        
        // push the boulder on switch -> the boulder goal not in goal string
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(res).contains(":treasure"));
        assertFalse(getGoals(res).contains(":boulders"));
        Position boulderPos = getEntities(res, "boulder").get(0).getPosition();
        Position switchPos = getEntities(res, "switch").get(0).getPosition();
        String expectedGoals = getGoals(res);

        // save the game
        assertDoesNotThrow(() -> dmc.saveGame("d_simpleAndGoalTest"));   

        // reload the game
        DungeonResponse reload = assertDoesNotThrow(() -> dmc.loadGame("d_simpleAndGoalTest"));
        assertEquals(boulderPos, getEntities(reload, "boulder").get(0).getPosition());
        assertEquals(switchPos, getEntities(reload, "switch").get(0).getPosition());

        // the goals do not contain boulders 
        assertTrue(getGoals(reload).contains(":treasure"));
        assertFalse(getGoals(reload).contains(":boulders"));
        assertEquals(expectedGoals, getGoals(reload));

        // push the boulder away and the boulder goal is in goal string now
        reload = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(reload).contains(":treasure"));
        assertTrue(getGoals(reload).contains(":boulders"));
        assertNotEquals(expectedGoals, getGoals(reload));
    }

    @Test
    @DisplayName("Test saving and loading a map with enemies and collectables")
    public void testSaveAndLoadWithCollectables() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_2Ands", "c_movementTest_testMovementDown");

        // collect the treasure 
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(0, getEntities(res, "treasure").size());
        Position expectedSpiderPos = getEntities(res, "spider").get(0).getPosition();

        // save game
        assertDoesNotThrow(() -> dmc.saveGame("d_complexGoalsTest_2Ands"));   
        // reload the game
        DungeonResponse reload = assertDoesNotThrow(() -> dmc.loadGame("d_complexGoalsTest_2Ands"));
        assertEquals(expectedSpiderPos, getEntities(reload, "spider").get(0).getPosition());

        // treasure still in player's inventory
        assertEquals(1, getInventory(reload, "treasure").size());
        // no treasure on the map
        assertEquals(0, getEntities(reload, "treasure").size());
    }

    @Test
    @DisplayName("Test saving on a map containing all types of entities") 
    public void testSaveAndLoadGameSaveEverything() throws IllegalArgumentException, InvalidActionException {
        // move the player and save the game
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_saveEverything", "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
    
        // pick up the key and open the door
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        res = dmc.tick(Direction.UP);

        Position expectedPortalPos = getEntities(res, "portal").get(0).getPosition();
        Position expectedDoorPos = getEntities(res, "door").get(0).getPosition();
        Position expectedMercPos = getEntities(res, "mercenary").get(0).getPosition();
        Position expectedHydraPos = getEntities(res, "hydra").get(0).getPosition();
        Position expectedSpiderPos = getEntities(res, "spider").get(0).getPosition();

        // save game
        assertDoesNotThrow(() -> dmc.saveGame("d_saveEverything"));
        // load game
        DungeonResponse reload = dmc.loadGame("d_saveEverything");

        // enemies are in the same position
        assertEquals(expectedMercPos, getEntities(reload, "mercenary").get(0).getPosition());
        assertEquals(expectedHydraPos, getEntities(reload, "hydra").get(0).getPosition());
        assertEquals(expectedSpiderPos, getEntities(reload, "spider").get(0).getPosition());
        
        // can walk through the locked door
        reload = dmc.tick(Direction.DOWN);
        assertEquals(expectedDoorPos, getPlayer(reload).get().getPosition());

        String potion = getInventory(reload, "invisibility_potion").get(0).getId();
        // use the potion
        dmc.tick(potion);
        
        // portal in the same position
        Position actualPortalPos = getEntities(reload, "portal").get(0).getPosition();
        assertEquals(expectedPortalPos, actualPortalPos);

        // put down the bomb on the map 
        reload = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(reload, "bomb").size());
        String bombId = getInventory(reload, "bomb").get(0).getId();
        reload = dmc.tick(bombId);
        
        // cannot pick the same bomb up again
        reload = dmc.tick(Direction.UP);
        reload = dmc.tick(Direction.DOWN);
        assertEquals(0, getInventory(reload, "bomb").size());
        // potion effect is off
        assertEquals(0, getInventory(reload, "invisibility_potion").size());
    }


    @Test
    @DisplayName("Test the mercenary will still follow the player")
    public void testMercenaryBribed() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_bribeMerc", "c_movementTest_testMovementDown");

        // two treasure
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "treasure").size());

        res = dmc.tick(Direction.DOWN);
        assertEquals(2, getInventory(res, "treasure").size());
        Position playerPos = getEntities(res, "player").get(0).getPosition();

        // get id of merc
        String mercId = getEntities(res, "mercenary").get(0).getId();

        // bribe the merc
        assertDoesNotThrow(() -> dmc.interact(mercId));

        // merc should follow the player
        res = dmc.tick(Direction.DOWN);
        
        EntityResponse mer = getEntities(res, "mercenary").get(0);
        assertEquals(playerPos, mer.getPosition());

        Position expectedPlayerPos = getEntities(res, "player").get(0).getPosition();

        // save the game
        assertDoesNotThrow(() -> dmc.saveGame("d_battleTest_bribeMerc"));    

        // reload the game
        DungeonResponse reload = dmc.loadGame("d_battleTest_bribeMerc");

        reload = dmc.tick(Direction.UP);
        // mercenary should continue following the player
        EntityResponse mer1 = getEntities(reload, "mercenary").get(0);
        assertEquals(expectedPlayerPos, mer1.getPosition());
    }
}
