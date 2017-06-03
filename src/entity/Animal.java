package entity;

import admin.Game;
import static entity.Animal.AnimalType.*;
import map.CurrentLevel;

/**
 *
 * @author Joe
 * 
 * Base class for all huntable animals
 */
public class Animal extends Enemy{
    
    public enum AnimalType { MOA, DEER, RABBIT, BOAR }
    
    private AnimalType type;
    private boolean dead;
    private String deadSprite;

    public Animal(Game game, CurrentLevel level, AnimalType type) {
        this.game = game;
        this.level = level;
        this.type = type;
        getData(type);
        dead = false;
    }
    
    //Moves idly if not dead
    @Override
    public void update() {
        if(!dead) {
            idleMovement();
        }
    }
    
    //Get damaged, will go into dead state if it has no HP left
    @Override
    public void getDamaged(int damage, Entity source) {
        if(!dead) {
            hp -= damage;
            if(hp < 1) {
                if(source instanceof Player) {
                    game.getPlayer().addXP(10);
                }
                game.getTribe().removeEnemy(this);
                sprite = deadSprite;
                dead = true;
                game.removeAnimal();
            }
        }
    }
    
    //Get harvested, returns its food value and then removes itself
    public int getHarvested() {
        remove = true;
        if(type == MOA) return 20;
        if(type == DEER) return 10;
        if(type == BOAR) return 10;
        if(type == RABBIT) return 5;
        return 0;
    }
    
    //Data fields for all animals. If we add heaps more animals this should be moved into a data class.
    private void getData(AnimalType type) {
        if(type == MOA) {
            sprite = "moa";
            deadSprite = "moa_dead"; 
            spriteCentreX = 18; 
            spriteCentreY = 16; 
            hp = 30;
        } 
        if(type == DEER) {
            sprite = "deer"; 
            deadSprite = "deer_dead"; 
            spriteCentreX = 8;
            spriteCentreY = 10; 
            hp = 15;
        }
        if(type == BOAR) {
            sprite = "boar"; 
            deadSprite = "boar_dead"; 
            spriteCentreX = 8; 
            spriteCentreY = 5; 
            hp = 20;
        }
        if(type == RABBIT) {
            sprite = "rabbit"; 
            deadSprite = "rabbit_dead"; 
            spriteCentreX = 5; 
            spriteCentreY = 5; 
            hp = 5;
        }
    }

    public boolean isDead() {
        return dead;
    }
}
