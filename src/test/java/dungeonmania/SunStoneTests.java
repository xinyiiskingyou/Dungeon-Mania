package dungeonmania;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.entities.*;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.items.*;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.enemy.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getGoals;
import static dungeonmania.TestUtils.getPlayer;

public class SunStoneTests {

    @Test
    @DisplayName("Test sunstone can be picked up by player")
    public void canPickUpSunStone(){
        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");

        ItemEntity sunstone= new SunStone(new Position(1, 1), "treasure", 1);
        dungeon.addItem(sunstone);

        player.move(dungeon, Direction.RIGHT);
        assertEquals(sunstone, player.getInventory().get(0));
    }


    @Test
    @DisplayName("Test sunstone can open door and is retained")
    public void canOpenDoorWithSunStone(){
        Dungeon dungeon = new Dungeon();

        Player player = new Player(new Position(1, 1), 10, 10, "player");
        dungeon.addEntity(player);

        ItemEntity sunstone = new SunStone(new Position(1, 2), "sun_stone", 1);
        dungeon.addItem(sunstone);
    
        player.move(dungeon, Direction.DOWN);
        assertEquals(1, player.getInventory().size());
        assertEquals("sun_stone", player.getInventory().get(0).getType());
        
        Door door = new Door(new Position(1, 3), "door", 1);
        dungeon.addEntity(door);

        player.move(dungeon, Direction.DOWN);
        assertEquals(true, door.isOpen());
        assertEquals(1, player.getInventory().size());

        // player will move up
        Position expected = new Position(1, 3);
        assertEquals(expected, player.getPosition());
        
    }

    @Test
    @DisplayName("Test sunstone can open door and sun stone + key retained")
    public void canOpenDoorWithSunStoneNotUseKey(){
        Dungeon dungeon = new Dungeon();

        Player player = new Player(new Position(1, 1), 10, 10, "player");
        dungeon.addEntity(player);

        ItemEntity sunstone = new SunStone(new Position(1, 2), "sun_stone", 1);
        dungeon.addItem(sunstone);

        ItemEntity key = new Key(new Position(1, 2), "key", 1);
        dungeon.addItem(key);
    
        player.move(dungeon, Direction.DOWN);
        assertEquals(2, player.getInventory().size());
        assertEquals("sun_stone", player.getInventory().get(0).getType());
        
        Door door = new Door(new Position(1, 3), "door", 1);
        dungeon.addEntity(door);

        player.move(dungeon, Direction.DOWN);
        assertEquals(true, door.isOpen());
        assertEquals(2, player.getInventory().size());

        // player will move up
        Position expected = new Position(1, 3);
        assertEquals(expected, player.getPosition());
        
    }

    @Test
    @DisplayName("Test sunstone can build shield and is retained")
    public void canBuildShieldWithSunStone(){
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_test_sunstone_build", "c_movementTest_testMovementDown");
        
        // collect two woods
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "wood").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, getInventory(res, "wood").size());

        // collect one treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "sun_stone").size());

        
        assertDoesNotThrow( () -> dmc.build("shield"));
        res = dmc.getDungeonResponseModel();
        // Once the shield built, the items should be removed
        assertEquals(1, getInventory(res, "shield").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());

        // shield is added to inventory
        assertEquals(1, getInventory(res, "shield").size());
        // player can now no longer build anything
        assertEquals(0, res.getBuildables().size());
        assertThrows(InvalidActionException.class, () -> dmc.build("bow"));
    }

    @Test
    @DisplayName("Test sunstone can build shield and key + sun stone are retained")
    public void canBuildShieldWithSunStoneKey() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_test_sunstone_key_build", "c_movementTest_testMovementDown");
        
        // collect two woods
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "wood").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, getInventory(res, "wood").size());

        // collect one treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(1, getInventory(res, "sun_stone").size());

        
        assertDoesNotThrow( () -> dmc.build("shield"));
        res = dmc.getDungeonResponseModel();
        // Once the shield built, the items should be removed
        assertEquals(1, getInventory(res, "shield").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "key").size());

        // shield is added to inventory
        assertEquals(1, getInventory(res, "shield").size());
        // player can now no longer build anything
        assertEquals(0, res.getBuildables().size());
        assertThrows(InvalidActionException.class, () -> dmc.build("bow"));
    }


    @Test
    @DisplayName("Test sunstone cannot not be used to bribe mercenary")
    public void cannotBribeMercWithSunStone(){
        // Create a mercenary and set its starting position
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 1,0,0,"mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Give player some treasure, but not enough to bribe the mercenary
        Treasure t1 = new Treasure(initialPosPlayer, "treasure", 4);
        SunStone s1 = new SunStone(initialPosPlayer, "sun_stone", 4);
        player.addItem(t1);
        player.addItem(s1);

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);

        assertEquals(2, player.getInventory().size());
        assertThrows(InvalidActionException.class, () -> mercenary.bribe(player));
        assertTrue(mercenary.isHostile());
        assertEquals(2, player.getInventory().size());
        
    }

    @Test
    @DisplayName("Test sunstone counts towards treasure goal")
    public void testSunStoneTreasureGoal(){
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunstone_treasure_goal", "c_movementTest_testMovementDown");
        
        // goal
        assertTrue(getGoals(res).contains(":treasure"));

        // collect one key
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 1), getPlayer(res).get().getPosition());
        assertEquals(1, getInventory(res, "key").size());
        assertTrue(getGoals(res).contains(":treasure"));

        // collect one treasure and game over [1, 1]
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals("", getGoals(res));
        
    }
}



