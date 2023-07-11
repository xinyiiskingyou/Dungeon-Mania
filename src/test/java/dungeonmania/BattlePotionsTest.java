package dungeonmania;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.battles.Battle;
import dungeonmania.battles.Round;
import dungeonmania.enemy.Mercenary;
import dungeonmania.enemy.Spider;
import dungeonmania.enemy.ZombieToast;
import dungeonmania.entities.Player;
import dungeonmania.items.InvinciblePotion;
import dungeonmania.items.InvisiblePotion;
import dungeonmania.items.Potion;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class BattlePotionsTest {
    /* UNIT TESTS */

    @Test
    @DisplayName("Test battle with invincibility potion zombie")
    public void testBattleInvincibleZombie() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 0, 0, 0, "mercenary");
        double enemyHealth = 50;
        ZombieToast zombie = new ZombieToast(initialPosMercenary, enemyHealth, 10, "zombie_toast");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        double playerHealth = 100;
        Player player = new Player(initialPosPlayer, 0, playerHealth, "player");

        dungeon.addEntity(mercenary);
        dungeon.addEntity(zombie);
        dungeon.addEntity(player);

        assertEquals("FightStrategy", zombie.getBattleStrategyName());

        // Create invincible potion and consume it
        Potion invinciblePotion = new InvinciblePotion(initialPosPlayer, "invincibility_potion", 1);
        player.subscribe(mercenary);
        player.subscribe(zombie);
        player.addItem(invinciblePotion);
        player.consumePotion(invinciblePotion);

        // make sure zombie battle strat is invincible run
        assertEquals("InvincibleRunStrategy", zombie.getBattleStrategyName());

        Battle b = zombie.battle(player, dungeon);
        // assert only played 1 round

        assertEquals(1, b.getRoundsList().size());
        Round round = b.getRoundsList().get(0);

        // check potion used in weapon
        assertEquals(1, round.getWeaponsUsed().size());
        assertEquals(invinciblePotion, round.getWeaponsUsed().get(0));

        // check player received no damage
        assertEquals(0, round.getDeltaPlayerHealth());

        // check enemy immediately lost all health
        assertEquals(-enemyHealth, round.getDeltaEnemyHealth());

    }

    @Test
    @DisplayName("Test battle with invisibility potion zombie")
    public void testBattleInvisibilityZombie() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        Mercenary mercenary = new Mercenary(initialPosMercenary, 10, 10, 2, 0, 0, 0, "mercenary");
        double enemyHealth = 50;
        ZombieToast zombie = new ZombieToast(initialPosMercenary, enemyHealth, 10, "zombie_toast");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        double playerHealth = 100;
        Player player = new Player(initialPosPlayer, 0, playerHealth, "player");

        dungeon.addEntity(mercenary);
        dungeon.addEntity(zombie);
        dungeon.addEntity(player);

        // Create invincible potion and consume it
        Potion invisiblePotion = new InvisiblePotion(initialPosPlayer, "invisibility_potion", 1);
        player.subscribe(mercenary);
        player.subscribe(zombie);
        player.addItem(invisiblePotion);
        player.consumePotion(invisiblePotion);

        // make sure zombie battle strat is invisible
        assertEquals("InvisibleAvoidStrategy", zombie.getBattleStrategyName());
        Battle b = zombie.battle(player, dungeon);

        // assert no battle occured
        assertEquals(null, b);

    }

    @Test
    @DisplayName("Test battle with invincibility potion all enemies")
    public void testBattleInvincibleAllEnemies() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        double enemyHealth = 50;
        Mercenary mercenary = new Mercenary(initialPosMercenary, enemyHealth, 10, 2, 0, 0, 0, "mercenary");

        ZombieToast zombie = new ZombieToast(initialPosMercenary, enemyHealth, 10, "zombie_toast");
        Mercenary mercenaryEnemy = new Mercenary(initialPosMercenary, enemyHealth, 10, 2, 0, 0, 0, "mercenary");
        Spider spider = new Spider(initialPosMercenary, enemyHealth, 10, "spider");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        double playerHealth = 100;
        Player player = new Player(initialPosPlayer, 0, playerHealth, "player");

        dungeon.addEntity(mercenary);
        dungeon.addEntity(zombie);
        dungeon.addEntity(spider);
        dungeon.addEntity(mercenaryEnemy);

        assertEquals("FightStrategy", zombie.getBattleStrategyName());
        assertEquals("FightStrategy", mercenaryEnemy.getBattleStrategyName());
        assertEquals("FightStrategy", spider.getBattleStrategyName());

        // Create invincible potion and consume it
        Potion invinciblePotion = new InvinciblePotion(initialPosPlayer, "invincibility_potion", 1);
        player.subscribe(mercenary);
        player.subscribe(zombie);
        player.subscribe(spider);
        player.subscribe(mercenaryEnemy);
        player.addItem(invinciblePotion);
        player.consumePotion(invinciblePotion);

        // make sure zombie battle strat is invincible run
        assertEquals("InvincibleRunStrategy", zombie.getBattleStrategyName());
        assertEquals("InvincibleRunStrategy", mercenaryEnemy.getBattleStrategyName());
        assertEquals("InvincibleRunStrategy", spider.getBattleStrategyName());

        Battle b = zombie.battle(player, dungeon);
        // assert only played 1 round

        assertEquals(1, b.getRoundsList().size());
        Round round = b.getRoundsList().get(0);

        // check potion used
        assertEquals(invinciblePotion, round.getWeaponsUsed().get(0));

        // check player received no damage
        assertEquals(0, round.getDeltaPlayerHealth());

        // check enemy immediately lost all health
        assertEquals(-enemyHealth, round.getDeltaEnemyHealth());

        b = mercenaryEnemy.battle(player, dungeon);
        assertEquals(1, b.getRoundsList().size());
        round = b.getRoundsList().get(0);
        assertEquals(invinciblePotion, round.getWeaponsUsed().get(0));

        // check player received no damage
        assertEquals(0, round.getDeltaPlayerHealth());

        // check enemy immediately lost all health
        assertEquals(-enemyHealth, round.getDeltaEnemyHealth());

        b = spider.battle(player, dungeon);
        assertEquals(1, b.getRoundsList().size());
        round = b.getRoundsList().get(0);
        assertEquals(invinciblePotion, round.getWeaponsUsed().get(0));

        // check player received no damage
        assertEquals(0, round.getDeltaPlayerHealth());

        // check enemy immediately lost all health
        assertEquals(-enemyHealth, round.getDeltaEnemyHealth());

    }

    @Test
    @DisplayName("Test battle with invisibility potion all enemies")
    public void testBattleInvisibilityAllEnemies() {
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        double enemyHealth = 50;
        Mercenary mercenary = new Mercenary(initialPosMercenary, enemyHealth, 10, 2, 0, 0, 0, "mercenary");

        ZombieToast zombie = new ZombieToast(initialPosMercenary, enemyHealth, 10, "zombie_toast");
        Mercenary mercenaryEnemy = new Mercenary(initialPosMercenary, enemyHealth, 10, 2, 0, 0, 0, "mercenary");
        Spider spider = new Spider(initialPosMercenary, enemyHealth, 10, "spider");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        double playerHealth = 100;
        Player player = new Player(initialPosPlayer, 0, playerHealth, "player");

        dungeon.addEntity(zombie);
        dungeon.addEntity(spider);
        dungeon.addEntity(mercenaryEnemy);

        // Create invincible potion and consume it
        Potion invisiblePotion = new InvisiblePotion(initialPosPlayer, "invisibility_potion", 1);
        player.subscribe(mercenary);
        player.subscribe(zombie);
        player.subscribe(spider);
        player.subscribe(mercenaryEnemy);
        player.addItem(invisiblePotion);
        player.consumePotion(invisiblePotion);

        // make sure zombie battle strat is invisible
        assertEquals("InvisibleAvoidStrategy", zombie.getBattleStrategyName());
        assertEquals("InvisibleAvoidStrategy", spider.getBattleStrategyName());
        assertEquals("InvisibleAvoidStrategy", mercenaryEnemy.getBattleStrategyName());
        Battle b = zombie.battle(player, dungeon);
        // assert no battle occured
        assertEquals(null, b);

        b = spider.battle(player, dungeon);
        assertEquals(null, b);

        b = mercenaryEnemy.battle(player, dungeon);
        assertEquals(null, b);

    }

    @Test
    @DisplayName("Test battle with invincibility potion reach duration mid battle")
    public void testBattleInvincibleUsed() {
        // Create a mercenary and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        double enemyHealth = 50;
        Mercenary mercenary = new Mercenary(initialPosMercenary, enemyHealth, 10, 2, 0, 0, 0, "mercenary");

        ZombieToast zombie = new ZombieToast(initialPosMercenary, enemyHealth, 10, "zombie_toast");
        Mercenary mercenaryEnemy = new Mercenary(initialPosMercenary, enemyHealth, 10, 2, 0, 0, 0, "mercenary");
        Spider spider = new Spider(initialPosMercenary, enemyHealth, 10, "spider");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        double playerHealth = 100;
        Player player = new Player(initialPosPlayer, 0, playerHealth, "player");

        dungeon.addEntity(mercenary);
        dungeon.addEntity(zombie);
        dungeon.addEntity(spider);
        dungeon.addEntity(mercenaryEnemy);

        assertEquals("FightStrategy", zombie.getBattleStrategyName());
        assertEquals("FightStrategy", mercenaryEnemy.getBattleStrategyName());
        assertEquals("FightStrategy", spider.getBattleStrategyName());

        // Create invincible potion and consume it
        Potion invinciblePotion = new InvinciblePotion(initialPosPlayer, "invincibility_potion", 1);
        player.subscribe(mercenary);
        player.subscribe(zombie);
        player.subscribe(spider);
        player.subscribe(mercenaryEnemy);
        player.addItem(invinciblePotion);
        player.consumePotion(invinciblePotion);

        // make sure zombie battle strat is invincible run
        assertEquals("InvincibleRunStrategy", zombie.getBattleStrategyName());
        assertEquals("InvincibleRunStrategy", mercenaryEnemy.getBattleStrategyName());
        assertEquals("InvincibleRunStrategy", spider.getBattleStrategyName());

        Battle b = zombie.battle(player, dungeon);
        // assert only played 1 round

        assertEquals(1, b.getRoundsList().size());
        Round round = b.getRoundsList().get(0);
        assertEquals(invinciblePotion, round.getWeaponsUsed().get(0));

        // check player received no damage
        assertEquals(0, round.getDeltaPlayerHealth());

        // check enemy immediately lost all health
        assertEquals(-enemyHealth, round.getDeltaEnemyHealth());

        player.tickPotions();
        // potion should have weared off, no longer invincible
        assertEquals("FightStrategy", mercenaryEnemy.getBattleStrategyName());
        assertEquals("FightStrategy", spider.getBattleStrategyName());

        b = mercenaryEnemy.battle(player, dungeon);
        assertFalse(b.getRoundsList().size() == 1);
        round = b.getRoundsList().get(0);
        assertEquals(0, round.getWeaponsUsed().size());
        assertFalse(round.getDeltaPlayerHealth() == 0);
        assertFalse(round.getDeltaEnemyHealth() == -enemyHealth);

        b = spider.battle(player, dungeon);
        round = b.getRoundsList().get(0);
        assertEquals(0, round.getWeaponsUsed().size());
        assertFalse(round.getDeltaPlayerHealth() == 0);
        assertFalse(round.getDeltaEnemyHealth() == -enemyHealth);

    }

    @Test
    @DisplayName("Test battle with invisibility potion reach duration")
    public void testBattleInvisibileUsed() {
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        double enemyHealth = 50;
        Mercenary mercenary = new Mercenary(initialPosMercenary, enemyHealth, 10, 2, 0, 0, 0, "mercenary");

        ZombieToast zombie = new ZombieToast(initialPosMercenary, enemyHealth, 10, "zombie_toast");
        Mercenary mercenaryEnemy = new Mercenary(initialPosMercenary, enemyHealth, 10, 2, 0, 0, 0, "mercenary");
        Spider spider = new Spider(initialPosMercenary, enemyHealth, 10, "spider");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        double playerHealth = 100;
        Player player = new Player(initialPosPlayer, 0, playerHealth, "player");

        dungeon.addEntity(zombie);
        dungeon.addEntity(spider);
        dungeon.addEntity(mercenaryEnemy);

        // Create invincible potion and consume it
        Potion invisiblePotion = new InvisiblePotion(initialPosPlayer, "invisibility_potion", 2);
        player.subscribe(mercenary);
        player.subscribe(zombie);
        player.subscribe(spider);
        player.subscribe(mercenaryEnemy);
        player.addItem(invisiblePotion);
        player.consumePotion(invisiblePotion);

        // make sure zombie battle strat is invisible
        assertEquals("InvisibleAvoidStrategy", zombie.getBattleStrategyName());
        assertEquals("InvisibleAvoidStrategy", spider.getBattleStrategyName());
        assertEquals("InvisibleAvoidStrategy", mercenaryEnemy.getBattleStrategyName());
        Battle b = zombie.battle(player, dungeon);
        // assert no battle occured
        assertEquals(null, b);

        // tick potion
        player.tickPotions();

        // potion has 1 tick duration left
        b = spider.battle(player, dungeon);
        assertEquals(null, b);
        assertEquals("InvisibleAvoidStrategy", spider.getBattleStrategyName());
        assertEquals("InvisibleAvoidStrategy", mercenaryEnemy.getBattleStrategyName());

        player.tickPotions();
        assertEquals("FightStrategy", mercenaryEnemy.getBattleStrategyName());

        // potion weared off
        b = mercenaryEnemy.battle(player, dungeon);
        assertFalse(b == null);

    }

    @Test
    @DisplayName("Test battle after invisibility potion")
    public void testBattleAfterInvisible() {
        Dungeon dungeon = new Dungeon();
        Position initialPosMercenary = new Position(0, 0);
        double enemyHealth = 50;
        Mercenary mercenary = new Mercenary(initialPosMercenary, enemyHealth, 10, 2, 0, 0, 0, "mercenary");

        // Create a player and set its starting position above mercenary
        Position initialPosPlayer = new Position(0, 2);
        double playerHealth = 100;
        Player player = new Player(initialPosPlayer, 0, playerHealth, "player");

        dungeon.addEntity(mercenary);

        // Create invincible potion and consume it
        Potion invisiblePotion = new InvisiblePotion(initialPosPlayer, "invisibility_potion", 2);
        player.subscribe(mercenary);
        player.addItem(invisiblePotion);
        player.consumePotion(invisiblePotion);
        player.tickPotions();// durability is 1

        // make sure battle strat is invisible
        assertEquals("InvisibleAvoidStrategy", mercenary.getBattleStrategyName());
        Battle b = mercenary.battle(player, dungeon);
        // assert no battle occured
        assertEquals(null, b);

        // tick potion
        player.tickPotions(); // durability is 0
        assertEquals(null, player.getActivePotion());

        // merc battle strat should be fight start now
        assertEquals("FightStrategy", mercenary.getBattleStrategyName());

    }

    /* INTEGRATION TESTS */

    @Test
    @DisplayName("Test invincible when battle")
    public void testInvincibleBattle() {
        // consume potion
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_potions", "c_battleTest_potions");
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        String potion = getEntities(res, "invincibility_potion").get(0).getId(); // potion durability is 4

        // move player into potion
        dmc.tick(Direction.LEFT);
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());

        // consume potion
        assertDoesNotThrow(() -> dmc.tick(potion)); // durability is 3
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        // player has consumed the potion - remove from inventory
        assertEquals(0, getInventory(res, "invincibility_potion").size());

        // push boulder that's blocking a zombie
        DungeonResponse zombieBattlePlayer = dmc.tick(Direction.LEFT); // durability is 2
        // player jump onto zombie
        DungeonResponse playerBattleZombie = dmc.tick(Direction.UP); // durability is 1
        // check which tick battle occured
        DungeonResponse postBattleResponse;
        if (zombieBattlePlayer.getBattles().size() > 0) {
            System.out.println(" zombie initiated battle");
            postBattleResponse = zombieBattlePlayer;
        } else {
            postBattleResponse = playerBattleZombie;
        }

        // player is invincible, battle should end in one round
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertEquals(1, battle.getRounds().size());
        RoundResponse round = battle.getRounds().get(0);

        // assert used invincible potion
        assertEquals(1, round.getWeaponryUsed().size());
        assertEquals(potion, round.getWeaponryUsed().get(0).getId());
        assertEquals(0, round.getDeltaCharacterHealth());
        int zombieHealth = 5;
        assertEquals(-zombieHealth, round.getDeltaEnemyHealth());

    }

    @Test
    @DisplayName("Test invisible when battle")
    public void testInvisibeBattle() {
        // consume potion
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_potions", "c_battleTest_potions");
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        String potion = getEntities(res, "invisibility_potion").get(0).getId(); // potion durability is 4

        // move player into invisibility potion
        dmc.tick(Direction.DOWN);
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getEntities(res, "invisibility_potion").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());

        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);

        // consume potion
        assertDoesNotThrow(() -> dmc.tick(potion)); // durability is 3
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getEntities(res, "invisibility_potion").size());
        // player has consumed the potion - remove from inventory
        assertEquals(0, getInventory(res, "invisibility_potion").size());
        // push boulder that's blocking a zombie
        DungeonResponse zombieBattlePlayer = dmc.tick(Direction.LEFT); // durability is 2
        // player jump onto zombie
        DungeonResponse playerBattleZombie = dmc.tick(Direction.UP); // durability is 1
        // check which tick battle occured
        DungeonResponse postBattleResponse;
        if (zombieBattlePlayer.getBattles().size() > 0) {
            postBattleResponse = zombieBattlePlayer;
        } else {
            postBattleResponse = playerBattleZombie;
        }

        // player is invisible, battle should be avoided
        List<BattleResponse> battle = postBattleResponse.getBattles();
        assertEquals(0, battle.size());

    }

    @Test
    @DisplayName("Test multiple potions battle")
    public void testQueuePotionsBattle() {
        // consume potion
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_potions", "c_battleTest_potions");
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        String invis_potion = getEntities(res, "invisibility_potion").get(0).getId(); // potion durability is 4
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        String invinc_potion = getEntities(res, "invincibility_potion").get(0).getId(); // potion durability is 4

        // move player into invisibility potion
        dmc.tick(Direction.DOWN);
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getEntities(res, "invisibility_potion").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());

        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());

        // consume potion
        assertDoesNotThrow(() -> dmc.tick(invis_potion)); // durability is 3
        assertDoesNotThrow(() -> dmc.tick(invinc_potion)); // durability is 3
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        // player has consumed the potion - remove from inventory
        assertEquals(0, getInventory(res, "invincibility_potion").size());
        assertEquals(0, getEntities(res, "invisibility_potion").size());
        // player has consumed the potion - remove from inventory
        assertEquals(0, getInventory(res, "invisibility_potion").size());

        // push boulder that's blocking a zombie
        DungeonResponse zombieBattlePlayer = dmc.tick(Direction.LEFT); // durability is 2
        // player jump onto zombie
        DungeonResponse playerBattleZombie = dmc.tick(Direction.UP); // durability is 1
        // check which tick battle occured
        DungeonResponse postBattleResponse;
        if (zombieBattlePlayer.getBattles().size() > 0) {
            postBattleResponse = zombieBattlePlayer;
        } else {
            postBattleResponse = playerBattleZombie;
        }

        // player is invisible, battle should be avoided
        List<BattleResponse> battle = postBattleResponse.getBattles();
        assertEquals(0, battle.size());

        //  battle another player, now player should be invincible
        DungeonResponse zombie2BattlePlayer = dmc.tick(Direction.RIGHT);
        DungeonResponse playerBattleZombie2 = dmc.tick(Direction.UP);
        if (zombie2BattlePlayer.getBattles().size() > 0) {
            postBattleResponse = zombieBattlePlayer;
        } else {
            postBattleResponse = playerBattleZombie2;
        }

        // player is invincible, battle should end in one round
        BattleResponse battle2 = postBattleResponse.getBattles().get(0);
        assertEquals(1, battle2.getRounds().size());
        RoundResponse round = battle2.getRounds().get(0);
        assertEquals(0, round.getDeltaCharacterHealth());
        int zombieHealth = 5;
        assertEquals(-zombieHealth, round.getDeltaEnemyHealth());
        // assert used invincible potion
        assertEquals(1, round.getWeaponryUsed().size());
        assertEquals(invinc_potion, round.getWeaponryUsed().get(0).getId());


    }

}