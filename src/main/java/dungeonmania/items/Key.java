package dungeonmania.items;

import dungeonmania.util.Position;

public class Key extends ItemEntity {

    private int keyId;

    public Key(Position position, String type, int keyId) {
        super(position, type);
        this.keyId = keyId;
    }

    public int getKeyId() {
        return keyId;
    }
       
}
