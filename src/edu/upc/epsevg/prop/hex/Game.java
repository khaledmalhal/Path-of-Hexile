package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.players.HumanPlayer;
//import edu.upc.epsevg.prop.hex.players.PlayerID;
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
                IPlayer player1 = new HumanPlayer( "Human");
                IPlayer player2 = new PathOfMinMax("PathOfMinMax", 4);
                // IPlayer player2 = new H_E_X_Player(2/*GB*/);

                new Board(player1, player2, 5 /*mida*/, 10/*s*/, false);
            }
        });
    }
}
