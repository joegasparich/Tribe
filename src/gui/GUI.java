package gui;

import admin.Game;
import static admin.Game.WINDOW_SIZE_X;
import static admin.Game.WINDOW_SIZE_Y;
import entity.TribeMember;
import static gui.GUI.Tab.*;
import java.awt.MouseInfo;
import java.awt.Point;


/**
 *
 * @author Joe
 * 
 * Handles clicking on GUI
 */
public class GUI {
    
    public static enum Tab { CHARACTER, TRIBE, BUILDING }
    
    private static Tab currentTab = CHARACTER;
    private static int mouseX;
    private static int mouseY;
    
    private static int windowX = (WINDOW_SIZE_X + 10);
    private static int windowY = (WINDOW_SIZE_Y + 10);
    
    public static Area guiMain = new Area(0, windowY - 138, 366, 138);
    public static Area guiTab = new Area(0, windowY - 201, 242, 63);
    public static Area guiTop = new Area(0, 0, 288, 30);
    
    public static Area tabCharacter = new Area(0, windowY - 201, 84, 68);
    public static Area tabTribe = new Area(79, windowY - 201, 84, 68);
    public static Area tabBuilding = new Area(158, windowY - 201, 84, 68);
    
    public static Area buttonCut = new Area(250, windowY - 130, 52, 38);
    public static Area buttonMine = new Area(306, windowY - 130, 52, 38);
    public static Area buttonGather = new Area(250, windowY - 88, 52, 38);
    public static Area buttonHunt = new Area(306, windowY - 88, 52, 38);
    public static Area buttonHaul = new Area(250, windowY - 46, 52, 38);
    public static Area buttonASDF = new Area(306, windowY - 46, 52, 38);
    
    public static void click(Game game) {
        Point p = MouseInfo.getPointerInfo().getLocation(); //Get mouse location
        Point q = game.getLocationOnScreen();
        //Subtracts the game location from the mouse location to get the relative mouse location
        mouseX = (p.x - q.x);
        mouseY = (p.y - q.y);
        
        if(tabCharacter.inArea(mouseX, mouseY)) currentTab = CHARACTER;
        else if(tabTribe.inArea(mouseX, mouseY)) currentTab = TRIBE;
        else if(tabBuilding.inArea(mouseX, mouseY)) currentTab = BUILDING;
        
        else if(buttonCut.inArea(mouseX, mouseY)) game.getTribe().addJob(TribeMember.Job.CUT);
        else if(buttonMine.inArea(mouseX, mouseY)) game.getTribe().addJob(TribeMember.Job.MINE);
        else if(buttonGather.inArea(mouseX, mouseY)) game.getTribe().addJob(TribeMember.Job.GATHER);
        else if(buttonHunt.inArea(mouseX, mouseY)) game.getTribe().addJob(TribeMember.Job.HUNT);
    }
    
    public static boolean isGUI(Game game) {
        Point p = MouseInfo.getPointerInfo().getLocation(); //Get mouse location
        Point q = game.getLocationOnScreen();
        //Subtracts the game location from the mouse location to get the relative mouse location
        mouseX = (p.x - q.x);
        mouseY = (p.y - q.y);
        
        if(guiMain.inArea(mouseX, mouseY) || guiTab.inArea(mouseX, mouseY) || guiTop.inArea(mouseX, mouseY)) {
            return true;
        }
        return false;
    }

    public static Tab getCurrentTab() {
        return currentTab;
    }
}
