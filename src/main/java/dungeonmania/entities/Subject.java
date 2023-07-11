package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.enemy.Subscriber;
import java.io.Serializable;

public interface Subject extends Serializable {
    public List <Subscriber> subscribers = new ArrayList<>();

    // add subcribers
    public void subscribe(Subscriber subscriber);

    public void unsubscribe(Subscriber subscriber);

    // notify subscribers of updates
    public void notifySubscribers();

}
