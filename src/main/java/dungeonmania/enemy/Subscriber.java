package dungeonmania.enemy;

import dungeonmania.items.Potion;

import java.io.Serializable;

public interface Subscriber extends Serializable {
    // give observer new player potion and allow them to update their strategy accordingly
    public void update(Potion playerPotion);
}
