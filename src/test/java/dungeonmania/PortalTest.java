package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dungeonmania.entities.Door;
import dungeonmania.entities.Player;
import dungeonmania.entities.Portal;
import dungeonmania.entities.Wall;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class PortalTest {

    @Test
    public void testPortalSimpleTeleports() {
        Dungeon dungeon = new Dungeon();

        Player player = new Player(new Position(0, 0), 1, 10, "player");
        dungeon.addEntity(player);

        Portal portal = new Portal(new Position(1, 0), "portal", "RED");    
        dungeon.addEntity(portal);

        Portal portal1 = new Portal(new Position(2, 2), "portal", "RED");    
        dungeon.addEntity(portal1);

        // player moves right to the portal
        // player should at the right of portal1
        player.move(dungeon, Direction.RIGHT);
        assertEquals(portal1.getPosition().translateBy(Direction.RIGHT), player.getPosition());

        // player moves left to the portal1
        // player should at the left of portal
        player.move(dungeon, Direction.LEFT);
        assertEquals(portal.getPosition().translateBy(Direction.LEFT), player.getPosition());
    }


    @Test
    public void testPortalCannotTeleportToWall() {
        Dungeon dungeon = new Dungeon();

        Player player = new Player(new Position(0, 0), 1, 10, "player");
        dungeon.addEntity(player);

        Portal portal = new Portal(new Position(1, 0), "portal", "RED");    
        dungeon.addEntity(portal);

        Portal portal1 = new Portal(new Position(2, 2), "portal", "RED");    
        dungeon.addEntity(portal1);

        Wall wall = new Wall(new Position(3, 2), "wall");
        dungeon.addEntity(wall);

        // player moves right to the portal
        // player cannot teleport as there is a wall 
        player.move(dungeon, Direction.RIGHT);
        assertEquals(new Position(0, 0), player.getPosition());

        // player can move up to the portal1
        player.move(dungeon, Direction.DOWN);
        player.move(dungeon, Direction.RIGHT);
        player.move(dungeon, Direction.UP);

        // player should at the top of portal
        assertEquals(portal1.getPosition().translateBy(Direction.UP), player.getPosition());
    }

    @Test
    public void testPortalCannotTeleportToLockedDoor() {
        Dungeon dungeon = new Dungeon();

        Player player = new Player(new Position(0, 0), 1, 10, "player");
        dungeon.addEntity(player);

        Portal portal = new Portal(new Position(1, 0), "portal", "RED");    
        dungeon.addEntity(portal);

        Portal portal1 = new Portal(new Position(2, 2), "portal", "RED");    
        dungeon.addEntity(portal1);

        Door door = new Door(new Position(3, 2), "door", 1);
        dungeon.addEntity(door);

        // player moves right to the portal
        // player cannot teleport as there is a door 
        player.move(dungeon, Direction.RIGHT);
        assertEquals(new Position(0, 0), player.getPosition());

        // player can move up to the portal1
        player.move(dungeon, Direction.DOWN);
        player.move(dungeon, Direction.RIGHT);
        player.move(dungeon, Direction.UP);

        // player should at the top of portal
        assertEquals(portal1.getPosition().translateBy(Direction.UP), player.getPosition());
    }
    
    @Test
    public void testPortalTeleportsDifferentColour() {

        Dungeon dungeon = new Dungeon();

        Player player = new Player(new Position(0, 0), 1, 10, "player");
        dungeon.addEntity(player);

        Portal redPortal = new Portal(new Position(1, 0), "portal", "RED");    
        dungeon.addEntity(redPortal);

        Portal redPortal2 = new Portal(new Position(2, 2), "portal", "RED");    
        dungeon.addEntity(redPortal2);

        Portal bluePortal = new Portal(new Position(4, 4), "portal", "BLUE");
        dungeon.addEntity(bluePortal);

        Portal bluePortal1 = new Portal(new Position(3, 5), "portal", "BLUE");
        dungeon.addEntity(bluePortal1);

        // player moves right -> teleports to the right of redPortal2
        player.move(dungeon, Direction.RIGHT);
        assertEquals(redPortal2.getPosition().translateBy(Direction.RIGHT), player.getPosition());

        // player moves next to the bluePortal -> [4, 3]
        player.move(dungeon, Direction.RIGHT);
        player.move(dungeon, Direction.DOWN);
        player.move(dungeon, Direction.DOWN);

        // player teleports to the other portal
        assertEquals(bluePortal1.getPosition().translateBy(Direction.DOWN), player.getPosition());

    }
}
