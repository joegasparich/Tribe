package entity;

import admin.JMath;
import entity.pathfinding.ClosestHeuristic;
import entity.pathfinding.Path;
import entity.pathfinding.PathFinder;

/**
 *
 * @author Joe
 * 
 * Any class that could use the pathfinding should inherit this
 */
public class Mover extends Entity{
    
    //The amount of time between pathfinds
    public static final int NEW_PATH_TIMER = 500;
    
    public double xDir;
    public double yDir;
    
    //Pathfinding shit
    public PathFinder pf;
    public Path path;
    public ClosestHeuristic heuristic = new ClosestHeuristic();
    public int step = 0;
    public int newPath = 0; 
    
    public double moveSpeed = 0.5;
    
    public Entity entityTarget = null;

    public Mover() {
    }
    
    //Finds a new path
    public void findNewPath() {
        step = 0;
        path = pf.findPath((int) x, (int) y, (int) entityTarget.getX(), (int) entityTarget.getY());
    }
    
    //Moves directly to location
    public void moveToLocation(double tx, double ty) {
        double xDiff = tx - x;
        double yDiff = ty - y;
        double distance = Math.sqrt((xDiff * xDiff)+(yDiff * yDiff));
        xDir = xDiff / distance * moveSpeed;
        yDir = yDiff / distance * moveSpeed;
        x += xDir;
        y += yDir;
        
        if(JMath.getDistance(x, y, tx, ty) < 1) {
            step++;
            if(step >= path.getLength()) {
                path = null;
            }
        }
    }
    
    //Moves directly to target entity's location
    public void moveToTarget(Entity target) {
        double xDiff = target.getX() - x;
        double yDiff = target.getY() - y;
        double distance = Math.sqrt((xDiff * xDiff)+(yDiff * yDiff));
        xDir = xDiff / distance * moveSpeed;
        yDir = yDiff / distance * moveSpeed;
        x += xDir;
        y += yDir;
    }

    public Entity getEntityTarget() {
        return entityTarget;
    }
}
