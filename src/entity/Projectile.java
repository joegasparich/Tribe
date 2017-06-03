package entity;

import admin.Game;
import admin.JMath;
import items.ItemData;
import items.ItemData.ProjectileType;
import java.util.ArrayList;

/**
 *
 * @author Joe
 * 
 * Projectile object
 */
public class Projectile extends Entity{
    
    //Currently all projectiles have speed 2, may change later
    private static final double SPEED = 2;
    
    private int distance = 0; //Once this reaches range, the projectile will disappear
    
    //Stats
    private double angle;
    private int damage;
    private int range;
    private ProjectileType type;
    private Entity source;

    //Constructor
    public Projectile(Game game, double x, double y, double angle, int damage, int range, ProjectileType type, Entity source) {
        this.game = game;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.damage = damage;
        this.range = range;
        this.type = type;
        this.sprite = ItemData.getProjectileSprite(type);
        this.source = source;
    }
    
    //Called every tick
    @Override
    public void update() {
        //Move projectile speed amount in the direction of angle
        x += SPEED * Math.cos(Math.toRadians(angle));
        y += SPEED * Math.sin(Math.toRadians(angle));
        //Remove projectile once its hit its range
        ArrayList<Entity> entities = game.getEntities();
        for(Entity e : entities) {
            if((source instanceof TribeMember || source instanceof Player) && e instanceof Enemy && JMath.getDistance(x, y, e.getX(), e.getY()) < 6) {
                Enemy enemy = (Enemy) e;
                enemy.getDamaged(damage, source);
                remove = true;
            }
            if(source instanceof Enemy && e instanceof TribeMember && JMath.getDistance(x, y, e.getX(), e.getY()) < 6) {
                TribeMember tm = (TribeMember) e;
                tm.getDamaged(damage, source);
                remove = true;
            }
            if(source instanceof Enemy && e instanceof Player && JMath.getDistance(x, y, e.getX(), e.getY()) < 6) {
                Player p = (Player) e;
                p.getDamaged(damage, source);
                remove = true;
            }
        }
        distance += SPEED;
        if(distance > range) {
            remove = true;
        }
    }

    //Getters and Setters
    public ProjectileType getType() {
        return type;
    }

    public double getAngle() {
        return angle;
    }
}
