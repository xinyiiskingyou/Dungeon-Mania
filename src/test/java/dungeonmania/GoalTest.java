package dungeonmania;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getGoals;
import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getPlayer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class GoalTest {
    
    @Test 
    @DisplayName("Test the player can win the game with simple exit")
    public void testSimpleExitGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
        
        // goal
        assertTrue(getGoals(res).contains(":exit"));
        Position exitPos = getEntities(res, "exit").get(0).getPosition();
        
        // exit at [1, 3]
        // after 2 ticks player will be onto exit
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        Position playerPos =  getPlayer(res).get().getPosition();
        assertEquals(exitPos, playerPos);
        assertEquals("", getGoals(res));
    }
    
    @Test
    @DisplayName("Test the player wins the game with pushing boulder")
    public void testBasicBoulder() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_boulderTest_basicGoal", "c_movementTest_testMovementDown");
        
        // goal
        assertTrue(getGoals(res).contains(":boulders"));

        // boulder at [2, 1]
        Position boulderPos = getEntities(res, "boulder").get(0).getPosition();
        
        // push the boulder to [3, 1]
        res = dmc.tick(Direction.RIGHT);
        Position currBoulder = getEntities(res, "boulder").get(0).getPosition();
        assertEquals(boulderPos, getPlayer(res).get().getPosition());
        assertNotEquals(boulderPos, currBoulder);
        assertTrue(getGoals(res).contains(":boulders"));

        // push the boulder onto switch [4, 1] and game over
        res = dmc.tick(Direction.RIGHT);
        Position playerPos = getPlayer(res).get().getPosition();
        assertEquals(currBoulder, playerPos);
        assertEquals("", getGoals(res));
    }
    
    @Test
    @DisplayName("Test the player wins the game with collecting treasure")
    public void testBasicCollectTreasure() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_treasureTest_basicGoal", "c_movementTest_testMovementDown");
        
        // goal
        assertTrue(getGoals(res).contains(":treasure"));

        // collect one key
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 1), getPlayer(res).get().getPosition());
        assertEquals(1, getInventory(res, "key").size());
        assertTrue(getGoals(res).contains(":treasure"));

        // collect one treasure and game over [1, 1]
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test the player wins the game with killing enemy")
    public void testBasicEnemyGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicEnemyGoal", "c_movementTest_testMovementDown");
        
        // goal
        assertTrue(getGoals(res).contains(":enemies"));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayer(res).get().getPosition());
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test simple AND goals") 
    public void testSimpleANDgoals() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_simpleAndGoalTest", "c_movementTest_testMovementDown");

        assertTrue(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));

        // push the boulder onto switch
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(res).contains(":treasure"));
        assertFalse(getGoals(res).contains(":boulders"));

        // push the boulder away from switch
        // [3, 1]
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));

        // push the boulder back to switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        
        assertTrue(getGoals(res).contains(":treasure"));
        assertFalse(getGoals(res).contains(":boulders"));

        // collect the treasure
        res = dmc.tick(Direction.UP);
        // win the game
        assertEquals("", getGoals(res));
    }


    @Test
    @DisplayName("Test simple or goals") 
    public void testSimpleOrGoals() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicORgoalsTest", "c_movementTest_testMovementDown");

        assertTrue(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));

        // moving up to collect treaure and game over
        res = dmc.tick(Direction.UP);
        assertEquals("", getGoals(res));
    }
    
    @Test
    @DisplayName("Test 4 disjunction goals") 
    public void testOrGoals() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_Disjunction", "c_movementTest_testMovementDown");

        assertTrue(getGoals(res).contains(":exit"));
        assertTrue(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains(":enemies"));

        // moving right to collect treaure
        res = dmc.tick(Direction.RIGHT);
        assertEquals("", getGoals(res));
    }
    
    @Test
    @DisplayName("Test combination with OR supergoal and 'or && and' subgoals") 
    public void testOrSuperGoalsOneAnd() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_2ORs", "c_movementTest_testMovementDown");

        assertTrue(getGoals(res).contains(":exit"));
        assertTrue(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains(":enemies"));

        // moving right to collect treaure
        res = dmc.tick(Direction.RIGHT);
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test combination with And supergoal and 'or && and' subgoals") 
    public void testAndSuperGoalsOneAnd() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_2Ands", "c_movementTest_testMovementDown");

        assertTrue(getGoals(res).contains(":exit"));
        assertTrue(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains(":enemies"));

        // moving right to collect treaure
        res = dmc.tick(Direction.RIGHT);

        assertTrue(getGoals(res).contains(":exit"));
        assertFalse(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains(":enemies"));

        // moving right to push boulders
        // delete the whole OR substring when OR goal is finished
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(res).contains(":exit"));
        assertFalse(getGoals(res).contains(":treasure"));
        assertFalse(getGoals(res).contains(":boulders"));
        assertFalse(getGoals(res).contains(":enemies"));

        // push the boulder away from switch
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(res).contains(":exit"));
        assertFalse(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains(":enemies"));

        // push the boulder back
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);

        assertTrue(getGoals(res).contains(":exit"));
        assertFalse(getGoals(res).contains(":treasure"));
        assertFalse(getGoals(res).contains(":boulders"));
        assertFalse(getGoals(res).contains(":enemies"));

        // move down for exit and win the game
        res = dmc.tick(Direction.DOWN);
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test the player cannot exit without doing other goals") 
    public void testPlayerCannotExit() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_2Ands", "c_movementTest_testMovementDown");

        assertTrue(getGoals(res).contains(":exit"));
        assertTrue(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains(":enemies"));

        // move to exit
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertNotEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test the combination of supergoal AND and subgoals ORs") 
    public void testSubgoalsWith2Ors() {

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoal_SuperAnd", "c_movementTest_testMovementDown");

        assertTrue(getGoals(res).contains(":exit"));
        assertTrue(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains(":enemies"));

        // moving to [3, 2] -> exit
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // cannot exit straight away 
        assertTrue(getGoals(res).contains(":exit"));
        assertTrue(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains(":enemies"));
        assertNotEquals("", getGoals(res));

        // [2, 2]
        res = dmc.tick(Direction.LEFT);
        // [2, 1] -> collect a treasure
        res = dmc.tick(Direction.UP);
        assertFalse(getGoals(res).contains(":exit"));
        assertFalse(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains(":enemies"));

        // push boulder at position [2, 1] and trigger switch
        res = dmc.tick(Direction.RIGHT);

        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Testing that the exit goal must be achieved last in EXIT and TREASURE")
    public void testExitGoalLast() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoal_exitGoal", "c_complexGoal_exitGoal");

        assertTrue(getGoals(res).contains(":exit"));
        assertTrue(getGoals(res).contains(":treasure"));

        // collect the first treasure -> [2, 1]
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(res).contains(":exit"));
        assertTrue(getGoals(res).contains(":treasure"));

        // move to exit -> cannot exit [3, 2]
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(res).contains(":treasure"));
        assertTrue(getGoals(res).contains(":exit"));
        assertNotEquals("", getGoals(res));

        // collect the second treasure to complete treasure goal [3, 1]
        res = dmc.tick(Direction.UP);
        assertTrue(getGoals(res).contains(":exit"));
        assertFalse(getGoals(res).contains(":treasure"));

        // move to exit [3, 2] -> can exit the game
        res = dmc.tick(Direction.DOWN);
        assertFalse(getGoals(res).contains(":exit"));
        assertFalse(getGoals(res).contains(":treasure"));

        assertEquals("", getGoals(res));
    }

}
