package entity;

import gui.Draw;
import static entity.Player.Direction.*;
import map.CurrentLevel;
import admin.Game;
import admin.JMath;
import gui.GUI;
import items.Armour;
import items.ItemData;
import static items.ItemData.ArmourEnum.*;
import static items.ItemData.WeaponEnum.*;
import items.Weapon;
import java.awt.MouseInfo;
import java.awt.Point;
import javax.swing.JOptionPane;

/**
 *
 * @author Joe
 *
 * Main player class, handles everything to do with the player
 */
public class Player extends Entity {

    //Constants, put any on/off features here
    private static final boolean STOP_WHEN_ATTACKING = false;
    private static final boolean COLLISION = true;
    
    //Constants, put numbers here
    

    //Sprites + Collision Box

    //Enum of possible directions only used in collision atm
    public static enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    //Variables that convey the state of the player
    private boolean canMove = true;
    private boolean moving = false;
    private boolean canAttack = true;
    private boolean attacking = false;
    private boolean canPickup = false;
    private int pickupTimer = 20;

    private int attackTime = 0; //This gets set to attackSpeed and counts down to when you can attack again
    //The location the mouse clicked
    private int attackX;
    private int attackY;
    
    private int spriteWidth = 4;
    private int spriteHeight = 4;

    //Stats
    private int maxHP = 100;
    private int hp = maxHP;
    private double moveSpeed = 1; //Is an integer and 2 is too fast so 1 will probably just be the constant moveSpeed value, I may change moveSpeed and x,y to a double later
    private int damage = 0; //This is added to weapon damage
    private double attackSpeedMultiplier = 1; //This is multiplied with weapon attackSpeed, lower is better
    
    //XP code
    private int playerLevel = 1; //level of the player, starts at 1;
    private int playerXP = 0; //player experience
    private int xpToNextLevel = 20; //sets the xp to the next level (2) to be the base
    private int xpToLastLevel = 0; //sets the xp to the next level (2) to be the base

    //Inventory
    private Weapon weapon = ItemData.newWeapon(SPEAR); //Temporarily just give him a wooden spear
    private Armour armour = ItemData.newArmour(TIKI); //Temporarily just give him tiki armour

    //Constructor
    public Player(Game game, CurrentLevel level) {
        game.addEntity(this);
        this.game = game;
        this.level = level;
        sprite = "character";
        spriteCentreX = 4;
        spriteCentreY = 4;
    }

    //Called every loop of the game
    @Override
    public void update() {
        if (canAttack) {
            //Attacking code
            if (attacking) {
                //This handles attack moveSpeed, you can only attack once the attackTime variable has reached 0
                if (attackTime == 0) {
                    canMove = true; //This is obselete unless the STOP_WHEN_ATTACKING constant is true
                    attacking = false;
                } else {
                    attackTime--;
                }
                //If not attacking currently
            } else if (game.isButtonLeft()) {
                if(!GUI.isGUI(game)) {
                    if (STOP_WHEN_ATTACKING) {
                        canMove = false;
                    }
                    attacking = true;
                    Point p = MouseInfo.getPointerInfo().getLocation(); //Get mouse location
                    Point q = game.getLocationOnScreen();
                    //Subtracts the game location from the mouse location to get the relative mouse location
                    attackX = (p.x - q.x) / game.SCALE + (int) Draw.getCamX();
                    attackY = (p.y - q.y) / game.SCALE + (int) Draw.getCamY();
                    //Create new projcetile in the direction of the mouse and add it to the collection
                    Projectile proj = new Projectile(game, x, y, JMath.getAngle(x, y, attackX, attackY), weapon.getDamage() + this.damage, weapon.getRange(), weapon.getType(), this);
                    game.addEntity(proj);
                    //Set the attackTime to attackSpeed
                    attackTime = (int) (weapon.getAttackSpeed() * attackSpeedMultiplier);
                }
            }
        }
        if (canMove) {
            //Movement code
            //Each key checks collision in the direction and them moves the character if the place is free
            //If the place isn't free, a collision method is called that moves the character to the wall, which prevents the player
            //from getting stuck in the wall if he moves like 5 pixels a tick
            if (game.isKeyUp()) {
                if(COLLISION) {
                    if (!collisionCheck(UP)) {
                        y -= moveSpeed;
                        moving = true;
                    } else {
                        collideWithTile(UP);
                    }
                } else {
                    y -= moveSpeed;
                    moving = true;
                }
            }
            if (game.isKeyDown()) {
                if(COLLISION) {
                    if (!collisionCheck(DOWN)) {
                        y += moveSpeed;
                        moving = true;
                    } else {
                        collideWithTile(DOWN);
                    }
                } else {
                    y += moveSpeed;
                    moving = true;
                }
            }
            if (game.isKeyLeft()) {
                if(COLLISION) {
                    if (!collisionCheck(LEFT)) {
                        x -= moveSpeed;
                        moving = true;
                    } else {
                        collideWithTile(LEFT);
                    }
                } else {
                    x -= moveSpeed;
                    moving = true;
                }
            }
                
            if (game.isKeyRight()) {
                if(COLLISION) {
                    if (!collisionCheck(RIGHT)) {
                        x += moveSpeed;
                        moving = true;
                    } else {
                        collideWithTile(RIGHT);
                    }
                } else {
                    x += moveSpeed;
                    moving = true;
                }
            }
            //If no movement key is pressed, set moving to false
            if (!game.isKeyUp() && !game.isKeyDown() && !game.isKeyLeft() && !game.isKeyRight()) {
                moving = false;
            }
        } else {
            moving = false;
        }
        //Picking up items
        if(game.isKeyPickup()) {
            double minDist = 20;
            GroundItem item = null;
            for(Entity e : game.getEntities()) {
                if(e instanceof GroundItem && JMath.getDistance(x, y, e.getX(), e.getY()) < minDist) {
                    minDist = JMath.getDistance(x, y, e.getX(), e.getY());
                    item = (GroundItem) e;
                }
            }
            if(minDist < 10 && pickupTimer == 0) {
                item.pickUp();
                pickupTimer = 20;
            }
        }
        if(pickupTimer > 0) {
            pickupTimer--;
        }
    }
    
    //Levels up your character
    private void levelUp() {
        playerLevel++;
        xpToLastLevel = xpToNextLevel;
        xpToNextLevel = (int) (playerXP + 10 * Math.sqrt(playerXP));
        Object[] options = {"HP", "ATTACK_SPEED", "DAMAGE", "MOVESPEED"};
        int action = JOptionPane.showOptionDialog(null, "Choose a stat to increase", "Level Up", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        switch (action) {
            case 0:
                hp += 10;
                break;
            case 1:
                if(attackSpeedMultiplier > 0.2) {
                    attackSpeedMultiplier -= 0.1;
                }
                break;
            case 2:
                damage += 1;
                break;
            case 3:
                if(moveSpeed < 7)
                moveSpeed += 0.4;
                break;
            default:
                break;
        }

    }
    
    //Checks upper and lower bounds of a direction for collisions and returns true if there is a collisions
    private boolean collisionCheck(Direction dir) {
        if (dir == UP && level.isPlaceFree(x - spriteWidth / 2, y - spriteHeight / 2 - moveSpeed) && level.isPlaceFree(x + spriteWidth / 2, y - spriteHeight / 2 - moveSpeed)) {
            return false;
        } else if (dir == DOWN && level.isPlaceFree(x - spriteWidth / 2, y + spriteHeight / 2 + moveSpeed) && level.isPlaceFree(x + spriteWidth / 2, y + spriteHeight / 2 + moveSpeed)) {
            return false;
        } else if (dir == LEFT && level.isPlaceFree(x - spriteWidth / 2 - moveSpeed, y - spriteHeight / 2) && level.isPlaceFree(x - spriteWidth / 2 - spriteHeight, y + spriteCentreY / 2)) {
            return false;
        } else if (dir == RIGHT && level.isPlaceFree(x + spriteWidth / 2 + moveSpeed, y - spriteHeight / 2) && level.isPlaceFree(x + spriteWidth / 2 + spriteHeight, y + spriteCentreY / 2)) {
            return false;
        } else {
            return true;
        }
    }

    //Moves object to wall in direction dir
    private void collideWithTile(Direction dir) {
        int xval = 0;
        int yval = 0;
        if (dir == UP) {
            yval = -1;
        }
        if (dir == DOWN) {
            yval = 1;
        }
        if (dir == LEFT) {
            xval = -1;
        }
        if (dir == RIGHT) {
            xval = 1;
        }

        //Checks upper and lower bounds of direction (e.g. when colliding left, checks left of top and bottom of sprite)
        while (level.isPlaceFree(x + xval + (spriteWidth / 2 * xval) + (spriteWidth / 2 * yval), y + yval + (spriteHeight / 2 * yval) + (spriteHeight / 2 * xval))
                && level.isPlaceFree(x + xval + (spriteWidth / 2 * xval) + ((spriteWidth / 2) * (yval * -1)), y + yval + (spriteHeight / 2 * yval) + ((spriteHeight / 2) * (xval * -1)))) {
            x += xval;
            y += yval;
        }
    }

    //Getters and Setters
   
    public void addXP(int amount) {
        playerXP += amount;
        if (playerXP >= xpToNextLevel) {
            levelUp();
        }
    }
    
    public void getDamaged(int amount, Entity source) {
        //fucc you I'm invincible
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public int getAttackX() {
        return attackX;
    }

    public int getAttackY() {
        return attackY;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public int getPlayerXP() {
        return playerXP - xpToLastLevel;
    }

    public int getXpToNextLevel() {
        return xpToNextLevel - xpToLastLevel;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Armour getArmour() {
        return armour;
    }

    public void setArmour(Armour armour) {
        this.armour = armour;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getHp() {
        return hp;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public int getDamage() {
        return damage + weapon.getDamage();
    }

    public double getAttackSpeed() {
        return weapon.getAttackSpeed() * attackSpeedMultiplier;
    }
}
