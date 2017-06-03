package entity;

import admin.JMath;

/**
 *
 * @author Joe
 * 
 * The base class for anything that the player or tribemembers can kill
 * Should be renamed to killable
 */
public class Enemy extends Mover {
    
    public double targetX = -1;
    public double targetY = -1;
    
    public int hp;
    
    //Gets damaged
    public void getDamaged(int damage, Entity source) {
        hp -= damage;
        //Die
        if(hp < 1) {
            //Give player xp if they hit the final blow
            if(source instanceof Player) {
                game.getPlayer().addXP(10);
            }
            game.getTribe().removeEnemy(this);
            remove = true;
        }
    }
    
    //Either sit still or find a new target in a small range around it
    public void idleMovement() {
        //-1 if sitting still
        if(targetX == -1 && targetY == -1) {
            if(JMath.randomInteger(0, 300) == 0) {
                do {
                targetX = JMath.randomDouble(x - 50, x + 50);
                targetY = JMath.randomDouble(y - 50, y + 50);
                } while(!level.isPlaceFree(targetX, targetY));
            }
        } else if(Math.abs(x - targetX) < 2 && Math.abs(y - targetY) < 2) {
            targetX = -1;
            targetY = -1;
        } else {
            moveToLocation(targetX, targetY);
        }
    }
    
    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
