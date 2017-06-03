package map;

import admin.Game;
import admin.JMath;
import static map.CurrentLevel.Tile.*;
import static java.lang.Math.*;

/**
 *
 * @author Joe
 * 
 * The level class, calls the level generation, and stores and retrieves information about the level
 */
public class CurrentLevel {
    
    //Size of the map in tiles
    private int width;
    private int height;
    
    private Game game;
    
    //These are the types of tiles
    public enum Tile { SAND, WATER, GRASS, DIRT, STONE }
    public enum ResourceType { TREE, ROCK, BERRY_BUSH }
    
    //Level data is a 2D array of tiles
    private Tile[][] tileArray;
    
    //Constructor
    public CurrentLevel(Game game, int width, int height) {
        this.width = width;
        this.height = height;
        this.game = game;
        tileArray = new Tile[width][height];
        
        //Generate the level
        System.out.println("Generating Tiles");
        tileArray = MapGenerator.generateLevel(game, width, height);
        System.out.println("Generating Entities");
        EntityGenerator.generateEntities(game, this);
    }
    
    //Generates the map
    public void generate() {
        tileArray = MapGenerator.generateLevel(game, width, height);
    }
    
    //Is used for collision checking, converts the x and y value into a tile location and checks if a tile other than empty exists there
    public boolean isPlaceFree(double x, double y) {
        int tileX = (int) floor(x/game.TILE_SIZE);
        int tileY = (int) floor(y/game.TILE_SIZE);
        if(tileArray[tileX][tileY] != WATER) {
            return true;
        } else {
            return false;
        }
    }
    
    //Same as above but for tile x,y coords
    public boolean isTileFree(int x, int y) {
        if(tileArray[x][y] != WATER) {
            return true;
        } else {
            return false;
        }
    }
    
    //Returns the tile at the x, y location (not tile coords)
    public Tile tileAtLocation(double x, double y) {
        int tileX = (int) floor(x/game.TILE_SIZE);
        int tileY = (int) floor(y/game.TILE_SIZE);
        return tileArray[tileX][tileY];
    }
    
    //Returns true if there is water along a line
    public boolean collisionLine(double x1, double y1, double x2, double y2) {
        int count = (int) (2 * JMath.getDistance(x1, y1, x2, y2));
        for(int i = 0; i < count; i++) {
            double xDiff = x2 - x1;
            double yDiff = y2 - y1;
            double distance = Math.sqrt((xDiff * xDiff)+(yDiff * yDiff));
            double xDir = xDiff / distance * (i/2);
            double yDir = yDiff / distance * (i/2);
            if(!isPlaceFree(x1 + xDir, y1 + yDir)) return true;
        }
        return false;
    }

    //Returns the level array
    public Tile[][] getTileArray() {
        return tileArray;
    }

    public int getWidthTiles() {
        return width;
    }

    public int getHeightTiles() {
        return height;
    }

    public int getWidthX() {
        return width * game.TILE_SIZE;
    }

    public int getHeightY() {
        return height * game.TILE_SIZE;
    }
}
