package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.items.ItemEntity;
import dungeonmania.util.Direction;
import dungeonmania.Dungeon;
import dungeonmania.enemy.ZombieToast;
import dungeonmania.util.Position;
import dungeonmania.weapon.Sword;

public class ZombieToastSpawner extends Entity{
    public ZombieToastSpawner(Position position, String type) {
        super(position, type);
        setInteractable(true);
    }

    /**
     * function to spawn new zombies in an open cardinally adjacent square
     * @param health
     * @param attackDamage
     * @param type
     * @param dungeon
     */

    public void spawn(Dungeon dungeon) {
        Position pos = getOpenSquare(dungeon); // get the open square (if it exists)
        if (pos == null) {
            return;
        }
        ZombieToast zombie = new ZombieToast(pos, dungeon.getZombieHealth(), dungeon.getZombieAttack(), "zombie_toast");
        dungeon.addEnemy(zombie);
        dungeon.getPlayer().subscribe(zombie);
    }

    /**
     * get a list of entities at a given position
     * @param pos
     * @param dungeon
     * @return entities at pos
     */
    public List <Entity> getEntitiesAtPos(Position pos, Dungeon dungeon) {
        List <Entity> entities = dungeon.getEntities();
        List <Entity> entitiesAtPos =  new ArrayList<>();
        for (Entity entity: entities) {
            if (entity.getPosition().equals(pos)) {
                entitiesAtPos.add(entity);
            }
        }
        return entitiesAtPos;
    }

    /**
     * function to find an open square
     * @param dungeon
     * @return pos
     * return null if no open position found
     */
    public Position getOpenSquare(Dungeon dungeon) {
        // spawn into an open adj square (need to fix position here)
        List <Position> adjPos = new ArrayList<>();
        adjPos.add(getPosition().translateBy(Direction.UP));
        adjPos.add(getPosition().translateBy(Direction.LEFT));
        adjPos.add(getPosition().translateBy(Direction.RIGHT));
        adjPos.add(getPosition().translateBy(Direction.DOWN));
        for (Position pos: adjPos) {
            List <Entity> entitiesAtPos = getEntitiesAtPos(pos, dungeon);
            if (entitiesAtPos.size() == 0) {return pos;} // no blocking
            for (Entity entity: entitiesAtPos) {
                if (entity instanceof FloorSwitch) {continue;}
                if (!(entity instanceof Wall || entity instanceof Boulder)) {
                    return pos;
                }
            }
        }
        return null; // nothing found
    }

    public void destroy(Dungeon dungeon) throws InvalidActionException {
        Player player = dungeon.getPlayer();
        Position playerPosition = player.getPosition();

        // Check if player has a sword
        boolean hasSword = false;
        for (ItemEntity weapon : player.getWeapons()) {
            if (weapon instanceof Sword) {
                hasSword = true;
            }
        }

        // Check if player is not cardinally adjacent || if player does not have a weapon
        List<Position> spawnerAdjacentPositions = getPosition().getCardinallyAdjacentPositions();
        if (!spawnerAdjacentPositions.contains(playerPosition) || player.getWeapons().size() == 0 || !hasSword) {
            throw new InvalidActionException("Invalid destroy");
        }

        dungeon.removeEntity(this);
    }
}
