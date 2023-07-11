package dungeonmania.enemy;

import java.util.List;
import java.util.Arrays;

import dungeonmania.entities.Player;
import dungeonmania.Dungeon;
import dungeonmania.battles.Battle;
import dungeonmania.entities.*;
import dungeonmania.util.Position;

public class Spider extends Enemy {
    private int currMove; // Current index of move in moveSequence
    private boolean reverse;
    private List<Position> moveSequence;

    // CONSTRUCTORS //
    public Spider(Position position, double health, double attackDamage, String type) {
        super(position, health, attackDamage, type);
        // Maybe check if there is a boulder in top square since spiders can't move through boulders.
        currMove = 0;
        reverse = false;

        moveSequence = Arrays.asList(position.translateBy(0, -1), position.translateBy(1, -1),
                position.translateBy(1, 0), position.translateBy(1, 1),
                position.translateBy(0, 1), position.translateBy(-1, 1),
                position.translateBy(-1, 0), position.translateBy(-1, -1));
    }

    // MOVEMENT METHODS //
    @Override
    public void move(List<Entity> entities, Dungeon dungeon) {
        Position positionToMove = moveSequence.get(currMove);

        // Reverse movement if boulder is in positionToMove
        if (!isMovable(entities, positionToMove, dungeon)) {
            reverse ^= true;

            currMove = reverse ? currMove - 2 : currMove + 2;
            if (reverse && currMove < 0) {
                currMove = moveSequence.size() - 1;
            } else {
                currMove %= moveSequence.size();
            }
            positionToMove = moveSequence.get(currMove);
        }

        if (getSwampTileTick() > 0) {
            setSwampTileTick(getSwampTileTick()-1);
            return;
        } else {
            SwampTile swampTile = isSwampTile(entities, positionToMove);
            if (swampTile != null) {
                setSwampTileTick(swampTile.getMovementFactor());
            }
            setPosition(positionToMove);
        }

        currMove = reverse ? --currMove : ++currMove;
        if (reverse && currMove < 0) {
            currMove = moveSequence.size() - 1;
        } else {
            currMove %= moveSequence.size();
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

            if (entity instanceof Boulder) {
                return false;
            }
        }
        return true;
    }
}