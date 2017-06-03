package entity.pathfinding;

import java.util.ArrayList;

/**
 *
 * @author Joe
 * 
 * Fuck knows what most of this shit does I just copied it from the internet
 * This is a path that the pathfinder returns. It's a list of steps, or locations to follow
 * Could potentially be reused if we want to have something follow some sort of path.
 */
public class Path {
    
    private ArrayList steps = new ArrayList();
    
    public Path() {}
    
    public int getLength() {
        return steps.size();
    }
    
    public Step getStep(int index) {
        return (Step) steps.get(index);
    }
    
    public int getX(int index) {
        return getStep(index).getX();
    }
    
    public int getY(int index) {
        return getStep(index).getY();
    }
    
    public void appendStep(int x, int y) {
        steps.add(new Step(x, y));
    }
    
    public void prependStep(int x, int y) {
        steps.add(0, new Step(x, y));
    }
    
    public boolean contains(int x, int y) {
        return steps.contains(new Step(x, y));
    }
    
    public String toString() {
        String s = "";
        for(int i = 0; i < getLength(); i++) {
            s = s.concat(getStep(i).toString() + ", ");
        }
        return s;
    }
}