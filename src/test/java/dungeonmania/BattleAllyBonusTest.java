package dungeonmania;

import static dungeonmania.TestUtils.getEntities;
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
import dungeonmania.enemy.ZombieToast;
import dungeonmania.entities.Player;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.weapon.Bow;
import dungeonmania.weapon.Shield;
import dungeonmania.weapon.Sword;

public class BattleAllyBonusTest {
    /* UNIT TESTS */

    @Test
    @DisplayName("Test battle with no weapons and one ally bonus")
    public void testNoWeaponsAllyBonus() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position playerInitialPos = new Position(1, 1);
        Position initialPos = new Position(2, 1);
        Player player = new Player(playerInitialPos, 10, 100, "player");
        ZombieToast zombie = new ZombieToast(initialPos, 9, 10, "zombie_toast");
        int allyAttack = 2;
        int allyDefence = 5;
        Mercenary merc = new Mercenary(initialPos, 10, 10, 0, 0, allyAttack, allyDefence, "mercenary");
        dungeon.addEnemy(zombie);
        dungeon.addEnemy(merc);

        // befriend merc
        merc.setHostile(false);

        // intitiate battle
        Battle b = zombie.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 5 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(4, numRounds);

        // assert no weapons used and player and enemy health decreases by correct
        // amount each round
        assertEquals(zombie.getAttackDamage() / 10, 1);
        for (Round r : b.getRoundsList()) {
            assertEquals(r.getItemResponseWeaponsUsed().size(), 0);
            assertEquals(r.getDeltaPlayerHealth(), -((zombie.getAttackDamage() - allyDefence) / 10));
            assertEquals(r.getDeltaEnemyHealth(), -((player.getAttackDamage() + allyAttack) / 5));
            enemyHealth += r.getDeltaEnemyHealth();
            playerHealth += r.getDeltaPlayerHealth();
        }
        assertTrue(enemyHealth <= 0);
        assertEquals(enemyHealth, zombie.getHealth());
        assertEquals(playerHealth, player.getHealth());
    }

    @Test
    @DisplayName("Test battle with no weapons and multiple ally bonus")
    public void testNoWeaponsMultipleAllyBonus() {

        // Create a player
        Dungeon dungeon = new Dungeon();
        Position playerInitialPos = new Position(1, 1);
        Position initialPos = new Position(2, 1);
        // create enemies
        Player player = new Player(playerInitialPos, 10, 100, "player");
        ZombieToast zombie = new ZombieToast(initialPos, 9, 10, "zombie_toast");
        int allyAttackMerc1 = 1;
        int allyDefenceMerc1 = 2;
        int allyAttackMerc2 = 3;
        int allyDefenceMerc2 = 4;
        int allyAttackMerc3 = 5;
        int allyDefenceMerc3 = 6;
        Mercenary merc1 = new Mercenary(initialPos, 10, 10, 0, 0, allyAttackMerc1, allyDefenceMerc1, "mercenary");
        Mercenary merc2 = new Mercenary(initialPos, 10, 10, 0, 0, allyAttackMerc2, allyDefenceMerc2, "mercenary");
        Mercenary merc3 = new Mercenary(initialPos, 10, 10, 0, 0, allyAttackMerc3, allyDefenceMerc3, "mercenary");
        dungeon.addEnemy(zombie);
        dungeon.addEnemy(merc1);
        dungeon.addEnemy(merc2);
        dungeon.addEnemy(merc3);

        // befriend mercs
        merc1.setHostile(false);
        merc2.setHostile(false);
        merc3.setHostile(false);

        // intitiate battle
        Battle b = zombie.battle(player, dungeon);

        double playerHealth = b.getInitialPlayerHealth();
        double enemyHealth = b.getInitialEnemyHealth();

        // assert played 3 rounds
        int numRounds = b.getRoundsList().size();
        assertEquals(3, numRounds);

        // assert no weapons used and player and enemy health decreases by correct
        // amount each round
        assertEquals(zombie.getAttackDamage() / 10, 1);
        for (Round r : b.getRoundsList()) {
            assertEquals(r.getItemResponseWeaponsUsed().size(), 0);
            assertEquals(r.getDeltaPlayerHealth(),
                    -((zombie.getAttackDamage() - allyDefenceMerc1 - allyDefenceMerc2 - allyDefenceMerc3) / 10));
            assertEquals(r.getDeltaEnemyHealth(),
                    -((player.getAttackDamage() + allyAttackMerc1 + allyAttackMerc2 + allyAttackMerc3) / 5));
            enemyHealth += r.getDeltaEnemyHealth();
            playerHealth += r.getDeltaPlayerHealth();
        }
        // zombie died
        assertTrue(enemyHealth <= 0);
        assertTrue(zombie.getHealth() <= 0);
        assertEquals(enemyHealth, zombie.getHealth());

        // player health unaffected
        assertTrue(playerHealth >= 0);
        assertTrue(player.getHealth() >= 0);
        assertEquals(playerHealth, player.getHealth());

    }

    @Test
    @DisplayName("Test battle with shield, bow, sword, ally bonus")
    public void testBattleShieldBowSwordAllyBonus() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        int allyAttack = 2;
        int allyDefence = 5;
        Mercenary merc = new Mercenary(initialPos, 12, 10, 0, 0, allyAttack, allyDefence, "mercenary");
        Mercenary merc1 = new Mercenary(initialPos, 10, 10, 0, 0, allyAttack, allyDefence, "mercenary");
        dungeon.addEnemy(merc);
        dungeon.addEnemy(merc1);

        // befriend merc1
        merc1.setHostile(false);

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

        // assert played 3 rounds
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

            assertEquals(roundsList.get(i).getDeltaPlayerHealth(),
                    -((merc.getAttackDamage() - shieldDefence - allyDefence) / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(),
                    -((2 * (player.getAttackDamage() + swordDamage + allyAttack)) / 5));
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

            assertEquals(roundsList.get(i).getDeltaPlayerHealth(),
                    -((merc.getAttackDamage() - shieldDefence - allyDefence) / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(),
                    -((player.getAttackDamage() + swordDamage + allyAttack) / 5));
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

            assertEquals(roundsList.get(i).getDeltaPlayerHealth(),
                    -((merc.getAttackDamage() - shieldDefence - allyDefence) / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((player.getAttackDamage() + allyAttack) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }

        // last round, no weapons
        for (int i = 3; i < 4; i++) {

            assertEquals(roundsList.get(i).getItemResponseWeaponsUsed().size(), 0);

            assertEquals(roundsList.get(i).getDeltaPlayerHealth(), -((merc.getAttackDamage() - allyDefence) / 10));
            assertEquals(roundsList.get(i).getDeltaEnemyHealth(), -((player.getAttackDamage() + allyAttack) / 5));
            enemyHealth += roundsList.get(i).getDeltaEnemyHealth();
            playerHealth += roundsList.get(i).getDeltaPlayerHealth();
        }

        assertEquals(enemyHealth, merc.getHealth());
        assertEquals(playerHealth, player.getHealth());
    }

    /* INTEGRATION TESTS */
    // bribe finished
    @Test
    @DisplayName("Test player bribe merc then battle")
    public void testBattleZombieShield() {
        // create game
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_battleTest_bribeMerc", "c_battleTest_bribeMerc");

        // two treasure
        res = controller.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "treasure").size());

        res = controller.tick(Direction.DOWN);
        assertEquals(2, getInventory(res, "treasure").size());

        // get id of merc
        List<EntityResponse> entities = getEntities(res, "mercenary");
        String mercId = entities.get(0).getId();

        // bribe the merc
        assertDoesNotThrow(() -> controller.interact(mercId));

        // make player bush boulder and battle zombie
        DungeonResponse zombieBattlePlayer = controller.tick(Direction.RIGHT);
       

        DungeonResponse postBattleResponse;
        if (zombieBattlePlayer.getBattles().size() > 0) {
            postBattleResponse = zombieBattlePlayer;
        } else {
            postBattleResponse = controller.tick(Direction.DOWN);
        }

        BattleResponse battle = postBattleResponse.getBattles().get(0);
        int allyAttack = 3;
        int allyDefence = 2;

        assertAllyBattleCalculations("zombie",allyAttack, allyDefence, battle, false, "c_battleTest_bribeMerc");

    }

    private void assertAllyBattleCalculations(String enemyType, int allyAttack, int allyDefence, BattleResponse battle, boolean enemyDies,
            String configFilePath) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));
             
        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -((enemyAttack - allyDefence) / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((playerAttack + allyAttack) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }
}
