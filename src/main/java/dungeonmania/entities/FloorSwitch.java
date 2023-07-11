package dungeonmania.entities;

import dungeonmania.util.Position;
import dungeonmania.items.Bomb;
import dungeonmania.items.ItemEntity;
import java.util.List;

import dungeonmania.Dungeon;

public class FloorSwitch extends Entity {
    private boolean isOn;

    public FloorSwitch(Position position, String type) {
        super(position, type);
        this.isOn = false;
    }

    public boolean isOn() {
        return isOn;
    }
    
    public void setOn(boolean isOn, Dungeon dungeon) {
        this.isOn = isOn;
        triggerBomb(dungeon);
    }

    public void triggerBomb(Dungeon dungeon) {
        Player player = dungeon.getPlayer();
        if (!this.isOn) {
            return;
        }
        List<Position> positions = this.getPosition().getAdjacentPositions();
        for (Position position: positions) {
            ItemEntity item = dungeon.getItemEntity(position);
            if (item== null) {
                continue;
            }
            if (item.getType().equals("bomb")) {
                if (((Bomb)item).isPicked()) {
                    player.tickBomb((Bomb)item, dungeon);
                }
            }
        }
    }

}
