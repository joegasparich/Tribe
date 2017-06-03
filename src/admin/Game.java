package admin;

import gui.GUI;
import gui.Draw;
import entity.Animal;
import entity.Animal.AnimalType;
import static entity.Animal.AnimalType.*;
import entity.Colonist;
import entity.Entity;
import entity.GroundItem;
import entity.Player;
import entity.Projectile;
import static entity.TribeMember.Job.*;
import init.InitSprites;
import items.Item;
import static items.ItemData.*;
import static items.ItemData.WeaponEnum.*;
import static items.ItemData.ArmourEnum.*;
import map.CurrentLevel;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Thread.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JFrame;
import tribe.Tribe;

/**
 *
 * @author Joe
 * Main game class runs the thread and the keyboard and mouse listeners
 * and initializes all the game objects
 */
public class Game extends Canvas implements Runnable, KeyListener, MouseListener{
    
    public JFrame frame;
    
    //Constants
    public static final int SCALE = 4; //Pixels are 4 times larger
    public static final int TILE_SIZE = 8;
    public static final int VIEWPORT_SIZE_X = 300;
    public static final int VIEWPORT_SIZE_Y = 200;
    public static final int LEVEL_SIZE_X = 200;
    public static final int LEVEL_SIZE_Y = 200;
    public static final int WINDOW_SIZE_X = VIEWPORT_SIZE_X * SCALE;
    public static final int WINDOW_SIZE_Y = VIEWPORT_SIZE_Y * SCALE;
    public static final int GAME_SIZE_X = LEVEL_SIZE_X * TILE_SIZE;
    public static final int GAME_SIZE_Y = LEVEL_SIZE_Y * TILE_SIZE;
    private static final int GAME_SPEED = 10; //Amount of time to sleep between ticks
    private static final int MAX_ANIMALS = 20;
    private static final int ANIMAL_RESPAWN_RATE = 2000;
    
    //Resources
    private HashMap<String, BufferedImage> sprites; //Stores all sprites after being loaded from file
    private Font font;
    
    //Entities
    private Player player;
    private CurrentLevel level;
    private Tribe tribe;
    private ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<Entity> entitiesToAdd = new ArrayList<>();
    private int animalCount = 0; //The current amount of animals
    private int animalChecker = 0; // The timer to repopulate the animal population
    
    //Constructor
    public Game(int width, int height) {
        Dimension size = new Dimension(width, height);
        frame = new JFrame();
        frame.getContentPane().setPreferredSize(size);
        frame.pack();
        addKeyListener(this); //Keyboard Listener
        addMouseListener(this); //Mouse Listener
        
        //Initialise resources
        System.out.println("Initialising Sprites");
        sprites = InitSprites.initSprites(); //Load sprites from file and put them in the map
        System.out.println("Initialising Font");
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            font = Font.createFont(Font.TRUETYPE_FONT, new File("resources/best_font.ttf"));
            ge.registerFont(font);
        } catch(FontFormatException|IOException e) {}
        
        //Create game objects
        System.out.println("Creating Level");
        level = new CurrentLevel(this, LEVEL_SIZE_X, LEVEL_SIZE_Y); //Create the level 
        System.out.println("Creating Player");
        player = new Player(this, level); //Create the player
        player.setPos((LEVEL_SIZE_X/2)*TILE_SIZE, (LEVEL_SIZE_Y/2)*TILE_SIZE); //Put the player on tile (5, 5) of the room
        System.out.println("Creating Tribe");
        tribe = new Tribe(this, (LEVEL_SIZE_X/2)*TILE_SIZE, (LEVEL_SIZE_Y/2)*TILE_SIZE, 4);
        //These are temporary
        System.out.println("Spawning Temp Enemy");
        spawnColonist((LEVEL_SIZE_X/2 + 20.5)*TILE_SIZE, (LEVEL_SIZE_X/2 + 20.5)*TILE_SIZE);
        System.out.println("Spawning Temp Items");
        spawnItem((LEVEL_SIZE_X/2)*TILE_SIZE + 20, (LEVEL_SIZE_Y/2)*TILE_SIZE, newWeapon(SHORT_BOW));
        spawnItem((LEVEL_SIZE_X/2)*TILE_SIZE - 20, (LEVEL_SIZE_Y/2)*TILE_SIZE, newWeapon(BLOW_GUN));
        spawnItem((LEVEL_SIZE_X/2)*TILE_SIZE, (LEVEL_SIZE_Y/2)*TILE_SIZE + 20, newArmour(BONE));
        spawnItem((LEVEL_SIZE_X/2)*TILE_SIZE, (LEVEL_SIZE_Y/2)*TILE_SIZE - 20, newArmour(CHIEFTAN));
    }
    
    //Thread stuff
    private Thread thread;
    private boolean running = false;
    
    public synchronized void start() {
        thread = new Thread(this, "Display");
        running = true;
        thread.start();
    }
    
    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    //This is the main loop of the game
    public void run() {
        while(running) {
            //Sort entities by Y value to create depth in sprites
            Collections.sort(entities, new Comparator<Entity>(){
                public int compare(Entity o1, Entity o2){
                    if(o1.getY() == o2.getY())
                        return 0;
                    return o1.getY() < o2.getY() ? -1 : 1;
                }
            });
            //Maintains animal population
            if(animalChecker > 0) {
                animalChecker--;
            } else {
                spawnAnimals();
                animalChecker = ANIMAL_RESPAWN_RATE;
            }
            //Update entities, had to add flags to add and remove entities because threads fuck you in the ass
            for(Entity eta : entitiesToAdd) {
                entities.add(eta);
            }
            entitiesToAdd.clear();
            for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
                Entity e = it.next();
                e.update();
                if(e.isRemove()) it.remove(); 
            }
            
            Draw.drawWorld(this); //Draws the world
//            DrawMap.drawMap(this, level);
            //Eventually will replace this with a separated frame rate and tick rate
            try {
                sleep(GAME_SPEED);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        stop();
    }
        
    //Mouse booleans are activated upon button press and deactivated on button release
    private static boolean buttonLeft;
    private static boolean buttonRight;

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        int button = e.getButton();
        switch (button) {
            case MouseEvent.BUTTON1: //Left mouse button
                buttonLeft = true;
                GUI.click(this);
                break;
            case MouseEvent.BUTTON3: //Right mouse button
                buttonRight = true;
                break;
            default:
                break;
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        int button = e.getButton();
        switch (button) {
            case MouseEvent.BUTTON1: //Left mouse button
                buttonLeft = false;
                break;
            case MouseEvent.BUTTON3: //Right mouse button
                buttonRight = false;
                break;
            default:
                break;
        }
    }
    //Mouse Getters
    public static boolean isButtonLeft() {
        return buttonLeft;
    }

    public static boolean isButtonRight() {
        return buttonRight;
    }
    
    
    //Add more keys here in the same way
    //These are the constants for the controls, change the button here to change it everywhere
    private static final int CONTROL_UP = KeyEvent.VK_W;
    private static final int CONTROL_DOWN = KeyEvent.VK_S;
    private static final int CONTROL_LEFT = KeyEvent.VK_A;
    private static final int CONTROL_RIGHT = KeyEvent.VK_D;
    private static final int CONTROL_PICKUP = KeyEvent.VK_E;
    
    //Keyboard booleans are activated upon key press and deactivated on key release
    private static boolean keyUp;
    private static boolean keyDown;
    private static boolean keyRight;
    private static boolean keyLeft;
    private static boolean keyPickup;
    
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case CONTROL_UP:
                keyUp = false;
                break;
            case CONTROL_DOWN:
                keyDown = false;
                break;
            case CONTROL_LEFT:
                keyLeft = false;
                break;
            case CONTROL_RIGHT:
                keyRight = false;
                break;
            case CONTROL_PICKUP:
                keyPickup = false;
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0); 
                break;
            //These are all temporary
            case KeyEvent.VK_R:
                level.generate();
                break;
            case KeyEvent.VK_C:
                tribe.addJob(CUT);
                break;
            case KeyEvent.VK_G:
                tribe.addJob(GATHER);
                break;
            case KeyEvent.VK_M:
                tribe.addJob(MINE);
                break;
            case KeyEvent.VK_H:
                tribe.addJob(HUNT);
                break;
            case KeyEvent.VK_N:
                spawnColonist(JMath.randomDouble(600, 1000), JMath.randomDouble(600, 1000));
                break;
            default:
                break;
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode){
            case CONTROL_UP:
                keyUp = true;
                break;
            case CONTROL_DOWN:
                keyDown = true;
                break;
            case CONTROL_LEFT:
                keyLeft = true;
                break;
            case CONTROL_RIGHT:
                keyRight = true;
                break;
            case CONTROL_PICKUP:
                keyPickup = true;
                break;
            default:
                break;
        }
    }

    //Key Getters
    public boolean isKeyUp() {
        return keyUp;
    }

    public boolean isKeyDown() {
        return keyDown;
    }

    public boolean isKeyRight() {
        return keyRight;
    }

    public boolean isKeyLeft() {
        return keyLeft;
    }

    public static boolean isKeyPickup() {
        return keyPickup;
    }
    
    //Game Code
    
    //Spawns an individial colonist
    public void spawnColonist(double x, double y) {
        Colonist c = new Colonist(this, level, "colonist");
        c.setPos(x, y);
        addEntity(c);
    }
    
    //Spawns an individual animal
    public void spawnAnimal(double x, double y, AnimalType type) {
        Animal a = new Animal(this, level, type);
        a.setPos(x, y);
        addEntity(a);
    }
    
    //This is called every so many ticks, and replaces all animals that died between calls
    private void spawnAnimals() {
        for(int i = animalCount; i < MAX_ANIMALS; i++) {
            int choose = JMath.randomInteger(0, AnimalType.values().length);
            double placeX;
            double placeY;
            do {
                placeX = JMath.randomDouble(0, (LEVEL_SIZE_X - 1) * TILE_SIZE);
                placeY = JMath.randomDouble(0, (LEVEL_SIZE_Y - 1) * TILE_SIZE);
            } while(!level.isPlaceFree(placeX, placeY));
            if(choose == 0) spawnAnimal(placeX, placeY, MOA);
            if(choose == 1) spawnAnimal(placeX, placeY, DEER);
            if(choose == 2) spawnAnimal(placeX, placeY, BOAR);
            if(choose == 3) spawnAnimal(placeX, placeY, RABBIT);
            animalCount++;
        }
    }
    
    //Remove animal from its slot, allowing another to be spawned and replace it
    public void removeAnimal() {
        animalCount--;
    }
    
    //Spawns an item at the position given
    public void spawnItem(double x, double y, Item item) {
        GroundItem groundItem = new GroundItem(this, x, y, item);
        addEntity(groundItem);
    }
    
    //Returns whether there is an entity in a certain location of area size dist
    public boolean isEntityLocation(double x, double y, int dist) {
        for(Entity entity : entitiesToAdd) {
            if(JMath.getDistance(x, y, entity.getX(), entity.getY()) < dist) {
                return true;
            }
        }
        return false;
    }
    
    //Main Getters and Setters

    public Player getPlayer() {
        return player;
    }

    public CurrentLevel getCurrentLevel() {
        return level;
    }

    public Tribe getTribe() {
        return tribe;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }
    
    public void addEntity(Entity e) {
        entitiesToAdd.add(e);
    }
    
    public boolean inEntities(Entity e) {
        return entities.contains(e);
    }

    public HashMap<String, BufferedImage> getSprites() {
        return sprites;
    }

    public Font getFont() {
        return font;
    }
}
