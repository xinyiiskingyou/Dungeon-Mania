package dungeonmania.battles;

import dungeonmania.enemy.Enemy;
import dungeonmania.entities.Player;
import dungeonmania.response.models.RoundResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Battle implements Serializable, Cloneable {
    private Enemy enemy;
    private Player player;
    
    private double initialPlayerHealth;
    private double initialEnemyHealth;

    private List<Round> roundsList;

    public Battle(Player player, Enemy enemy) {
        this.initialEnemyHealth = enemy.getHealth();
        this.initialPlayerHealth = player.getHealth();
        this.player = player;
        this.enemy = enemy;
        roundsList = new ArrayList<>();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public List<RoundResponse> getRoundResponseList() {
        List<RoundResponse> responseList = new ArrayList<>();
        for (Round r: roundsList) {
            responseList.add(new RoundResponse(r.getDeltaPlayerHealth(), r.getDeltaEnemyHealth(), r.getItemResponseWeaponsUsed()));
        }
        return responseList;
    }

    public void addToRounds(Round r) {
        roundsList.add(r);
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public Player getPlayer() {
        return player;
    }

    public double getInitialPlayerHealth() {
        return initialPlayerHealth;
    }

    public double getInitialEnemyHealth() {
        return initialEnemyHealth;
    }

    public List<Round> getRoundsList() {
        return roundsList;
    }

}