package dungeonmania.weapon;

import dungeonmania.enemy.Assassin;
import dungeonmania.enemy.Enemy;
import dungeonmania.enemy.Mercenary;
import dungeonmania.util.Position;

public class Sceptre extends Buildable {
    private int controlDuration = 0;
    private Enemy target = null;

    public Sceptre(Position position, int damage, int controlDuration, int durability, String type) {
        super(position, damage, durability, type);
        this.controlDuration = controlDuration;
    }

    public int getControlDuration() {
        return controlDuration;
    }

    public void setControlDuration(int controlDuration) {
        this.controlDuration = controlDuration;
    }
    public Enemy getTarget() {
        return target;
    }

    public void setTarget(Enemy target) {
        this.target = target;
    }

    public void tickDuration() {
        if (controlDuration == 0) {
            return;
        }
        controlDuration -= 1;
        if (controlDuration == 0) {
            //  finish mind control, enemy is no longer ally
            if (target instanceof Mercenary) {
                Mercenary m = (Mercenary) target;
                m.setHostile(true);
                m.setInteractable(true);
            } 

            if (target instanceof Assassin) {
                Assassin m = (Assassin) target;
                m.setHostile(true);
                m.setInteractable(true);
            } 
        }
    }
}
