package admin;

import javax.swing.JFrame;


/**
 *
 * @author Joe
 */
public class Main {

    //Method runs when the game is started
    public static void main(String[] args) {
        Game game = new Game(1200, 800); //Create new game object with this window size
        game.frame.setResizable(false); //Resizing temporarily disabled
        game.frame.setTitle("Tribe"); //Title of window
        game.frame.add(game); //Add game to the frame
        game.frame.setLocationRelativeTo(null);
        game.frame.setVisible(true);
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.start(); //Starts the game thread
    }

}
