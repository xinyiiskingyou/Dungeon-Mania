package dungeonmania;

import dungeonmania.enemy.Assassin;
import dungeonmania.entities.*;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.items.InvisiblePotion;
import dungeonmania.items.Potion;
import dungeonmania.items.Treasure;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssassinTests {
    @Test
    @DisplayName("Test assassin moves towards player")
    public void testMovement() {
        Dungeon dungeon = new Dungeon();
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 0,0,0, 0, 0, "assassin");

        // Create a player and set its starting position above assassin
        Position initialPosPlayer = new Position(0, -4);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);

        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.UP), assassin.getPosition());

        // Update player's position to below assassin
        player.setPosition(new Position(0, 4));
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin, assassin.getPosition());

        // Update player's position to right of assassin
        player.setPosition(new Position(4, 0));
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.RIGHT), assassin.getPosition());

        // Update player's position to left of assassin
        player.setPosition(new Position(-4, 0));
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin, assassin.getPosition());
    }

    @Test
    @DisplayName("Test bribe fails because not enough gold but within radius")
    public void testBribeNotEnoughGold() {
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 0,0,0, 0, 0, "assassin");

        // Create a player and set its starting position above assassin
        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);

        assertTrue(assassin.isHostile());

        assertThrows(InvalidActionException.class, () -> assassin.bribe(player));
        assertTrue(assassin.isHostile());
    }

    @Test
    @DisplayName("Test bribe fails because not within radius")
    public void testBribeNotWithinRadius() {
        Position initialPosAssassin = new Position(0, 10);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 4,0,0,0, 0, "assassin");

        // Create a player and set its starting position above assassin
        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);

        // Case 1: Not within radius and not enough gold
        assertThrows(InvalidActionException.class, () -> assassin.bribe(player));
        assertTrue(assassin.isHostile());

        // Case 2: Not within radius and enough gold
        assassin.setBribeAmount(0);
        assertThrows(InvalidActionException.class, () -> assassin.bribe(player));
        assertTrue(assassin.isHostile());
    }

    @Test
    @DisplayName("Test assassin is able to move around a boulder")
    public void testBoulderCollision() {
        Dungeon dungeon = new Dungeon();
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 1,0,0, 0, 0, "assassin");

        // Create a player and set its starting position above assassin
        Position initialPosPlayer = new Position(1, 2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a boulder in between the player and assassin
        Position initialPosBoulder = new Position(1, 1);
        Boulder boulder = new Boulder(initialPosBoulder, "boulder");

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);
        entities.add(boulder);

        // Can move down
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.DOWN), assassin.getPosition());

        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.DOWN).translateBy(Direction.DOWN), assassin.getPosition());
    }

    @Test
    @DisplayName("Test assassin is able to move around a zombie toast spawner")
    public void testSpawnerCollision() {
        Dungeon dungeon = new Dungeon();
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 1,0,0, 0, 0,"assassin");

        // Create a player and set its starting position above assassin
        Position initialPosPlayer = new Position(1, 2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a boulder in between the player and assassin
        Position initialPosSpawner = new Position(1, 1);
        ZombieToastSpawner zombieToastSpawner = new ZombieToastSpawner(initialPosSpawner, "zombie_toast_spawner");

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);
        entities.add(zombieToastSpawner);

        // Can move down
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.DOWN), assassin.getPosition());

        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.DOWN).translateBy(Direction.DOWN), assassin.getPosition());
    }

    @Test
    @DisplayName("Test assassin takes shortest path to player")
    public void testShortestPathMovementForSingleWall() {
        Dungeon dungeon = new Dungeon();
        Position initialPosAssassin = new Position(1, 2);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 0,0,0, 0, 0, "assassin");

        // Create a player and set its starting position
        Position initialPosPlayer = new Position(0, 0);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a wall below the player
        Wall wall = new Wall(initialPosPlayer.translateBy(Direction.DOWN), "wall");

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);
        entities.add(wall);

        // assassin should move to up instead of moving to the left
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.UP), assassin.getPosition());

        // assassin should move to up
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.UP).translateBy(Direction.UP), assassin.getPosition());
    }

    @Test
    @DisplayName("Test assassin takes shortest path to player (left) when moving around walls")
    public void testShortestPathMovementLeftForMultipleWalls() {
        Dungeon dungeon = new Dungeon();
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 0,0,0, 0, 0, "assassin");

        // Create a player and set its starting position
        Position initialPosPlayer = new Position(0, -2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a wall above the mercenary
        Wall wall1 = new Wall(new Position(0, -1), "wall");
        Wall wall2 = new Wall(new Position(1, -1), "wall");
        Wall wall3 = new Wall(new Position(2, -1), "wall");

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);
        entities.add(wall1);
        entities.add(wall2);
        entities.add(wall3);

        // assassin should move to left instead of moving to the right
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.LEFT), assassin.getPosition());

        // assassin should move up
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.LEFT).translateBy(Direction.UP), assassin.getPosition());

        // assassin should move up
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.LEFT).translateBy(Direction.UP).translateBy(Direction.UP), assassin.getPosition());
    }

    @Test
    @DisplayName("Test assassin takes shortest path to player (right) when moving around walls")
    public void testShortestPathMovementRightForMultipleWalls() {
        // Create an assassin and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 0,0,0, 0, 0,"assassin");

        // Create a player and set its starting position
        Position initialPosPlayer = new Position(0, -2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a wall above the assassin
        Wall wall1 = new Wall(new Position(0, -1), "wall");
        Wall wall2 = new Wall(new Position(-1, -1), "wall");
        Wall wall3 = new Wall(new Position(-2, -1), "wall");

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);
        entities.add(wall1);
        entities.add(wall2);
        entities.add(wall3);

        assertEquals(initialPosAssassin.translateBy(Direction.RIGHT), assassin.dijkstraSearch(dungeon, entities, initialPosAssassin, initialPosPlayer));

        // assassin should move to right instead of moving to the left
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.RIGHT), assassin.getPosition());

        // assassin should move up
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.RIGHT).translateBy(Direction.UP), assassin.getPosition());

        // assassin should move up
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.RIGHT).translateBy(Direction.UP).translateBy(Direction.UP), assassin.getPosition());
    }

    @Test
    @DisplayName("Test assassin moves towards from player when player is invisible and in range")
    public void testInvisibleMovement() {
        // Create an assassin and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 0,0,2, 0, 0, "assassin");

        // Create a player and set its starting position above assassin
        Position initialPosPlayer = new Position(0, 2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create invisible potion and consume it
        Potion invisiblePotion = new InvisiblePotion(initialPosPlayer, "invisible_potion", 1);
        player.subscribe(assassin);
        player.addItem(invisiblePotion);
        player.consumePotion(invisiblePotion);

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);

        // assassin moves towards player
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.DOWN), assassin.getPosition());
    }

    @Test
    @DisplayName("Test treasure used to bribe is removed from players inventory even when failed")
    public void testTreasureRemovedWhenFailedBribed() {
        // Create an assassin and set its starting position
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 1,1.0,0, 0, 0, "assassin");

        // Create a player and set its starting position above assassin
        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Give player enough treasure to bribe assassin
        Treasure t1 = new Treasure(initialPosPlayer, "treasure", 4);
        Treasure t2 = new Treasure(initialPosPlayer, "treasure", 4);
        Treasure t3 = new Treasure(initialPosPlayer, "treasure", 4);
        player.addItem(t1);
        player.addItem(t2);
        player.addItem(t3);

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);

        // Bribe failed and no refund
        assertEquals(3, player.getInventory().size());
        assertDoesNotThrow(() -> assassin.bribe(player));
        assertTrue(assassin.isHostile());
        assertEquals(1, player.getInventory().size());
    }

    @Test
    @DisplayName("Test treasure used to bribe is removed from players inventory")
    public void testTreasureRemovedWhenSuccessBribed() {
        // Create an assassin and set its starting position
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 1,0.0,0, 0, 0, "assassin");

        // Create a player and set its starting position above assassin
        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Give player enough treasure to bribe assassin
        Treasure t1 = new Treasure(initialPosPlayer, "treasure", 4);
        Treasure t2 = new Treasure(initialPosPlayer, "treasure", 4);
        Treasure t3 = new Treasure(initialPosPlayer, "treasure", 4);
        player.addItem(t1);
        player.addItem(t2);
        player.addItem(t3);

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);

        // Bribe worked
        assertEquals(3, player.getInventory().size());
        assertDoesNotThrow(() -> assassin.bribe(player));
        assertFalse(assassin.isHostile());
        assertEquals(1, player.getInventory().size());
    }

    @Test
    @DisplayName("Test assassin moves towards player through swamp tile")
    public void testBasicMovementThroughSwampTile() {
        Dungeon dungeon = new Dungeon();
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 0,0,0, 0, 0, "assassin");

        // Create a player and set its starting position above assassin
        Position initialPosPlayer = new Position(0, -4);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        SwampTile swampTile = new SwampTile(initialPosAssassin.translateBy(Direction.UP), 1, "swamp_tile");

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);
        entities.add(swampTile);

        // Moves into swamp tile
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.UP), assassin.getPosition());

        // Stuck in swamp tile
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.UP), assassin.getPosition());

        // Move off swamp tile
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.UP).translateBy(Direction.UP), assassin.getPosition());
    }

    @Test
    @DisplayName("Test assassin takes shortest path to player with swamp tile")
    public void testShortestPathMovementSwampTile() {
        Dungeon dungeon = new Dungeon();
        Position initialPosAssassin = new Position(0, 0);
        Assassin assassin = new Assassin(initialPosAssassin, 10, 10, 2, 0,0,0, 0, 0, "assassin");

        // Create a player and set its starting position
        Position initialPosPlayer = new Position(0, -2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a wall above the mercenary
        Wall wall1 = new Wall(new Position(0, -1), "wall");
        Wall wall2 = new Wall(new Position(1, -1), "wall");
        Wall wall3 = new Wall(new Position(2, -1), "wall");

        SwampTile swampTile = new SwampTile(initialPosAssassin.translateBy(Direction.LEFT), 10, "swamp_tile");

        List<Entity> entities = new ArrayList<>();
        entities.add(assassin);
        entities.add(player);
        entities.add(wall1);
        entities.add(wall2);
        entities.add(wall3);
        entities.add(swampTile);

        // assassin should move to right instead of moving to the left
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.RIGHT), assassin.getPosition());

        // assassin should move right
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.RIGHT).translateBy(Direction.RIGHT), assassin.getPosition());

        // assassin should move right
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.RIGHT).translateBy(Direction.RIGHT).translateBy(Direction.RIGHT), assassin.getPosition());

        // assassin should move up
        assassin.move(entities, dungeon);
        assertEquals(initialPosAssassin.translateBy(Direction.RIGHT).translateBy(Direction.RIGHT).translateBy(Direction.RIGHT).translateBy(Direction.UP), assassin.getPosition());
    }
}
