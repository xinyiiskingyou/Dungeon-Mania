package dungeonmania.enemy;

import java.util.*;

import dungeonmania.Dungeon;
import dungeonmania.battles.Battle;
import dungeonmania.battles.InvisibleAvoidStrategy;
import dungeonmania.entities.*;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.items.ItemEntity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.weapon.Sceptre;

public class Assassin extends Enemy {
    private boolean hostile;
    private int bribeAmount;
    private int bribeRadius;
    private double bribeFailRate;
    private int reconRadius;
    private int allyAttack;
    private int allyDefence;
    private Position prevPlayerPosition;

    public Assassin(Position position, double health, double attackDamage, int bribeAmount, int bribeRadius,
                    double bribeFailRate, int reconRadius, int allyAttack, int allyDefence, String type) {
        super(position, health, attackDamage, type);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        this.bribeFailRate = bribeFailRate;
        this.reconRadius = reconRadius;
        this.allyAttack = allyAttack;
        this.allyDefence = allyDefence;
        hostile = true;
        setInteractable(true);
    }

    // METHODS //
    public void bribe(Player player) throws InvalidActionException {
        Position playerPosition = player.getPosition();

        // Get gold amount player has
        int goldAmount = 0;
        for (ItemEntity item : player.getInventory()) {
            if (item.getType().equals("treasure")) {
                goldAmount++;
            }
        }

        // if player has sceptre, bribe is immediately successful
        if (player.getCount("sceptre") >= 1) {
            // Bribe successful
            hostile = false;
            setInteractable(false);
            prevPlayerPosition = playerPosition;

            // Store the enemy being controlled by sceptre
            for (ItemEntity i: player.getInventory()) {
                if (i instanceof Sceptre) {
                    if (((Sceptre)i).getTarget() == null && ((Sceptre)i).getControlDuration() != 0) {
                        //  sceptre currently not in use and has not been used, can use to control enemy
                        ((Sceptre)i).setTarget(this);
                        break;
                    }
                }
            }
            return;
        }

        // Check if player is within bribe radius
        List<Position> pos = getPositionsInRadius(bribeRadius);
        if (pos.contains(playerPosition)) {
            if (goldAmount >= bribeAmount) {
                if (new Random().nextDouble() >= bribeFailRate) {
                    // Bribe successful
                    hostile = false;
                    setInteractable(false);
                    prevPlayerPosition = playerPosition;
                }

                // Decrease gold used by player
                decreasePlayerGold(player, bribeAmount);
            } else {
                throw new InvalidActionException("Player does not have enough gold to bribe assassin");
            }
        } else {
            throw new InvalidActionException("Player is not within specified bribing radius to the assassin");
        }
    }

    // MOVEMENT METHODS //
    @Override
    public void move(List<Entity> entities, Dungeon dungeon) {
        if (getPlayer(entities, dungeon) == null) {
            return;
        }
        Player player = getPlayer(entities, dungeon);
        Position playerPosition = player.getPosition();

        if (!isHostile()) {
            setPosition(prevPlayerPosition);
            prevPlayerPosition = playerPosition;
            return;
        }

        Position positionToMove = null;

        // Check if within recon radius
        List<Position> pos = getPositionsInRadius(reconRadius);
        if (getBattleStrategy() instanceof InvisibleAvoidStrategy && !pos.contains(playerPosition)) {
            Random randomGen = new Random();
            // Get random direction to move
            List<Direction> moveDirections = Arrays.asList(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
            int randomNum = randomGen.nextInt(4); // [0,n)
            Direction directionToMove = moveDirections.get(randomNum);
            positionToMove = getPosition().translateBy(directionToMove);
        } else {
            positionToMove = dijkstraSearch(dungeon, entities, getPosition(), playerPosition);
        }

        if (positionToMove == null) {
            positionToMove = getPosition();
        }

        if (playerPosition.equals(positionToMove) && isHostile()) {
            Battle b = super.battle(player, dungeon);
            if (b != null) {
                dungeon.addBattle(b);
            }
        }

        if (getSwampTileTick() > 0) {
            setSwampTileTick(getSwampTileTick()-1);
            return;
        } else {
            SwampTile swampTile = isSwampTile(entities, positionToMove);
            if (swampTile != null) {
                setSwampTileTick(swampTile.getMovementFactor());
            }
        }
        setPosition(positionToMove);
    }

    @Override
    public boolean isMovable(List<Entity> entities, Position positionToMove, Dungeon dungeon) {
        for (Entity entity : entities) {
            if (!entity.getPosition().equals(positionToMove)) {
                continue;
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

    // GETTERS //
    public boolean isHostile() {
        return hostile;
    }

    // SETTERS //
    public void setHostile(boolean hostile) {
        this.hostile = hostile;
    }

    // HELPERS //
    private Player getPlayer(List<Entity> entities, Dungeon dungeon) {
        for (Entity entity : entities) {
            if (entity.getType().equals("player")) {
                return (Player) entity;
            }
        }
        return null;
    }

    public int getAllyAttack() {
        return allyAttack;
    }

    public int getAllyDefence() {
        return allyDefence;
    }

    private void decreasePlayerGold(Player player, int bribeAmount) {
        while (bribeAmount > 0) {
            player.removeTreasure();
            bribeAmount--;
        }
    }

    public Position dijkstraSearch(Dungeon dungeon, List<Entity> entities, Position source, Position playerPos) {
        // Create grid
        List<Position> queue = new ArrayList<>();
        for (int x = -17; x <= 17; ++x) {
            for (int y = -17; y <= 17; ++y) {
                queue.add(new Position(x, y));
            }
        }

        Map<Position, Double> dist = new HashMap<Position, Double>();
        Map<Position, Position> prev = new HashMap<Position, Position>();

        for (Position p : queue) {
            dist.put(p, Double.MAX_VALUE);
            prev.put(p, null);
        }
        dist.put(source, 0.0);

        while (!queue.isEmpty()) {
            Position u = minDistance(queue, dist);
            queue.remove(u);

            for (Position v : u.getCardinallyAdjacentPositions()) {
                if (!dist.containsKey(v)) continue;
                if (!isMovable(entities, v, dungeon)) continue;

                SwampTile swampTile = isSwampTile(entities, v);
                if (swampTile != null) {
                    if (Double.compare(dist.get(u) + swampTile.getMovementFactor(), dist.get(v)) < 0) {
                        dist.put(v, dist.get(u) + swampTile.getMovementFactor());
                        prev.put(v, u);
                    }
                } else {
                    if (Double.compare(dist.get(u) + 1, dist.get(v)) < 0) {
                        dist.put(v, dist.get(u) + 1);
                        prev.put(v, u);
                    }
                }
            }
        }

        return nextPositionBacktrack(prev, playerPos, source);
    }

    private Position minDistance(List<Position> queue, Map<Position, Double> dist) {
        double min = Double.MAX_VALUE;
        Position minPos = null;

        for (Map.Entry<Position, Double> e : dist.entrySet()) {
            if (e.getValue() <= min && queue.contains(e.getKey())) {
                min = e.getValue();
                minPos = e.getKey();
            }
        }

        return minPos;
    }

    private Position nextPositionBacktrack(Map<Position, Position> previousPos, Position currPos, Position startingPos) {
        if (previousPos.get(currPos) == null) {
            return null;
        }
        if (previousPos.get(currPos).equals(startingPos)) return currPos;
        return nextPositionBacktrack(previousPos, previousPos.get(currPos), startingPos);
    }

    // Radius is either bribe radius or recon radius
    public List<Position> getPositionsInRadius(int radius) {
        List<Position> positions = new ArrayList<>();

        int upperBoundX = this.getPosition().getX() - radius;
        int lowerBoundX = this.getPosition().getX() + radius;
        int upperBoundY = this.getPosition().getY() - radius;
        int lowerBoundY = this.getPosition().getY() + radius;

        for (int i = upperBoundX; i <= lowerBoundX; i++) {
            for (int j = upperBoundY; j <= lowerBoundY; j++) {
                Position position = new Position(i, j);
                positions.add(position);
            }
        }
        return positions;
    }

    public void setBribeAmount(int bribeAmount) {
        this.bribeAmount = bribeAmount;
    }
}
