package dungeonmania.goals;

import dungeonmania.Dungeon;

public class EnemyGoal implements GoalsInterface{

    private Dungeon dungeon;
    private final String type = ":enemies";
    
    public EnemyGoal(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @Override
    public boolean isFinished(Dungeon dungeon) {
        // enemy will be remove from the list
        if (dungeon.getEnemyGoals() <= dungeon.getEnemiesKill()) {
            return true;
        }
        return false;
    }

    @Override
    public String getType() {
        return type;
    }
    
}
