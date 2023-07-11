package dungeonmania.enemy;

import dungeonmania.Dungeon;
import dungeonmania.battles.Battle;
import dungeonmania.battles.InvincibleRunStrategy;
import dungeonmania.entities.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Hydra extends Enemy {
    private double healthIncreaseRate;
    private double healthIncreaseAmount;

    private Random randomGen;

    // CONSTRUCTORS //
    public Hydra(Position position, double health, double attackDamage, double healthIncreaseRate, double healthIncreaseAmount, String type) {
        super(position, health, attackDamage, type);
        this.healthIncreaseRate = healthIncreaseRate;
        this.healthIncreaseAmount = healthIncreaseAmount;
        randomGen = new Random();
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

            int hydraX = getPosition().getX();
            int hydraY = getPosition().getY();
            int playerX = player.getPosition().getX();
            int playerY = player.getPosition().getY();

            if (playerY == hydraY) {
                directionToMove = playerX > hydraX ? Direction.LEFT : Direction.RIGHT;
            } else {
                directionToMove = playerY > hydraY ? Direction.UP : Direction.DOWN;
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

            // Hydras cannot push boulders or move through portals
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

    public double getHealthIncreaseRate() {
        return healthIncreaseRate;
    }

    public double getHealthIncreaseAmount() {
        return healthIncreaseAmount;
    }

}
