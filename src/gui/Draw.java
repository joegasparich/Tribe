/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import admin.Game;
import static gui.GUI.Tab.*;
import static gui.GUI.getCurrentTab;
import static admin.Game.*;
import entity.Colonist;
import entity.Entity;
import entity.Player;
import entity.Projectile;
import entity.TribeMember;
import map.CurrentLevel.Tile;
import static map.CurrentLevel.Tile.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import tribe.Tribe;

/**
 *
 * @author Joe
 * Is called each tick to draw the canvas
 * I may eventually move to a different engine than Graphics2D like OpenGL or something but cbf right now
 */
public class Draw {
    
    private static Player player;
    private static double playerX;
    private static double playerY;
    private static double camX;
    private static double camY;
    
    private static int gameScreenX = VIEWPORT_SIZE_X;
    private static int gameScreenY = VIEWPORT_SIZE_Y;
    
    private static int offsetMinX = 3*TILE_SIZE;
    private static int offsetMinY = 3*TILE_SIZE;
    private static int offsetMaxX = GAME_SIZE_X - gameScreenX - 3*TILE_SIZE;
    private static int offsetMaxY = GAME_SIZE_Y - gameScreenY - 3*TILE_SIZE;
    
    //Draws the world
    public static void drawWorld(Game game) {
        //Get info from the player and the game
        player = game.getPlayer();
        playerX = player.getX();
        playerY = player.getY();
        Tile[][] tileArray = game.getCurrentLevel().getTileArray();
        HashMap<String, BufferedImage> sprites = game.getSprites();
        
        //Camera
        camX = playerX - gameScreenX /2;
        camY = playerY - gameScreenY /2;
        
        if(camX < offsetMinX) camX = offsetMinX;
        if(camX > offsetMaxX) camX = offsetMaxX;
        if(camY < offsetMinY) camY = offsetMinY;
        if(camY > offsetMaxY) camY = offsetMaxY;
        
        
        //Uses triple buffering (ie. preloads 2 frames while showing a third)
        BufferStrategy bs = game.getBufferStrategy();
        if(bs == null) {
            game.createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.scale(SCALE, SCALE); //Multiply each pixel by the scale
        g.translate(-camX, -camY); //Move view to the camera location
        
        //Draw tiles in the camera range
        int drawX1 = (int) camX/TILE_SIZE;
        int drawY1 = (int) camY/TILE_SIZE;
        int drawX2 = (int) (camX + gameScreenX)/TILE_SIZE;
        int drawY2 = (int) (camY + gameScreenY)/TILE_SIZE;
        
        BufferedImage sand = sprites.get("sand");
        BufferedImage water = sprites.get("water");
        BufferedImage grass = sprites.get("grass");
        BufferedImage dirt = sprites.get("dirt");
        BufferedImage stone = sprites.get("stone");
        
        for(int i = drawX1 - 2; i < drawX2 + 2; i++) {
            for(int j = drawY1 - 2; j < drawY2 + 2; j++) {
                if(tileArray[i][j] == WATER) {
                    g.drawImage(water, i*TILE_SIZE, j*TILE_SIZE, null);
                }
                if(tileArray[i][j] == SAND) {
                    g.drawImage(sand, i*TILE_SIZE, j*TILE_SIZE, null);
                }
                if(tileArray[i][j] == GRASS) {
                    g.drawImage(grass, i*TILE_SIZE, j*TILE_SIZE, null);
                }
                if(tileArray[i][j] == DIRT) {
                    g.drawImage(dirt, i*TILE_SIZE, j*TILE_SIZE, null);
                }
                if(tileArray[i][j] == STONE) {
                    g.drawImage(stone, i*TILE_SIZE, j*TILE_SIZE, null);
                }
            }
        }
        
        //Draw all Entities
        ArrayList<Entity> entities = game.getEntities();
        for(Entity e : entities) {
            BufferedImage image = sprites.get(e.getSprite());
            AffineTransform at = new AffineTransform(); 
            //Projectile
            if(e instanceof Projectile) {
                Projectile p = (Projectile) e;
                at.rotate(Math.toRadians(p.getAngle() + 90), p.getX(), p.getY()); //Rotate to the angle of the projectile
                at.translate(p.getX(), p.getY()); //Move to the location of the projectile
                g.drawImage(image, at, null);
            //Everything Else
            } else if(e instanceof Player) {
                Player player = (Player) e;
                //Base Sprite
                at.translate(e.getX() - e.getCentreX() - 1, e.getY() - e.getCentreY() - 1);
                g.drawImage(image, at, null);
                //Armour
                BufferedImage armour = sprites.get(player.getArmour().getSprite());
                if(armour != null) {
                    at.translate(-6, -5);
                    g.drawImage(armour, at, null);
                    at.translate(6, 5);
                }
                //Weapon
                BufferedImage weapon = sprites.get(player.getWeapon().getSprite());
                if(weapon != null) {
                    at.translate(-2, -3);
                    g.drawImage(weapon, at, null);
                }
            } else if(e instanceof TribeMember) {
                TribeMember tm = (TribeMember) e;
                //Base Sprite
                at.translate(e.getX() - e.getCentreX() - 1, e.getY() - e.getCentreY() - 1);
                g.drawImage(image, at, null);
                //Weapon
                BufferedImage weapon = sprites.get(tm.getWeapon().getSprite());
                if(weapon != null) {
                    at.translate(-2, -2);
                    g.drawImage(weapon, at, null);
                }
            } else if(e instanceof Colonist) {
                Colonist colonist = (Colonist) e;
                //Base Sprite
                at.translate(e.getX() - e.getCentreX() - 1, e.getY() - e.getCentreY() - 1);
                g.drawImage(image, at, null);
                //Weapon
                BufferedImage weapon = sprites.get(colonist.getWeapon().getSprite());
                if(weapon != null) {
                    at.translate(-2, -2);
                    g.drawImage(weapon, at, null);
                }
            } else {
                at.translate(e.getX() - e.getCentreX() - 1, e.getY() - e.getCentreY() - 1);
                g.drawImage(image, at, null);
                //g.drawImage(image, (int) (e.getX() - e.getWidth()/2 + 1), (int) (e.getY() - e.getHeight()/2 + 1), null);
            }
        }
          

        drawGUI(g, game);

        
        //More bufferring stuff
        g.dispose();
        bs.show();
    }
    
    //Draws the GUI ontop of the game. Doesn't handle any mouse clicks etc.
    public static void drawGUI(Graphics2D g, Game game) {
        AffineTransform defaultTransform = g.getTransform();
        AffineTransform newTransform = new AffineTransform(defaultTransform);
        newTransform.setToScale(1, 1);
        g.setTransform(newTransform);

        int windowX = (WINDOW_SIZE_X + 10);
        int windowY = (WINDOW_SIZE_Y + 10);
        HashMap<String, BufferedImage> sprites = game.getSprites();
        Player player = game.getPlayer();
        Tribe tribe = game.getTribe();
        
        Color brown1 = new Color(105, 79, 57);
        Color brown2 = new Color(126, 98, 74);
        Color brown3 = new Color(146, 119, 96);
        Color brown4 = new Color(66, 50, 36);
        Color blue1 = new Color(67, 161, 178);
        Color blue2 = new Color(37, 106, 120);

        //Top Bar
        
        //Draw base rectangles
        g.setColor(brown1);
        g.fillRect(0, 0, 288, 40);
        g.setColor(brown3);
        g.fillRect(5, 5, 278, 30);
        
        //Resource numbers
        Font font = game.getFont();
        g.setFont(font.deriveFont(24.0f));
        g.setColor(brown4);
        AffineTransform at = new AffineTransform();
        at.scale(2, 2);
        BufferedImage food = sprites.get("food_resource");
        at.translate(2, 2);
        g.drawImage(food, at, null);
        g.drawString(Integer.toString(game.getTribe().getResourceFood()), 34, 27);
        BufferedImage wood = sprites.get("wood_resource");
        at.translate(46, 0);
        g.drawImage(wood, at, null);
        g.drawString(Integer.toString(game.getTribe().getResourceWood()), 128, 27);
        BufferedImage stone = sprites.get("stone_resource");
        at.translate(46, 0);
        g.drawImage(stone, at, null);
        g.drawString(Integer.toString(game.getTribe().getResourceStone()), 220, 27);
        
        //Bottom Bar
        
        //Draw base rectangle
        g.setColor(brown1);
        g.fillRect(0, windowY - 138, 366, 138);
        g.setColor(brown3);
        g.fillRect(5, windowY - 133, 356, 128);
        
        //Tabs
        BufferedImage character = sprites.get("character");
        BufferedImage campfire = sprites.get("campfire");
        BufferedImage hut = sprites.get("hut");
        
        at = new AffineTransform(); 
        g.setColor(brown1);
        g.fillRect(0, windowY - 201, 84, 68);
        if(getCurrentTab() == CHARACTER) {
            g.setColor(brown3);
        } else {
            g.setColor(brown2);
        }
        g.fillRect(5, windowY - 196, 74, 58);
        at.translate(28, windowY - 185);
        at.scale(4, 4);
        g.drawImage(character, at, null);
        
        at = new AffineTransform(); 
        g.setColor(brown1);
        g.fillRect(79, windowY - 201, 84, 68);
        if(getCurrentTab() == TRIBE) {
            g.setColor(brown3);
        } else {
            g.setColor(brown2);
        }
        g.fillRect(84, windowY - 196, 74, 58);
        at.translate(101, windowY - 186);
        at.scale(4, 4);
        g.drawImage(campfire, at, null);
        
        at = new AffineTransform(); 
        g.setColor(brown1);
        g.fillRect(158, windowY - 201, 84, 68);
        if(getCurrentTab() == BUILDING) {
            g.setColor(brown3);
        } else {
            g.setColor(brown2);
        }
        g.fillRect(163, windowY - 196, 74, 58);
        at.translate(174, windowY - 190);
        at.scale(3, 3);
        g.drawImage(hut, at, null);
        
        //Character tab
        if(getCurrentTab() == CHARACTER) {
            int bottom1 = 99;
            int bottom2 = 294;
            g.setColor(brown1);
            g.fillRect(0, windowY - 138, 104, 138);
            g.setColor(brown3);
            g.fillRect(5, windowY - 133, 94, 128);
            g.setColor(brown1);
            g.fillRect(0, windowY - 34, 104, 34);
            g.setColor(brown3);
            g.fillRect(5, windowY - 29, 94, 24);
            g.setColor(brown1);
            g.fillRect(99, windowY - 138, 200, 138);
            g.setColor(brown3);
            g.fillRect(104, windowY - 133, 190, 128);
            g.setColor(brown1);
            g.fillRect(bottom2, windowY - 138, 72, 72);
            g.setColor(brown3);
            g.fillRect(bottom2 + 5, windowY - 133, 62, 62);
            g.setColor(brown1);
            g.fillRect(bottom2, windowY - 71, 72, 71);
            g.setColor(brown3);
            g.fillRect(bottom2 + 5, windowY - 66, 62, 61);

            //Draw character
            BufferedImage weapon = sprites.get(player.getWeapon().getSprite());
            BufferedImage armour = sprites.get(player.getArmour().getSprite());
            at = new AffineTransform(); 
            //Base Sprite
            at.translate(56 - player.getCentreX() * 8, windowY - 74 + 6 - player.getCentreY() * 8);
            at.scale(8, 8);
            g.drawImage(character, at, null);
            //Armour
            if(armour != null) {
                at.translate(-6, -5);
                g.drawImage(armour, at, null);
                at.translate(6, 5);
            }
            //Weapon
            if(weapon != null) {
                at.translate(-2, -3);
                g.drawImage(weapon, at, null);
            }
            //Draw XP Section
            g.setColor(brown4);
            g.drawString(Integer.toString(player.getPlayerLevel()), 11, windowY - 10);
            g.setColor(blue2);
            g.fillRect(29, windowY - 25, 66, 16);
            double xpRatio = player.getPlayerXP() / (double) player.getXpToNextLevel();
            g.setColor(blue1);
            g.fillRect(29, windowY - 25, (int) (66 * xpRatio), 16);
            //Draw Stats Section
            g.setColor(new Color(133, 30, 30));
            g.fillRect(bottom1 + 9, windowY - 129, 182, 16);
            double hpRatio = player.getHp() / (double) player.getMaxHP();
            g.setColor(new Color(81, 169, 46));
            g.fillRect(bottom1 + 9, windowY - 129, (int) (182 * hpRatio) , 16);
            g.setColor(brown4);
            DecimalFormat df = new DecimalFormat("####0.0");
            g.drawString("HP: " + player.getMaxHP(), bottom1 + 9, windowY - 90);
            g.drawString("DAMAGE: " + player.getDamage(), bottom1 + 9, windowY - 64);
            g.drawString("ATK SPEED: " + df.format(player.getAttackSpeed()), bottom1 + 9, windowY - 38);
            g.drawString("MOVE SPEED: " + df.format(player.getMoveSpeed()), bottom1 + 9, windowY - 12);
            //Draw Items
            weapon = sprites.get(player.getWeapon().getGroundSprite());
            armour = sprites.get(player.getArmour().getGroundSprite());
            at = new AffineTransform(); 
            at.translate(bottom2 + 12, windowY - 126);
            at.scale(4, 4);
            g.drawImage(weapon, at, null);
            at = new AffineTransform(); 
            at.translate(bottom2 + 12, windowY - 59);
            at.scale(4, 4);
            g.drawImage(armour, at, null);
        //Tribe Tab
        } else if(getCurrentTab() == TRIBE) {
            int bottom1 = 99;
            g.setColor(brown1);
            g.fillRect(0, windowY - 138, 104, 138);
            g.setColor(brown3);
            g.fillRect(5, windowY - 133, 94, 128);
            g.setColor(brown1);
            g.fillRect(0, windowY - 34, 104, 34);
            g.setColor(brown3);
            g.fillRect(5, windowY - 29, 94, 24);
            g.setColor(brown1);
            g.fillRect(99, windowY - 138, 267, 138);
            g.setColor(brown3);
            g.fillRect(104, windowY - 133, 257, 128);
//            g.setColor(brown1);
//            g.fillRect(bottom2, windowY - 138, 72, 72);
//            g.setColor(brown3);
//            g.fillRect(bottom2 + 5, windowY - 133, 62, 62);
//            g.setColor(brown1);
//            g.fillRect(bottom2, windowY - 71, 72, 71);
//            g.setColor(brown3);
//            g.fillRect(bottom2 + 5, windowY - 66, 62, 61);

            //Draw campfire
            at = new AffineTransform(); 
            //Base Sprite
            at.translate(12, windowY - 120);
            at.scale(8, 8);
            g.drawImage(campfire, at, null);
            //Draw XP Section
            g.setColor(brown4);
            g.drawString(Integer.toString(player.getPlayerLevel()), 11, windowY - 10);
            g.setColor(blue2);
            g.fillRect(29, windowY - 25, 66, 16);
            double xpRatio = player.getPlayerXP() / (double) player.getXpToNextLevel();
            g.setColor(blue1);
            g.fillRect(29, windowY - 25, (int) (66 * xpRatio), 16);
            //Draw Stats Section
            g.setColor(brown4);
//            DecimalFormat df = new DecimalFormat("####0.0");
            g.drawString("MEMBERS: " + tribe.getTribeMemberCount(), bottom1 + 9, windowY - 110);
            g.drawString("BUILDINGS: " + tribe.getTribeMemberCount(), bottom1 + 9, windowY - 90);
            //Draw Buttons
            BufferedImage button_cut = sprites.get("button_cut");
            BufferedImage button_mine = sprites.get("button_mine");
            BufferedImage button_gather = sprites.get("button_gather");
            BufferedImage button_hunt = sprites.get("button_hunt");
            BufferedImage button_haul = sprites.get("button_haul");
            BufferedImage button_asdf = sprites.get("button_asdf");
            at = new AffineTransform();
            at.translate(250, windowY - 130);
            at.scale(2, 2);
            g.drawImage(button_cut, at, null);
            at.translate(28, 0);
            g.drawImage(button_mine, at, null);
            at.translate(-28, 21);
            g.drawImage(button_gather, at, null);
            at.translate(28, 0);
            g.drawImage(button_hunt, at, null);
            at.translate(-28, 21);
            g.drawImage(button_haul, at, null);
            at.translate(28, 0);
            g.drawImage(button_asdf, at, null);
        }
    }

    public static double getCamX() {
        return camX;
    }

    public static double getCamY() {
        return camY;
    }
}
