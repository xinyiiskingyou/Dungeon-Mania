package dungeonmania;

import dungeonmania.entities.*;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dungeonmania.enemy.Spider;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SpiderTests {
    @Test
    @DisplayName("Test movement sequence is correct assuming no collisions")
    public void testCircleMovement() {
        // Create a spider and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Spider spider = new Spider(initialPos, 10, 10, "spider");

        List<Position> positionSequence = Arrays.asList(new Position(4, 3), new Position(5, 3), new Position(5, 4),
                new Position(5, 5), new Position(4, 5), new Position(3, 5),
                new Position(3, 4), new Position(3, 3), new Position(4, 3));

        // Check circular movement for each tick
        for (Position expectedPosition : positionSequence) {
            spider.move(new ArrayList<Entity>(), dungeon);
            assertEquals(expectedPosition, spider.getPosition());
        }
    }

    @Test
    @DisplayName("Test movement sequence when boulder blocks path")
    public void testReverseMovement() {
        // Create a spider and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Spider spider = new Spider(initialPos, 10, 10, "spider");

        // Create boulder and add to list of entities
        Entity boulder = new Boulder(new Position(5, 5), "boulder");
        List<Entity> entityList = new ArrayList<>();
        entityList.add(boulder);

        List<Position> positionSequence = Arrays.asList(new Position(4, 3), new Position(5, 3), new Position(5, 4),
                new Position(5, 3), new Position(4, 3), new Position(3, 3),
                new Position(3, 4), new Position(3, 5), new Position(4, 5));

        // Check circular movement for each tick
        for (Position expectedPosition : positionSequence) {
            spider.move(entityList, dungeon);
            assertEquals(expectedPosition, spider.getPosition());
        }
    }

    @Test
    @DisplayName("Test movement through static entities")
    public void testMovementThroughEntities() {
        // Create a spider and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Spider spider = new Spider(initialPos, 10, 10, "spider");

        // Create entities to move through and add to list of entities
        Entity wall = new Wall(new Position(5, 3), "wall");
        Entity door = new Door(new Position(5, 4), "door", 10);
        Entity floorSwitch = new FloorSwitch(new Position(4, 5), "switch");
        Entity portal = new Portal(new Position(3, 5), "portal", "red");
        Entity exit = new Exit(new Position(4, 3), "exit");

        List<Entity> entityList = new ArrayList<>();
        entityList.add(wall);
        entityList.add(door);
        entityList.add(floorSwitch);
        entityList.add(portal);
        entityList.add(exit);

        List<Position> positionSequence = Arrays.asList(new Position(4, 3), new Position(5, 3), new Position(5, 4),
                new Position(5, 5), new Position(4, 5), new Position(3, 5),
                new Position(3, 4), new Position(3, 3), new Position(4, 3));

        // Check circular movement for each tick
        for (Position expectedPosition : positionSequence) {
            spider.move(entityList, dungeon);
            assertEquals(expectedPosition, spider.getPosition());
        }
    }

    @Test
    @DisplayName("Test movement sequence is correct for swamp tiles")
    public void testSwampTileMovement() {
        // Create a spider and set its starting position
        Dungeon dungeon = new Dungeon();
        Position initialPos = new Position(4, 4);
        Spider spider = new Spider(initialPos, 10, 10, "spider");

        SwampTile swampTile = new SwampTile(new Position(5, 3), 1, "swamp_tile");

        List<Entity> entities = new ArrayList<>();
        entities.add(spider);
        entities.add(swampTile);

        List<Position> positionSequence = Arrays.asList(new Position(4, 3), new Position(5, 3), new Position(5, 3), new Position(5, 4),
                new Position(5, 5), new Position(4, 5), new Position(3, 5),
                new Position(3, 4), new Position(3, 3), new Position(4, 3));

        // Check circular movement for each tick
        for (Position expectedPosition : positionSequence) {
            spider.move(entities, dungeon);
            assertEquals(expectedPosition, spider.getPosition());
        }
    }
}
