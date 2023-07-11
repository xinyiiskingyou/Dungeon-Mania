package dungeonmania;


import static org.junit.jupiter.api.Assertions.assertEquals;


import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getEntities;

import static dungeonmania.TestUtils.countEntityOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Position;

public class ControllerTest {
    @Test
    @DisplayName("Test dungeonResponse model")
    public void testDungeonResponse() {
       DungeonManiaController controller = new DungeonManiaController();
       DungeonResponse init = controller.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
       DungeonResponse res = controller.getDungeonResponseModel();

       String goals = init.getGoals();
       List<EntityResponse> entitiesList = new ArrayList<>();
       List<String> buildables = new ArrayList<>();
       assertEquals("d_movementTest_testMovementDown", res.getDungeonName());
       assertEquals("d_movementTest_testMovementDown", res.getDungeonId());
       assertEquals(goals, res.getGoals());
       assertEquals(buildables, res.getBuildables());

       // create the expected result
       EntityResponse initPlayer = getPlayer(init).get();
       EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 1), false);
       entitiesList.add(expectedPlayer);
       assertEquals(expectedPlayer, initPlayer);

       EntityResponse exit = getEntities(res, "exit").get(0);
       EntityResponse expected = new EntityResponse(exit.getId(), exit.getType(), new Position(1, 3), false);
       entitiesList.add(expected);
       assertEquals(expected, exit);

       // inventory and battle are expected empty at the start
       List<ItemResponse> inventoryList = new ArrayList<>();
       List<BattleResponse> battlesList = new ArrayList<>();
       assertEquals(inventoryList, res.getInventory());
       assertEquals(battlesList, res.getBattles());
       assertEquals(0, countEntityOfType(res, "arrow"));
       assertEquals(1, countEntityOfType(res, "exit"));
    }

   @Test
   @DisplayName("Test if entityId for interact is not a valid entity ID")
   public void testInvalidIdInteract() {
      DungeonManiaController controller = new DungeonManiaController();
      assertThrows(IllegalArgumentException.class, () -> controller.interact("invalidId"));
   }
}
