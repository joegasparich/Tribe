package entity;

import admin.Game;
import admin.JMath;
import static entity.Mover.NEW_PATH_TIMER;
import entity.pathfinding.PathFinder;
import static items.ItemData.WeaponEnum.*;
import static items.ItemData.newWeapon;
import items.Weapon;
import map.CurrentLevel;

/**
 *
 * @author Joe
 * 
 * Colonist, should be renamed into Enemy and Enemy renamed to Killable or something
 * Colonists and all other hostile enemies should extend this class
 */
public class Colonist extends Enemy{
    
    //Data fields
    private int damage = -2; //Should be generated
    private int attackSpeedMultiplier = 2; //Should be generated
    private int attackTime = 0; //This gets set to attackSpeed and counts down to when you can attack again
    private double visionRange = 150;
    private int targetTimer = 20;
    
    //Inventory
    private Weapon weapon;
    
    //Constructor
    public Colonist(Game game, CurrentLevel level, String sprite) {
        this.game = game;
        this.level = level;
        this.sprite = sprite;
        this.spriteCentreX = 4;
        this.spriteCentreY = 4;
        this.hp = 10;
        this.weapon = newWeapon(SPEAR);
        pf = new PathFinder(level, level.getTileArray(), 100, true, heuristic);
    }
    
    //Called every tick
    @Override
    public void update() {
        //Checks every so often for a new target
        if(targetTimer > 0) targetTimer--;
        if(targetTimer == 0) {
            if(entityTarget == null) findTarget();
            //If in range of the tribe, add it to the job list
            if(JMath.getDistance(x, y, game.getTribe().getCentreX(), game.getTribe().getCentreY()) < game.getTribe().getSize() * 4) {
                if(!remove) {
                    game.getTribe().addEnemy(this);
                }
            }
        }
        //Counters
        if(attackTime > 0) attackTime--;
        if(newPath > 0) newPath--;
        //If has a target
        if(entityTarget != null) {
            //Attack if in range
            if(JMath.getDistance(x, y, entityTarget.getX(), entityTarget.getY()) < weapon.getRange()) {
                if(attackTime == 0) {
                    attackTarget();
                }
            //Else move
            } else if(!level.collisionLine(x, y, entityTarget.getX(), entityTarget.getY())) {
                moveToTarget(entityTarget);
                newPath = 0;
            } else {
                if(path != null && newPath > 0) {
                    moveToLocation(path.getStep(step).getX() * game.TILE_SIZE, path.getStep(step).getY() * game.TILE_SIZE);
                } else {
                    findNewPath();
                    newPath = NEW_PATH_TIMER;
                }
            }
        //If no target, move idly
        } else {
            idleMovement();
        }
    }
    
    //Fires projectiles at target
    private void attackTarget() {
        //Create new projcetile in the direction of the target and add it to the collection
        Projectile proj = new Projectile(game, x, y, JMath.getAngle(x, y, entityTarget.getX(), entityTarget.getY()), weapon.getDamage() + this.damage, weapon.getRange(), weapon.getType(), this);
        game.addEntity(proj);
        //Set the attackTime to attackSpeed
        attackTime = (int) (weapon.getAttackSpeed() * attackSpeedMultiplier);
    }
    
    //Get damaged
    @Override
    public void getDamaged(int damage, Entity source) {
        hp -= damage;
        //Swaps target if hasn't already been targeted by a tribe member
        if(game.getTribe().entityIsTarget(this)) {
            if(source instanceof TribeMember) {
                entityTarget = source;
            }
        } else {
            entityTarget = source;
        }
        //Die
        if(hp < 1) {
            //Give player xp if player hit the final blow
            if(source instanceof Player) {
                game.getPlayer().addXP(10);
            }
            game.getTribe().removeEnemy(this);
            remove = true;
        }
    }
    
    //Search for nearest relevant target in range
    private void findTarget() {
        Entity nearestTarget = null;
        double minDist = visionRange;
        for(Entity entity : this.game.getEntities()) {
            if(entity instanceof Player || entity instanceof TribeMember) {
                if(JMath.getDistance(x, y, entity.getX(), entity.getY()) < minDist) {
                    minDist = JMath.getDistance(x, y, entity.getX(), entity.getY());
                    nearestTarget = entity;
                }
            }
        }
        entityTarget = nearestTarget;
    }

    public Weapon getWeapon() {
        return weapon;
    }
}
