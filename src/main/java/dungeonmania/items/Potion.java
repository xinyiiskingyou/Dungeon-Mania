package dungeonmania.items;

import dungeonmania.util.Position;

public abstract class Potion extends ItemEntity {
    private int duration;
    public Potion(Position position, String type, int duration) {
        super(position, type);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void decreaseDuration() {
        this.duration -= 1;
    }
}
