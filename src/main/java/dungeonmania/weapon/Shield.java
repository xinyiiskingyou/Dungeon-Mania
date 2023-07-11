package dungeonmania.weapon;

import dungeonmania.util.Position;

public class Shield extends Buildable {

    private int defence;

    public Shield(Position position, int defence, int durability, String type) {
        super(position, durability, type);
        this.defence = defence;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

}