package gui;

import admin.Game;
import static admin.Game.SCALE;
import map.CurrentLevel.Tile;
import static map.CurrentLevel.Tile.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import map.CurrentLevel;

/**
 *
 * @author Joe
 * 
 * Draws the tiles using pixels, currently unused but might become the map in the GUI
 */
public class DrawMap {
    
    //Draws the world
    public static void drawMap(Game game, CurrentLevel level) {
        Tile[][] tileArray = level.getTileArray();

        //Uses triple buffering (ie. preloads 2 frames while showing a third
        BufferStrategy bs = game.getBufferStrategy();
        if(bs == null) {
            game.createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.scale(4, 4);

        g.setColor(Color.white);
        g.fillRect(0, 0, game.getWidth()/SCALE + 1, game.getHeight()/SCALE + 1);
        
        for(int i = 0; i < level.getWidthTiles(); i++) {
            for(int j = 0; j < level.getHeightTiles(); j++) {
                if(tileArray[i][j] == WATER) {
                    g.setColor(new Color(54, 100, 118));
                }
                if(tileArray[i][j] == SAND) {
                    g.setColor(new Color(185, 165, 102));
                }
                if(tileArray[i][j] == GRASS) {
                    g.setColor(new Color(83, 131, 50));
                }
                if(tileArray[i][j] == DIRT) {
                    g.setColor(new Color(100, 83, 59));
                }
                g.fillRect(i, j, 1, 1);
            }
        }
        //More bufferring stuff
        g.dispose();
        bs.show();
    }
}
