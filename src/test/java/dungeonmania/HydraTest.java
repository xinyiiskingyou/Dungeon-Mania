package dungeonmania;

import dungeonmania.enemy.Hydra;
import dungeonmania.entities.*;
import dungeonmania.items.InvinciblePotion;
import dungeonmania.items.Potion;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HydraTest {
    @Test
    @DisplayName("Test movement changes position value")
    public void testPositionChange() {
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Hydra hydra = new Hydra(initialPos, 10, 10, 1, 1, "hydra");
        List<Entity> entities = new ArrayList<>();

        hydra.move(entities, dungeon);

        assertFalse(hydra.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test collision for boulders")
    public void testBoulderCollision() {
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Hydra hydra = new Hydra(initialPos, 10, 10, 1, 1, "hydra");

        // Surround with boulders so it cannot move
        List<Entity> entities = Arrays.asList(new Boulder(initialPos.translateBy(Direction.UP), "boulder"),
                new Boulder(initialPos.translateBy(Direction.DOWN), "boulder"),
                new Boulder(initialPos.translateBy(Direction.LEFT), "boulder"),
                new Boulder(initialPos.translateBy(Direction.RIGHT), "boulder"));

        hydra.move(entities, dungeon);

        // Check current position is the same as the initial position
        assertTrue(hydra.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test collision for locked doors")
    public void testDoorCollision() {
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Hydra hydra = new Hydra(initialPos, 10, 10, 1, 1, "hydra");

        // Surround with locked doors so it cannot move
        List<Entity> entities = Arrays.asList(new Door(initialPos.translateBy(Direction.UP), "door", 5),
                new Door(initialPos.translateBy(Direction.DOWN), "door", 6),
                new Door(initialPos.translateBy(Direction.LEFT), "door", 7),
                new Door(initialPos.translateBy(Direction.RIGHT), "door", 8));

        hydra.move(entities, dungeon);

        // Check current position is the same as the initial position
        assertTrue(hydra.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test collision for walls")
    public void testWallCollision() {
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Hydra hydra = new Hydra(initialPos, 10, 10, 1, 1, "hydra");

        // Surround with walls so it cannot move
        List<Entity> entities = Arrays.asList(new Wall(initialPos.translateBy(Direction.UP), "wall"),
                new Wall(initialPos.translateBy(Direction.DOWN), "wall"),
                new Wall(initialPos.translateBy(Direction.LEFT), "wall"),
                new Wall(initialPos.translateBy(Direction.RIGHT), "wall"));

        // Update position for 1 tick
        hydra.move(entities, dungeon);

        // Check current position is the same as the initial position
        assertTrue(hydra.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test collision for portals")
    public void testPortalCollision() {
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Hydra hydra = new Hydra(initialPos, 10, 10, 1, 1, "hydra");

        // Surround with portals so it cannot move
        List<Entity> entities = Arrays.asList(new Portal(initialPos.translateBy(Direction.UP), "portal", "red"),
                new Portal(initialPos.translateBy(Direction.DOWN), "portal", "red"),
                new Portal(initialPos.translateBy(Direction.LEFT), "portal", "blue"),
                new Portal(initialPos.translateBy(Direction.RIGHT), "portal", "blue"));

        hydra.move(entities, dungeon);

        // Check current position is the same as the initial position
        assertTrue(hydra.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test collision for zombie toast spawner")
    public void testSpawnerCollision() {
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Hydra hydra = new Hydra(initialPos, 10, 10, 1, 1, "hydra");

        // Surround zombie spawner
        List<Entity> entities = Arrays.asList(new ZombieToastSpawner(initialPos.translateBy(Direction.RIGHT), "zombie_toast_spawner"),
                new ZombieToastSpawner(initialPos.translateBy(Direction.DOWN), "Zombie_toast_spawner"),
                new ZombieToastSpawner(initialPos.translateBy(Direction.LEFT), "Zombie_toast_spawner"),
                new ZombieToastSpawner(initialPos.translateBy(Direction.UP), "Zombie_toast_spawner"));

        hydra.move(entities, dungeon);

        // Check current position is the same as the initial position
        assertTrue(hydra.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test hydra runs away from player when invincible")
    public void testInvinciblePotionMovement() {
        Dungeon dungeon = new Dungeon();
        Position initialPosHydra = new Position(0, 0);
        Hydra hydra = new Hydra(initialPosHydra, 10, 10, 1, 1, "hydra");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create invisible potion and consume it
        Potion invinciblePotion = new InvinciblePotion(initialPosPlayer, "invincibility_potion", 2);
        player.subscribe(hydra);
        player.addItem(invinciblePotion);
        player.consumePotion(invinciblePotion);

        List<Entity> entities = new ArrayList<>();
        entities.add(hydra);
        entities.add(player);

        // Hydra runs away
        hydra.move(entities, dungeon);
        assertEquals(initialPosHydra.translateBy(Direction.UP), hydra.getPosition());
        hydra.move(entities, dungeon);
        assertEquals(initialPosHydra.translateBy(Direction.UP).translateBy(Direction.UP), hydra.getPosition());
    }

    @Test
    @DisplayName("Test stuck in swamp tile")
    public void testStuckInSwampTile() {
        Dungeon dungeon = new Dungeon();
        Position initialPosHydra = new Position(0, 0);
        Hydra hydra = new Hydra(initialPosHydra, 10, 10, 1, 1, "hydra");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create invisible potion and consume it
        Potion invinciblePotion = new InvinciblePotion(initialPosPlayer, "invincibility_potion", 10);
        player.subscribe(hydra);
        player.addItem(invinciblePotion);
        player.consumePotion(invinciblePotion);

        // Place swamp tile
        SwampTile swampTile = new SwampTile(initialPosHydra.translateBy(Direction.UP), 2, "swamp_tile");

        List<Entity> entities = new ArrayList<>();
        entities.add(hydra);
        entities.add(player);
        entities.add(swampTile);

        // Hydra runs away but gets stuck in swamp tile for 2 ticks
        hydra.move(entities, dungeon);
        assertEquals(initialPosHydra.translateBy(Direction.UP), hydra.getPosition());

        hydra.move(entities, dungeon);
        assertEquals(initialPosHydra.translateBy(Direction.UP), hydra.getPosition());

        hydra.move(entities, dungeon);
        assertEquals(initialPosHydra.translateBy(Direction.UP), hydra.getPosition());

        hydra.move(entities, dungeon);
        assertEquals(initialPosHydra.translateBy(Direction.UP).translateBy(Direction.UP), hydra.getPosition());
    }
}
