package dungeonmania;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.TimeTravellingPortal;
import dungeonmania.items.Arrow;
import dungeonmania.items.ItemEntity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
public class TimeTravellingPortalTest {
    @Test
    @DisplayName("Test Portal Exists")
    public void TestPortalExists() {
        Dungeon dungeon = new Dungeon();
        Entity portal= new TimeTravellingPortal(new Position(0,2), "time_travelling_portal");
        dungeon.addEntity(portal);

        assertEquals(1, dungeon.getEntities().size());
    }

    @Test
    @DisplayName("TestPortalTeleportsInitialGameState")
    public void TestPortalTeleports() {
        Dungeon dungeon = new Dungeon();
        Position startPos = new Position(0,1);
        Player player = new Player(startPos, 10, 10, "player");
        dungeon.addEntity(player);

        Entity portal= new TimeTravellingPortal(new Position(0,2), "time_travelling_portal");
        dungeon.addEntity(portal);

        ItemEntity arrow = new Arrow(new Position(2,1), "arrow");
        dungeon.addItem(arrow);
        player.move(dungeon, Direction.RIGHT); // (1,1)
        player.move(dungeon, Direction.RIGHT); // (2,1)
        assertEquals(0, dungeon.getItems().size());
        assertEquals(1, player.getInventory().size());
        player.move(dungeon, Direction.DOWN); // (2,2)
        player.move(dungeon, Direction.LEFT); // (1,2)
        player.move(dungeon, Direction.LEFT); // (0,2)

        // test player's position on time travelling portal
        assertEquals(portal.getPosition(), player.getPosition());
        assertEquals(true, player.isTimeTravellingPortal(dungeon));
    }

}