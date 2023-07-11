package dungeonmania.weapon;

import dungeonmania.items.ItemEntity;
import dungeonmania.util.Position;

public class Sword extends ItemEntity implements Weapon {
    private double damage;
    private int durability;

    public Sword(Position position, double damage, int durability, String type) {
        super(position, type);
        this.damage = damage;
        this.durability = durability;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void decreaseDurability() {
        durability = durability - 1;
    }

}