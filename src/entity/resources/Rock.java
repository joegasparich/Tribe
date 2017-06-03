package entity.resources;

/**
 *
 * @author Joe
 * 
 * Rock. Contains some data as well as variables to allow it to be harvested or not
 */
public class Rock extends Resource {

    public Rock(double x, double y) {
        this.x = x;
        this.y = y;
        sprite = "rock";
        aliveSprite = sprite;
        deadSprite = "rock_empty";
        respawnTime = 10000;
        spriteCentreX = 6;
        spriteCentreY = 6;
    }
    
    public void destroy() {
        remove = true;
    }
}
