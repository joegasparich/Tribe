package entity.resources;

/**
 *
 * @author Joe
 * 
 * Berry bush. Contains some data as well as variables to allow it to be harvested or not
 */
public class BerryBush extends Resource {
    
    public BerryBush(double x, double y) {
        this.x = x;
        this.y = y;
        sprite = "berry_bush";
        aliveSprite = sprite;
        deadSprite = "berry_bush_empty";
        respawnTime = 1000;
        spriteCentreX = 8;
        spriteCentreY = 10;
    }
    
    public void destroy() {
        remove = true;
    }
}
