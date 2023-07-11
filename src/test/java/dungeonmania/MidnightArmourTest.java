package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import static dungeonmania.TestUtils.getValueFromConfigFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.weapon.MidnightArmour;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.RoundResponse;

import static dungeonmania.TestUtils.getInventory;

import dungeonmania.battles.Battle;
import dungeonmania.battles.Round;
import dungeonmania.enemy.Mercenary;
import dungeonmania.entities.Player;
import dungeonmania.exceptions.InvalidActionException;
public class MidnightArmourTest {
    // UNIT TEST BATTLE
    @Test
    @DisplayName("Test battle unit test")
    public void testUnitBattleMidnightArmour() {
        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        Mercenary merc = new Mercenary(initialPos, 15, 10, 0, 0,0,0, "mercenary");
        
        int maAttack = 10;
        int maDefence = 4;
        MidnightArmour ma = new MidnightArmour(initialPos,maAttack,maDefence,99,"midnight_armour");
        player.addWeapon(ma);
        dungeon.addEnemy(merc);

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
            assertEquals(r.getDeltaPlayerHealth(), -((merc.getAttackDamage() - maDefence)/ 10));
            assertEquals(r.getDeltaEnemyHealth(), -((player.getAttackDamage() + maAttack) / 5));
            enemyHealth += r.getDeltaEnemyHealth();
            playerHealth += r.getDeltaPlayerHealth();
        }
        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, merc.getHealth());
     

    }

    @Test
    @DisplayName("Test build armour with sword and stone")
    public void testBuildSuccessfully() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourNoZombie", "c_newBuildable");
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        
        //  collect sword, sunstone
        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));

        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, res.getBuildables().size());
        res = dmc.getDungeonResponseModel();

        assertDoesNotThrow( () -> dmc.build("midnight_armour"));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "sword").size());

        //  sunstone doesnt get removed
        assertEquals(1, getInventory(res, "sun_stone").size());

        assertEquals(1, getInventory(res, "midnight_armour").size());
        assertEquals(0, res.getBuildables().size());

    }

    @Test
    @DisplayName("Test build invalid exception insufficient items")
    public void testInvalidExceptionInsufficientItems() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourNoZombie", "c_newBuildable");
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        
        //  collect only sun
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));

        // now collect sword, and build
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sword").size());
        assertDoesNotThrow( () -> dmc.build("midnight_armour"));

    }



    @Test
    @DisplayName("Test build invalid exception zombies exst")
    public void testInvalidExceptionZombie() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourZombie", "c_newBuildable");
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        //  collect sword, sunstone
        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));

    }

    
    
    @Test
    @DisplayName("Test provides extra attack damage and armour")
    public void testBattleMidnightArmour() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourBattle", "c_newBuildable");
        
        //  collect sword, sunstone
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertDoesNotThrow( () -> dmc.build("midnight_armour"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "midnight_armour").size());

        DungeonResponse postBattleResponse = dmc.tick(Direction.RIGHT);
        BattleResponse battle = postBattleResponse.getBattles().get(0);

        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", "c_newBuildable"));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile("mercenary_health", "c_newBuildable"));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", "c_newBuildable"));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile("mercenary_attack", "c_newBuildable"));

                
        int maAttack = 10;
        int maDefence = 4;

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -((enemyAttack - maDefence) / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((playerAttack + maAttack)/ 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        assertEquals("midnight_armour", rounds.get(0).getWeaponryUsed().get(0).getType());
        assertEquals("midnight_armour", rounds.get(1).getWeaponryUsed().get(0).getType());
        assertEquals("midnight_armour", rounds.get(2).getWeaponryUsed().get(0).getType());
    }

}
