/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

/**
 *
 * @author Joe
 */
public class JMath {
    
    //Finds the angle between two points from the vertical
    public static double getAngle(double x1, double y1, double x2, double y2) {
        double angle = (double) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }
    
    //Finds distance between two points using pythagoras
    public static double getDistance(double x1, double y1, double x2, double y2) {
        double xdiff = x1 - x2;
        double ydiff = y1 - y2;
        return Math.sqrt((xdiff*xdiff)+(ydiff*ydiff));
    }
    
    //Returns a random double between min and max
    public static double randomDouble(double min, double max) {
        return min + (double)(Math.random() * ((max - min) + 1));
    }

    //Returns a random integer between min and max
    public static int randomInteger(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
    
    //Returns a random boolean
    public static boolean randomBoolean() {
        int bool = randomInteger(0, 1);
        if(bool == 1) return true;
        else return false;
    }
}
