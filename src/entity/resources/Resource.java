package entity.resources;

import admin.JMath;
import entity.Entity;

/**
 *
 * @author Joe
 * 
 * The base class for all the resource entities. 
 */
public class Resource extends Entity{
    
    public String aliveSprite;
    public String deadSprite;
    
    public int respawnTime = 0;
    public boolean harvestable = true;
    public int timer = -1;
    
    //Resets it to be harvestable after the timer finishes
    @Override
    public void update() {
        if(timer > 0) {
            timer--;
        }
        if(timer == 0) {
            harvestable = true;
            sprite = aliveSprite;
            timer = -1;
        }
    }
    
    public void setHarvestable(Boolean b) {
        if(b) {
            harvestable = true;
        } else {
            harvestable = false;
            sprite = deadSprite;
            timer = JMath.randomInteger(respawnTime - 200, respawnTime + 200);
        }
    }

    public boolean isHarvestable() {
        return harvestable;
    }
}
