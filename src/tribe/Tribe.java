package tribe;

import admin.Game;
import admin.JMath;
import entity.Enemy;
import entity.Entity;
import entity.Player;
import entity.TribeMember;
import entity.TribeMember.Gender;
import entity.TribeMember.Job;
import java.util.ArrayList;
import map.CurrentLevel;
import static tribe.Tribe.TribeResource.*;

/**
 *
 * @author Joe
 * 
 * Contains data about the tribe
 */
public class Tribe {
    
    public static enum TribeResource { WOOD, FOOD, STONE }
    
    private Game game;
    private Player player;
    private CurrentLevel level;
    
    //Resources
    private int resourceFood = 0;
    private int resourceWood = 0;
    private int resourceStone = 0;
    
    private double centreX;
    private double centreY;
    private double size;
    
    private int xp = 10;
    private int xpToNextLevel = 20;
    private int xpToLastLevel = 0;
    
    //Data
    private ArrayList<TribeMember> tribeMembers = new ArrayList<>();
    private ArrayList<Building> buildings = new ArrayList<>();
    private JobList jobList = new JobList();

    //Constructor
    public Tribe(Game game, double centreX, double centreY, int numberOfMembers) {
        this.game = game;
        this.centreX = centreX;
        this.centreY = centreY;
        this.size = 50;
        player = game.getPlayer();
        level = game.getCurrentLevel();
        for(int i = 0; i < numberOfMembers; i++) {
            TribeMember t = new TribeMember(game, level, this);
            t.setPos(centreX + JMath.randomDouble(-50, 50), centreY + JMath.randomDouble(-50, 50));
            tribeMembers.add(t);
            game.addEntity(t);
        }
    }
    
    public Job getJob(Gender gender) {
        return jobList.getJob(gender);
    }
    
    public void addJob(Job job) {
        jobList.addJob(job);
    }
    
    public Enemy getEnemy() {
        return jobList.getEnemy();
    }
    
    public void addEnemy(Enemy enemy) {
        jobList.addEnemy(enemy);
    }
    
    public void removeEnemy(Enemy enemy) {
        jobList.removeEnemy(enemy);
    }
    
    public boolean containsEnemy(Enemy enemy) {
        return jobList.containsEnemy(enemy);
    }
    
    public boolean entityIsTarget(Entity e) {
        for(TribeMember t : tribeMembers) {
            if(t.getEntityTarget() == e) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<TribeMember> getTribeMembers() {
        return tribeMembers;
    }
    
    public void removeMember(TribeMember tm) {
        tribeMembers.remove(tm);
    }
    
    public void addResource(TribeResource type, int amount) {
        if(type == WOOD) resourceWood += amount;
        if(type == STONE) resourceStone += amount;
        if(type == FOOD) resourceFood += amount;
    }

    public int getResourceFood() {
        return resourceFood;
    }

    public int getResourceWood() {
        return resourceWood;
    }

    public int getResourceStone() {
        return resourceStone;
    }

    public double getCentreX() {
        return centreX;
    }

    public double getCentreY() {
        return centreY;
    }

    public double getSize() {
        return size;
    }

    public int getTribeXP() {
        return xp - xpToLastLevel;
    }

    public int getXpToNextLevel() {
        return xpToNextLevel - xpToLastLevel;
    }
    
    public int getTribeMemberCount() {
        return tribeMembers.size();
    }
}
