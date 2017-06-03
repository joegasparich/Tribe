package map;

import admin.Game;
import admin.JMath;
import javax.swing.JOptionPane;
import map.CurrentLevel.Tile;
import static map.CurrentLevel.Tile.*;

/**
 *
 * @author Joe
 * 
 * Generates the map
 */
public class MapGenerator {
    
    //Careful of setting the fraction too small or the extra landmass too high incase you get out of bounds
    public static final int MAIN_LANDMASS_SIZE = 40; //Radius of main landmass
    public static final int EXTRA_LANDMASS_COUNT_MIN = 5; //Amount of extra landmasses
    public static final int EXTRA_LANDMASS_COUNT_MAX = 8;
    public static final int EXTRA_LANDMASS_SIZE_MIN = 30; //Radius of extra landmasses
    public static final int EXTRA_LANDMASS_SIZE_MAX = 40;
    public static final int EXTRA_LANDMASS_DIST = 10; //Distance from the radius of the main landmass to spawn extra landmasses
    public static final int EROSIONS = 100; //Amount of times the land is eroded
    public static final int EROSION_CHANCE = 30; //Chance is 1 in x + 1
    public static final int BEACH_SIZE = 15; //Distance sand spawns from the shore
    
    //simplex noise for patches
    public static OpenSimplexNoise noiseSand;
    public static OpenSimplexNoise noiseDirt;
    public static OpenSimplexNoise noiseStone;
    public static OpenSimplexNoise noiseWater;
    
    public static Tile[][] generateLevel(Game game, int width, int height) {
        
        //Warn if the settings are too big and will cause null pointer errors
        if(MAIN_LANDMASS_SIZE + EXTRA_LANDMASS_DIST + EXTRA_LANDMASS_SIZE_MAX > width/2 - 10) {
            JOptionPane.showMessageDialog(null, "Message", "Land has been set too big for this map size", JOptionPane.ERROR_MESSAGE);
            System.out.println("Land is too big");
        }
        
        //The array of tiles
        Tile[][] tileArray = new Tile[width][height];
        
        //Fill the level with water tiles
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                tileArray[i][j] = WATER;
            }
        }
        //Main Landmass
        tileArray = generateCircle(tileArray, width/2, height/2, MAIN_LANDMASS_SIZE, GRASS);
        //Extra Landmasses
        for(int i = 0; i < JMath.randomInteger(EXTRA_LANDMASS_COUNT_MIN, EXTRA_LANDMASS_COUNT_MAX); i++) {
            double angle = JMath.randomDouble(0, 360);
            int size = JMath.randomInteger(EXTRA_LANDMASS_SIZE_MIN, EXTRA_LANDMASS_SIZE_MAX);
            generateCircle(tileArray, (int) (width/2 + (MAIN_LANDMASS_SIZE+EXTRA_LANDMASS_DIST) * (Math.sin(angle))), (int) (height/2 + (MAIN_LANDMASS_SIZE+EXTRA_LANDMASS_DIST) * (Math.cos(angle))), size, GRASS);
        }
        //Erode and clean
        tileArray = smoothTerrain(tileArray, width, height); //Fixes the incomplete circles DO NOT REMOVE or the level will be fucked as
        tileArray = erodeTerrain(tileArray, width, height, EROSIONS);
        //Should consolidate these so that I can specify how many times I want it to repeat
        tileArray = smoothTerrain(tileArray, width, height);
        tileArray = smoothTerrain(tileArray, width, height);
        tileArray = smoothTerrain(tileArray, width, height);
        //Spawn beaches
        tileArray = spawnBeaches(tileArray, width, height, BEACH_SIZE);
        //Generate patches of dirt and sand and lakes
        noiseSand = new OpenSimplexNoise(JMath.randomInteger(0, 10000)); //increase these to gazillions
        noiseDirt = new OpenSimplexNoise(JMath.randomInteger(0, 10000));
        noiseStone = new OpenSimplexNoise(JMath.randomInteger(0, 10000));
        noiseWater = new OpenSimplexNoise(JMath.randomInteger(0, 10000));
        tileArray = generatePatches(tileArray, width, height);
        
        return tileArray;
    }
    
    //Uses Midpoint Circle Algorithm from Wikipedia, only makes a circle outline so the filled circle is 
    //filled with holes so you have to fill the holes with the smoothTerrain method.
    private static Tile[][] generateCircle(Tile[][] tileArray, int x0, int y0, int radius, Tile type) {
        for(int i = 0; i < radius + 1; i++) {
            int x = i;
            int y = 0;
            int err = 0;

            while(x >= y) {
                tileArray[(int) (x0 + x)][(int) (y0 + y)] = type;
                tileArray[(int) (x0 + y)][(int) (y0 + x)] = type;
                tileArray[(int) (x0 - y)][(int) (y0 + x)] = type;
                tileArray[(int) (x0 - x)][(int) (y0 + y)] = type;
                tileArray[(int) (x0 - x)][(int) (y0 - y)] = type;
                tileArray[(int) (x0 - y)][(int) (y0 - x)] = type;
                tileArray[(int) (x0 + y)][(int) (y0 - x)] = type;
                tileArray[(int) (x0 + x)][(int) (y0 - y)] = type;

                y++;
                err += 1 + 2*y;
                if(2*(err-x) + 1 > 0) {
                    x -= 1;
                    err += (1 - 2*x);
                }
            }
        }
        return tileArray;
    }
    
    //Recursively erodes with a chance to destroy sand touching water
    private static Tile[][] erodeTerrain(Tile[][] tileArray, int width, int height, int iterations) {
        int[][] toErode = new int[width][height];
        
        if(iterations > 0) {
            erodeTerrain(tileArray, width, height, iterations - 1);
        
            for(int i = 1; i < width-1; i++) {
                for(int j = 1; j < height-1; j++) {
                    //If next to water
                    int waterCount = 0;
                    if(tileArray[i+1][j] == WATER) waterCount +=1;
                    if(tileArray[i-1][j] == WATER) waterCount +=1;
                    if(tileArray[i][j+1] == WATER) waterCount +=1;
                    if(tileArray[i][j-1] == WATER) waterCount +=1;
                    if(tileArray[i][j] == GRASS && waterCount > 0) {
                        //Set to remove with a chance
                        if(JMath.randomInteger(0, EROSION_CHANCE ) == 0) {
                            toErode[i][j] = 1;
                        }
                    }
                }
            }
            //Remove those set to remove, has to be set to remove otherwise the loop just erodes the whole island tile by tile
            for(int i = 1; i < width-1; i++) {
                for(int j = 1; j < height-1; j++) {
                    if(toErode[i][j] == 1) {
                        tileArray[i][j] = WATER;
                    }
                }
            }
        }
        return tileArray;
    }
    
    //Fills holes and outcroppings of sand, as well as jaggy corners
    private static Tile[][] smoothTerrain(Tile[][] tileArray, int width, int height) {
        //Fix holes of water
        for(int i = 1; i < width-1; i++) {
            for(int j = 1; j < height-1; j++) {
                int waterCount = 0;
                if(tileArray[i+1][j] == WATER) waterCount +=1;
                if(tileArray[i-1][j] == WATER) waterCount +=1;
                if(tileArray[i][j+1] == WATER) waterCount +=1;
                if(tileArray[i][j-1] == WATER) waterCount +=1;
                if(tileArray[i][j] == WATER && waterCount < 2) tileArray[i][j] = GRASS;
            }
        }
        //Fix outcroppings / individual tiles of grass
        for(int i = 1; i < width-1; i++) {
            for(int j = 1; j < height-1; j++) {
                int waterCount = 0;
                if(tileArray[i+1][j] == WATER) waterCount +=1;
                if(tileArray[i-1][j] == WATER) waterCount +=1;
                if(tileArray[i][j+1] == WATER) waterCount +=1;
                if(tileArray[i][j-1] == WATER) waterCount +=1;
                if(tileArray[i][j] == GRASS && waterCount > 2) tileArray[i][j] = WATER;
            }
        }
        //Fix hard corners
        for(int i = 1; i < width-1; i++) {
            for(int j = 1; j < height-1; j++) {
                if(tileArray[i][j] == GRASS) {
                    if(tileArray[i-1][j] == GRASS && tileArray[i][j+1] == GRASS && tileArray[i+1][j] == WATER && tileArray[i+1][j+1] == WATER && tileArray[i][j-1] == WATER && tileArray[i-1][j-1] == WATER) tileArray[i][j] = WATER;
                    if(tileArray[i+1][j] == GRASS && tileArray[i][j+1] == GRASS && tileArray[i-1][j] == WATER && tileArray[i-1][j+1] == WATER && tileArray[i][j-1] == WATER && tileArray[i+1][j-1] == WATER) tileArray[i][j] = WATER;
                    if(tileArray[i-1][j] == GRASS && tileArray[i][j-1] == GRASS && tileArray[i+1][j] == WATER && tileArray[i+1][j-1] == WATER && tileArray[i][j-1] == WATER && tileArray[i-1][j+1] == WATER) tileArray[i][j] = WATER;
                    if(tileArray[i+1][j] == GRASS && tileArray[i][j-1] == GRASS && tileArray[i-1][j] == WATER && tileArray[i-1][j-1] == WATER && tileArray[i][j+1] == WATER && tileArray[i+1][j+1] == WATER) tileArray[i][j] = WATER;
                }
            }
        }
        return tileArray;
    }
    
    //Spawns beaches of a certain size around the island
    private static Tile[][] spawnBeaches(Tile[][] tileArray, int width, int height, int iterations) {
        int[][] toSand = new int[width][height];
        if(iterations > 0) {
            spawnBeaches(tileArray, width, height, iterations - 1);
            for(int i = 1; i < width-1; i++) {
                for(int j = 1; j < height-1; j++) {
                    if(tileArray[i][j] == GRASS) {
                        int sandCount = 0;
                        if(tileArray[i+1][j] == SAND || tileArray[i+1][j] == WATER) sandCount +=1;
                        if(tileArray[i-1][j] == SAND || tileArray[i-1][j] == WATER) sandCount +=1;
                        if(tileArray[i][j+1] == SAND || tileArray[i][j+1] == WATER) sandCount +=1;
                        if(tileArray[i][j-1] == SAND || tileArray[i][j-1] == WATER) sandCount +=1;
                        if(tileArray[i][j] == GRASS && sandCount > 0) toSand[i][j] = 1;
                    }
                }
            }
            for(int i = 1; i < width-1; i++) {
                for(int j = 1; j < height-1; j++) {
                    if(toSand[i][j] == 1) {
                        tileArray[i][j] = SAND;
                    }
                }
            }
        }
        return tileArray;
    }
    
    //Uses simplex noise to generate patches
    private static Tile[][] generatePatches(Tile[][] tileArray, int width, int height) {
        for(int i = 1; i < width-1; i++) {
            for(int j = 1; j < height-1; j++) {
                //Lakes
                double value = noiseWater.eval(i/50d, j/50d);
                if(value > 0.65) {
                    if(tileArray[i][j] == GRASS || tileArray[i][j] == SAND || tileArray[i][j] == DIRT) {
                        tileArray[i][j] = WATER;
                    }
                }
                //Lake beaches
                else if(value > 0.62) {
                    if(tileArray[i][j] == GRASS) {
                        tileArray[i][j] = SAND;
                    }
                }
                //Stone(ore deposits)
                value = noiseStone.eval(i/15d, j/15d);
                if(value > 0.75) {
                    if(tileArray[i][j] == GRASS) {
                        tileArray[i][j] = STONE;
                    }
                }
                //Sand
                value = noiseSand.eval(i/20d, j/20d);
                if(value > 0.65) {
                    if(tileArray[i][j] == GRASS) {
                        tileArray[i][j] = SAND;
                    }
                }
                //Dirt
                value = noiseDirt.eval(i/20d, j/20d);
                if(value > 0.65) {
                    if(tileArray[i][j] == GRASS) {
                        tileArray[i][j] = DIRT;
                    }
                }
            }
        }
        return tileArray;
    }
}
