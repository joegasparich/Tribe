package map;

import admin.Game;
import admin.JMath;
import java.util.ArrayList;
import map.CurrentLevel.ResourceType;
import static map.CurrentLevel.ResourceType.*;
import map.CurrentLevel.Tile;
import static map.CurrentLevel.Tile.*;
import entity.resources.BerryBush;
import entity.resources.Rock;
import entity.resources.Tree;

/**
 *
 * @author Joe
 * 
 * Generates all of the resources on the map, may eventually gen other entities, so I named it broadly
 */
public class EntityGenerator {
    
    //Currently only generates resources
    public static void generateEntities(Game game, CurrentLevel level) {
        generateResources(game, level);
    }
    
    //Generate map resources
    private static void generateResources(Game game, CurrentLevel level) {
        //Generate trees on grass and sand
        System.out.println("Generating Trees");
        ArrayList<Tile> tiles = new ArrayList<>();
        tiles.add(GRASS); tiles.add(SAND);
        generateClumps(game, level, TREE, tiles, 100, 9, 10, 150, 5);
        //Generate bushes on grass
        System.out.println("Generating Bushes");
        tiles.clear();
        tiles.add(GRASS);
        generateClumps(game, level, BERRY_BUSH, tiles, 40, 6, 5, 100, 10);
        //Generate rocks (Uses different generator)
        System.out.println("Generating Stone");
        generateRocks(game, level, game.TILE_SIZE);
    }
    
    //Generates clumps of resources, and scatters any remaining resources randomly
    private static void generateClumps(Game game, CurrentLevel level, ResourceType resourceType, ArrayList<Tile> tiles, int amount, int clumps, int clumpSize, int separation, int minDist) {
        int amountSpawned = 0;
        for(int i = 0; i < clumps; i++) {
            double clumpX;
            double clumpY;
            //Loop until suitable clump location is found (ie. not on water and on preferred tile)
            do {
                clumpX = JMath.randomDouble(0, game.LEVEL_SIZE_X * game.TILE_SIZE - 1);
                clumpY = JMath.randomDouble(0, game.LEVEL_SIZE_Y * game.TILE_SIZE - 1);
            } while(!level.isPlaceFree(clumpX, clumpY) || game.isEntityLocation(clumpX, clumpY, separation/2) || !tiles.contains(level.tileAtLocation(clumpX, clumpY)));
            for(int j = 0; j < Math.max(JMath.randomInteger(clumpSize - 3, clumpSize + 3), 0); j++) {
                double placeX;
                double placeY;
                //loop until suitable resource location is found (ie. not on water and on preferred tile and not ontop of another entity)
                do {
                    placeX = clumpX + JMath.randomDouble(-separation/2, separation/2);
                    placeY = clumpY + JMath.randomDouble(-separation/2, separation/2);
                } while(!level.isPlaceFree(placeX, placeY) || game.isEntityLocation(placeX, placeY, minDist) || !tiles.contains(level.tileAtLocation(placeX, placeY)));
                amountSpawned++;
                if(amountSpawned < amount) {
                    if(resourceType == TREE) game.addEntity(new Tree(placeX, placeY));
                    if(resourceType == ROCK) game.addEntity(new Rock(placeX, placeY));
                    if(resourceType == BERRY_BUSH) game.addEntity(new BerryBush(placeX, placeY));
                }
            }
        }
        //Scatter individuals
        for(int i = 0; i < amount - amountSpawned; i++) {
            double placeX;
            double placeY;
            do {
                placeX = JMath.randomDouble(0, game.LEVEL_SIZE_X * game.TILE_SIZE - 1);
                placeY = JMath.randomDouble(0, game.LEVEL_SIZE_Y * game.TILE_SIZE - 1);
            } while(!level.isPlaceFree(placeX, placeY) || game.isEntityLocation(placeX, placeY, minDist) || !tiles.contains(level.tileAtLocation(placeX, placeY)));
            amountSpawned++;
            if(resourceType == TREE) game.addEntity(new Tree(placeX, placeY));
            if(resourceType == BERRY_BUSH) game.addEntity(new BerryBush(placeX, placeY));
        }
    }
    
    //Generates rocks seperately because they only spawn on stone
    private static void generateRocks(Game game, CurrentLevel level, int minDist) {
        for(int i = 0; i < level.getWidthTiles(); i++) {
            for(int j = 0; j < level.getHeightTiles(); j++) {
                if(level.tileAtLocation(i*game.TILE_SIZE, j*game.TILE_SIZE) == STONE) {
                    if(JMath.randomInteger(0, 5) == 0) {
                        double oPlaceX = i*game.TILE_SIZE;
                        double oPlaceY = j*game.TILE_SIZE;
                        double placeX;
                        double placeY;
                        int loops = 0;
                        do {
                            placeX = JMath.randomDouble(oPlaceX, oPlaceX + game.TILE_SIZE);
                            placeY = JMath.randomDouble(oPlaceY, oPlaceY + game.TILE_SIZE);
                            loops++;
                        }while(game.isEntityLocation(placeX, placeY, minDist) && loops < 10);
                        if(loops < 10) game.addEntity(new Rock(placeX, placeY));
                    }
                }
            }
        }
    }
}
