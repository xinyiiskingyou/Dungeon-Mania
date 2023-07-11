package dungeonmania.entities;

import dungeonmania.util.Position;
import java.io.Serializable;

public abstract class Entity implements Serializable, Cloneable {

    private Position position;
    private String id;
    private String type;
    private static int entityCounter = 0;
    private boolean isInteractable = false;
    private double health;
    
    public Entity(Position position, String type) {
        this.position = position;
        this.id = String.valueOf(entityCounter);
        this.type = type;
        entityCounter++;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isInteractable() {
        return isInteractable;
    }

    public double getHealth() {
        return health;
    }

    public void setInteractable(boolean isInteractable) {
        this.isInteractable = isInteractable;
    }
}