package dungeonmania;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.battles.Battle;
import dungeonmania.enemy.Assassin;
import dungeonmania.enemy.Enemy;
import dungeonmania.enemy.Mercenary;
import dungeonmania.entities.Entity;
import dungeonmania.entities.FloorSwitch;
import dungeonmania.entities.Player;
import dungeonmania.entities.ZombieToastSpawner;
import dungeonmania.goals.AndGoal;
import dungeonmania.goals.GoalsInterface;
import dungeonmania.goals.OrGoal;
import dungeonmania.items.ItemEntity;
import dungeonmania.response.models.AnimationQueue;
import dungeonmania.util.Position;
import dungeonmania.weapon.Sceptre;
public class Dungeon implements Serializable, Cloneable {
    private EntityFactory factory = new EntityFactory();
    private List<Entity> entities = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private List<ItemEntity> inventory = new ArrayList<>();
    private List<String> buildables = new ArrayList<>();
    // private HealthBar healthBar;
    // private double intialPlayerHealth;
    private double ZombieAttack;
    private double ZombieHealth;
    private double SpiderAttack;
    private double SpiderHealth;
    private int spiderSpawnRate;
    private int zombieSpawnRate;
    private int zombieSpawnCounter;
    private List <Integer> dungeonSpawnLimits = new ArrayList<>();
    private SpiderSpawner spiderSpawner;
    private List<String> readyToBuild = new ArrayList<>();
    private List<Battle> battles = new ArrayList<>();
 
    private List<GoalsInterface> subgoalList = new ArrayList<>();
    private List<FloorSwitch> switchList = new ArrayList<>();
    List <AnimationQueue> animations = new ArrayList<>();
    
    private int enemyGoals = 0;
    private int enemiesKill = 0;
    private Player player = null;

    private String goals = "";
    private GoalsInterface superGoal;

    private List<HashMap<String, List<Object>>> gameStates = new ArrayList<>();

    public void createNewDungeon(JSONObject json, JSONObject configJson) {
        this.ZombieAttack = (double) configJson.getInt("zombie_attack");
        this.ZombieHealth = (double) configJson.getInt("zombie_health");
        this.SpiderAttack = (double) configJson.getInt("spider_attack");
        this.SpiderHealth = (double) configJson.getInt("spider_health");
        // this.intialPlayerHealth = (double) configJson.getInt("player_health");
        this.spiderSpawnRate = configJson.getInt("spider_spawn_rate");
        this.zombieSpawnRate = configJson.getInt("zombie_spawn_rate");
        this.zombieSpawnCounter = zombieSpawnRate - 1;
        this.spiderSpawner = new SpiderSpawner(spiderSpawnRate);
        createEntities(json, configJson);
        // if (this.checkPlayer()) {
        //     this.healthBar = new HealthBar(this);
        // }
        calcDungeonLimits();
    }

    public void createEntities(JSONObject json, JSONObject configJson) {
        JSONArray entitiesObj = (JSONArray) json.get("entities");
        for (int i = 0; i < entitiesObj.length(); i++) {
            JSONObject entity = entitiesObj.getJSONObject(i);
            String type = entity.getString("type");
            Position position = new Position(entity.getInt("x"), entity.getInt("y"));
            entities.add(factory.createEntity(entity, configJson, type, position));
            enemies.add(factory.createEnemy(configJson, type, position));
            inventory.add(factory.createItems(entity, configJson, type, position));
        }
        for (Entity entity: getEntities()) {
            if (entity instanceof Player) {
                this.player = (Player) entity;
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setGameState(HashMap<String, List<Object>> gamestate) {

        List<Entity> entities = new ArrayList<>();

        // get the player in the current dungeon
        Player currPlayer = this.getPlayer();
        entities.add(currPlayer);
        for (Object entity: gamestate.get("entities")) {
            if (((Entity)entity) instanceof Player) {
                ((Entity)entity).setType("older_player");
            }
            entities.add((Entity)entity);
        }
        this.entities = entities;

        List<ItemEntity> items = new ArrayList<>();
        for (Object item: gamestate.get("items")) {
            items.add((ItemEntity)item);
        }
        this.inventory = items;

        List<String> buildables = new ArrayList<>();
        for (Object buildable: gamestate.get("buildables")) {
            buildables.add((String)buildable);
        }
        this.buildables = buildables;

        List<Battle> battles = new ArrayList<>();
        for (Object battle: gamestate.get("battles")) {
            battles.add((Battle)battle);
        }
        this.battles = battles;

        List<Enemy> enemies = new ArrayList<>();
        for (Object enemy: gamestate.get("enemies")) {
            enemies.add((Enemy)enemy);
        }
        this.enemies = enemies;
    }

    public void addSubgoals(JSONArray subgoalArray, JSONObject json) {
        JSONObject supergoalObject = (JSONObject) json.get("goal-condition");
        superGoal = factory.addGoal(supergoalObject, this);
        String goal = printGoals(factory.addGoal(supergoalObject, this));
        this.setGoals(goal);
    }
    
    public List<GoalsInterface> getSubgoalList() {
        if (superGoal instanceof OrGoal) {
            subgoalList = ((OrGoal) superGoal).getOrGoalList();
        } else if (superGoal instanceof AndGoal) {
            subgoalList = ((AndGoal) superGoal).getAndGoalList();
        }
        return subgoalList;
    }

    public boolean checkOneGoalFinished() {
        for (GoalsInterface goal: this.getSubgoalList()) {
            if (goal.isFinished(this)) {
                return true;
            }
        }
        return false;
    }

    public GoalsInterface getSuperGoal() {
        return superGoal;
    }

    public String printGoals(GoalsInterface goalsInterface) {
        if (goalsInterface == null) {
            return null;
        }
        return goalsInterface.getType();
    }

    public String getGoals() {
        return goals;
    }
    
    public void setGoals(String goals) {
        this.goals = goals;
    }

    public List<Entity> getEntities() {
        return entities.stream().filter(x -> x != null).collect(Collectors.toList());
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        if (entity instanceof Player) {
            this.player = (Player) entity;
        }
    }

    public List<Enemy> getEnemies() {
        return enemies.stream().filter(x -> x != null).collect(Collectors.toList());
    }
    
    public List<ItemEntity> getItems() {
        return inventory.stream().filter(x -> x != null).collect(Collectors.toList());
    }

    public List<ZombieToastSpawner> getZombieToastSpawners() {
        List <Entity> entities = getEntities();
        List <ZombieToastSpawner> spawners = new ArrayList<>();
        for(Entity entity: entities) {
            if (entity.getType().equals("zombie_toast_spawner")) {
                spawners.add((ZombieToastSpawner) entity);
            }
        }
        return spawners;
    }

    public void addItem(ItemEntity item) {
        inventory.add(item);
    }

    public void removeItem(ItemEntity item) {
        inventory.remove(item);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }
    
    public void explode(Position position) {
        for (Entity entity: getEntities()) {
            if (entity.getPosition().equals(position)) {
                String type = getEntity(position).getType();
                if (!type.equals("player") && !type.equals("exit") && !type.equals("portal")) {
                    removeEntity(getEntity(position));    
                }
            }
        }
        
        for (Enemy enemy: getEnemies()) {
            if (enemy.getPosition().equals(position)) {
                removeEnemy(getEnemy(position));
            }
        }

        for (ItemEntity item: getItems()) {
            if (item.getPosition().equals(position)) {
                String type = getItemEntity(position).getType();
                if (! type.equals("bomb")) {
                    removeItem(getItemEntity(position));
                }
            }
        }
    }
    
    public Entity getEntity(Position position) {
        for (Entity entity: getEntities()) {
            if (entity.getPosition().equals(position)) {
                return entity;
            }
        }
        return null;
    }

    public Enemy getEnemy(Position position) {
        for (Enemy enemy: getEnemies()) {
            if (enemy.getPosition().equals(position)) {
                return enemy;
            }
        }
        return null;
    }

    public ItemEntity getItemEntity(Position position) {
        for (ItemEntity item: getItems()) {
            if (item.getPosition().equals(position)) {
                return item;
            }
        }
        return null;
    }

    public List<String> getBuildables() {
        return this.buildables;
    }

    public List<String> getReadyToBuild() {
        if (!checkPlayer()) {return null;}
        readyToBuild.clear();
        checkBuildableEntity();
        return readyToBuild;
    }

    public void checkBuildableEntity() {
        // list of buildable stores item types that the player can build
        player = this.getPlayer();
        if (player == null) return;
        int numWoods = player.getCount("wood");
        int numArrows = player.getCount("arrow");
        int numTreasure = player.getCount("treasure");
        int numKey = player.getCount("key");
        int numSword = player.getCount("sword");
        int numSunStone = player.getCount("sun_stone");
        while (numWoods >= 1 && numArrows >= 3) {
                numWoods -= 1;
                numArrows -= 3;
                readyToBuild.add("bow");
        }
        while (numWoods >= 2 &&(numSunStone >= 1 || numKey >= 1 || numTreasure >= 1) ) {
                numWoods -= 2;
                if (numSunStone >= 1) {
                    // no more sunstones to potentially build with
                    numSunStone -= 1;
                }
                else if (numTreasure >= 1) {
                    // if used a key remove it
                    numTreasure -= 1;
                } else if (numKey >= 1) {
                    // otherwise remove the treasure
                        numKey -= 1;
                }
                readyToBuild.add("shield");          
        }
        if (numSword >= 1 && numSunStone >= 1) {
            readyToBuild.add("midnight_armour");          
        }
        if (numSunStone >= 1 && (numWoods >= 1 || numArrows >= 2) && (numKey >= 1 || numTreasure >= 1 || numSunStone >= 1)) {
            readyToBuild.add("sceptre");          
        }

    }

    public void createBuildableItem(String type, JSONObject configJson, Position position) {
        getReadyToBuild();
        for (String buildable: readyToBuild) {
            if (buildable.equals(type)) {
                EntityFactory factory = new EntityFactory();
                ItemEntity item = factory.createBuildableEntity(type, configJson, position);
                if (!(type.equals("sun_stone") && !(type.equals("sceptre")))) {
                    getPlayer().addWeapon(item);
                }
                getPlayer().addItem(item);
                readyToBuild.remove(type);
                player.craftBuildable(type);
                break;
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        entities.add(player);
        this.player = player;
    }

    public void removePlayer(Player player) {
        entities.remove(player);
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public List<Enemy> getAlliesList() {
        List<Enemy> allies = new ArrayList<>();
        for (Enemy e: getEnemies()) {
            if (e instanceof Mercenary) {
                Mercenary merc = (Mercenary)e;
                if (!merc.isHostile()) {
                    allies.add(merc);
                }
            }

            if (e instanceof Assassin) {
                Assassin a = (Assassin)e;
                if (!a.isHostile()) {
                    allies.add(a);
                }
            }
        }
        return allies;
    }

    public List<ItemEntity> getInventory() {
        return inventory;
    }

    public double getZombieHealth() {
        return ZombieHealth;
    }

    public double getSpiderAttack() {
        return SpiderAttack;
    }

    public double getSpiderHealth() {
        return SpiderHealth;
    }

    public double getZombieAttack() {
        return ZombieAttack;
    }

    public void setZombieHealth(double health) {
        this.ZombieHealth = health;
    }

    public void setZombieAttack(double attack) {
        this.ZombieAttack = attack;
    }

    public void setSpiderSpawner(SpiderSpawner spawner) {
        this.spiderSpawner = spawner;
        this.spiderSpawnRate = spawner.getSpiderSpawnRate();
    }

    public void non_player_acions() {
        // spawn new enemies
        if (zombieSpawnCounter == 0) {
            zombieSpawnCounter = zombieSpawnRate -1;
            List <ZombieToastSpawner> zombieSpawners = getZombieToastSpawners();
            for (ZombieToastSpawner spawner: zombieSpawners) {
                spawner.spawn(this);
            }
        } else {
            zombieSpawnCounter --;
        }
        // spawn spiders
        if (spiderSpawner != null) {
            spiderSpawner.spawn(this);
        }

        // Move all enemies 
        for (Enemy enemy : getEnemies()) {
            enemy.move(getEntities(), this);
        }
    }

    public void tickSceptre() {
  
        Player p = getPlayer();
        for (ItemEntity i: p.getInventory()) {
            if (i instanceof Sceptre) {
                Sceptre s = (Sceptre) i;
                if (s.getTarget() != null) {
                    // sceptre curr in use, tick duration
                    s.tickDuration();
                }
            }
        }
    }

    /**
     * function to calculate limites of the dungeon
     */
    public void calcDungeonLimits(){
        List <Entity> entities = getEntities();
        int highestX = Integer.MIN_VALUE;
        int highestY = highestX;
        for (Entity entity: entities) {
            if (entity.getPosition().getX() > highestX) {
                highestX = entity.getPosition().getX();
            }
            if (entity.getPosition().getY() > highestY) {
                highestY = entity.getPosition().getY();
            }
        }
        dungeonSpawnLimits.add(highestX);
        dungeonSpawnLimits.add(highestY);
    }

    public List <Integer> getDungeonLimits() {
        return dungeonSpawnLimits;
    }

    public void setDungeonSpawnLimit(int x, int y) {
        dungeonSpawnLimits.clear();
        dungeonSpawnLimits.add(x);
        dungeonSpawnLimits.add(y);
    }

    public void setZombieSpawnRate(int zombieSpawnRate) {
        this.zombieSpawnRate = zombieSpawnRate;
        this.zombieSpawnCounter = zombieSpawnRate - 1;
    }

    public int getEnemyGoals() {
        return enemyGoals;
    }

    public void setEnemyGoal(int enemyGoals) {
        this.enemyGoals = enemyGoals;
    }

    public List<Battle> getBattles() {
        return battles;
    }

    public void addBattle(Battle battle) {
        battles.add(battle);
    }

    public boolean checkPlayer() {
        player = getPlayer();
        if (player == null) {
            return false;
        }
        if (! getEntities().contains(player)) {
            return false;
        }
        return true;
    }

    public Player getOlderPlayer() {
        for (Entity entity: this.getEntities()) {
            if (entity.getType().equals("older_player")) {
                return (Player)entity;
            }
        }
        return null;
    }
    public int getEnemiesKill() {
        return enemiesKill;
    }

    public void incrementEnemiesKill() {
        this.enemiesKill += 1;
    }

    /*
     * For boulders goal
     */
    public List<FloorSwitch> getSwitchList() {
        return switchList;
    }

    public int getSwitchCount() {
        int count = 0;
        for (Entity entity: this.getEntities()) {
            if (entity.getType().equals("switch")) {
                count++;
            }
        }
        return count;
    }

    public void addTriggeredSwitch(FloorSwitch floorSwitch) { 
        switchList.add(floorSwitch);
    }

    public void removeTriggeredSwitch(FloorSwitch entity) {
        switchList.remove(entity);
    }

    public void AddGameState() {
        HashMap<String, List<Object>> gameState= new HashMap<String, List<Object>>();
        List<Object> entities = new ArrayList<>();
        List<Object> build = new ArrayList<>();
        List<Object> enemies = new ArrayList<>();
        List<Object> battles = new ArrayList<>();
        List<Object> items = new ArrayList<>();
        for (Entity entity: this.getEntities()) {
            try {
                entities.add(entity.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

        }
        gameState.put("entities", entities);

        for (ItemEntity item: this.getItems()) {
            try {
                items.add(item.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        gameState.put("items", items);

        for (String item: this.readyToBuild) {
            build.add((Object)item);
        }
        gameState.put("buildables", build);

        for (Battle battle: this.getBattles()) {
            try {
                battles.add(battle.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        gameState.put("battles", battles);

        for (Enemy enemy: this.getEnemies()) {
            try {
                enemies.add(enemy.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        gameState.put("enemies", enemies);
        gameStates.add(gameState);
    }

    public HashMap<String, List<Object>> getGameState(int tick) {
        return gameStates.get(tick);
    }

    /*
     * Frontend customisations 
     */
    // public List<AnimationQueue> getAnimations() {
    //     return this.animations;
    // }

    // public void addAnimation(AnimationQueue animationQueue) {
    //     this.animations.add(animationQueue);
    // }

    // public void renewHealthBar() {
    //     this.healthBar = new HealthBar(this);
    // }

    // public void tickHealthBar(Direction movementDirection) {
    //     if (! checkPlayer()) return;
    //     renewHealthBar();
    //     this.animations.clear();
    //     healthBar.moveHealthBar(this, movementDirection);
    // }

    // public double getInitialPlayerHealth() {
    //     return this.intialPlayerHealth;
    // }

    // public Double getPlayerHealthPercent() {
    //     return getPlayer().getHealth() / getInitialPlayerHealth();
    // }

    public void rewind(int ticks) {
        HashMap<String, List<Object>> gameState = getGameState(ticks);
        this.setGameState(gameState);
    }
}