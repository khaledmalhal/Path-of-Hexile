package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.*;
import edu.upc.epsevg.prop.hex.heuristic.Dijkstra;

import java.awt.Point;
import java.util.List;

/**
 * Clase que implementa el algoritmo MinMax con la poda alpha-beta para determinar la mejor jugada en Hex.
 */
public class PlayerID implements IPlayer, IAuto {
    private String name = "PathOfMinMax";
    private PlayerType myType, enemyType;
    private int boardSize;
    private Dijkstra dijkstra;

    private long numNodes;
    private boolean timeoutReached;

    /**
     * Constructor de la clase PathOfMinMax.
     * @param name Nombre del bot.
     */
    public PlayerID(String name) {
        this.name = name;
        this.timeoutReached = false; // Control de timeout
    }

    /**
     * Retorna el nombre asignado del bot.
     * @return Nombre del bot.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Implementación de la jugada que el bot ha de realizar.
     * Usa iterative deepening con timeout para determinar la mejor jugada.
     * @param hgs Estado actual del juego.
     * @return Retorna la mejor jugada encontrada antes del timeout.
     */
    @Override
    public PlayerMove move(HexGameStatus hgs) {
        this.myType = hgs.getCurrentPlayer();
        this.boardSize = hgs.getSize();
        this.enemyType = PlayerType.opposite(myType);
        this.dijkstra = new Dijkstra(this.boardSize);

        PlayerMove bestMove = null;

        // Iterative Deepening: Incrementa la profundidad hasta que se alcance el timeout
        for (int currentDepth = 1; !timeoutReached; currentDepth++) {
            bestMove = iterativeDeepening(hgs, currentDepth, bestMove);
        }

        return bestMove;
    }

    /**
     * Marca el timeout como alcanzado.
     */
    @Override
    public void timeout() {
        this.timeoutReached = true; // Indica que se ha alcanzado el tiempo límite
    }

    /**
     * Realiza iterative deepening aumentando la profundidad de búsqueda.
     *
     * @param hgs Estado actual del tablero.
     * @param depth Profundidad actual a analizar.
     * @param lastBest Última mejor jugada encontrada.
     * @return La mejor jugada encontrada para esta profundidad.
     */
    private PlayerMove iterativeDeepening(HexGameStatus hgs, int depth, PlayerMove lastBest) {
        PlayerMove bestMove = lastBest;
        int bestValue = Integer.MIN_VALUE;

        List<MoveNode> moves = hgs.getMoves();
        if (moves.isEmpty()) {
            return new PlayerMove(null, numNodes, depth, SearchType.MINIMAX);
        }

        for (MoveNode mn : moves) {
            if (timeoutReached) break;

            Point p = mn.getPoint();
            HexGameStatus newT = new HexGameStatus(hgs);
            newT.placeStone(p);

            int value = MIN(newT, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, p);
            if (value > bestValue) {
                bestValue = value;
                bestMove = new PlayerMove(p, numNodes, depth, SearchType.MINIMAX);
            }
        }

        return bestMove;
    }

    //////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////   MINIMAX   /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    private int MAX(HexGameStatus t, int depth, int alpha, int beta, Point lastPlayed) {
        if (timeoutReached) return 0;

        if (t.isGameOver()) {
            PlayerType win = t.GetWinner();
            return win == myType ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        if (depth == 0) {
            numNodes++;
            return heuristic(t, myType, lastPlayed);
        }

        int maxVal = Integer.MIN_VALUE;

        for (MoveNode mn : t.getMoves()) {
            Point p = mn.getPoint();
            HexGameStatus newT = new HexGameStatus(t);
            newT.placeStone(p);

            int val = MIN(newT, depth - 1, alpha, beta, p);
            maxVal = Math.max(maxVal, val);

            alpha = Math.max(alpha, maxVal);
            if (alpha >= beta) break;
        }

        return maxVal;
    }

    private int MIN(HexGameStatus t, int depth, int alpha, int beta, Point lastPlayed) {
        if (timeoutReached) return 0;

        if (t.isGameOver()) {
            PlayerType win = t.GetWinner();
            return win == myType ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        if (depth == 0) {
            numNodes++;
            return heuristic(t, enemyType, lastPlayed);
        }

        int minVal = Integer.MAX_VALUE;

        for (MoveNode mn : t.getMoves()) {
            Point p = mn.getPoint();
            HexGameStatus newT = new HexGameStatus(t);
            newT.placeStone(p);

            int val = MAX(newT, depth - 1, alpha, beta, p);
            minVal = Math.min(minVal, val);

            beta = Math.min(beta, minVal);
            if (alpha >= beta) break;
        }

        return minVal;
    }

    /**
     * Heurística mejorada para evaluar el tablero.
     */
    public int heuristic(HexGameStatus board, PlayerType player, Point lastPlayed) {
        List<Point> path = this.dijkstra.dijkstra(board, player, lastPlayed);
        if (path == null) return player == myType ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        int score = 0;
        int playerColor = PlayerType.getColor(player);
        int enemyColor = PlayerType.getColor(PlayerType.opposite(player));
        int cost = dijkstra.getCostOfPath(path);

        for (Point p : path) {
            int color = board.getPos(p);
            if (color == playerColor) score += 350;
            else if (color == enemyColor) score -= 400;
        }

        return score / cost;
    }
}
