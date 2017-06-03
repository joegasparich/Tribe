/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import admin.Game;
import items.Armour;
import items.Item;
import items.Weapon;

/**
 *
 * @author Joe
 * 
 * Is the entity for the ground form of items
 */
public class GroundItem extends Entity{
    private Item item;
    private Player player;

    public GroundItem(Game game, double x, double y, Item item) {
        this.game = game;
        this.x = x;
        this.y = y;
        this.item = item;
        player = game.getPlayer();
        sprite = item.getGroundSprite();
        spriteCentreX = game.getSprites().get(sprite).getHeight()/2;
        spriteCentreY = game.getSprites().get(sprite).getWidth()/2;
    }
    
    //Swaps the item with the one in the players inventory
    public void pickUp() {
        if(item instanceof Weapon) {
            game.spawnItem(x, y, player.getWeapon());
            player.setWeapon((Weapon) item);
            remove = true;
        } else if(item instanceof Armour) {
            game.spawnItem(x, y, player.getArmour());
            player.setArmour((Armour) item);
            remove = true;
        }
    }
}
