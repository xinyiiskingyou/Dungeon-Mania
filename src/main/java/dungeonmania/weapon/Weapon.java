package dungeonmania.weapon;

public interface Weapon {

    public double getDamage();

    public void setDamage(double damage);

    public String getType();

    public void setType(String type);

    public int getDurability();

    public void setDurability(int durability);

    public String getId();

    public void setId(String id);

    public void decreaseDurability();
}