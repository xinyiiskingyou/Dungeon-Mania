package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class SpiderSpawnClassTests {
    // correct number of spiders are spawned
    @Test
    @DisplayName("Test correct spider spawn rate")
    public void basicSpiderSpawn() {
        int spawnRate =  4;
        SpiderSpawner spawner = new SpiderSpawner(spawnRate);
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(0,0), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        dungeon.setDungeonSpawnLimit(10, 10);
        for (int i = 1; i < spawnRate; i++) {
            // tick 1 - 3
            spawner.spawn(dungeon);
            assertEquals(0, dungeon.getEnemies().size());
        }
        // tick 4
        spawner.spawn(dungeon);
        assertEquals(1, dungeon.getEnemies().size());
        // tick 5 - 7
        for (int i = 1; i < spawnRate; i++) {
            spawner.spawn(dungeon);
            assertEquals(1, dungeon.getEnemies().size());
        }
        // tick 8
        spawner.spawn(dungeon);
        assertEquals(2, dungeon.getEnemies().size());        

    }

    // spiders can spawn on anything
    @Test
    @DisplayName("Test spider can spawn on anything")
    public void spiderSpawnNotBlocked() {
        int spawnRate =  1;
        SpiderSpawner spawner = new SpiderSpawner(spawnRate);
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(0,0), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        dungeon.setDungeonSpawnLimit(1, 1);
        spawner.spawn(dungeon);
        assertEquals(1, dungeon.getEnemies().size());
    }

        // spiders can spawn on anything
        @Test
        @DisplayName("Test spider spawn rate of 0")
        public void spiderSpawnRate0() {
            int spawnRate =  0;
            SpiderSpawner spawner = new SpiderSpawner(spawnRate);
            Dungeon dungeon = new Dungeon();
            Player player = new Player(new Position(0,0), 1.0, 1.0, "player");
            dungeon.setPlayer(player);
            dungeon.setDungeonSpawnLimit(1, 1);
            spawner.spawn(dungeon);
            assertEquals(0, dungeon.getEnemies().size());
            spawner.spawn(dungeon);
            assertEquals(0, dungeon.getEnemies().size());
            spawner.spawn(dungeon);
            assertEquals(0, dungeon.getEnemies().size());
        }

}
