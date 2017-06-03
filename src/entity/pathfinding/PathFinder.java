package entity.pathfinding;

import admin.Game;
import java.util.ArrayList;
import map.CurrentLevel;
import map.CurrentLevel.Tile;
/**
 *
 * @author Joe
 * 
 * Fuck knows what most of this shit does I just copied it from the internet
 * I have very little idea how this works, but I think it goes through the tiles, creating nodes with a cost until it finds
 * the target and returns the path through the nodes that has the lowest cost
 */
public class PathFinder {
    
    private ArrayList closed = new ArrayList();
    private SortedList open = new SortedList();
    
    private CurrentLevel level;
    private Node[][] nodes;
    private int maxSearchDistance;
    private boolean allowDiagMovement;
    private ClosestHeuristic heuristic;

    public PathFinder(CurrentLevel level, Tile[][] tileArray, int maxSearchDistance, boolean allowDiagMovement, ClosestHeuristic heuristic) {
        this.level = level;
        this.maxSearchDistance = maxSearchDistance;
        this.allowDiagMovement = allowDiagMovement;
        this.heuristic = heuristic;
        nodes = new Node[level.getWidthTiles()][level.getHeightTiles()];
        for(int i = 0; i < tileArray.length; i++) {
            for(int j = 0; j < tileArray[i].length; j++) {
                nodes[i][j] = new Node(i, j);
            }
        }
    }
    
    public Path findPath(int sx, int sy, int tx, int ty) {
        sx /= Game.TILE_SIZE;
        sy /= Game.TILE_SIZE;
        tx /= Game.TILE_SIZE;
        ty /= Game.TILE_SIZE;
        
        if(!level.isTileFree(tx, ty)) {
            return null;
        }
        
        nodes[sx][sy].setCost(0);
        nodes[sx][sy].setDepth(0);
        closed.clear();
        open.clear();
        open.add(nodes[sx][sy]);
        
        nodes[tx][ty].setParent(null);
        
        int maxDepth = 0;
        while(maxDepth < maxSearchDistance && open.size() != 0) {
            Node current = getFirstInOpen();
            if(current == nodes[tx][ty]) break;
            
            removeFromOpen(current);
            addToClosed(current);
            
            for(int x = -1; x < 2; x++) {
                for(int y = -1; y < 2; y++) {
                    if(x == 0 && y == 0) continue;
                    if(!allowDiagMovement && x != 0 && y != 0) continue;
                    
                    int xp = x + current.getX();
                    int yp = y + current.getY();
                    
                    if(isValidLocation(sx, sy, xp, yp)) {
                        float nextStepCost = current.getCost() + 1;
                        Node neighbour = nodes[xp][yp];
                        if(nextStepCost < neighbour.getCost()) {
                            if(inOpenList(neighbour)) removeFromOpen(neighbour);
                            if(inClosedList(neighbour)) removeFromClosed(neighbour);
                        }
                        if(!inOpenList(neighbour) && !inClosedList(neighbour)) {
                            neighbour.setCost(nextStepCost);
                            neighbour.setHeuristic(heuristic.getCost(xp, yp, tx, ty));
                            maxDepth = Math.max(maxDepth, neighbour.setParent(current));
                            addToOpen(neighbour);
                        }
                    }
                }
            }
        }
        if(nodes[tx][ty].getParent() == null) {
            return null;
        }
        
        Path path = new Path();
        Node target = nodes[tx][ty];
        while(target != nodes[sx][sy]) {
            path.prependStep(target.getX(), target.getY());
            target = target.getParent();
        }
        path.prependStep(sx, sy);
        
        return path;
    }
    
    private Node getFirstInOpen() {
        return (Node) open.first();
    }
    
    private void addToOpen(Node node) {
        open.add(node);
    }
    
    private boolean inOpenList(Node node) {
        return open.contains(node);
    }
    
    private void removeFromOpen(Node node) {
        open.remove(node);
    }
    
    private void addToClosed(Node node) {
        closed.add(node);
    }
    
    private boolean inClosedList(Node node) {
        return closed.contains(node);
    }
    
    private void removeFromClosed(Node node) {
        closed.remove(node);
    }
    
    private boolean isValidLocation(int sx, int sy, int x, int y) {
        boolean invalid = (x < 0) || (y < 0) || (x >= level.getWidthTiles() || (y >= level.getHeightTiles()));
        if(!invalid && (sx != x || sy != y)) {
            invalid = !level.isTileFree(x, y);
        }
        return !invalid;
    }
    
    public float getHeuristicCost(int x, int y, int tx, int ty) {
        return heuristic.getCost(x, y, tx, ty);
    }
}
