package items;

/**
 *
 * @author Rory
 * 
 * Contains info on weapon, gets drawn on wearer
 */
public class Armour extends Item {  
    
    //Data
    private String name;
    private String sprite;
    private String groundSprite;
    private int hp;

    //Constructor
    public Armour(String name, String sprite, int hp) {
        this.name = name;
        this.sprite = sprite;
        StringBuilder sb = new StringBuilder();
        sb.append(sprite);
        sb.append("_ground");
        this.groundSprite = sb.toString();
        this.hp = hp;
    }

    //Getters and Setters
    @Override
    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    @Override
    public String getSprite() {
        return sprite;
    }
    
    @Override
    public String getGroundSprite() {
        return groundSprite;
    }
}
