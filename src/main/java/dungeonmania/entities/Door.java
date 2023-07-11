package dungeonmania.entities;

import dungeonmania.util.Position;

public class Door extends Entity {
    private boolean isOpen;
    private int keyId;

    public Door(Position position, String type, int keyId) {
        super(position, type);
        this.isOpen = false;
        this.keyId = keyId;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public int getKeyId() {
        return keyId;
    }

}
