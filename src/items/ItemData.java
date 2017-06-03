package items;

import static items.ItemData.ProjectileType.*;

/**
 *
 * @author Joe
 * 
 * Static class that contains item data
 */
public class ItemData {

    //Add new items here
    public static enum WeaponEnum { SPEAR, SHORT_BOW, BLOW_GUN }
    public static enum ArmourEnum { TIKI, BONE, CHIEFTAN }
    public static enum ProjectileType { SPEAR, ARROW, BLOW_DART }    
    
    //Add weapon data here
    public static Weapon newWeapon (WeaponEnum weapon) {
        switch(weapon) {
            //                            Weapon(Name, Sprite, Projectile, Damage, Attack Speed, Range)
            case SPEAR: return new Weapon("Spear", "spear", SPEAR, 2, 20, 50);
            case SHORT_BOW: return new Weapon("Short Bow", "short_bow", ARROW, 8, 50, 100);
            case BLOW_GUN: return new Weapon("Blow Gun", "blow_gun", BLOW_DART, 5, 30, 100);
            //More Weapons here
            default: break;
        }
        return null;
    }
    
    public static Armour newArmour (ArmourEnum armour) {
        switch(armour) {
            //                    Armour(Name, Sprite, HP)
            case TIKI: return new Armour("Tiki Armour", "tiki_armour", 5);
            case BONE: return new Armour("Bone Armour", "bone_armour", 5);
            case CHIEFTAN: return new Armour("Chieftan Armour", "chieftan_armour", 5);
            //More Armours here
            default: break;
        }
        return null;
    }
    
    //Add projectile sprite info here
    public static String getProjectileSprite(ProjectileType type) {
        String sprite = null;
        switch(type) {
            case SPEAR: sprite = "spear_projectile"; break;
            case ARROW: sprite = "arrow"; break;
            case BLOW_DART: sprite = "blow_dart"; break;
            default: break;
        }
        return sprite;
    }
}
