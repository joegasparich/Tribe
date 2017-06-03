package entity;

import admin.Game;
import admin.JMath;
import static entity.TribeMember.Gender.*;
import static entity.TribeMember.Job.*;
import entity.pathfinding.PathFinder;
import map.CurrentLevel;
import entity.resources.BerryBush;
import entity.resources.Rock;
import entity.resources.Tree;
import static items.ItemData.WeaponEnum.*;
import static items.ItemData.newWeapon;
import items.Weapon;
import tribe.Tribe;
import static tribe.Tribe.TribeResource.*;

/**
 *
 * @author Joe
 * 
 * Tribe Member, does jobs and shit
 */
public class TribeMember extends Mover{
    
    //Enums
    public enum Job { MINE, CUT, GATHER, HUNT, HAUL, FIGHT, NURTURE }
    public enum Gender { MALE, FEMALE }
    public enum FightingClass { WARRIOR, ARCHER, SHAMAN }
    
    //Constants
    private static final int CUT_TIME = 100;
    private static final int GATHER_TIME = 100;
    private static final int MINE_TIME = 100;
    private static final int HUNT_TIME = 200;
    
    //Job related variables
    private int workTimer = 0;
    private Job currentJob = null;
    private Job pausedJob = null;
    private Entity pausedTarget = null;
    private boolean pausedHunting = false;
    private boolean hunting = false;
    
    private Game game;
    private CurrentLevel level;
    private Tribe tribe;
    
    //Stats
    private int hp = 100; //Should be generated
    private int damage = 0; //Should be generated
    private int attackSpeedMultiplier = 2; //Should be generated
    
    //Movement/Attack variables
    private boolean canMove = true;
    private double targetX = -1;
    private double targetY = -1;
    private int attackTime = 0;
    
    //Inventory
    private Weapon weapon;
    
    public Gender gender;
    public FightingClass fightingClass;

    //Constructor
    public TribeMember(Game game, CurrentLevel level, Tribe tribe) {
        this.game = game;
        this.level = level;
        this.tribe = tribe;
        spriteCentreX = 4;
        spriteCentreY = 4;
//        Randomly assigns gender, temporary
        if(JMath.randomBoolean() == false) {
            gender = MALE;
        } else {
            gender = FEMALE;
        }
        gender = FEMALE;
        if(gender == MALE) sprite = "tribe_member_male";
        if(gender == FEMALE) sprite = "tribe_member_female";
        pf = new PathFinder(level, level.getTileArray(), 100, true, heuristic);
        weapon = newWeapon(SHORT_BOW); //Temp
    }

    //Called every tick
    @Override
    public void update() {
        //Counters
        if(newPath > 0) newPath--;
        if(attackTime > 0) attackTime--;
        //Get job if don't have one
        if(currentJob == null) {
            currentJob = tribe.getJob(gender);
        }
        Enemy nearestEnemy = null;
        if(gender == FEMALE) {
            double minDist = 100;
            for(Entity entity : game.getEntities()) {
                if(entity instanceof Colonist) {
                    Colonist enemy = (Colonist) entity;
                    if(!tribe.entityIsTarget(entity) && JMath.getDistance(x, y, entity.getX(), entity.getY()) < minDist) {
                        minDist = JMath.getDistance(x, y, entity.getX(), entity.getY());
                        nearestEnemy = enemy;
                    }
                }
            }
        }
        if(nearestEnemy != null) {
            if(JMath.getDistance(x, y, nearestEnemy.getX(), nearestEnemy.getY()) < 90) {
                runFromTarget(nearestEnemy);
            }
        } else {
            //Do job if you have one
            if(currentJob != null) {
                doJob();
            }
            if(canMove) {
                //If no job and outside of village, move back to village and move idly
                if(currentJob == null && pausedJob == null) {
                    if(JMath.getDistance(x, y, tribe.getCentreX(), tribe.getCentreY()) > tribe.getSize()) {
                        moveToLocation(tribe.getCentreX(), tribe.getCentreY());
                    } else {
                        idleMovement();
                    }
                }
                //If has target(i.e. job), go to the target of the job
                if(entityTarget != null) {
                    walkToTarget();
                }
            }
        }
    }
    
    //Switch statement with the code for each job. Could possibly split into seperate methods to be cleaner
    private void doJob() {
        switch(currentJob) {
            //Cut nearest tree that hasn't already been harvested or targeted by another tribe member
            case CUT: 
                cutTree();
                break;
            //Mine nearest rock that hasn't already been harvested or targeted by another tribe member
            case MINE: 
                mineRocks();
                break;
            //Gather from nearest berry bush that hasn't already been harvested or targeted by another tribe member
            case GATHER: 
                harvestBush();
                break;
            //Fight target, can be called by tribe, hunting or being attacked
            case FIGHT:
                fightTarget();
                break;
            //Hunt nearest animal
            case HUNT: 
                huntAnimal();
                break;
            default: 
                break;
        }
    }
    
    private void cutTree() {
        //Find nearest Tree
        if(entityTarget == null) {
            Tree nearestTree = null;
            double minDist = 10000;
            for(Entity entity : game.getEntities()) {
                if(entity instanceof Tree ) {
                    Tree tree = (Tree) entity;
                    if(!tribe.entityIsTarget(entity) && JMath.getDistance(x, y, entity.getX(), entity.getY()) < minDist && tree.isHarvestable()) {
                        minDist = JMath.getDistance(x, y, entity.getX(), entity.getY());
                        nearestTree = tree;
                    }
                }
            }
            entityTarget = nearestTree;
            //No (harvestable) tree on map, fuck this job off
            if(entityTarget == null) {
                System.out.println("no trees");
                entityTarget = null;
                currentJob = null;
                canMove = true;
                return;
            }
        }
        //Cut tree if in range, tribe member will auto walk to any target so we set canMove to false to stop them
        if(entityTarget != null && JMath.getDistance(x, y, entityTarget.getX(), entityTarget.getY()) < 10) {
            canMove = false;
            //Start cutting tree
            if(workTimer == 0) {
                workTimer = CUT_TIME;
            }
            //Finish cutting tree
            if(workTimer == 1) {
                Tree tree = (Tree) entityTarget;
                tree.setHarvestable(false);
                entityTarget = null;
                currentJob = null;
                canMove = true;
                tribe.addResource(WOOD, 10);
            }
            workTimer--;
        }
    }
    
    private void mineRocks() {
        //Find nearest rock
        if(entityTarget == null) {
            Rock nearestRock = null;
            double minDist = 10000;
            for(Entity entity : game.getEntities()) {
                if(entity instanceof Rock ) {
                    Rock rock = (Rock) entity;
                    if(!tribe.entityIsTarget(entity) && JMath.getDistance(x, y, entity.getX(), entity.getY()) < minDist && rock.isHarvestable()) {
                        minDist = JMath.getDistance(x, y, entity.getX(), entity.getY());
                        nearestRock = rock;
                    }
                }
            }
            entityTarget = nearestRock;
            //No (harvestable) rock on map, fuck this job off
            if(entityTarget == null) {
                System.out.println("no rocks");
                entityTarget = null;
                currentJob = null;
                canMove = true;
                return;
            }
        }
        //Mine rock if in range, tribe member will auto walk to any target so we set canMove to false to stop them
        if(entityTarget != null && JMath.getDistance(x, y, entityTarget.getX(), entityTarget.getY()) < 10) {
            canMove = false;
            //Start mining
            if(workTimer == 0) {
                workTimer = MINE_TIME;
            }
            //Finish mining
            if(workTimer == 1) {
                Rock rock = (Rock) entityTarget;
                rock.setHarvestable(false);
                entityTarget = null;
                currentJob = null;
                canMove = true;
                tribe.addResource(STONE, 10);
            }
            workTimer--;
        }
    }
    
    private void harvestBush() {
        //Find nearest bush
        if(entityTarget == null) {
            BerryBush nearestBush = null;
            double minDist = 10000; 
            for(Entity entity : game.getEntities()) {
                if(entity instanceof BerryBush ) {
                    BerryBush bush = (BerryBush) entity;
                    if(!tribe.entityIsTarget(entity) && JMath.getDistance(x, y, entity.getX(), entity.getY()) < minDist && bush.isHarvestable()) {
                        minDist = JMath.getDistance(x, y, entity.getX(), entity.getY());
                        nearestBush = bush;
                    }
                }
            }
            entityTarget = nearestBush;
            //No (harvestable) bush on map, fuck this job off
            if(entityTarget == null) {
                System.out.println("no bushes");
                entityTarget = null;
                currentJob = null;
                canMove = true;
                return;
            }
        }
        //Harbest bush if in range, tribe member will auto walk to any target so we set canMove to false to stop them
        if(entityTarget != null && JMath.getDistance(x, y, entityTarget.getX(), entityTarget.getY()) < 10) {
            canMove = false;
            //Start harvesting
            if(workTimer == 0) {
                workTimer = GATHER_TIME;
            }
            //Finish harvesting
            if(workTimer == 1) {
                BerryBush bush = (BerryBush) entityTarget;
                bush.setHarvestable(false);
                entityTarget = null;
                currentJob = null;
                canMove = true;
                tribe.addResource(FOOD, 10);
            }
            workTimer--;
        }
    }
    
    private void fightTarget() {
        if(entityTarget == null) {
            //Get next enemy off of the enemy list
            entityTarget = tribe.getEnemy();
            //If no enemy on list, leave job
            if(entityTarget == null) {
                currentJob = null;
                canMove = true;
                return;
            }
        }
        //If in range fight enemy or finish up after enemy has died
        if(entityTarget != null && JMath.getDistance(x, y, entityTarget.getX(), entityTarget.getY()) < weapon.getRange()) {
            //If hunting, create animal from target so we can access animal specific methods
            Animal animal = null;
            if(hunting) {
                 animal = (Animal) entityTarget;
            }
            //If the tribe's target is dead and is not an animal, or we are hunting and the animal is dead, or was a local fight and enemy is dead, exit fighting
            if((pausedJob == null && !tribe.containsEnemy((Enemy) entityTarget) && !(entityTarget instanceof Animal))
                    || (hunting && animal.isDead())
                    || (pausedJob != null && (entityTarget.isRemove() || entityTarget == null))) {
                //^ god awful boolean statement
                //We are only here if the target has died
                //Change job back to hunting if we were hunting
                if(hunting) {
                    currentJob = HUNT;
                //If job was paused because of being attacked, return to that job
                } else if(pausedJob != null) {
                    currentJob = pausedJob;
                    entityTarget = pausedTarget;
                    hunting = pausedHunting;
                    pausedJob = null;
                    pausedTarget = null;
                    pausedHunting = false;
                //If was a tribe called fight, end job
                } else{
                    currentJob = null;
                    entityTarget = null;
                }
                canMove = true;
                return;
            }
            //If enemy isn't dead, fight it
            canMove = false;
            if(attackTime == 0) {
                attackTarget();
            }
        //Not in range, go to enemy.
        } else {
            canMove = true;
        }
    }
    
    private void huntAnimal() {
        //Set hunting to true so we can return after entering fight
        hunting = true;
        //Find nearest target animal
        if(entityTarget == null) {
            //Prioritises already dead animals (ie. animals you've killed)
            Animal nearestAnimal = null;
            Animal nearestDeadAnimal = null;
            double minDist = 10000; 
            double minDeadDist = 10000; 
            for(Entity entity : game.getEntities()) {
                if(entity instanceof Animal) {
                    Animal a = (Animal) entity;
                    if(!tribe.entityIsTarget(a) && JMath.getDistance(x, y, a.getX(), a.getY()) < minDeadDist && a.isDead()) {
                        minDeadDist = JMath.getDistance(x, y, a.getX(), a.getY());
                        nearestDeadAnimal = a;
                    }
                    if(!tribe.entityIsTarget(a) && JMath.getDistance(x, y, a.getX(), a.getY()) < minDist) {
                        minDist = JMath.getDistance(x, y, a.getX(), a.getY());
                        nearestAnimal = a;
                    }
                }
            }
            entityTarget = nearestAnimal;
            //Override nearest animal with nearest dead animal if there is one
            if(nearestDeadAnimal != null) entityTarget = nearestDeadAnimal;
            //No (untargeted) animals left on map, fuck this job off
            if(entityTarget == null) {
                System.out.println("no animals");
                entityTarget = null;
                currentJob = null;
                canMove = true;
                return;
            }
        }
        //If we have a target animal
        if(entityTarget != null) {
            Animal animal = (Animal) entityTarget;
            //If we've killed (or the player has killed) the animal go harvest it
            if(animal.isDead()) {
                if(JMath.getDistance(x, y, entityTarget.getX(), entityTarget.getY()) < 10) {
                    canMove = false;
                    //Start harvesting
                    if(workTimer == 0) {
                        workTimer = HUNT_TIME;
                    }
                    //Finish harvesting
                    if(workTimer == 1) {
                        entityTarget = null;
                        currentJob = null;
                        canMove = true;
                        tribe.addResource(FOOD, animal.getHarvested());
                        hunting = false;
                    }
                    workTimer--;
                }
            //If the animal isn't dead, enter fight. Job will return here once its dead
            } else {
                currentJob = FIGHT;
            }
        }
    }
    
    //Sit still or move to a random location in the village
    private void idleMovement() {
        //-1 if sitting still
        if(targetX == -1 && targetY == -1) {
            if(JMath.randomInteger(0, 300) == 0) {
                double tribeLength = Math.sqrt((Math.pow(2 * tribe.getSize() , 2))/2);
                targetX = JMath.randomDouble(tribe.getCentreX() - tribeLength/2, tribe.getCentreX() + tribeLength/2);
                targetY = JMath.randomDouble(tribe.getCentreY() - tribeLength/2, tribe.getCentreY() + tribeLength/2);
            }
        } else if(Math.abs(x - targetX) < 2 && Math.abs(y - targetY) < 2) {
            targetX = -1;
            targetY = -1;
        } else {
            moveToLocation(targetX, targetY);
        }
    }
    
    private void walkToTarget() {
        if(!level.collisionLine(x, y, entityTarget.getX(), entityTarget.getY())) {
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
    }
    
    private void runFromTarget(Entity target) {
        double xDiff = x - target.getX();
        double yDiff = y - target.getY();
        double distance = Math.sqrt((xDiff * xDiff)+(yDiff * yDiff));
        xDir = xDiff / distance * moveSpeed;
        yDir = yDiff / distance * moveSpeed;
        if(level.isPlaceFree(x + xDir, y)) x += xDir;
        if(level.isPlaceFree(x, y + yDir)) y += yDir;
    }
    
    //Fire projectiles at target
    private void attackTarget() {
        //Create new projcetile in the direction of the target and add it to the collection
        Projectile proj = new Projectile(game, x, y, JMath.getAngle(x, y, entityTarget.getX(), entityTarget.getY()), weapon.getDamage() + this.damage, weapon.getRange(), weapon.getType(), this);
        game.addEntity(proj);
        //Set the attackTime to attackSpeed
        attackTime = (int) (weapon.getAttackSpeed() * attackSpeedMultiplier);
    }
    
    //Get damage
    public void getDamaged(int damage, Entity source) {
        //If we aren't already fighting, and we're a male, pause job and fight 
        if(currentJob != FIGHT && gender == MALE) {
            pausedJob = currentJob;
            pausedTarget = entityTarget;
            pausedHunting = hunting;
            canMove = true;
            currentJob = FIGHT;
            entityTarget = source;
            hunting = false;
        }
        hp -= damage;
        //die
        if(hp < 1) {
            //rip
            tribe.removeMember(this);
            remove = true;
        }
    }
    
    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Weapon getWeapon() {
        return weapon;
    }
}
