package dungeonmania.goals;

import dungeonmania.Dungeon;

public class BoulderGoal implements GoalsInterface{

    private Dungeon dungeon;
    private final String type = ":boulders";

    public BoulderGoal(Dungeon dungeon) {
        this.dungeon = dungeon;
    }
    
    @Override
    public boolean isFinished(Dungeon dungeon) {
        // all the switches must be triggered
        if ((dungeon.getSwitchCount() == dungeon.getSwitchList().size())) {
            return true;
        }
        return false;
    }

    @Override
    public String getType() {
        return type; 
    }
    
}
