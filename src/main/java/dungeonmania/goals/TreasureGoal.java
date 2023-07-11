package dungeonmania.goals;

import dungeonmania.Dungeon;
import dungeonmania.entities.Player;
import dungeonmania.items.ItemEntity;
import dungeonmania.items.Treasure;

public class TreasureGoal implements GoalsInterface{

    private Dungeon dungeon;

    private final String type = ":treasure";
    
    public TreasureGoal(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @Override
    public boolean isFinished(Dungeon dungeon) {
        // collect the target treasure
        Player player = dungeon.getPlayer();
        for (ItemEntity item: player.getInventory()) {
            if (item instanceof Treasure) {
                if ((player.getCount("treasure") + player.getCount("sun_stone")) >= ((Treasure) item).getGoal()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getType() {
        return type;
    }
    
}
