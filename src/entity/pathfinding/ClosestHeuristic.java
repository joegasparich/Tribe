package entity.pathfinding;

/**
 *
 * @author Joe
 * 
 * Fuck knows what most of this shit does I just copied it from the internet
 * I think this is used to find the cost of going from one node to the next
 */
public class ClosestHeuristic {
    public float getCost(int x, int y, int tx, int ty) {		
        float dx = tx - x;
        float dy = ty - y;

        float result = (float) (Math.sqrt((dx*dx)+(dy*dy)));

        return result;
    }
}
