package dungeonmania.items;

import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

public abstract class ItemEntity extends Entity {

    public ItemEntity(Position position, String type) {
        super(position, type);
    }


}
