package dungeonmania.entities;

import dungeonmania.Dungeon;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Portal extends Entity {
    private String colour;

    // CONSTRUCTOR //
    public Portal(Position position, String type, String colour) {
        super(position, type);
        this.colour = colour;
    }

    // GETTERS //
    public String getColour() {
        return colour;
    }

    public Portal findPortal(Dungeon dungeon, Direction direction, Portal startingPortal) {
        for (Entity entity: dungeon.getEntities()) {
            if (entity instanceof Portal) {
                if (entity.equals(startingPortal)) {
                    continue;
                }
                // get the other portal with same colour
                if (((Portal) entity).getColour().equals(startingPortal.getColour())) {
                    Portal chainPortal = checkTeleportation(dungeon, direction, ((Portal) entity).getPosition());
                    if (chainPortal != null) {
                        return findPortal(dungeon, direction, chainPortal);
                    }
                    return (Portal) entity;
                }
            }
        }
        return null;
    }

    public Portal checkTeleportation(Dungeon dungeon, Direction direction, Position portalPos) {
        Position newPosition = portalPos.translateBy(direction);
        for (Entity entity: dungeon.getEntities()) {
            if (entity.getPosition().equals(newPosition)) {
                // check if the portal is surrounded by other portals
                if (isMovable(dungeon, direction, portalPos)) {
                    if (entity instanceof Portal) {
                        return (Portal) entity;
                    }
                }
            }
        }
        return null;
    }

    public boolean isMovable(Dungeon dungeon, Direction direction, Position portalPos) {
        Position newPosition = portalPos.translateBy(direction);
        for (Entity entity: dungeon.getEntities()) {
            if (entity.getPosition().equals(newPosition)) {
                // if the player teleports and end up on wall, locked door or boulder
                if (entity instanceof Wall || entity instanceof Boulder || 
                    (entity instanceof Door && ! ((Door) entity).isOpen())) {
                    return false;
                }
            }
        }
        return true;
    }

}
