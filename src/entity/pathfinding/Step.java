package entity.pathfinding;

/**
 *
 * @author Joe
 * 
 * Fuck knows what most of this shit does I just copied it from the internet
 * A single step in the path
 */
public class Step {
    
    private int x;
    private int y;
    
    public Step(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public int hashCode() {
        return x*y;
    }
    
    public boolean equals(Step step) {
        return (step.getX() == x) && (step.getY() == y);
    }
    
    public String toString() {
        return x + " " + y;
    }
}
