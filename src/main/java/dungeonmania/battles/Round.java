package dungeonmania.battles;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.items.ItemEntity;
import dungeonmania.response.models.ItemResponse;

import java.io.Serializable;

public class Round implements Serializable {
    private double deltaPlayerHealth;
    private double deltaEnemyHealth;
    private List<ItemEntity> itemsUsed;

    private List<ItemEntity> weaponsUsed;

    public Round() {
        itemsUsed = new ArrayList<>();
        weaponsUsed = new ArrayList<>();
        deltaPlayerHealth = 0;
        deltaEnemyHealth = 0;
    }

    public List<ItemResponse> getItemResponseWeaponsUsed() {
        List<ItemResponse> itemResponseList = new ArrayList<>();
        for (ItemEntity w : itemsUsed) {
            itemResponseList.add(new ItemResponse(w.getId(), w.getType()));
        }

        for (ItemEntity w: weaponsUsed) {
            itemResponseList.add(new ItemResponse(w.getId(), w.getType()));
        }

        return itemResponseList;
    }

    public void addToWeaponsUsed(ItemEntity w) {
        weaponsUsed.add(w);
    }

    public double getDeltaPlayerHealth() {
        return deltaPlayerHealth;
    }

    public void setDeltaPlayerHealth(double deltaPlayerHealth) {
        this.deltaPlayerHealth = deltaPlayerHealth;
    }
    public double getDeltaEnemyHealth() {
        return deltaEnemyHealth;
    }

    public void setDeltaEnemyHealth(double deltaEnemyHealth) {
        this.deltaEnemyHealth = deltaEnemyHealth;
    }

    public List<ItemEntity> getItemsUsed() {
        return itemsUsed;
    }

    public List<ItemEntity> getWeaponsUsed() {
        return weaponsUsed;
    }

}