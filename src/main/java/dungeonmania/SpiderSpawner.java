package dungeonmania;

import java.util.List;
import java.util.Random;

import dungeonmania.enemy.Spider;
import dungeonmania.util.Position;
import java.io.Serializable;

public class SpiderSpawner implements Serializable {
    
    private int spawnRate;
    private int spawnCountdown;
    
    public SpiderSpawner(int spawnRate) {
        this.spawnRate = spawnRate;
        this.spawnCountdown = spawnRate - 1;
    }
    // class to spawn spider
    public void spawn(Dungeon dungeon) {
        if (spawnCountdown != 0 || spawnRate == 0) {  
            spawnCountdown --;
            return;
        }
        spawnCountdown = spawnRate - 1;
        Spider spider = new Spider(spiderSpawnPos(dungeon.getDungeonLimits()), dungeon.getSpiderHealth(), dungeon.getSpiderAttack(), "spider");
        dungeon.addEnemy(spider);
        dungeon.getPlayer().subscribe(spider);
    }
    
    public Position spiderSpawnPos(List <Integer> dungeonSpawnLimits) {
        Random randomX = new Random();
        int spiderX = Math.abs(randomX.nextInt(dungeonSpawnLimits.get(0)));

        Random randomY = new Random();
        int spiderY = Math.abs(randomY.nextInt(dungeonSpawnLimits.get(1)));
        return new Position(spiderX, spiderY);
    }

    public int getSpiderSpawnRate() {
        return this.spawnRate;
    }
}
