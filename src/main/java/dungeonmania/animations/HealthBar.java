package dungeonmania.animations;

import java.util.Arrays;

import dungeonmania.util.Direction;
import dungeonmania.Dungeon;
import dungeonmania.response.models.AnimationQueue;

public class HealthBar{
    // private String playerId;
    // private String colour;
    // public HealthBar(Dungeon dungeon) {
    //     this.colour = "0x00FF00";
    //     this.playerId = dungeon.getPlayer().getId();
    //     moveHealthBar(dungeon, null);

    // }

    // // add health bar for potions

    // // public void staticHealthBar(Dungeon dungeon) {
    // //     if (!dungeon.checkPlayer()) {return;}
    // //     Double health_percent = dungeon.getPlayerHealthPercent();
    // //     setColour(health_percent);
    // //     dungeon.getAnimations().add(new AnimationQueue("PostTick", getPlayer_id(), Arrays.asList(
    // //             "healthbar set " + health_percent, "healthbar tint " + getColour()
    // //         ), false, 1));
    // // }

    // // move health bar
    // // translate back to old position (opposite of movement translation direction) over 0s, then move to new position over 1 second

    // // use already existing health bar, don't create new one, just have a modify health bar method

    // // once have this change the colour according to the health
    // public void moveHealthBar(Dungeon dungeon, Direction movementDirection) {
    //     if (!dungeon.checkPlayer()) {return;}
    //     double health_percent = dungeon.getPlayerHealthPercent();
    //     setColour(health_percent);
    //     double goBack = -0.2; // moved direction down or right
    //     String axis = "y";
    //     double moveTo = 0.2; // moving down or right
    //     if ((movementDirection != null) && (movementDirection.equals(Direction.UP) || movementDirection.equals(Direction.LEFT))) {
    //         // moving up or left
    //         goBack = 0.2;
    //         moveTo = -0.2;
    //     }
    //     if ((movementDirection != null) && (movementDirection.equals(Direction.LEFT) || (movementDirection.equals(Direction.RIGHT)))) {
    //         axis = "x";
    //     }
    //     if (movementDirection == null) {
    //         moveTo = 0;
    //         goBack = 0;
    //     }
    //     dungeon.getAnimations().add(new AnimationQueue("PostTick", getPlayer_id(), Arrays.asList(
    //             "healthbar set " + health_percent, "healthbar tint " + getColour(), "translate-" + axis +" " + goBack + ", over 0s", "translate-" + axis +" " + moveTo + ", over 0.25s", getPlayer_id()
    //         ), false, 0.12));
    // }

    // public String getPlayer_id() {
    //     return playerId;
    // }
    // public void setPlayer_id(String playerId) {
    //     this.playerId = playerId;
    // }
    // public String getColour() {
    //     return colour;
    // }
    // public void setColour(Double health_percent) {
    //     if (health_percent >= 0.5) {
    //         // bright green
    //         this.colour = "0x00FF00"; 
    //     }
    //     else if (health_percent >= 0.25) {
    //         // orange health bar
    //         this.colour = "0xFF9900";
    //     }
    //     else if (health_percent < 0.25) {
    //         // red health bar
    //         this.colour = "0xFF0000";
    //     }
    // }

}

