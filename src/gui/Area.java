/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author Joe
 */
public class Area {
    
    private int x;
    private int y;
    private int width;
    private int height;

    public Area(int x1, int y1, int width, int height) {
        this.x = x1;
        this.y = y1;
        this.width = width;
        this.height = height;
    }

    public int getX1() {
        return x;
    }

    public int getY1() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public boolean inArea(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }
}
