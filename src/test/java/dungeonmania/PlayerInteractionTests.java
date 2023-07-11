package dungeonmania;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.entities.*;
import dungeonmania.items.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerInteractionTests {
    @Test
    @DisplayName("Test player can collect item")
    public void playerCanCollectItem() {
        Dungeon dungeon = new Dungeon();
        Position position = new Position(0,1);
        Player player = new Player(position, 10, 10, "player");

        ItemEntity treasure = new Treasure(new Position(1, 1), "treasure", 1);
        dungeon.addItem(treasure);

        player.move(dungeon, Direction.RIGHT);
        assertEquals(treasure, player.getInventory().get(0));
    }

    @Test
    @DisplayName("Test player can collect multiple treasures")
    public void playerCanCollectMultiTreasures() {
        Dungeon dungeon = new Dungeon();

        Position position = new Position(1,1);
        Player player = new Player(position, 10, 10, "player");

        // collect one treasure
        Direction movementDirection = Direction.LEFT;
        ItemEntity treasure = new Treasure(player.getPosition().translateBy(movementDirection), "treasure", 1);
        dungeon.addItem(treasure);
        
        // collect another treasure
        ItemEntity treasure2 = new Treasure(player.getPosition().translateBy(movementDirection), "treasure", 1);
        dungeon.addItem(treasure2);

        player.move(dungeon, Direction.LEFT);
        assertEquals(2, player.getInventory().size());
    }
    
    @Test
    @DisplayName("Test player can collect item on a switch")
    public void playerCanCollectItemOverSwitch() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(1,1);
        Player player = new Player(position, 10, 10, "player");
        
        // treasure and floorSwitch is on the same position
        Direction movementDirection = Direction.LEFT;
        ItemEntity treasure = new Treasure(position.translateBy(movementDirection), "treasure", 1);
        dungeon.addItem(treasure);

        Entity floorSwitch = new FloorSwitch(position.translateBy(movementDirection), "floor_switch");
        dungeon.addEntity(floorSwitch);

        player.move(dungeon, movementDirection);
        assertEquals(treasure, player.getInventory().get(0));
    }

    @Test
    @DisplayName("Test player can collect item on a switch reverse")
    public void playerCanCollectItemOverSwitchReverseOrder() {

        Dungeon dungeon = new Dungeon();
        Position position = new Position(1,1);
        Player player = new Player(position, 10, 10, "player");
        Direction movementDirection = Direction.LEFT;

        ItemEntity treasure = new Treasure(position.translateBy(movementDirection), "treasure", 1);
        dungeon.addItem(treasure);

        Entity floorSwitch = new FloorSwitch(position.translateBy(movementDirection), "floor_switch");
        dungeon.addEntity(floorSwitch);

        player.move(dungeon, movementDirection);
        assertEquals(treasure, player.getInventory().get(0));
    }

    // need weapons interface for this
    // @Test
    // @DisplayName("Test player can collect weapon")
    // public void playerCanCollectWeapon() {
    // }

    // @Test
    // @DisplayName("Test player can destroy spawner with weapon")
    // public void playerCanDestroySpawner() {
    // }

}


