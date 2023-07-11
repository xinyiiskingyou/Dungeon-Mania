package dungeonmania.entities;

import dungeonmania.util.Position;

public class SwampTile extends Entity {
    private int movementFactor;

    public SwampTile(Position position, int movementFactor, String type) {
        super(position, type);
        this.movementFactor = movementFactor;
    }

    public int getMovementFactor() {
        return movementFactor;
    }

    public void setMovementFactor(int movementFactor) {
        this.movementFactor = movementFactor;
    }
}
