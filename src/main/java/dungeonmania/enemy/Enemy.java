package dungeonmania.enemy;

import java.util.List;

import dungeonmania.entities.*;
import dungeonmania.items.*;
import dungeonmania.battles.*;
import dungeonmania.Dungeon;
import dungeonmania.battles.Battle;
import dungeonmania.battles.BattleStrategy;
import dungeonmania.battles.FightStrategy;
import dungeonmania.util.Position;
import dungeonmania.util.Direction;

public abstract class Enemy extends Entity implements Subscriber {
    private double health;
    private double attackDamage;
    private BattleStrategy battleStrategy;
    private int swampTileTick;

    // CONSTRUCTOR //
    public Enemy(Position position, double health, double attackDamage, String type) {
        super(position, type);
        this.health = health;
        this.attackDamage = attackDamage;
        this.battleStrategy = new FightStrategy();
        swampTileTick = 0;
    }

    public Battle battle(Player player, Dungeon dungeon) {

        if (getBattleStrategy() instanceof InvincibleRunStrategy) {
            setBattleStrategy(new InvincibleFightStrategy());
        }
        Battle battle = new Battle(player, this);
        return battleStrategy.battleResponse(player, this, battle, dungeon);
    }

    // MOVEMENT METHOD //
    public abstract void move(List<Entity> entities, Dungeon dungeon);

    public abstract boolean isMovable(List<Entity> entities, Position positionToMove, Dungeon dungeon);

    public void moveDirection(Direction direction) {
        setPosition(getPosition().translateBy(direction));
    }

    // GETTERS //
    public double getHealth() {
        return health;
    }
    
    public double getAttackDamage() {
        return attackDamage;
    }

    public BattleStrategy getBattleStrategy() {
        return battleStrategy;
    }

    public String getBattleStrategyName() {
        if (battleStrategy instanceof FightStrategy) {
            return "FightStrategy";
        }

        if (battleStrategy instanceof InvincibleFightStrategy) {
            return "InvincibleFightStrategy";
        }

        if (battleStrategy instanceof InvincibleRunStrategy) {
            return "InvincibleRunStrategy";
        }

        if (battleStrategy instanceof InvisibleAvoidStrategy) {
            return "InvisibleAvoidStrategy";
        }

        return null;
    }

    public void updateHealth(double deltaHealth) {
        this.health += deltaHealth;
    }

    public void setAttackDamage(double attackDamage) {
        this.attackDamage = attackDamage;
    }

    public void setBattleStrategy(BattleStrategy battleStrategy) {
        this.battleStrategy = battleStrategy;
    }

    public void update(Potion playerPotion) {
        // effect has worn off
        if (playerPotion == null) {
            setBattleStrategy(new FightStrategy());
        }
        // if a battle is triggered while invincible trigger invincible fight
        if (playerPotion instanceof InvinciblePotion) {
            setBattleStrategy(new InvincibleRunStrategy());

        } 
        // avoid player when invisble, not battles can be triggered
        if (playerPotion instanceof InvisiblePotion) {
            setBattleStrategy(new InvisibleAvoidStrategy());
        }
    }

    public int getSwampTileTick() {
        return swampTileTick;
    }

    public void setSwampTileTick(int swampTileTick) {
        this.swampTileTick = swampTileTick;
    }

    public SwampTile isSwampTile(List<Entity> entities,Position positionToMove) {
        for (Entity entity : entities) {
            if (!entity.getPosition().equals(positionToMove)) {
                continue;
            }

            if (entity instanceof SwampTile) return (SwampTile) entity;
        }
        return null;
    }
}