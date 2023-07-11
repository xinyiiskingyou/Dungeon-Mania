package dungeonmania.enemy;

import java.util.Random;
import java.util.List;
import java.util.Arrays;

import dungeonmania.battles.InvincibleRunStrategy;
import dungeonmania.entities.Player;
import dungeonmania.Dungeon;
import dungeonmania.battles.Battle;
import dungeonmania.entities.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy {
    private Random randomGen;

    // CONSTRUCTORS //
    public ZombieToast(Position position, double health, double attackDamage, String type) {
        super(position, health, attackDamage, type);
        randomGen = new Random(); // Create a random number generator
    }

    // MOVEMENT METHODS //
    @Override
    public void move(List<Entity> entities, Dungeon dungeon) {
        // Get random direction to move
        List<Direction> moveDirections = Arrays.asList(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
        int randomNum = randomGen.nextInt(4); // [0,n)
        Direction directionToMove = moveDirections.get(randomNum);

        if (getBattleStrategy() instanceof InvincibleRunStrategy) {
            // Get player
            Player player = null;
            for (Entity entity: entities) {
                if (entity.getType().equals("player")) {
                    player = (Player) entity;
                }
            }

            int zombieX = getPosition().getX();
            int zombieY = getPosition().getY();
            int playerX = player.getPosition().getX();
            int playerY = player.getPosition().getY();

            if (playerY == zombieY) {
                directionToMove = playerX > zombieX ? Direction.LEFT : Direction.RIGHT;
            } else {
                directionToMove = playerY > zombieY ? Direction.UP : Direction.DOWN;
            }
        }

        Position positionToMove = getPosition().translateBy(directionToMove);

        if (getSwampTileTick() > 0) {
            setSwampTileTick(getSwampTileTick()-1);
            return;
        } else {
            SwampTile swampTile = isSwampTile(entities, positionToMove);
            if (swampTile != null) {
                setSwampTileTick(swampTile.getMovementFactor());
            }
        }

        if (isMovable(entities, positionToMove, dungeon)) {
            moveDirection(directionToMove);
        }
    }

    @Override
    public boolean isMovable(List<Entity> entities, Position positionToMove, Dungeon dungeon) {
        for (Entity entity : entities) {
            if (!entity.getPosition().equals(positionToMove)) {
                continue;
            }

            if (entity instanceof Player) {
                Player p = (Player) entity;
                Battle b = super.battle(p, dungeon);
                if (b != null) {
                    dungeon.addBattle(b);
                }
            }

            if (entity instanceof Wall ||
                entity instanceof Boulder ||
                entity instanceof ZombieToastSpawner ||
                entity instanceof Portal ||
                (entity instanceof Door && !((Door) entity).isOpen())) {
                return false;
            }
        }
        return true;
    }

}