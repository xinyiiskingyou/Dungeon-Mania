package dungeonmania.items;

import dungeonmania.Dungeon;
import dungeonmania.entities.Entity;
import dungeonmania.entities.FloorSwitch;
import dungeonmania.util.Position;
import java.util.ArrayList;
import java.util.List;

public class Bomb extends ItemEntity {

    private int radius;
    private Boolean picked=false;

    public Bomb(Position position, String type, int radius) {
        super(position, type);
        this.radius = radius;
    }
    
    public int getRadius() {
        return radius;
    }

    public Boolean isPicked() {
        return picked;
    }
    public void setPicked() {
        this.picked = true;
    }

    public void detonate(Dungeon dungeon) {

        List<Position> positions = this.getPositioninRadius();
        for (Position position: positions) {
            dungeon.explode(position);
        }
        return;
    }

    public Boolean hasActiveSwitch(Dungeon dungeon) {
        List<Position> positions = new ArrayList<>();
        positions.add(new Position(this.getPosition().getX() - 1, this.getPosition().getY()));
        positions.add(new Position(this.getPosition().getX() + 1, this.getPosition().getY()));
        positions.add(new Position(this.getPosition().getX(), this.getPosition().getY() - 1));
        positions.add(new Position(this.getPosition().getX(), this.getPosition().getY() + 1));
        for (Position position: positions) {
            Entity entity = dungeon.getEntity(position);
            if (entity == null) {
                continue;
            }
            if (entity.getType().equals("switch")) {
                if (((FloorSwitch)entity).isOn()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Position> getPositioninRadius() {
        List<Position> positions = new ArrayList<>();
        
        int upperBoundX = this.getPosition().getX() - this.getRadius();
        int lowerBoundX = this.getPosition().getX() + this.getRadius();
        int upperBoundY = this.getPosition().getY() - this.getRadius();
        int lowerBoundY = this.getPosition().getY() + this.getRadius();

        for (int i = upperBoundX; i <= lowerBoundX; i++) {
            for (int j = upperBoundY; j <= lowerBoundY; j++) {
                Position position = new Position(i, j);
                positions.add(position);
            }
        }
        return positions;
    }
}