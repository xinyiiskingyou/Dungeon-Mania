package dungeonmania.weapon;

import dungeonmania.util.Position;

public class MidnightArmour extends Buildable{
    private int defence;

    public MidnightArmour(Position position, int damage, int defence, int durability, String type) {
        super(position, damage, durability, type);
        this.defence = defence;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    @Override
    public void decreaseDurability() {
        return;
    }
}
