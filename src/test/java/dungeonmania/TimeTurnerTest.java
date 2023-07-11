package dungeonmania;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.entities.Boulder;
import dungeonmania.entities.Door;
import dungeonmania.entities.Entity;
import dungeonmania.entities.FloorSwitch;
import dungeonmania.entities.Player;
import dungeonmania.items.Bomb;
import dungeonmania.items.ItemEntity;
import dungeonmania.items.Key;
import dungeonmania.items.TimeTurner;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
public class TimeTurnerTest {
    @Test
    @DisplayName("TestCollectTimeTurner")
    public void TestCollectTimeTurner() {
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(0,1), 10, 10, "player");
        ItemEntity timeTurner= new TimeTurner(new Position(0,2), "time_turner");
        dungeon.addItem(timeTurner);
        // collect time turner
        assertEquals(0, player.getInventory().size());
        player.move(dungeon, Direction.DOWN);
        assertEquals(1, player.getInventory().size());
    }

    @Test
    @DisplayName("TestDoorState")
    public void TestDoorState() {
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(3,1), 10, 10, "player");
        ItemEntity timeTurner= new TimeTurner(new Position(4,1), "time_turner");
        Door door = new Door(new Position(6, 1), "door", 1);
        String id1 = door.getId();
        Key key = new Key(new Position(5, 1), "key", 1);
        int tickOccurred = 0;
        dungeon.addItem(timeTurner);
        dungeon.addItem(key);
        dungeon.addEntity(door);
        dungeon.addEntity(player);
        // collect time turner 
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;
        assertEquals(1, player.getInventory().size());
        // collect key 
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;
        assertEquals(2, player.getInventory().size());
        // open door
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;
        assertEquals(true, door.isOpen());
        // key has been used
        assertEquals(1, player.getInventory().size());
        dungeon.rewind(tickOccurred - 2);
        // player's inventory persists
        assertEquals(1, player.getInventory().size());

        // door should not be opened
        for (Entity entity: dungeon.getEntities()) {
            if (entity instanceof Door) {
                door = (Door)entity;
                
            }
        }
        assertEquals(false, door.isOpen());
        String id2 = door.getId();
        // id are the same even cloning an object
        assertEquals(id1, id2);
    }
    
    @Test
    @DisplayName("testActivateSWitch")
    public void testActivateSWitch() {
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(3,1), 10, 10, "player");
        ItemEntity timeTurner= new TimeTurner(new Position(4,1), "time_turner");
        Position boulderPos = new Position(5,1);
        Boulder boulder = new Boulder(boulderPos, "boulder");
        FloorSwitch floorSwitch = new FloorSwitch(new Position(6,1), "switch"); 
        int tickOccurred = 0;

        dungeon.addItem(timeTurner);
        dungeon.addEntity(floorSwitch);
        dungeon.addEntity(boulder);
        dungeon.addEntity(player);
        // collect time turner 
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;
        assertEquals(1, player.getInventory().size());
        assertEquals(boulderPos, boulder.getPosition());

        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;
        // expect boulder at (6,1)
        assertEquals(boulderPos.translateBy(Direction.RIGHT), boulder.getPosition());

        // expect switch is active
        assertEquals(true, floorSwitch.isOn());
        // tickOccurred - 1 = length of ticks
        dungeon.rewind(tickOccurred-2);

        // update the entity state in dungeon
        for (Entity entity: dungeon.getEntities()) {
            if (entity instanceof Boulder) {
                boulder = (Boulder)entity;
                System.out.println(boulder.getPosition());
            }
            if (entity instanceof FloorSwitch) {
                floorSwitch = (FloorSwitch)entity;
                System.out.println(floorSwitch.isOn());
            }
        }
        // expect switch not active
        assertEquals(false, floorSwitch.isOn());
        // expect boulder at its original position
        assertEquals(boulderPos, boulder.getPosition());
    }

    @Test
    @DisplayName("TestExplodeBomb")
    public void TestExplodeBomb() {
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(3,1), 10, 10, "player");
        ItemEntity timeTurner= new TimeTurner(new Position(4,1), "time_turner");
        Boulder boulder = new Boulder(new Position(5,1), "boulder");
        FloorSwitch floorSwitch = new FloorSwitch(new Position(6,1), "switch"); 
        Bomb bomb = new Bomb(new Position(6,2), "bomb", 1);
        int tickOccurred = 0;

        dungeon.addItem(bomb);
        dungeon.addItem(timeTurner);
        dungeon.addEntity(floorSwitch);
        dungeon.addEntity(boulder);
        dungeon.addEntity(player);
        // collect time turner 
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;

        // push boulder
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;

        player.move(dungeon, Direction.DOWN);
        dungeon.AddGameState();
        tickOccurred++;

        // pick up bomb
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;
        
        // equivalent to tick(itemUseId) in system test
        player.placeBomb(bomb, dungeon);
        dungeon.AddGameState();
        tickOccurred++;
        
        Boolean isBoulder = false;
        Boolean isSwitch = false;
        Boolean isBomb = false;
        for (Entity entity: dungeon.getEntities()) {
            if (entity instanceof Boulder) {
                isBoulder = true;
            }
            if (entity instanceof FloorSwitch) {
                isSwitch = true;
            }
        }
        for (ItemEntity item: player.getInventory()) {
            if (item instanceof Bomb) {
                isBomb = true;
            }
        }

        // expect they are all destroyed
        assertEquals(false, isBoulder);
        assertEquals(false, isSwitch);
        assertEquals(false, isBomb);
        dungeon.rewind(tickOccurred-2);


        // update the entity state in dungeon
        for (Entity entity: dungeon.getEntities()) {
            if (entity instanceof Boulder) {
                boulder = (Boulder)entity;
                isBoulder = true;
                
            }
            if (entity instanceof FloorSwitch) {
                floorSwitch = (FloorSwitch)entity;
                isSwitch = true;
                
            }
        }
        for (ItemEntity item: player.getInventory()) {
            if (item instanceof Bomb) {
                isBomb = true;
            }
        }
        assertEquals(true, isBoulder);
        assertEquals(true, isSwitch);
        // player's inventory persists
        assertEquals(false, isBomb);
        // expect boulder on top of switch
        assertEquals(new Position(6,1), boulder.getPosition());
        // expect switch active
        assertEquals(true, floorSwitch.isOn());
    }

    @Test
    @DisplayName("TestRewindAfterExplodeBomb")
    public void TestRewindAfterExplodeBomb() {
        Dungeon dungeon = new Dungeon();
        Player player = new Player(new Position(3,1), 10, 10, "player");
        ItemEntity timeTurner= new TimeTurner(new Position(4,1), "time_turner");
        Boulder boulder = new Boulder(new Position(5,1), "boulder");
        FloorSwitch floorSwitch = new FloorSwitch(new Position(6,1), "switch"); 
        Bomb bomb = new Bomb(new Position(6,2), "bomb", 1);
        int tickOccurred = 0;

        dungeon.addItem(bomb);
        dungeon.addItem(timeTurner);
        dungeon.addEntity(floorSwitch);
        dungeon.addEntity(boulder);
        dungeon.addEntity(player);
        // collect time turner 
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;

        // push boulder
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;

        player.move(dungeon, Direction.DOWN);
        dungeon.AddGameState();
        tickOccurred++;

        // pick up bomb
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;
        
        // equivalent to tick(itemUseId) in system test
        player.placeBomb(bomb, dungeon);
        dungeon.AddGameState();
        tickOccurred++;

        // random move
        player.move(dungeon, Direction.RIGHT);
        dungeon.AddGameState();
        tickOccurred++;

        dungeon.rewind(tickOccurred-2);

        Boolean isBoulder = false;
        Boolean isSwitch = false;
        Boolean isBomb = false;
        // update the entity state in dungeon
        for (Entity entity: dungeon.getEntities()) {
            if (entity instanceof Boulder) {
                boulder = (Boulder)entity;
                isBoulder = true;
                
            }
            if (entity instanceof FloorSwitch) {
                floorSwitch = (FloorSwitch)entity;
                isSwitch = true;
                
            }
        }
        for (ItemEntity item: player.getInventory()) {
            if (item instanceof Bomb) {
                isBomb = true;
            }
        }
        // epxect time travel after tick takes place
        assertEquals(false, isBoulder);
        assertEquals(false, isSwitch);
        // player's inventory persists
        assertEquals(false, isBomb);
    }

}
