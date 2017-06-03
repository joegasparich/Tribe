package items;

import items.ItemData.ProjectileType;

/**
 *
 * @author Joe
 * 
 * Contains info on weapon, gets drawn on holder
 */
public class Weapon extends Item {
    
    //Data
    private String name;
    private String sprite;
    private String groundSprite;
    private ProjectileType type;
    private int damage;
    private int attackSpeed;
    private int range;

    //Constructor
    public Weapon(String name, String sprite, ProjectileType type, int damage, int attackSpeed, int range) {
        this.name = name;
        this.sprite = sprite;
        StringBuilder sb = new StringBuilder();
        sb.append(sprite);
        sb.append("_ground");
        this.groundSprite = sb.toString();
        this.type = type;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.range = range;
    }

    //Getters and Setters
    @Override
    public String getName() {
        return name;
    }

    public ProjectileType getType() {
        return type;
    }

    public int getDamage() {
        return damage;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public int getRange() {
        return range;
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
