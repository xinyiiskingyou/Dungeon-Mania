package dungeonmania;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.battles.Battle;
import dungeonmania.battles.Round;
import dungeonmania.enemy.Hydra;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class HydraBattleTest {
    @Test
    @DisplayName("Test battle with hydra 100% health rate")
    public void testHydra100() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 10, "player");
        double healthIncreaseAmount = 10;
        Hydra hydra = new Hydra(initialPos, 100, 20, 1, healthIncreaseAmount, "hydra");
        dungeon.addEnemy(hydra);

        // call battle
        Battle b = hydra.battle(player, dungeon);

        // assert no weapons used and player and enemy health decreases by correct
        // amount each round
        for (Round r : b.getRoundsList()) {
            assertEquals(r.getDeltaPlayerHealth(), -(hydra.getAttackDamage() / 10));
            assertEquals(r.getDeltaEnemyHealth(), healthIncreaseAmount);
        }

        assertEquals(150, hydra.getHealth());
        assertEquals(0, player.getHealth());
    }

    @Test
    @DisplayName("Test battle with hydra 0% health rate")
    public void testHydra0() {

        // Create a player and enemy on same square
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Player player = new Player(initialPos, 10, 100, "player");
        double healthIncreaseAmount = 10;
        Hydra hydra = new Hydra(initialPos, 20, 20, 0, healthIncreaseAmount, "hydra");
        dungeon.addEnemy(hydra);

        // call battle
        Battle b = hydra.battle(player, dungeon);

        // assert no weapons used and player and enemy health decreases by correct
        // amount each round
        for (Round r : b.getRoundsList()) {
            assertEquals(r.getDeltaPlayerHealth(), -(hydra.getAttackDamage() / 10));
            assertEquals(r.getDeltaEnemyHealth(), -(player.getAttackDamage() / 5));
        }

        assertEquals(0, hydra.getHealth());
        assertEquals(80, player.getHealth());
    }


}
