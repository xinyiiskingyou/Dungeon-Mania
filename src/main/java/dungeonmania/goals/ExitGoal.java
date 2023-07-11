package dungeonmania.goals;

import dungeonmania.Dungeon;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Exit;

public class ExitGoal implements GoalsInterface{

    private Dungeon dungeon;
    private final String type = ":exit";
    
    public ExitGoal(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @Override
    public boolean isFinished(Dungeon dungeon) {
        for (Entity entity: dungeon.getEntities()) {
            if (entity instanceof Exit && (((Exit) entity).isOpen())) {    
                return true;
            }
        }
        return false;
    }

    @Override
    public String getType() {
        return type;
    }
    
}
