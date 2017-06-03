/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package init;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;


/**
 *
 * @author Joe
 * 
 * Reads all the sprites from files and puts them in a map
 */
public class InitSprites {
    
    private static String path = "resources/";
    
    public static HashMap initSprites() {
        HashMap<String, BufferedImage> sprites = new HashMap();
        
        //Add new sprites in here
        try {
            sprites.put("food_resource", ImageIO.read(new File(path + "food_resource.png")));
            sprites.put("wood_resource", ImageIO.read(new File(path + "wood_resource.png")));
            sprites.put("stone_resource", ImageIO.read(new File(path + "stone_resource.png")));
            sprites.put("button_cut", ImageIO.read(new File(path + "button_cut.png")));
            sprites.put("button_mine", ImageIO.read(new File(path + "button_mine.png")));
            sprites.put("button_gather", ImageIO.read(new File(path + "button_gather.png")));
            sprites.put("button_hunt", ImageIO.read(new File(path + "button_hunt.png")));
            sprites.put("button_haul", ImageIO.read(new File(path + "button_haul.png")));
            sprites.put("button_asdf", ImageIO.read(new File(path + "button_asdf.png")));
            
            sprites.put("character", ImageIO.read(new File(path + "character.png")));
            
            sprites.put("spear", ImageIO.read(new File(path + "spear.png")));
            sprites.put("spear_ground", ImageIO.read(new File(path + "spear_ground.png")));
            sprites.put("spear_projectile", ImageIO.read(new File(path + "spear_projectile.png")));
            sprites.put("short_bow", ImageIO.read(new File(path + "short_bow.png")));
            sprites.put("short_bow_ground", ImageIO.read(new File(path + "short_bow_ground.png")));
            sprites.put("arrow", ImageIO.read(new File(path + "arrow.png")));
            sprites.put("blow_gun", ImageIO.read(new File(path + "blow_gun.png")));
            sprites.put("blow_gun_ground", ImageIO.read(new File(path + "blow_gun_ground.png")));
            sprites.put("blow_dart", ImageIO.read(new File(path + "blow_dart.png")));
            
            sprites.put("tiki_armour", ImageIO.read(new File(path + "tiki_armour.png")));
            sprites.put("tiki_armour_ground", ImageIO.read(new File(path + "tiki_armour_ground.png")));
            sprites.put("bone_armour", ImageIO.read(new File(path + "bone_armour.png")));
            sprites.put("bone_armour_ground", ImageIO.read(new File(path + "bone_armour_ground.png")));
            sprites.put("chieftan_armour", ImageIO.read(new File(path + "chieftan_armour.png")));
            sprites.put("chieftan_armour_ground", ImageIO.read(new File(path + "chieftan_armour_ground.png")));
            
            sprites.put("campfire", ImageIO.read(new File(path + "campfire.png")));
            sprites.put("hut", ImageIO.read(new File(path + "hut.png")));
            
            sprites.put("tribe_member_male", ImageIO.read(new File(path + "tribe_member_male.png")));
            sprites.put("tribe_member_female", ImageIO.read(new File(path + "tribe_member_female.png")));
            sprites.put("colonist", ImageIO.read(new File(path + "colonist.png")));
            
            sprites.put("moa", ImageIO.read(new File(path + "moa.png")));
            sprites.put("moa_dead", ImageIO.read(new File(path + "moa_dead.png")));
            sprites.put("deer", ImageIO.read(new File(path + "deer.png")));
            sprites.put("deer_dead", ImageIO.read(new File(path + "deer_dead.png")));
            sprites.put("boar", ImageIO.read(new File(path + "boar.png")));
            sprites.put("boar_dead", ImageIO.read(new File(path + "boar_dead.png")));
            sprites.put("rabbit", ImageIO.read(new File(path + "rabbit.png")));
            sprites.put("rabbit_dead", ImageIO.read(new File(path + "rabbit_dead.png")));
            
            sprites.put("sand", ImageIO.read(new File(path + "sand.png")));
            sprites.put("water", ImageIO.read(new File(path + "water.png")));
            sprites.put("grass", ImageIO.read(new File(path + "grass.png")));
            sprites.put("dirt", ImageIO.read(new File(path + "dirt.png")));
            sprites.put("stone", ImageIO.read(new File(path + "stone.png")));
            
            sprites.put("tree", ImageIO.read(new File(path + "tree.png")));
            sprites.put("tree_empty", ImageIO.read(new File(path + "tree_empty.png")));
            sprites.put("rock", ImageIO.read(new File(path + "rock.png")));
            sprites.put("rock_empty", ImageIO.read(new File(path + "rock_empty.png")));
            sprites.put("berry_bush", ImageIO.read(new File(path + "berry_bush.png")));
            sprites.put("berry_bush_empty", ImageIO.read(new File(path + "berry_bush_empty.png")));
            
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return sprites;
    }
}
