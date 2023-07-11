package dungeonmania.goals;

import dungeonmania.Dungeon;

import java.io.Serializable;

public interface GoalsInterface extends Serializable {
    /*
     * Check if each single goal is finished
     */
    public boolean isFinished(Dungeon dungeon);

    /*
     * Return the type of undone goals
     */
    public String getType();
}
