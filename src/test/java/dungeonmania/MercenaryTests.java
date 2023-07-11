package dungeonmania;

import dungeonmania.entities.*;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.items.InvinciblePotion;
import dungeonmania.items.Potion;
import dungeonmania.items.Treasure;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.enemy.Mercenary;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class MercenaryTests {
    @Test
    @DisplayName("Test mercenary moves towards player")
    public void testMovement() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 0,0,0, "mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, -4);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);

        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.UP), mercenary.getPosition());

        // Update player's position to below mercenary
        player.setPosition(new Position(0, 4));
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary, mercenary.getPosition());

        // Update player's position to right of mercenary
        player.setPosition(new Position(4, 0));
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.RIGHT), mercenary.getPosition());

        // Update player's position to left of mercenary
        player.setPosition(new Position(-4, 0));
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary, mercenary.getPosition());
    }

    @Test
    @DisplayName("Test bribe works")
    public void testBribeWorks() {
        // Create a mercenary and set its starting position
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 0, 1,0,0,"mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);

        assertTrue(mercenary.isHostile());

        assertDoesNotThrow(() -> mercenary.bribe(player));
        assertFalse(mercenary.isHostile());
    }

    @Test
    @DisplayName("Test bribe fails because not enough gold but within radius")
    public void testBribeNotEnoughGold() {
        // Create a mercenary and set its starting position
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 1,0,0,"mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);

        assertTrue(mercenary.isHostile());

        assertThrows(InvalidActionException.class, () -> mercenary.bribe(player));
        assertTrue(mercenary.isHostile());
    }

    @Test
    @DisplayName("Test bribe fails because not within radius")
    public void testBribeNotWithinRadius() {
        // Create a mercenary and set its starting position
        Position initialPosMercenary = new Position(0, 10);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 4,0,0,"mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);

        // Case 1: Not within radius and not enough gold
        assertThrows(InvalidActionException.class, () -> mercenary.bribe(player));
        assertTrue(mercenary.isHostile());

        // Case 2: Not within radius and enough gold
        mercenary.setBribeAmount(0);
        assertThrows(InvalidActionException.class, () -> mercenary.bribe(player));
        assertTrue(mercenary.isHostile());
    }

    @Test
    @DisplayName("Test mercenary is able to move around a boulder")
    public void testBoulderCollision() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 1,0,0,"mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(1, 2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a boulder in between the player and mercenary
        Position initialPosBoulder = new Position(1, 1);
        Boulder boulder = new Boulder(initialPosBoulder, "boulder");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);
        entities.add(boulder);

        // Can move down
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.DOWN), mercenary.getPosition());

        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.DOWN).translateBy(Direction.DOWN), mercenary.getPosition());
    }

    @Test
    @DisplayName("Test mercenary is able to move around a zombie toast spawner")
    public void testSpawnerCollision() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 1,0,0,"mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(1, 2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a boulder in between the player and mercenary
        Position initialPosSpawner = new Position(1, 1);
        ZombieToastSpawner zombieToastSpawner = new ZombieToastSpawner(initialPosSpawner, "zombie_toast_spawner");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);
        entities.add(zombieToastSpawner);

        // Can move down
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.DOWN), mercenary.getPosition());

        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.DOWN).translateBy(Direction.DOWN), mercenary.getPosition());
    }

    @Test
    @DisplayName("Test mercenary runs away from player when player is invincible")
    public void testInvincibleMovement() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 0,0,0, "mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create invisible potion and consume it
        Potion invinciblePotion = new InvinciblePotion(initialPosPlayer, "invincibility_potion", 1);
        player.subscribe(mercenary);
        player.addItem(invinciblePotion);
        player.consumePotion(invinciblePotion);

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);

        // Mercenary runs away
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.UP), mercenary.getPosition());
        player.tickPotions();

        // Mercenary moves towards player since potion duration has run out
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary, mercenary.getPosition());
    }

    @Test
    @DisplayName("Test mercenary movement when it is an ally")
    public void testAllyMovement() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 0, 1,0,0,"mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);

        // Bribe mercenary
        assertDoesNotThrow(() -> mercenary.bribe(player));
        assertFalse(mercenary.isHostile());

        // Move player and mercenary
        player.move(dungeon, Direction.RIGHT);
        mercenary.move(entities, dungeon);
        assertEquals(initialPosPlayer, mercenary.getPosition());

        player.move(dungeon, Direction.DOWN);
        mercenary.move(entities, dungeon);
        assertEquals(initialPosPlayer.translateBy(Direction.RIGHT), mercenary.getPosition());

        player.move(dungeon, Direction.LEFT);
        mercenary.move(entities, dungeon);
        assertEquals(initialPosPlayer.translateBy(Direction.RIGHT).translateBy(Direction.DOWN), mercenary.getPosition());

        player.move(dungeon, Direction.UP);
        mercenary.move(entities, dungeon);
        assertEquals(initialPosPlayer.translateBy(Direction.RIGHT).translateBy(Direction.DOWN).translateBy(Direction.LEFT), mercenary.getPosition());
    }

    @Test
    @DisplayName("Test treasure used to bribe is removed from players inventory")
    public void testTreasureRemovedWhenBribed() {
        // Create a mercenary and set its starting position
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 1,0,0,"mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 1);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Give player enough treasure to bribe mercenary
        Treasure t1 = new Treasure(initialPosPlayer, "treasure", 4);
        Treasure t2 = new Treasure(initialPosPlayer, "treasure", 4);
        Treasure t3 = new Treasure(initialPosPlayer, "treasure", 4);
        player.addItem(t1);
        player.addItem(t2);
        player.addItem(t3);

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);

        assertEquals(3, player.getInventory().size());
        assertDoesNotThrow(() -> mercenary.bribe(player));
        assertFalse(mercenary.isHostile());
        assertEquals(1, player.getInventory().size());
    }

    @Test
    @DisplayName("Test mercenary takes shortest path to player")
    public void testShortestPathMovementForSingleWall() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(1, 2);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 0,0,0, "mercenary");

        // Create a player and set its starting position
        Position initialPosPlayer = new Position(0, 0);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a wall below the player
        Wall wall = new Wall(initialPosPlayer.translateBy(Direction.DOWN), "wall");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);
        entities.add(wall);

        // Mercenary should move to up instead of moving to the left
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.UP), mercenary.getPosition());

        // Mercenary should move to up
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.UP).translateBy(Direction.UP), mercenary.getPosition());
    }

    @Test
    @DisplayName("Test mercenary takes shortest path to player (left) when moving around walls")
    public void testShortestPathMovementLeftForMultipleWalls() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 0,0,0, "mercenary");

        // Create a player and set its starting position
        Position initialPosPlayer = new Position(0, -2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a wall above the mercenary
        Wall wall1 = new Wall(new Position(0, -1), "wall");
        Wall wall2 = new Wall(new Position(1, -1), "wall");
        Wall wall3 = new Wall(new Position(2, -1), "wall");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);
        entities.add(wall1);
        entities.add(wall2);
        entities.add(wall3);

        // Mercenary should move to left instead of moving to the right
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.LEFT), mercenary.getPosition());

        // Mercenary should move up
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.LEFT).translateBy(Direction.UP), mercenary.getPosition());

        // Mercenary should move up
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.LEFT).translateBy(Direction.UP).translateBy(Direction.UP), mercenary.getPosition());
    }

    @Test
    @DisplayName("Test mercenary takes shortest path to player (right) when moving around walls")
    public void testShortestPathMovementRightForMultipleWalls() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 0,0,0, "mercenary");

        // Create a player and set its starting position
        Position initialPosPlayer = new Position(0, -2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a wall above the mercenary
        Wall wall1 = new Wall(new Position(0, -1), "wall");
        Wall wall2 = new Wall(new Position(-1, -1), "wall");
        Wall wall3 = new Wall(new Position(-2, -1), "wall");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);
        entities.add(wall1);
        entities.add(wall2);
        entities.add(wall3);

        assertEquals(initialPosMercenary.translateBy(Direction.RIGHT), mercenary.dijkstraSearch(dungeon, entities, initialPosMercenary, initialPosPlayer));

        // Mercenary should move to right instead of moving to the left
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.RIGHT), mercenary.getPosition());

        // Mercenary should move up
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.RIGHT).translateBy(Direction.UP), mercenary.getPosition());

        // Mercenary should move up
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.RIGHT).translateBy(Direction.UP).translateBy(Direction.UP), mercenary.getPosition());
    }

    @Test
    @DisplayName("Test mercenary moves towards player through swamp tile")
    public void testBasicMovementThroughSwampTile() {
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 0,0,0, "mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, -4);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        SwampTile swampTile = new SwampTile(initialPosMercenary.translateBy(Direction.UP), 1, "swamp_tile");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);
        entities.add(swampTile);

        // Moves into swamp tile
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.UP), mercenary.getPosition());

        // Stuck in swamp tile
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.UP), mercenary.getPosition());

        // Move off swamp tile
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.UP).translateBy(Direction.UP), mercenary.getPosition());
    }

    @Test
    @DisplayName("Test mercenary takes shortest path to player with swamp tile")
    public void testShortestPathMovementSwampTile() {
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 0,0,0,"mercenary");

        // Create a player and set its starting position
        Position initialPosPlayer = new Position(0, -2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create a wall above the mercenary
        Wall wall1 = new Wall(new Position(0, -1), "wall");
        Wall wall2 = new Wall(new Position(1, -1), "wall");
        Wall wall3 = new Wall(new Position(2, -1), "wall");

        SwampTile swampTile = new SwampTile(initialPosMercenary.translateBy(Direction.LEFT), 10, "swamp_tile");

        List<Entity> entities = new ArrayList<>();
        entities.add(mercenary);
        entities.add(player);
        entities.add(wall1);
        entities.add(wall2);
        entities.add(wall3);
        entities.add(swampTile);

        // mercenary should move to right instead of moving to the left
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.RIGHT), mercenary.getPosition());

        // mercenary should move right
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.RIGHT).translateBy(Direction.RIGHT), mercenary.getPosition());

        // mercenary should move right
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.RIGHT).translateBy(Direction.RIGHT).translateBy(Direction.RIGHT), mercenary.getPosition());

        // mercenary should move up
        mercenary.move(entities, dungeon);
        assertEquals(initialPosMercenary.translateBy(Direction.RIGHT).translateBy(Direction.RIGHT).translateBy(Direction.RIGHT).translateBy(Direction.UP), mercenary.getPosition());
    }
}
