package dungeonmania.entities;

import dungeonmania.Dungeon;
import dungeonmania.battles.Battle;
import dungeonmania.enemy.Assassin;
import dungeonmania.enemy.Enemy;
import dungeonmania.enemy.Mercenary;
import dungeonmania.enemy.Subscriber;
import dungeonmania.items.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.weapon.MidnightArmour;
import dungeonmania.weapon.Sword;
import dungeonmania.weapon.Weapon;
import java.util.ArrayList;
import java.util.List;

public class Player extends Entity implements Subject {

    private List<ItemEntity> weapons = new ArrayList<>();
    private List <Enemy> opponents = new ArrayList<>();
    private double attackDamage;
    private double playerHealth;

    private List <ItemEntity> playerInventory =  new ArrayList<>();
    private List<String> bombsCollected = new ArrayList<>();
    private List <Potion> potionsQueue = new ArrayList<>();
    private Potion activePotion;

    public Player(Position position, double attackDamage, double playerHealth, String type) {
        super(position, type);
        this.attackDamage = attackDamage;
        this.playerHealth = playerHealth;
    }

    public void move(Dungeon dungeon, Direction direction) {
        Position originalPosition = this.getPosition();
        Position newPosition = originalPosition.translateBy(direction);

        if (isMovable(dungeon, newPosition, direction)) {
            // dungeon.tickHealthBar(direction);
            // player can collect the item
            collectItem(dungeon, newPosition);
            // set to the new position
            setPosition(setTeleportPos(dungeon, direction, newPosition));
        } else {direction = null;}

        for (Enemy e: opponents) {
            //battle the enemy and add it to the battles list
            if (e instanceof Mercenary) {
                if (!((Mercenary)e).isHostile()) {
                    //  if mercenary is ally, do not battle
                    continue;
                }
            }

            if (e instanceof Assassin) {
                if (!((Assassin)e).isHostile()) {
                    //  if mercenary is ally, do not battle
                    continue;
                }
            }
            Battle b = e.battle(this, dungeon);
            if (b != null) {
                dungeon.addBattle(b);
            }
            //unsubscribe(e); // if here enemy has died and want to remove from subscribers
        }

        // reset opponents list
        opponents.clear();
        // tell subscribers no longer in battle
        notifySubscribers();
    }

    public void placeBomb(Bomb bomb, Dungeon dungeon) {
        // remove bomb from player items list
        this.playerInventory.remove(bomb);

        // set new bomb position
        bomb.setPicked();
        bomb.setPosition(this.getPosition());
        dungeon.addItem(bomb);

        // check if next to active switch
        // do tickbomb
        if (bomb.hasActiveSwitch(dungeon)) {
            this.tickBomb(bomb, dungeon);
        }
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public double getPlayerHealth() {
        return playerHealth;
    }

    public void updateHealth(double deltaHealth) {
        this.playerHealth += deltaHealth;
    }
    
    @Override
    public double getHealth() {
        return playerHealth;
    }

    public List<ItemEntity> getWeapons() {
        return weapons;
    }
    public void addWeapon(ItemEntity w) {
        weapons.add(w);
    }

    public void removeWeapon(ItemEntity  weapon) {
        weapons.remove(weapon);
    }

    // player's inventory list
    public List<ItemEntity> getInventory() {
        return playerInventory;
    }

    public void addItem(ItemEntity item) {
        this.playerInventory.add(item);
    }

    public void removeItem(ItemEntity item) {
        this.playerInventory.remove(item);
    }

    public void removeTreasure() {
        // Remove first instance of treasure in inventory
        for (ItemEntity item : this.playerInventory) {
            if (item instanceof Treasure) {
                removeItem(item);
                return;
            }
        }
    }

    public Key getKey() {
        for (ItemEntity item: this.playerInventory) {
            if (item instanceof Key) {
                return (Key) item;
            }
        }
        return null;
    }

    public int getCount(String type) {
        int count = 0;
        for (ItemEntity item: this.playerInventory) {
            if (item.getType().equals(type)) {
                count++;
            }
        }
        return count;
    }

    public Boolean containsType(String type) {
        for (ItemEntity item: this.playerInventory) {
            if (item.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public List<Enemy> getOpponents() {
        return opponents;
    }

    public void tickBomb(Bomb bomb, Dungeon dungeon) {
        //bomb.setPosition(this.getPosition());
        bomb.detonate(dungeon);
        dungeon.removeItem(bomb);
    }

    public List<String> getBombsCollected() {
        return bombsCollected;
    }

    public void collectBomb(Bomb bomb) {
        this.bombsCollected.add(bomb.getId());
    }

    public void collectItem(Dungeon dungeon, Position positionToMove) {
        for (ItemEntity item: dungeon.getItems()) {
            if (!(item.getPosition().equals(positionToMove))) {
                continue; // has to be at position
            }
            // player can only carry one key at a time
            if (item instanceof Key && getCount("key") == 1) {
                continue;
            } else if (item instanceof Bomb) {
                if (bombsCollected.contains(item.getId())) {
                    // cannot collect bomb which has already been placed on the map
                    continue;
                }
                // else collect the bomb 
                //collectBomb((Bomb) item);
            } 
            // add weapons to weapon list
            if (item instanceof Weapon) {
                weapons.add(item);
            }

            if (item.getPosition().equals(positionToMove)) {
                if (item instanceof Bomb) {
                    collectBomb((Bomb) item);
                }
                playerInventory.add(item);
                // remove from the map once the item is picked up
                dungeon.removeItem(item);
            }

            if (dungeon.getGoals().equals(":treasure") && (item instanceof Treasure || item instanceof SunStone)) {
                if ((getCount("treasure") + getCount("sun_stone"))>= ((Treasure)item).getGoal()) {
                    dungeon.setGoals("");
                }
            }
        }
    }

    public boolean isMovable(Dungeon dungeon, Position positionToMove, Direction direction) {
        for (Enemy entity : dungeon.getEnemies()) {
            if (entity.getPosition().equals(positionToMove)) {
                opponents.add(entity);
            }
        }

        for (Entity entity : dungeon.getEntities()) {
            if (entity.getPosition().equals(positionToMove)) {
                if (entity instanceof Wall || entity instanceof ZombieToastSpawner) {
                    return false;
                }
                if (entity instanceof Exit) {
                    // check if goals are completed
                    if (((Exit) entity).checkExitStatus(dungeon)) {
                        ((Exit) entity).setOpen(true);
                        dungeon.setGoals("");
                    }
                }
                if (entity instanceof Door) {
                    // if the player can open the door in the next tick
                    if (getCount("sun_stone") > 0) {
                        entity.setType("door_open");
                        ((Door) entity).setOpen(true);
                        return true;
                    }
                    Key key = this.getKey(); 
                    if (key != null && key.getKeyId() == ((Door) entity).getKeyId()) {
                        ((Door) entity).setOpen(true);
                        entity.setType("door_open");
                        // remove the key from inventory list and move to the next position
                        playerInventory.remove(key);
                        return true;
                    }
                    if (!((Door) entity).isOpen()) {
                        return false;
                    }
                }
                if (entity instanceof Boulder) {
                    return ((Boulder) entity).pushBoulder(direction, dungeon);
                }
                if (entity instanceof Portal || entity instanceof TimeTravellingPortal) {
                    if (this.getType().equals("older_player")) {
                        dungeon.removeEntity(this);
                    }
                }
                if (entity instanceof Portal) {
                    Portal corresponding = ((Portal) entity).findPortal(dungeon, direction, (Portal) entity);
                    return ((Portal) entity).isMovable(dungeon, direction, corresponding.getPosition());
                }
            }
        }
        return true;
    }

    public Position setTeleportPos(Dungeon dungeon, Direction direction, Position positionToMove) {
        Position newPosition = this.getPosition().translateBy(direction);
        for (Entity entity: dungeon.getEntities()) {
            if (entity.getPosition().equals(positionToMove)) {
                if (entity instanceof Portal) {
                    Portal corresponding = ((Portal) entity).findPortal(dungeon, direction, (Portal) entity);
                    Position correspondingPos = corresponding.getPosition();
                    if (((Portal) entity).isMovable(dungeon, direction, correspondingPos)) {
                        newPosition = correspondingPos.translateBy(direction);
                    }
                }
            }
        }
        return newPosition;
    }

    public Boolean isTimeTravellingPortal(Dungeon dungeon) {
        for (Entity entity: dungeon.getEntities()) {
            if (entity.getPosition().equals(this.getPosition())) {
                if (entity instanceof TimeTravellingPortal) {
                    return true;
                }
            }
        }
        return false;
    }
    // remove from player inventory by type string
    // returns true if item removed
    public boolean removeByType(String type) {
        List <ItemEntity> inventory = getInventory();
        for (ItemEntity item: inventory) {
            if (item.getType().equals(type)) {
                getInventory().remove(item);
                return true;
            }
        }
        return false;
    }


    public void craftBuildable(String type) {
        if (type.equals("shield")) {
            removeByType("wood");
            removeByType("wood");
            // check for sunstone
            if (getCount("sun_stone") > 0) {
                return;
            }
            // remove key OR treasure 
            if (!removeByType("treasure")) {
                removeByType("key");
            }
        }
        if (type.equals("bow")) {
            removeByType("wood");
            for (int i = 0; i < 3; i ++) {
                removeByType("arrow");
            }
        }
        if (type.equals("sceptre")) {
            int numWood = getCount("wood");
            int numArrow = getCount("arrow");
            int numKey = getCount("key");
            int numTreasure = getCount("treasure");

            //  if has wood but no arrows
            if (numWood >=1 && numArrow < 2) {
                removeByType("wood");
            }
            // has arrow but no wood
            if (numWood < 1 && numArrow >= 2) {
                removeByType("arrow");
                removeByType("arrow");
            }
            // has key but no treasure
            if (numKey >= 1 && numTreasure < 1) {
                removeByType("key");
            }
            // has treasure but no key
            if (numKey < 1 && numTreasure >= 1) {
                removeByType("treasure");
            }

        }

        if (type.equals("midnight_armour")) {
            removeByType("sword");
            // remove sword from weapons list
            List<ItemEntity> removeList = getWeapons();
            for (int i = 0; i < removeList.size(); i++) {
                if (removeList.get(i) instanceof Sword) {
                    removeWeapon(removeList.get(i));
                    break;
                }
            }

        }
    }


    // potions interactions methods
    public List<Potion> getPotionQueue() {
        return potionsQueue;
    }

    public void addPotionToQueue(Potion potion) {
        this.potionsQueue.add(potion);
    }

    public Potion getActivePotion() {
        return activePotion;
    }

    public void setActivePotion(Potion potion) {
        this.activePotion = potion;
    }

    public boolean consumePotion(Potion potion) {
        // check potion is in players inventory
        if (!(this.playerInventory.contains(potion))) {
            return false; // cannot consume potion not in inventory
        }
        // add to potions queue
        addPotionToQueue(potion);
        this.playerInventory.remove(potion);
        if (getPotionQueue().size() == 1) {
            // only potion in queue
            setActivePotion(potion);
            notifySubscribers();
        }
        return true;
    }

    public void checkPotionQueue() {
        if (potionsQueue.size() == 0) {
            // nothing to check
            return;
        }
        // remove potion from queue if duration has worn off
        if (potionsQueue.get(0).getDuration() == 0) {
            potionsQueue.remove(potionsQueue.get(0));
            if (potionsQueue.size() > 0) {
                // if other effects queued up, set active to first in queue
                setActivePotion(potionsQueue.get(0));
            } else {
                // if 
                setActivePotion(null);
            }
            // notify subscribers of change due to effects wearing off
            notifySubscribers();
        }
    }

    public void tickPotions() {
        if (getActivePotion() == null) {
            notifySubscribers();
            return;
        }
        // decrease duration of active potion
        getActivePotion().decreaseDuration();
        // check potions queue
        checkPotionQueue();
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        if (subscribers.contains(subscriber)) {
            // already subscribed
            return; 
        }
        subscribers.add(subscriber);
        // notify of current state when subscribers subscribe
        subscriber.update(getActivePotion());
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscribers() {
        for (Subscriber subscriber: subscribers) {
            // update subscribers with active potion
            subscriber.update(getActivePotion());
        }
    }
}
