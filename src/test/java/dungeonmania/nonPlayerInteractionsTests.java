package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.entities.Player;
import dungeonmania.entities.ZombieToastSpawner;
import dungeonmania.util.Position;

public class nonPlayerInteractionsTests {
    @Test
    @DisplayName("Test zombie spawn rate 0 in dungeon") 
    public void testZombieSpawnRate(){
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        // spawn zombie every second tick
        dungeon.setZombieSpawnRate(0);
        dungeon.setDungeonSpawnLimit(3, 3);
        dungeon.setSpiderSpawner(new SpiderSpawner(3));
        for (int i = 1; i < 3; i++) {
            dungeon.non_player_acions();
            assertEquals(0, dungeon.getEnemies().size());
        }
        
        dungeon.non_player_acions();
        assertEquals(1, dungeon.getEnemies().size());
        assertEquals("spider", dungeon.getEnemies().get(0).getType());
    }

    @Test
    @DisplayName("Test non zero spawn rates for spider and zombie") 
    public void testZombieSpiderSpawnRate(){
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        int spawnRate = 1;
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        // spawn zombie every second tick
        dungeon.setZombieSpawnRate(spawnRate);
        dungeon.setDungeonSpawnLimit(3, 3);
        dungeon.setSpiderSpawner(new SpiderSpawner(spawnRate));
        dungeon.non_player_acions();
        assertEquals(2, dungeon.getEnemies().size());
        assertEquals("zombie_toast", dungeon.getEnemies().get(0).getType());
        assertEquals("spider", dungeon.getEnemies().get(1).getType());
    }

    @Test
    @DisplayName("Test different spawn rates zombie spider") 
    public void testDifferentZombieSpiderSpawnRate(){
        Position position = new Position(1,1);
        Dungeon dungeon = new Dungeon();
        int spawnRateZ = 2;
        int spawnRateS = 3;
        ZombieToastSpawner spawner = new ZombieToastSpawner(position, "zombie_toast_spawner");
        dungeon.addEntity(spawner);
        Player player = new Player(new Position(5, 5), 1.0, 1.0, "player");
        dungeon.setPlayer(player);
        dungeon.setZombieAttack(1.0);
        dungeon.setZombieHealth(1.0);
        // spawn zombie every second tick
        dungeon.setZombieSpawnRate(spawnRateZ);
        dungeon.setDungeonSpawnLimit(3, 3);
        dungeon.setSpiderSpawner(new SpiderSpawner(spawnRateS));
        dungeon.non_player_acions();
        assertEquals(0, dungeon.getEnemies().size());
        dungeon.non_player_acions();
        assertEquals(1, dungeon.getEnemies().size());
        assertEquals("zombie_toast", dungeon.getEnemies().get(0).getType());

        dungeon.non_player_acions();
        assertEquals(2, dungeon.getEnemies().size());
        assertEquals("spider", dungeon.getEnemies().get(1).getType());

        dungeon.non_player_acions();
        assertEquals(3, dungeon.getEnemies().size());
        assertEquals("zombie_toast", dungeon.getEnemies().get(2).getType());
    }
}
