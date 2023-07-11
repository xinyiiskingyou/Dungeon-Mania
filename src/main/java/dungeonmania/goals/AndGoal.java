package dungeonmania.goals;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Dungeon;

public class AndGoal implements GoalsInterface{

    private Dungeon dungeon;
    private List<GoalsInterface> andGoalList = new ArrayList<>();

    public AndGoal(Dungeon dungeon, List<GoalsInterface> andGoalList) {
        this.dungeon = dungeon;
        this.andGoalList = andGoalList;
    }

    public void setGoals(GoalsInterface goal) {
        andGoalList.add(goal);
    }

    public List<GoalsInterface> getAndGoalList() {
        return andGoalList;
    }

    @Override
    public boolean isFinished(Dungeon dungeon) {
        for (GoalsInterface goals: andGoalList) {
            if (! goals.isFinished(dungeon)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getType() {
        String type = "";
        for (int i = 0; i < andGoalList.size(); i++) {
            if (! andGoalList.get(i).isFinished(dungeon)) {
                type += andGoalList.get(i).getType() + " AND ";
            } 
        }
        if (type.length() >= 4) {
            type = type.substring(0, type.length() - 4);
        }
        return "(" + type.trim().replace("null", "") + ")";
    }
}
