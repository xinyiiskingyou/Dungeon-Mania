package dungeonmania.items;

import dungeonmania.util.Position;

public class Treasure extends ItemEntity{

    private int goal;

    public Treasure(Position position, String type, int goal) {
        super(position, type);
        this.goal = goal;
    }

    public int getGoal() {
        return goal;
    }

}