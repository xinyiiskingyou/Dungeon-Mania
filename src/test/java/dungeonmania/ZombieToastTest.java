package dungeonmania;

import dungeonmania.entities.*;
import dungeonmania.items.InvinciblePotion;
import dungeonmania.items.Potion;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.enemy.ZombieToast;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZombieToastTest {
    @Test
    @DisplayName("Test movement changes position value")
    public void testPositionChange() {
        // Create a zombie and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        ZombieToast zombie = new ZombieToast(initialPos, 10, 10, "zombie_toast");
        List<Entity> entities = new ArrayList<>();

        // Update zombie position for 1 tick
        zombie.move(entities, dungeon);

        // Check current position is different from initial position
        assertFalse(zombie.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test collision for boulders")
    public void testBoulderCollision() {
        // Create a zombie and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        ZombieToast zombie = new ZombieToast(initialPos, 10, 10, "zombie_toast");

        // Surround zombie with boulders so it cannot move
        List<Entity> entities = Arrays.asList(new Boulder(initialPos.translateBy(Direction.UP), "boulder"),
                new Boulder(initialPos.translateBy(Direction.DOWN), "boulder"),
                new Boulder(initialPos.translateBy(Direction.LEFT), "boulder"),
                new Boulder(initialPos.translateBy(Direction.RIGHT), "boulder"));

        // Update zombie position for 1 tick
        zombie.move(entities, dungeon);

        // Check current position is the same as the initial position
        assertTrue(zombie.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test collision for locked doors")
    public void testDoorCollision() {
        // Create a zombie and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        ZombieToast zombie = new ZombieToast(initialPos, 10, 10, "zombie_toast");

        // Surround zombie with locked doors so it cannot move
        List<Entity> entities = Arrays.asList(new Door(initialPos.translateBy(Direction.UP), "door", 5),
                new Door(initialPos.translateBy(Direction.DOWN), "door", 6),
                new Door(initialPos.translateBy(Direction.LEFT), "door", 7),
                new Door(initialPos.translateBy(Direction.RIGHT), "door", 8));

        // Update zombie position for 1 tick
        zombie.move(entities, dungeon);

        // Check current position is the same as the initial position
        assertTrue(zombie.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test collision for walls")
    public void testWallCollision() {
        // Create a zombie and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        ZombieToast zombie = new ZombieToast(initialPos, 10, 10, "zombie_toast");

        // Surround zombie with walls so it cannot move
        List<Entity> entities = Arrays.asList(new Wall(initialPos.translateBy(Direction.UP), "wall"),
                new Wall(initialPos.translateBy(Direction.DOWN), "wall"),
                new Wall(initialPos.translateBy(Direction.LEFT), "wall"),
                new Wall(initialPos.translateBy(Direction.RIGHT), "wall"));

        // Update zombie position for 1 tick
        zombie.move(entities, dungeon);

        // Check current position is the same as the initial position
        assertTrue(zombie.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test collision for portals")
    public void testPortalCollision() {
        // Create a zombie and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        ZombieToast zombie = new ZombieToast(initialPos, 10, 10, "zombie_toast");

        // Surround zombie with portals so it cannot move
        List<Entity> entities = Arrays.asList(new Portal(initialPos.translateBy(Direction.UP), "portal", "red"),
                new Portal(initialPos.translateBy(Direction.DOWN), "portal", "red"),
                new Portal(initialPos.translateBy(Direction.LEFT), "portal", "blue"),
                new Portal(initialPos.translateBy(Direction.RIGHT), "portal", "blue"));

        // Update zombie position for 1 tick
        zombie.move(entities, dungeon);

        // Check current position is the same as the initial position
        assertTrue(zombie.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test collision for zombie toast spawner")
    public void testSpawnerCollision() {
        // Create a zombie and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        ZombieToast zombie = new ZombieToast(initialPos, 10, 10, "zombie_toast");

        // Surround zombie spawner
        List<Entity> entities = Arrays.asList(new ZombieToastSpawner(initialPos.translateBy(Direction.RIGHT), "zombie_toast_spawner"),
                new ZombieToastSpawner(initialPos.translateBy(Direction.DOWN), "Zombie_toast_spawner"),
                new ZombieToastSpawner(initialPos.translateBy(Direction.LEFT), "Zombie_toast_spawner"),
                new ZombieToastSpawner(initialPos.translateBy(Direction.UP), "Zombie_toast_spawner"));
        // Update zombie position for 1 tick
        zombie.move(entities, dungeon);

        // Check current position is the same as the initial position
        assertTrue(zombie.getPosition().equals(initialPos));
    }

    @Test
    @DisplayName("Test zombie runs away from player when invincible")
    public void testInvinciblePotionMovement() {
        // Create a zombie and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosZombie = new Position(0, 0);
        ZombieToast zombieToast = new ZombieToast(initialPosZombie, 10, 10, "zombie_toast");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create invisible potion and consume it
        Potion invinciblePotion = new InvinciblePotion(initialPosPlayer, "invincibility_potion", 2);
        player.subscribe(zombieToast);
        player.addItem(invinciblePotion);
        player.consumePotion(invinciblePotion);

        List<Entity> entities = new ArrayList<>();
        entities.add(zombieToast);
        entities.add(player);

        // Zombie runs away
        zombieToast.move(entities, dungeon);
        assertEquals(initialPosZombie.translateBy(Direction.UP), zombieToast.getPosition());
        zombieToast.move(entities, dungeon);
        assertEquals(initialPosZombie.translateBy(Direction.UP).translateBy(Direction.UP), zombieToast.getPosition());
    }

    @Test
    @DisplayName("Test zombie runs away from player when invincible and stuck in swamp tile")
    public void testSwampTileMovement() {
        // Create a zombie and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosZombie = new Position(0, 0);
        ZombieToast zombieToast = new ZombieToast(initialPosZombie, 10, 10, "zombie_toast");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        Player player = new Player(initialPosPlayer, 0, 2, "player");

        // Create invisible potion and consume it
        Potion invinciblePotion = new InvinciblePotion(initialPosPlayer, "invincibility_potion", 2);
        player.subscribe(zombieToast);
        player.addItem(invinciblePotion);
        player.consumePotion(invinciblePotion);

        SwampTile swampTile = new SwampTile(initialPosZombie.translateBy(Direction.UP), 2, "swamp_tile");

        List<Entity> entities = new ArrayList<>();
        entities.add(zombieToast);
        entities.add(player);
        entities.add(swampTile);

        // Zombie runs away
        zombieToast.move(entities, dungeon);
        assertEquals(initialPosZombie.translateBy(Direction.UP), zombieToast.getPosition());

        // Stuck in swamp tile
        zombieToast.move(entities, dungeon);
        assertEquals(initialPosZombie.translateBy(Direction.UP), zombieToast.getPosition());

        zombieToast.move(entities, dungeon);
        assertEquals(initialPosZombie.translateBy(Direction.UP), zombieToast.getPosition());

        // Move off swamp tile
        zombieToast.move(entities, dungeon);
        assertEquals(initialPosZombie.translateBy(Direction.UP).translateBy(Direction.UP), zombieToast.getPosition());
    }
}
