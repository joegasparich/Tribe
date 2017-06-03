package entity.pathfinding;

/**
 *
 * @author Joe
 * 
 * Fuck knows what most of this shit does I just copied it from the internet
 * This is a node in the pathfinder algorithm. It has like a cost and a depth or something so the pathfinder can find
 * a path through the nodes with the lowest cost
 */
public class Node implements Comparable{
    
    private int x;
    private int y;
    private float cost;
    private Node parent;
    private float heuristic;
    private int depth;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int setParent(Node parent) {
        if(parent == null) {
            return 0;
        } else {
            depth = parent.getDepth() + 1;
            this.parent = parent;
            return depth;
        }
    }
    
    public int compareTo(Object other) {
        Node o = (Node) other;
			
        float f = heuristic + cost;
        float of = o.heuristic + o.cost;

        if (f < of) {
                return -1;
        } else if (f > of) {
                return 1;
        } else {
                return 0;
        }
    }

    public float getCost() {
        return cost;
    }

    public float getHeuristic() {
        return heuristic;
    }

    public int getDepth() {
        return depth;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setHeuristic(float heuristic) {
        this.heuristic = heuristic;
    }

    public Node getParent() {
        return parent;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
