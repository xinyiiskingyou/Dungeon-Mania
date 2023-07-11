package dungeonmania;

import static dungeonmania.TestUtils.countEntityOfType;
import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getValueFromConfigFile;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.battles.Battle;
import dungeonmania.battles.Round;
import dungeonmania.enemy.Mercenary;
import dungeonmania.enemy.Spider;
import dungeonmania.enemy.ZombieToast;
import dungeonmania.entities.Player;
import dungeonmania.items.ItemEntity;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.weapon.Bow;
import dungeonmania.weapon.Shield;
import dungeonmania.weapon.Sword;

public class BattleTest {
    /* UNIT TESTS */

    @Test
    @DisplayName("Test battle with no weapons vs zombie and zombie dies")
    public void testNoWeaponsZombieDies() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        ZombieToast zombie = new ZombieToast(initialPos, 10, 10, "zombie_toast");
        dungeon.addEnemy(zombie);

        // call battle
        Battle b = zombie.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 5 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(5, numRounds);

        // assert no weapons used and player and enemy health decreases by correct
        // amount each round
        assertEquals(zombie.getAttackDamage() / 10, 1);
        for (Round r : b.getRoundsList()) {
            assertEquals(r.getItemResponseWeaponsUsed().size(), 0);
            assertEquals(r.getDeltaPlayerHealth(), -(zombie.getAttackDamage() / 10));
            assertEquals(r.getDeltaEnemyHealth(), -(player.getAttackDamage() / 5));
            enemyHealth += r.getDeltaEnemyHealth();
            playerHealth += r.getDeltaPlayerHealth();
        }

        assertEquals(0, enemyHealth);
        assertEquals(enemyHealth, zombie.getHealth());
        assertEquals(95, playerHealth);
        assertEquals(playerHealth, player.getHealth());

        // make sure zombie removed from game
        assertTrue(!dungeon.getEnemies().contains(zombie));
    }

    @Test
    @DisplayName("Test battle with multiple enemies")
    public void testBattleMultiple() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position playerInitialPos = new Position(1, 1);
        Position initialPos = new Position(2, 1);
        Player player = new Player(playerInitialPos, 10, 100, "player");
        ZombieToast zombie = new ZombieToast(initialPos, 10, 10, "zombie_toast");
        Spider spider = new Spider(initialPos, 10, 10, "spider");
        Mercenary merc = new Mercenary(initialPos, 10,10,0,0,0,0,"mercenary");
        dungeon.addEnemy(zombie);
        dungeon.addEnemy(spider);
        dungeon.addEnemy(merc);
        

        // move player right to battle
        player.move(dungeon, Direction.RIGHT);
        assertEquals(85, player.getHealth());
        assertEquals(0, zombie.getHealth());
        assertEquals(0, spider.getHealth());
        assertEquals(0, merc.getHealth());
    }

    @Test
    @DisplayName("Test battle with no weapons vs zombie and player dies")
    public void testNoWeaponsZombiePlayerDies() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 6, 37, "player");
        ZombieToast zombie = new ZombieToast(initialPos, 88, 57, "zombie_toast");
        dungeon.addEnemy(zombie);
        dungeon.addEntity(player);

        // call battle
        Battle b = zombie.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 7 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(7, numRounds);

        // assert no weapons used and player and enemy health decreases by correct
        // amount each round
        assertEquals(zombie.getAttackDamage() / 10, 5.7);
        for (Round r : b.getRoundsList()) {
            assertEquals(r.getItemResponseWeaponsUsed().size(), 0);
            assertEquals(r.getDeltaPlayerHealth(), -(zombie.getAttackDamage() / 10));
            assertEquals(r.getDeltaEnemyHealth(), -(player.getAttackDamage() / 5));
            enemyHealth += r.getDeltaEnemyHealth();
            playerHealth += r.getDeltaPlayerHealth();
        }

        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, zombie.getHealth());

        assertTrue(dungeon.getEnemies().contains(zombie));
        assertTrue(!dungeon.getEntities().contains(player));
    }

    @Test
    @DisplayName("Test battle with no weapons vs spider and spider dies")
    public void testNoWeaponsSpiderPlayerDies() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 42, 109, "player");
        Spider spider = new Spider(initialPos, 23, 3, "spider");

        // call battle
        Battle b = spider.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 7 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(3, numRounds);

        for (Round r : b.getRoundsList()) {
            assertEquals(r.getItemResponseWeaponsUsed().size(), 0);
            assertEquals(r.getDeltaPlayerHealth(), -(spider.getAttackDamage() / 10));
            assertEquals(r.getDeltaEnemyHealth(), -(player.getAttackDamage() / 5));
            enemyHealth += r.getDeltaEnemyHealth();
            playerHealth += r.getDeltaPlayerHealth();
        }

        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, spider.getHealth());
    }

    @Test
    @DisplayName("Test battle with no weapons vs mercenary and mercenary dies")
    public void testNoWeaponsMercenaryDies() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 243, 103, "player");
        Mercenary merc = new Mercenary(initialPos, 21, 13, 0, 0,0,0, "mercenary");

        // call battle
        Battle b = merc.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 1 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(1, numRounds);

        // assert no weapons used and player and enemy health decreases by correct
        // amount each round
        for (Round r : b.getRoundsList()) {
            assertEquals(r.getItemResponseWeaponsUsed().size(), 0);
            assertEquals(r.getDeltaPlayerHealth(), -(merc.getAttackDamage() / 10));
            assertEquals(r.getDeltaEnemyHealth(), -(player.getAttackDamage() / 5));
            enemyHealth += r.getDeltaEnemyHealth();
            playerHealth += r.getDeltaPlayerHealth();
        }
        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, merc.getHealth());
    }

    @Test
    @DisplayName("Test battle with sword")
    public void testBattleSword() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        Mercenary merc = new Mercenary(initialPos, 15, 10, 0, 0,0,0, "mercenary");

        // add sword to player list
        int swordDamage = 10;
        Sword sword = new Sword(initialPos, swordDamage, 5, "sword");
        player.addWeapon((sword));

        // call battle
        Battle b = merc.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 4 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(4, numRounds);

        // assert 1 weapon used each round and player and enemy health decreases by
        // correct amount each round
        for (Round r : b.getRoundsList()) {
            assertEquals(r.getItemResponseWeaponsUsed().size(), 1);
            assertEquals(r.getDeltaPlayerHealth(), -(merc.getAttackDamage() / 10));
            assertEquals(r.getDeltaEnemyHealth(), -((player.getAttackDamage() + swordDamage) / 5));
            enemyHealth += r.getDeltaEnemyHealth();
            playerHealth += r.getDeltaPlayerHealth();
        }
        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, merc.getHealth());
    }

    @Test
    @DisplayName("Test battle with sword durability maxed during battle")
    public void testBattleSwordMaxDurability() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        Mercenary merc = new Mercenary(initialPos, 15, 10, 0, 0,0,0, "mercenary");

        // add sword to player list
        int swordDamage = 10;
        Sword sword = new Sword(initialPos, swordDamage, 2, "sword");
        dungeon.addItem((ItemEntity) sword);
        player.addItem((ItemEntity) sword);
        player.addWeapon((sword));

        // call battle
        Battle b = merc.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 4 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(6, numRounds);

        // assert sword used for first 2 rounds
        List<Round> roundsList = b.getRoundsList();
        for (int i = 0; i < 2; i++) {
            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 1);
            assertEquals(roundsList.get(i).getDeltaPlayerHealth(), -(merc.getAttackDamage() / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((player.getAttackDamage() + swordDamage) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }
        // sword should have reached max durability, next rounds of battle without sword
        for (int i = 2; i < 6; i++) {
            // no weapons used
            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 0);
            assertEquals(roundsList.get(i).getDeltaPlayerHealth(), -(merc.getAttackDamage() / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((player.getAttackDamage()) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }

        // check sword removed from all inventories 
        assertEquals(0, player.getInventory().size());
        assertEquals(0, player.getWeapons().size());

        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, merc.getHealth());

    }

    @Test
    @DisplayName("Test battle with bow")
    public void testBattleBow() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        Mercenary merc = new Mercenary(initialPos, 7, 10, 0, 0,0,0, "mercenary");

        // add bow to player list
        int bowDamage = 2;
        Bow bow = new Bow(initialPos, bowDamage, 5, "bow");
        player.addWeapon((bow));

        // call battle
        Battle b = merc.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 2 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(4, (bowDamage * player.getAttackDamage()) / 5);
        assertEquals(2, numRounds);

        // assert 1 weapon used each round and player and enemy health decreases by
        // correct amount each round
        for (Round r : b.getRoundsList()) {
            assertEquals(r.getItemResponseWeaponsUsed().size(), 1);
            assertEquals(r.getDeltaPlayerHealth(), -(merc.getAttackDamage() / 10));
            assertEquals(r.getDeltaEnemyHealth(), -((bowDamage * player.getAttackDamage()) / 5));
            enemyHealth += r.getDeltaEnemyHealth();
            playerHealth += r.getDeltaPlayerHealth();
        }
        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, merc.getHealth());
    }

    @Test
    @DisplayName("Test battle with Shield")
    public void testBattleShield() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        Mercenary merc = new Mercenary(initialPos, 5, 10, 0, 0,0,0, "mercenary");

        // add sword to player list
        int shieldDefence = 2;
        Shield shield = new Shield(initialPos, shieldDefence, 5, "shield");
        player.addWeapon((shield));

        // call battle
        Battle b = merc.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 3 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(3, numRounds);

        // assert 1 weapon used each round and player and enemy health decreases by
        // correct amount each round
        for (Round r : b.getRoundsList()) {
            assertEquals(r.getItemResponseWeaponsUsed().size(), 1);
            assertEquals(r.getDeltaPlayerHealth(), -((merc.getAttackDamage() - shieldDefence) / 10));
            assertEquals(r.getDeltaEnemyHealth(), -(player.getAttackDamage()) / 5);
            enemyHealth += r.getDeltaEnemyHealth();
            playerHealth += r.getDeltaPlayerHealth();
        }
        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, merc.getHealth());
    }

    // test two bows
    @Test
    @DisplayName("Test battle with multiple bows")
    public void testBattleMultipleBows() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        Mercenary merc = new Mercenary(initialPos, 24, 10, 0, 0,0,0, "mercenary");

        // add bows to player list
        int bowDamage = 2;
        Bow bow = new Bow(initialPos, bowDamage, 5, "bow");
        Bow bow2 = new Bow(initialPos, bowDamage, 2, "bow");
        player.addWeapon((bow));
        player.addWeapon((bow2));

        // call battle
        Battle b = merc.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 2 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(4, numRounds);

        // assert 2 bows used for first 2 rounds
        List<Round> roundsList = b.getRoundsList();
        for (int i = 0; i < 2; i++) {
            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 2);
            assertEquals(roundsList.get(i).getDeltaPlayerHealth(), -(merc.getAttackDamage() / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((4 * player.getAttackDamage()) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }
        // 1 bow reached max durability, next round of battle with 1 bow
        for (int i = 2; i < 4; i++) {
            // no weapons used
            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 1);
            assertEquals(roundsList.get(i).getDeltaPlayerHealth(), -(merc.getAttackDamage() / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((2 * player.getAttackDamage()) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }

        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, merc.getHealth());
    }

    @Test
    @DisplayName("Test battle with multiple shields")
    public void testBattleMultipleShields() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        Mercenary merc = new Mercenary(initialPos, 6, 10, 0, 0,0,0, "mercenary");

        // add bows to player list
        int shieldDefence1 = 2;
        int shieldDefence2 = 5;
        Shield shield1 = new Shield(initialPos, shieldDefence1, 5, "shield");
        Shield shield2 = new Shield(initialPos, shieldDefence2, 1, "shield");
        player.addWeapon((shield1));
        player.addWeapon((shield2));

        // call battle
        Battle b = merc.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 2 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(3, numRounds);

        // assert 2 bows used for first round
        List<Round> roundsList = b.getRoundsList();
        for (int i = 0; i < 1; i++) {
            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 2);
            assertEquals(roundsList.get(i).getDeltaPlayerHealth(),
                    -((merc.getAttackDamage() - (shieldDefence1 + shieldDefence2)) / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((player.getAttackDamage()) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }
        // 1 shield reached max durability, next round of battle with 1 shield
        for (int i = 1; i < 3; i++) {
            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 1);
            assertEquals(roundsList.get(i).getDeltaPlayerHealth(), -((merc.getAttackDamage() - shieldDefence1) / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((player.getAttackDamage()) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }

        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, merc.getHealth());
    }

    @Test
    @DisplayName("Test battle with shield, bow, sword")
    public void testBattleShieldBowSword() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        Mercenary merc = new Mercenary(initialPos, 10, 10, 0, 0,0,0, "mercenary");

        // add sword, shield, bow to player list
        int shieldDefence = 2;
        int swordDamage = 3;
        int bowDamage = 2;
        Shield shield = new Shield(initialPos, shieldDefence, 3, "shield");
        Sword sword = new Sword(initialPos, swordDamage, 2, "sword");
        Bow bow = new Bow(initialPos, bowDamage, 1, "bow");
        player.addWeapon((shield));
        player.addWeapon(bow);
        player.addWeapon(sword);

        // call battle
        Battle b = merc.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 4 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(4, numRounds);
        // first round, all 3 weapons
        List<Round> roundsList = b.getRoundsList();
        for (int i = 0; i < 1; i++) {
            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 3);
            // ensure weapons used are all 3
            boolean hasBow = false;
            boolean hasSword = false;
            boolean hasShield = false;
            for (ItemResponse item : roundsList.get(i).getItemResponseWeaponsUsed()) {
                if (item.getType().equals("bow")) {
                    hasBow = true;
                }
                if (item.getType().equals("shield")) {
                    hasShield = true;
                }
                if (item.getType().equals("sword")) {
                    hasSword = true;
                }
            }

            assertTrue(hasBow && hasSword && hasShield);

            assertEquals(roundsList.get(i).getDeltaPlayerHealth(), -((merc.getAttackDamage() - shieldDefence) / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(),
                    -((2 * (player.getAttackDamage() + swordDamage)) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }

        // second round, just shield and sword
        for (int i = 1; i < 2; i++) {
            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 2);
            // ensure weapons used are just shield and sword
            boolean hasBow = false;
            boolean hasSword = false;
            boolean hasShield = false;
            for (ItemResponse item : roundsList.get(i).getItemResponseWeaponsUsed()) {
                if (item.getType().equals("bow")) {
                    hasBow = true;
                }
                if (item.getType().equals("shield")) {
                    hasShield = true;
                }
                if (item.getType().equals("sword")) {
                    hasSword = true;
                }
            }
            assertFalse(hasBow);
            assertTrue(hasSword && hasShield);

            assertEquals(roundsList.get(i).getDeltaPlayerHealth(), -((merc.getAttackDamage() - shieldDefence) / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((player.getAttackDamage() + swordDamage) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }

        // third round just shield
        for (int i = 2; i < 3; i++) {

            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 1);
            // ensure weapons used are just shield and sword
            boolean hasBow = false;
            boolean hasSword = false;
            boolean hasShield = false;
            for (ItemResponse item : roundsList.get(i).getItemResponseWeaponsUsed()) {
                if (item.getType().equals("bow")) {
                    hasBow = true;
                }
                if (item.getType().equals("shield")) {
                    hasShield = true;
                }
                if (item.getType().equals("sword")) {
                    hasSword = true;
                }
            }
            assertFalse(hasBow && hasSword);
            assertTrue(hasShield);

            assertEquals(roundsList.get(i).getDeltaPlayerHealth(), -((merc.getAttackDamage() - shieldDefence) / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((player.getAttackDamage()) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }

        // last round, no weapons
        for (int i = 3; i < 4; i++) {

            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 0);

            assertEquals(roundsList.get(i).getDeltaPlayerHealth(), -(merc.getAttackDamage() / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((player.getAttackDamage()) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }
        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, merc.getHealth());
    }

    /* INTEGRATION TESTS */
    private void assertBasicBattleCalculations(String enemyType, BattleResponse battle, boolean enemyDies,
            String configFilePath) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), -(playerAttack / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    private void assertSwordBattleCalculations(String enemyType, int swordDamage, BattleResponse battle,
            boolean enemyDies,
            String configFilePath) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((playerAttack + swordDamage) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    @Test
    @DisplayName("Test player battle spider and spider dies")
    public void testPlayerInitiateSpider() {
        // create game
        DungeonManiaController controller = new DungeonManiaController();
        controller.newGame("d_battleTest_basicSpider", "c_battleTests_basic");

        // move player on top of spider to initiate battle
        DungeonResponse postBattleResponse = controller.tick(Direction.RIGHT);
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBasicBattleCalculations("spider", battle, true, "c_battleTests_basic");

        //  check enemy removed from map
        assertEquals(0,countEntityOfType(postBattleResponse, "spider"));
        assertEquals(1,countEntityOfType(postBattleResponse, "player"));
   
    }

    @Test
    @DisplayName("Test player battle zombie and player dies")
    public void testPlayerInitiateZombie() {
        // create game
        DungeonManiaController controller = new DungeonManiaController();
        controller.newGame("d_battleTest_basicZombie", "c_battleTests_basic");

        // move player on top of zombie to initiate battle
        DungeonResponse postBattleResponse = controller.tick(Direction.RIGHT);

        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBasicBattleCalculations("zombie", battle, false, "c_battleTests_basic");

        //  check player dies and removed from map
        assertEquals(1,countEntityOfType(postBattleResponse, "zombie_toast"));
        assertEquals(0,countEntityOfType(postBattleResponse, "player"));
    }

    @Test
    @DisplayName("Test player battle zombie with sword")
    public void testBattleZombieSword() {
        // create game
        DungeonManiaController controller = new DungeonManiaController();
        controller.newGame("d_battleTest_weapon", "c_battleTests_weapon");

        // make player collect sword
        DungeonResponse collectItemResp = controller.tick(Direction.DOWN);
        assertEquals(1, collectItemResp.getInventory().size());

        // make player move right to push boulder
        DungeonResponse zombieBattlePlayer = controller.tick(Direction.RIGHT);
        DungeonResponse playerBattleZombie = controller.tick(Direction.DOWN);
        // check which tick battle occured
        DungeonResponse postBattleResponse;
        if (zombieBattlePlayer.getBattles().size() > 0) {
            postBattleResponse = zombieBattlePlayer;
        } else {
            postBattleResponse = playerBattleZombie;
        }
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertSwordBattleCalculations("zombie", 2, battle, true, "c_battleTests_weapon");

    }

    @Test
    @DisplayName("Test player battle zombie with bow")
    public void testBattleZombieBow() {
        // create game
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_battleTest_bow", "c_battleTests_bow");

        // collect one wood
        res = controller.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "wood").size());

        // collect three arrows
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "arrow").size());

        res = controller.tick(Direction.RIGHT);
        assertEquals(2, getInventory(res, "arrow").size());

        res = controller.tick(Direction.UP);
        assertEquals(3, getInventory(res, "arrow").size());

        assertDoesNotThrow(() -> controller.build("bow"));
        res = controller.getDungeonResponseModel();
        // Once the bow is built, the items should be removed
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(0, getInventory(res, "wood").size());

        // make player move right to push boulder
        DungeonResponse zombieBattlePlayer = controller.tick(Direction.RIGHT);
        DungeonResponse playerBattleZombie = controller.tick(Direction.UP);
        // check which tick battle occured
        DungeonResponse postBattleResponse;
        if (zombieBattlePlayer.getBattles().size() > 0) {
            postBattleResponse = zombieBattlePlayer;
        } else {
            postBattleResponse = playerBattleZombie;
        }

        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBowBattleCalculations("zombie", battle, true, "c_battleTests_bow");

    }

    private void assertBowBattleCalculations(String enemyType, BattleResponse battle,
            boolean enemyDies,
            String configFilePath) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((2 * playerAttack) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    @Test
    @DisplayName("Test player battle zombie with shield")
    public void testBattleZombieShield() {
        // create game
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_battleTest_bow", "c_battleTests_shield");

        // collect two woods
        res = controller.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "wood").size());

        res = controller.tick(Direction.DOWN);
        assertEquals(2, getInventory(res, "wood").size());

        // collect one treasure
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());

        assertDoesNotThrow(() -> controller.build("shield"));

        // make player move to enemy and push boulder
        controller.tick(Direction.RIGHT);
        controller.tick(Direction.RIGHT);
        controller.tick(Direction.UP);
        DungeonResponse zombieBattlePlayer = controller.tick(Direction.RIGHT);
        DungeonResponse playerBattleZombie = controller.tick(Direction.UP);
        // check which tick battle occured
        DungeonResponse postBattleResponse;
        if (zombieBattlePlayer.getBattles().size() > 0) {
            postBattleResponse = zombieBattlePlayer;
        } else {
            postBattleResponse = playerBattleZombie;
        }

        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertShieldBattleCalculations("zombie", 2, battle, true, "c_battleTests_shield");

    }

    private void assertShieldBattleCalculations(String enemyType, int shieldDefence, BattleResponse battle,
            boolean enemyDies,
            String configFilePath) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -((enemyAttack - shieldDefence) / 10));
            assertEquals(round.getDeltaEnemyHealth(), -(playerAttack / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    @Test
    @DisplayName("Test player battle with bow, shield, sword")
    public void testBattleBowShieldSword() {
        // create game
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_battleTest_allWeapons", "c_battleTests_shield");
        // collect sword
        res = controller.tick(Direction.LEFT);
        assertEquals(1, getInventory(res, "sword").size());
        // collect 3 woods
        res = controller.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "wood").size());

        res = controller.tick(Direction.DOWN);
        assertEquals(2, getInventory(res, "wood").size());

        res = controller.tick(Direction.DOWN);
        assertEquals(3, getInventory(res, "wood").size());
        // collect one treasure
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());

        // collec three arrows
        res = controller.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "arrow").size());
        res = controller.tick(Direction.RIGHT);
        assertEquals(2, getInventory(res, "arrow").size());
        res = controller.tick(Direction.UP);
        assertEquals(3, getInventory(res, "arrow").size());

        assertDoesNotThrow(() -> controller.build("shield"));
        assertDoesNotThrow(() -> controller.build("bow"));

        // make player move to enemy and push boulder
        controller.tick(Direction.RIGHT);
        controller.tick(Direction.UP);
        DungeonResponse zombieBattlePlayer = controller.tick(Direction.RIGHT);
        DungeonResponse playerBattleZombie = controller.tick(Direction.UP);
        // check which tick battle occured
        DungeonResponse postBattleResponse;
        if (zombieBattlePlayer.getBattles().size() > 0) {
            postBattleResponse = zombieBattlePlayer;
        } else {
            postBattleResponse = playerBattleZombie;
        }

        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertAllWeaponsBattleCalculations("zombie", 2, 2, battle, true, "c_battleTests_shield");

    }

    private void assertAllWeaponsBattleCalculations(String enemyType, int shieldDefence, int swordDamage,
            BattleResponse battle,
            boolean enemyDies,
            String configFilePath) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -((enemyAttack - shieldDefence) / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((2 * (playerAttack + swordDamage)) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    @Test
    @DisplayName("Test player battle with bow, shield, sword, with varying durability")
    public void testBattleBowShieldSwordMaxDurability() {
        // create game
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_battleTest_allWeapons", "c_battleTest_allWeaponsVary");
        // collect sword
        res = controller.tick(Direction.LEFT);
        assertEquals(1, getInventory(res, "sword").size());
        // collect 3 woods
        res = controller.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "wood").size());

        res = controller.tick(Direction.DOWN);
        assertEquals(2, getInventory(res, "wood").size());

        res = controller.tick(Direction.DOWN);
        assertEquals(3, getInventory(res, "wood").size());
        // collect one treasure
        res = controller.tick(Direction.RIGHT);
        res = controller.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());

        // collec three arrows
        res = controller.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "arrow").size());
        res = controller.tick(Direction.RIGHT);
        assertEquals(2, getInventory(res, "arrow").size());
        res = controller.tick(Direction.UP);
        assertEquals(3, getInventory(res, "arrow").size());

        assertDoesNotThrow(() -> controller.build("shield"));
        assertDoesNotThrow(() -> controller.build("bow"));

        // make player move to enemy and push boulder
        controller.tick(Direction.RIGHT);
        controller.tick(Direction.UP);
        DungeonResponse zombieBattlePlayer = controller.tick(Direction.RIGHT);
        DungeonResponse playerBattleZombie = controller.tick(Direction.UP);
        // check which tick battle occured
        DungeonResponse postBattleResponse;
        if (zombieBattlePlayer.getBattles().size() > 0) {
            postBattleResponse = zombieBattlePlayer;
        } else {
            postBattleResponse = playerBattleZombie;
        }

        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertAllWeaponsVaryBattleCalculations("zombie", 2, 3, battle, true, "c_battleTest_allWeaponsVary");

    }

    private void assertAllWeaponsVaryBattleCalculations(String enemyType, int shieldDefence, int swordDamage,
            BattleResponse battle,
            boolean enemyDies,
            String configFilePath) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));

       // assert played 4 rounds
       int numRounds = rounds.size();
       assertEquals(4, numRounds);
       // first round, all 3 weapons
       List<RoundResponse> roundsList = rounds;
       for (int i = 0; i < 1; i++) {
           assertEquals(roundsList.get(i).getWeaponryUsed().size(), 3);
           // ensure weapons used are all 3
           boolean hasBow = false;
           boolean hasSword = false;
           boolean hasShield = false;
           for (ItemResponse item : roundsList.get(i).getWeaponryUsed()) {
               if (item.getType().equals("bow")) {
                   hasBow = true;
               }
               if (item.getType().equals("shield")) {
                   hasShield = true;
               }
               if (item.getType().equals("sword")) {
                   hasSword = true;
               }
           }

           assertTrue(hasBow && hasSword && hasShield);

           assertEquals(roundsList.get(i).getDeltaCharacterHealth(), -((enemyAttack - shieldDefence) / 10));
           assertEquals(roundsList.get(i).getDeltaEnemyHealth(),
                   -((2 * (playerAttack + swordDamage)) / 5));
           enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
           playerHealth += roundsList.get(i).getDeltaCharacterHealth();
       }

       // second round, just shield and sword
       for (int i = 1; i < 2; i++) {
           assertEquals(roundsList.get(i).getWeaponryUsed().size(), 2);
           // ensure weapons used are just shield and sword
           boolean hasBow = false;
           boolean hasSword = false;
           boolean hasShield = false;
           for (ItemResponse item : roundsList.get(i).getWeaponryUsed()) {
               if (item.getType().equals("bow")) {
                   hasBow = true;
               }
               if (item.getType().equals("shield")) {
                   hasShield = true;
               }
               if (item.getType().equals("sword")) {
                   hasSword = true;
               }
           }
           assertFalse(hasBow);
           assertTrue(hasSword && hasShield);

           assertEquals(roundsList.get(i).getDeltaCharacterHealth(), -((enemyAttack - shieldDefence) / 10));
           assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((playerAttack + swordDamage) / 5));
           enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
           playerHealth += roundsList.get(i).getDeltaCharacterHealth();
       }

       // third round just shield
       for (int i = 2; i < 3; i++) {

           assertEquals(roundsList.get(i).getWeaponryUsed().size(), 1);
           // ensure weapons used are just shield and sword
           boolean hasBow = false;
           boolean hasSword = false;
           boolean hasShield = false;
           for (ItemResponse item : roundsList.get(i).getWeaponryUsed()) {
               if (item.getType().equals("bow")) {
                   hasBow = true;
               }
               if (item.getType().equals("shield")) {
                   hasShield = true;
               }
               if (item.getType().equals("sword")) {
                   hasSword = true;
               }
           }
           assertFalse(hasBow && hasSword);
           assertTrue(hasShield);

           assertEquals(roundsList.get(i).getDeltaCharacterHealth(), -((enemyAttack - shieldDefence) / 10));
           assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((playerAttack) / 5));
           enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
           playerHealth += roundsList.get(i).getDeltaCharacterHealth();
       }

       // last round, no weapons
       for (int i = 3; i < 4; i++) {

           assertEquals(roundsList.get(i).getWeaponryUsed().size(), 0);

           assertEquals(roundsList.get(i).getDeltaCharacterHealth(), -(enemyAttack / 10));
           assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((playerAttack) / 5));
           enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
           playerHealth += roundsList.get(i).getDeltaCharacterHealth();
       }

    }

    @Test
    @DisplayName("Test player battle multiple enemies")
    public void testPlayerBattleMultiple() {
        // create game
        DungeonManiaController controller = new DungeonManiaController();
        controller.newGame("d_battleTest_multipleEnemies", "c_battleTests_basic");

        // move player on top of 2 spiders and 1 zombietoast to initiate battle
        DungeonResponse postBattleResponse = controller.tick(Direction.RIGHT);

        //  should battle with 3 enemies
        assertEquals(3,postBattleResponse.getBattles().size());

    }
}
