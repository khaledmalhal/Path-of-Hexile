package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.players.HumanPlayer;
import edu.upc.epsevg.prop.hex.players.RandomPlayer;

import edu.upc.epsevg.prop.hex.players.PlayerID;
import edu.upc.epsevg.prop.hex.players.H_E_X_Player;
import edu.upc.epsevg.prop.hex.players.PathOfMinMax;



import javax.swing.SwingUtilities;

/**
 * Checkers: el joc de taula.
 * @author bernat
 */
public class Game {
    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
<<<<<<< Updated upstream
                // IPlayer player1 = new HumanPlayer( "Human");
                IPlayer player1 = new PathOfMinMax("PathOfMinMax", 4);
                IPlayer player2 = new H_E_X_Player(2/*GB*/);
=======
                //IPlayer player1 = new HumanPlayer( "Human");
                IPlayer player2 = new PathOfMinMax("PathOfMinMax", 4);
                IPlayer player1 = new H_E_X_Player(2/*GB*/);
                 //IPlayer player1 = new RandomPlayer("r");
                //IPlayer player2 = new PlayerID("PlayerID");

>>>>>>> Stashed changes

                new Board(player1, player2, 7 /*mida*/, 40/*s*/, false);
            }
        });
    }
}
