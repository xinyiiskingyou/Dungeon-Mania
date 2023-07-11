package dungeonmania.entities;

import dungeonmania.Dungeon;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Boulder extends Entity {

    public Boulder (Position position, String type) {
        super(position, type);
    }

    public boolean pushBoulder(Direction direction, Dungeon dungeon) {
        
        String goal = dungeon.getGoals();
        Position originalPosition = this.getPosition();
        Position newPosition = originalPosition.translateBy(direction);
        
        for (Entity entity: dungeon.getEntities()) {
            // if there are other entites next to boulder
            if (newPosition.equals(entity.getPosition())) {
                // cannot push a boulder next to wall, another boulder, locked door and portal
                if (entity instanceof Boulder || entity instanceof Wall || entity instanceof Portal || 
                    (entity instanceof Door && ! ((Door) entity).isOpen())){
                    return false;
                } 
                // floor switch is triggered when a boulder is pushed onto it
                if (entity instanceof FloorSwitch) {
                    ((FloorSwitch) entity).setOn(true, dungeon);
                    this.setPosition(newPosition);
                    dungeon.addTriggeredSwitch((FloorSwitch) entity);
                    if ((dungeon.getSwitchCount() == dungeon.getSwitchList().size()) && goal.equals(":boulders")) {
                        dungeon.setGoals("");
                    }
                    return true;
                }      
            } else {
                // pushing a boulder off the floor switch untriggers
                if (entity instanceof FloorSwitch && originalPosition.equals(entity.getPosition())) {
                    ((FloorSwitch) entity).setOn(false, dungeon);
                    dungeon.removeTriggeredSwitch((FloorSwitch) entity);
                    continue;
                }
            }
        }
        this.setPosition(newPosition);
        return true;
    }
}
