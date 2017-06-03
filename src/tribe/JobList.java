package tribe;

import entity.Enemy;
import entity.TribeMember.Gender;
import entity.TribeMember.Job;
import static entity.TribeMember.Job.*;
import java.util.ArrayList;

/**
 *
 * @author Joe
 * 
 * Array list of jobs, works as a queue
 */
public class JobList {
    
    private ArrayList<Job> jobList;
    private ArrayList<Enemy> enemyList;

    public JobList() {
        this.jobList = new ArrayList<>();
        this.enemyList = new ArrayList<>();
    }
    
    public void addJob(Job job) {
        jobList.add(job);
    }
    
    public Job getJob(Gender gender) {
        switch(gender) {
            case MALE:
                //Males prioritise fighting
                if(jobList.contains(FIGHT)) {
                    for(Job job : jobList) {
                        if(job == FIGHT) {
                            jobList.remove(job);
                            return job;
                        }
                    }
                } else {
                    for(Job job : jobList) {
                        //Put female only jobs here
                        if(job != NURTURE) {
                            jobList.remove(job);
                            return job;
                        }
                    }
                }
                //No applicable jobs
                break;
                
            case FEMALE:
                for(Job job : jobList) {
                    //Put male only jobs here
                    if(job != FIGHT && job != HUNT) {
                        jobList.remove(job);
                        return job;
                    }
                }
                //No applicable jobs
                break;
        }
        return null;
    }
    
    public void addEnemy(Enemy enemy) {
        if(!enemyList.contains(enemy)) {
            enemyList.add(enemy);
            addJob(FIGHT);
        }
    }
    
    public Enemy getEnemy() {
        for(Enemy enemy : enemyList) {
            removeEnemy(enemy);
            addEnemy(enemy); //Re adds enemy to the end of the list so multiple tribemembers can attack
            return enemy;
        }
        return null;
    }
    
    public void removeEnemy(Enemy enemy) {
        enemyList.remove(enemy);
    }
    
    public boolean containsEnemy(Enemy enemy) {
        return enemyList.contains(enemy);
    }
}
