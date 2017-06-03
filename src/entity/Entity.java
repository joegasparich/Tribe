package entity;

import admin.Game;
import map.CurrentLevel;

/**
 *
 * @author Joe
 * 
 * Base class for anything in the game that has an x,y coord that isn't a tile
 */
public class Entity {
    
    public Game game;
    public CurrentLevel level;
    
    public double x;
    public double y;
    
    //The place on the sprite where the entity is
    public int spriteCentreX;
    public int spriteCentreY;
    
    public String sprite;
    
    public boolean remove = false;
    
    public void update() {}
    
    //Getters and setters
    
    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getCentreX() {
        return spriteCentreX;
    }
    public double getCentreY() {
        return spriteCentreY;
    }
    
    public String getSprite() {
        return sprite;
    }
}
