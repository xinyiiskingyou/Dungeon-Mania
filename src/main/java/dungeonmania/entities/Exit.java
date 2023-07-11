package dungeonmania.entities;

import dungeonmania.Dungeon;
import dungeonmania.goals.AndGoal;
import dungeonmania.goals.GoalsInterface;
import dungeonmania.goals.OrGoal;
import dungeonmania.util.Position;

public class Exit extends Entity {
    
    private boolean isOpen;
    public Exit(Position position, String type) {
        super(position, type);
        this.isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }
    
    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean checkExitStatus(Dungeon dungeon) {
        int flag = 0;
        int found = 0;

        found = checkExitInAndGoalList(dungeon);
        for (GoalsInterface goalsInterface: dungeon.getSubgoalList()) {
            if (goalsInterface instanceof AndGoal) {
                // if exit exists in "AND" subgoal list -> the other goal must finish first
                for (GoalsInterface goals: ((AndGoal) goalsInterface).getAndGoalList()) {
                    if (goals.getType().equals(":exit")) {
                        continue;
                    }
                    if (goals.isFinished(dungeon)) {
                        flag = 1;
                    }
                }
            } else if (goalsInterface instanceof OrGoal) {
                // if exit exists in "OR" subgoal list -> true
                for (GoalsInterface goals: ((OrGoal)goalsInterface).getOrGoalList()) {
                    if (goals.getType().equals(":exit")) {
                        flag = 1;
                    }
                }
            } else {
                if (goalsInterface.isFinished(dungeon) && found == 0) {
                    flag = 1;
                }
            }
        }
        if (dungeon.getSuperGoal() instanceof OrGoal) {
            if (flag == 1) {
                return true;
            }
        } else if (dungeon.getSuperGoal() instanceof AndGoal) {
            if (dungeon.checkOneGoalFinished() && flag == 1) {
                return true;
            }
        } else if (dungeon.getGoals().equals(":exit")) {
            return true;
        }
        return false;
    }

    public int checkExitInAndGoalList(Dungeon dungeon) {
        for (GoalsInterface goalsInterface: dungeon.getSubgoalList()) {
            if (goalsInterface instanceof AndGoal) {
                // if exit exists in "AND" subgoal list -> the other goal must finish first
                for (GoalsInterface goals: ((AndGoal) goalsInterface).getAndGoalList()) {
                    if (goals.getType().equals(":exit")) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

}
