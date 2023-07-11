package dungeonmania.goals;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Dungeon;

public class OrGoal implements GoalsInterface {

    private Dungeon dungeon;
    private List<GoalsInterface> orGoalList = new ArrayList<>();

    public OrGoal(Dungeon dungeon, List<GoalsInterface> orGoalList) {
        this.dungeon = dungeon;
        this.orGoalList = orGoalList;
    }

    public void setGoals(GoalsInterface goal) {
        orGoalList.add(goal);
    }

    public List<GoalsInterface> getOrGoalList() {
        return orGoalList;
    }

    @Override
    public boolean isFinished(Dungeon dungeon) {
        for (GoalsInterface goals: orGoalList) {
            if (goals.isFinished(dungeon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getType() {
        String type = "";
        for (int i = 0; i < orGoalList.size(); i++) {
            if (! orGoalList.get(i).isFinished(dungeon)) {
                type += orGoalList.get(i).getType() + " OR ";
            }
        }
        if (type.length() >= 4) {
            type = type.substring(0, type.length() - 4);
        }
        return "(" + type.trim().replace("null", "") + ")";
    }
}
