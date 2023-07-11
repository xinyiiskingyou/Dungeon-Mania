package dungeonmania;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.battles.Battle;
import dungeonmania.enemy.Assassin;
import dungeonmania.enemy.Enemy;
import dungeonmania.enemy.Mercenary;
import dungeonmania.enemy.ZombieToast;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Exit;
import dungeonmania.entities.Player;
import dungeonmania.entities.Wall;
import dungeonmania.entities.ZombieToastSpawner;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.items.Bomb;
import dungeonmania.items.ItemEntity;
import dungeonmania.items.Potion;
import dungeonmania.response.models.AnimationQueue;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

public class DungeonManiaController {
    private String goals = "";
    private Player player;
    private String name;
    private String config;
    private Dungeon dungeon = new Dungeon();
    //private List<Direction> movements = new ArrayList<>();
    private HashMap<Integer, Direction> movements = new HashMap<>();
    // to store the original dungeon during time travel

    private int tickOccurred = 0;
    private int tickAtTimeTravel = 0;

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /saves
     */
    public static List<String> saves() {
        return FileLoader.listFileNamesInResourceDirectory("saves");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        name = dungeonName;
        config = configName;

        // Exception when file is not exist
        if (! dungeons().contains(dungeonName) || ! configs().contains(configName)) {
            throw new IllegalArgumentException();
        }
        
        try {
            // read dungeon file
            String dungeonfile = FileLoader.loadResourceFile("dungeons/" + dungeonName + ".json");
            JSONObject json = new JSONObject(dungeonfile);
            // read config file
            String configFile = FileLoader.loadResourceFile("configs/" + configName + ".json");
            JSONObject configJson = new JSONObject(configFile);
            
            // supergoal
            JSONObject supergoal = (JSONObject) json.get("goal-condition");
            goals = (String) supergoal.get("goal");  
            dungeon.setGoals(":" + goals);

            int enemyGoals = configJson.getInt("enemy_goal");
            dungeon.setEnemyGoal(enemyGoals);
        
            dungeon.createNewDungeon(json, configJson);
            
            // add subgoal to the string
            if (supergoal.has("subgoals")) {
                if (supergoal.get("subgoals") instanceof JSONArray) {
                    JSONArray subgoalArray = supergoal.getJSONArray("subgoals");
                    dungeon.addSubgoals(subgoalArray, json);
                }
            }
        
        } catch (IOException e) {
            e.printStackTrace();
        }
        subscribeEnemies();
        dungeon.AddGameState();

        //dungeon.renewHealthBar();
        return getDungeonResponseModel();
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        List<EntityResponse> entitiesList = new ArrayList<>();
        List<ItemResponse> inventoryList = new ArrayList<>();
        List<BattleResponse> battlesList = new ArrayList<>();
        List<String> buildables = dungeon.getReadyToBuild();
        List <Entity> entities = dungeon.getEntities();

        for (Entity entity: entities) {
            entitiesList.add(new EntityResponse(entity.getId(), entity.getType(), entity.getPosition(), entity.isInteractable()));
        }

        for (Entity entity: dungeon.getItems()) {
            entitiesList.add(new EntityResponse(entity.getId(), entity.getType(), entity.getPosition(), entity.isInteractable()));
        }

        player = dungeon.getPlayer();
        if (entities.contains(player)) {
            List <ItemEntity> player_inventory = player.getInventory();
            for (Entity itemEntity: player_inventory) {
                inventoryList.add(new ItemResponse(itemEntity.getId(), itemEntity.getType()));
            }
        }

        for (Enemy enemy : dungeon.getEnemies()) {
            entitiesList.add(new EntityResponse(enemy.getId(), enemy.getType(), enemy.getPosition(), enemy.isInteractable()));
        }

        for (Battle battle: dungeon.getBattles()) {
            battlesList.add(new BattleResponse(battle.getEnemy().getType(), battle.getRoundResponseList(), 
            battle.getInitialPlayerHealth(), battle.getInitialEnemyHealth()));

        }

        if (dungeon.printGoals(dungeon.getSuperGoal()) != null) {
            if (dungeon.getSuperGoal().isFinished(dungeon)) {
                dungeon.setGoals("");
            } else {
                dungeon.setGoals(dungeon.printGoals(dungeon.getSuperGoal()));
            }
        }

        return new DungeonResponse(name, name, entitiesList, inventoryList, battlesList, buildables, dungeon.getGoals());
    }


    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        checkExistPlayer();

        // tick active potions before a new one is consumed
        Player player = dungeon.getPlayer();
        List <ItemEntity> items = player.getInventory();
        // for all items in the player inventory

        for (ItemEntity item: items) {
            // find the item being used, which must be a potion or a bomb
            if (item.getId().equals(itemUsedId)) {
                if (item instanceof Potion) {
                    if (player.consumePotion((Potion) item)) {
                        dungeon.non_player_acions();
                        player.tickPotions();
                        tickOccurred++;
                        dungeon.AddGameState();
                        return getDungeonResponseModel();
                    }
                }
                if (item instanceof Bomb) {
                    dungeon.non_player_acions();
                    player.tickPotions(); // dont move this even though written twice as it must happed at the end of the tick
                    player.placeBomb((Bomb)item, dungeon);
                    tickOccurred++;
                    dungeon.AddGameState();
                    return getDungeonResponseModel();
                } else {
                    throw new IllegalArgumentException("Not in player's inventory");
                }
            }
        } 

        //  tick mind control effect from sceptre
        dungeon.tickSceptre();
        throw new InvalidActionException("Item id does not match a type of item that the player can use.");
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        checkExistPlayer();
        tickOccurred++;
        // Move player
        player = dungeon.getPlayer();
        if (player == null) {return getDungeonResponseModel();}
        player.move(dungeon, movementDirection);

        Player older = dungeon.getOlderPlayer();
        if (older != null) {
            if (tickOccurred > tickAtTimeTravel) {
                dungeon.removeEntity(older);
            }
            if (movements.containsKey(tickOccurred)) {
                older.move(dungeon, movements.get(tickOccurred));
            }
        }
        
        // after player(s) move, check position at portal
        if (player.isTimeTravellingPortal(dungeon)) {
            tickAtTimeTravel = tickOccurred;
            movements.put(tickOccurred, movementDirection);
            if (tickOccurred >= 30) {
                tickOccurred = tickOccurred - 30;
            } else {
                tickOccurred = 0;
            }
            dungeon.rewind(tickOccurred);
        } else {
            movements.put(tickOccurred, movementDirection);
        }
        if (! dungeon.checkPlayer()) {
            return getDungeonResponseModel();
        } 
        
        // Move all enemies
        dungeon.non_player_acions();
        checkExistPlayer();

        checkEnemiesGoal(dungeon);
        player.tickPotions();
        checkEnemiesGoal(dungeon);

        //  tick mind control effect from sceptre
        dungeon.tickSceptre();

        dungeon.AddGameState();
        movements.put(tickOccurred, movementDirection);

        return getDungeonResponseModel();
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {

        player = dungeon.getPlayer();
        try {
            String configFile = FileLoader.loadResourceFile("configs/" + config + ".json");
            JSONObject configJson = new JSONObject(configFile);
            
            if (! buildable.equals("bow") && ! buildable.equals("shield") && ! buildable.equals("sceptre") && ! buildable.equals("midnight_armour")) {
                throw new IllegalArgumentException();
            }

            if (buildable.equals("shield")) {
                if (player.getCount("wood") < 2 || ! (player.containsType("key") || player.containsType("treasure") || player.containsType("sun_stone"))) {
                    throw new InvalidActionException("Not enough item");
                }
            }
            if (buildable.equals("bow")) {
                if (player.getCount("arrow") < 3 || player.getCount("wood") < 1) {
                    throw new InvalidActionException("Not enough item"); 
                }
            }
            if (buildable.equals("sceptre")) {
                int numArrow = player.getCount("arrow");
                int numWood = player.getCount("wood");
                int numTreasure = player.getCount("treasure");
                int numSunStone = player.getCount("sun_stone");
                int numKey = player.getCount("key");

                if (numSunStone < 1) {
                    throw new InvalidActionException("Not enough item"); 
                }
                if (numArrow < 2 && numWood < 1 ) {
                    throw new InvalidActionException("Not enough item"); 
                }

                if (numKey < 1 && numTreasure < 1 && numSunStone < 2) {
                    throw new InvalidActionException("Not enough item"); 
                }
            }
            if (buildable.equals("midnight_armour")) {
                boolean zombies_exist = false;
                 for (Enemy e: dungeon.getEnemies()) {
                    if (e instanceof ZombieToast) {
                        zombies_exist = true;
                        break;
                    }
                }
                if (zombies_exist) {
                    throw new InvalidActionException("There are zombies in the dungeon"); 
                }
                if (player.getCount("sword") < 1 || player.getCount("sun_stone") < 1) {
                    throw new InvalidActionException("Not enough item"); 
                }
            }
            dungeon.createBuildableItem(buildable, configJson, player.getPosition());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return getDungeonResponseModel();
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        for (Enemy e: dungeon.getEnemies()) {
            // If mercenary
            if (e.getId().equals(entityId)) {
                if (e instanceof Mercenary) {
                    Mercenary m = (Mercenary) e;
                    m.bribe(player);
                } 
                if (e instanceof Assassin) {
                    Assassin a = (Assassin) e;
                    a.bribe(player);
                } 
                return getDungeonResponseModel();
            }
        }

        for (Entity e : dungeon.getEntities()) {
            // If spawner
            if (e.getId().equals(entityId)) {
                ZombieToastSpawner s = (ZombieToastSpawner) e;
                s.destroy(dungeon);
                return getDungeonResponseModel();
            }
        }

        throw new IllegalArgumentException("Interact: entityId is not a valid entity ID");
    }
    
    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        
        long timeStamp = System.currentTimeMillis();
        
        // check if the save folder exists
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("saves");

        // get the path of the save folder should be located in
        String path = getClass().getClassLoader().getResource(".").getPath();

        if (inputStream == null) {
            // create the folder if not exist
            File saveDir = new File(path, "saves");
            saveDir.mkdir();
        }

        String filename = name + "-" + timeStamp + ".json";
        String filePath = "";
        try {
            filePath = FileLoader.getPathForNewFile("saves", filename);
        } catch (NullPointerException | IOException e1) {
            e1.printStackTrace();
        }

        try {
            // saving object by using serialization
            SerializationUtils.serialization(dungeon, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return getDungeonResponseModel();
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {

        int found = 0;
        // check if the file starts with the name or equals to the name
        for (String file: allGames()) {
            if (file.startsWith(name) || file.equals(name)) {
                name = file;
                found = 1;
            }
        } 

        if (found == 0) {
            throw new IllegalArgumentException();
        }

        // get the path of the game 
        String filePath = "";
        try {
            filePath = FileLoader.getPathForNewFile("saves", name);
        } catch (NullPointerException | IOException e1) {
            e1.printStackTrace();
        }

        try {
            // deserialize the dungeon object
            dungeon = (Dungeon) SerializationUtils.deserialization(filePath + ".json");
        } catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
        }

        subscribeEnemies();
        return getDungeonResponseModel();
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        return saves();
    }

    /**
     * /game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        if (ticks <= 0) {
            throw new IllegalArgumentException("Invalid tick");
        }
        
        //If the number of ticks have not occurred yet
        if (tickOccurred < ticks ) {
            throw new IllegalArgumentException("Number of ticks have not occurred yet");
        }
        tickAtTimeTravel = tickOccurred;
        tickOccurred = tickOccurred - ticks;
        dungeon.rewind(tickOccurred);
        return getDungeonResponseModel();
    }
    
    
    /**
     * /games/generate
     */
    public DungeonResponse generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String configName) throws IllegalArgumentException{

        if (! configs().contains(configName)) {
            throw new IllegalArgumentException();
        }

        int playerHealth = -1;
        int playerAttack = -1;

        try {
            String configFile = FileLoader.loadResourceFile("configs/" + configName + ".json");
            JSONObject configJson = new JSONObject(configFile);
            playerHealth = configJson.getInt("player_health");
            playerAttack = configJson.getInt("player_attack");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int width = Math.abs(xEnd - xStart) + 2;
        int height = Math.abs(yEnd - yStart) + 2;
        // maze be a 2d array of booleans and default false
        boolean[][] mazeArray = new boolean[width + 1][height + 1];

        // generate map
        DungeonGenerator generator = new DungeonGenerator(mazeArray, width, height);
        mazeArray = generator.generateMaze();

        // add entity to dungeon
        for (int i = 0; i < mazeArray.length; i++) {
            for (int j = 0; j < mazeArray[0].length; j++) {
                if (i == 1 && j == 1) {
                    player = new Player(new Position(xStart, yStart), playerAttack, playerHealth, "player");
                    dungeon.setPlayer(player);
                } else if (i == width - 1 && j == height - 1) {
                    Exit exit = new Exit(new Position(xEnd, yEnd), "exit");
                    dungeon.addEntity(exit);
                } 
                else {
                    if (! mazeArray[i][j]) {
                        Wall wall = new Wall(new Position(i + xStart - 1, j + yStart -1), "wall");
                        dungeon.addEntity(wall);
                    }
                }
            }
        }
        // assume the only goal is exit
        dungeon.setGoals(":exit");
    
        return getDungeonResponseModel();
    }

    // HELPERS
    /*
     * Check if player exists 
     * Return getDungeonResponseModel() if not exist
     */
    public DungeonResponse checkExistPlayer() {
        if (dungeon.checkPlayer()) {
            return null;
        }
        return getDungeonResponseModel();
    }

    /*
     * Let the player subscribe Enemies
     */
    public void subscribeEnemies(){
        List <Enemy> enemies = dungeon.getEnemies();
        for (Enemy enemy: enemies) {
            dungeon.getPlayer().subscribe(enemy);
        }
    }

    /*
     * Keep track with EnemiesGoal
     */
    public void checkEnemiesGoal(Dungeon dungeon) {
        if (dungeon.getEnemyGoals() <= dungeon.getEnemiesKill() && dungeon.getGoals().equals(":enemies")) {
            dungeon.setGoals("");
        }
    }
}
