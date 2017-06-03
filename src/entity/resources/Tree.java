package entity.resources;

/**
 *
 * @author Joe
 * 
 * Tree. Contains some data as well as variables to allow it to be harvested or not
 */
public class Tree extends Resource {

    public Tree(double x, double y) {
        this.x = x;
        this.y = y;
        sprite = "tree";
        aliveSprite = sprite;
        deadSprite = "tree_empty";
        respawnTime = 4000;
        spriteCentreX = 7;
        spriteCentreY = 13;
    }
    
    public void destroy() {
        remove = true;
    }
}
