/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.*;
import edu.upc.epsevg.prop.hex.heuristic.Dijkstra;

import java.awt.Point;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Clase que implementa el algoritmo MinMax con la poda alpha-beta para determinar cual es la mejor 
 * jugada en un juego de Hex.
 * @author kmalhal
 * @author jmoreno
 */
public class PathOfMinMax implements IPlayer, IAuto
{
    private String name = "PathOfMinMax";
    private PlayerType myType, enemyType;
    private int boardSize;
    private int depth;
    private Dijkstra dijkstra;
    private LocalDateTime start;
    private LocalDateTime time1;
    private DateTimeFormatter formatter;
    private boolean first = true;

    private long numNodes;

    /**
     * Constructor de la clase {@link PathOfMinMax}.
     * @param name  El nombre del bot.
     * @param depth La profunditat de cerca.

     */
    public PathOfMinMax(String name, int depth) {
        this.name = name;
        this.depth = depth;
        formatter = DateTimeFormatter.ofPattern("mm:ss.SSS");
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
     * <p>
     * Este método hace una llamada al método {@link minmax} el cual simulará varias jugadas
     * y determinará cual es el mejor.
     * @param hgs El objeto de la clase {@link HexGameStatus} que determina el estado del juego.
     * @return    Retorna el mejor movimiento posible a jugar.
     * 
     * @see HexGameStatus
     * @see PlayerMove
     */
    @Override
    public PlayerMove move(HexGameStatus hgs) {
        if (this.first == true) {
            this.start = LocalDateTime.now();
            this.first = false;
        }
        this.time1      = LocalDateTime.now();
        this.numNodes   = 0;
        this.myType     = hgs.getCurrentPlayer();
        this.boardSize  = hgs.getSize();
        this.enemyType  = PlayerType.opposite(myType);

        this.dijkstra = new Dijkstra(this.boardSize);

        // System.out.printf("%s is player type %s\n", name, myType == PlayerType.PLAYER2 ? "PLAYER2" : "PLAYER1");
        // System.out.printf("Depth: %d\n", depth);

        PlayerMove ret = minmax(hgs, depth);

        LocalDateTime now = LocalDateTime.now();
        long milli  = ChronoUnit.MILLIS.between(this.time1, now);
        long milli2 = ChronoUnit.MILLIS.between(this.start, now);
        LocalDateTime instant  = LocalDateTime.ofInstant(Instant.ofEpochMilli(milli),  ZoneId.systemDefault());
        LocalDateTime instant2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(milli2), ZoneId.systemDefault());
        System.out.println("Time to execute MinMax: " + instant.format(formatter));
        System.out.println("Time to execute MinMax (since first iteration): " + instant2.format(formatter));
        return ret;
    }

    /**
     * TODO
     */
    @Override
    public void timeout() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    //////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////   MINIMAX   /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Escoge el mejor movimiento donde colocaremos nuestra ficha.  
     * Primero probamos todos los movimientos desde la perspectiva MAX y 
     * para cada uno llamamos a MIN en la profundidad siguiente.
     *
     * @param t     Tablero actual.
     * @param depth Profundidad máxima del algoritmo minimax.
     * @return      Devuelve el movimiento óptimo que realizar según el tablero actual.
     */
    public PlayerMove minmax(HexGameStatus t, int depth) {
        // Si no hay movimientos posibles, no hacemos nada
        List<MoveNode> moves = t.getMoves();
        if (moves.isEmpty()) {
            return new PlayerMove(null, numNodes, depth, SearchType.MINIMAX);
        }

        int valor = Integer.MIN_VALUE;
        Point p0 = moves.get(0).getPoint();
        PlayerMove bestMove = new PlayerMove(p0, 0, 0, SearchType.MINIMAX);

        // Recorrer todas las opciones
        for (MoveNode mn : moves) {
            Point p = (Point)mn.getPoint().clone();
            HexGameStatus newT = new HexGameStatus(t);
            newT.placeStone(p);  // Jugamos nuestra ficha BF en p

            int value = MIN(newT, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, p);

            // Escoger el mejor
            if (value > valor) {
                valor = value;
                bestMove = new PlayerMove(p, numNodes, depth, SearchType.MINIMAX);
            }
        }
        return bestMove;
    }

    /**
     * Nodo MAX del algoritmo Minimax con poda alpha-beta que devuelve el máximo valor heurístico.
     *
     * @param t     Tablero tras el movimiento anterior.
     * @param depth Profundidad restante.
     * @param alpha Valor de α (mejor opción de MAX hasta el momento).
     * @param beta  Valor de β (mejor opción de MIN hasta el momento).
     * @return      Devuelve el valor heurístico máximo de todos los movimientos posibles.
     */
    private int MAX(HexGameStatus t, int depth, int alpha, int beta, Point lastPlayed) {
        // Si se acabó la partida, evaluamos
        if (t.isGameOver()) {
            PlayerType win = t.GetWinner();
            if (win == myType) {
                return Integer.MAX_VALUE;
            } else {
                return Integer.MIN_VALUE;
            }
        }

        // Caso base: profundidad 0 o no hay más movimientos
        if (depth == 0 || Utils.countEmptyCells(t) == 0) {
            numNodes++;
            return heuristic(t, myType, lastPlayed);
        }

        // Generamos todos los movimientos posibles
        List<MoveNode> moves = t.getMoves();
        // Si no hay movimientos, devolvemos la heurística
        if (moves.isEmpty()) {
            return heuristic(t, myType, lastPlayed);
        }

        // Recorremos cada movimiento y llamamos a MIN
        for (MoveNode mn : moves) {
            Point p = mn.getPoint();
            HexGameStatus newT = new HexGameStatus(t);
            newT.placeStone(p);

            int val = MIN(newT, depth - 1, alpha, beta, p);

            alpha = Math.max(alpha, val);
            // Poda
            if (alpha >= beta) {
                break;
            }
        }
        return alpha;
    }

    /**
     * Nodo MIN del algoritmo Minimax con poda alpha-beta que devuelve el mínimo valor heurístico.
     *
     * @param t     Tablero tras el movimiento anterior.
     * @param depth Profundidad restante.
     * @param alpha Valor de α (mejor opción de MAX hasta el momento).
     * @param beta  Valor de β (mejor opción de MIN hasta el momento).
     * @return      Devuelve el valor heurístico mínimo de todos los movimientos posibles.
     */
    private int MIN(HexGameStatus t, int depth, int alpha, int beta, Point lastPlayed) {
        // Si se acabó la partida, evaluamos
        if (t.isGameOver()) {
            PlayerType win = t.GetWinner();
            if (win == myType) {
                return Integer.MAX_VALUE;
            } else {
                return Integer.MIN_VALUE;
            }
        }

        // Caso base: profundidad 0 o no hay más movimientos
        if (depth == 0 || Utils.countEmptyCells(t) == 0) {
            numNodes++;
            return heuristic(t, enemyType, lastPlayed);
        }

        // Generamos todos los movimientos posibles
        List<MoveNode> moves = t.getMoves();
        // Si no hay movimientos, devolvemos la heurística
        if (moves.isEmpty()) {
            return heuristic(t, enemyType, lastPlayed);
        }

        // Recorremos cada movimiento y llamamos a MAX
        for (MoveNode mn : moves) {
            Point p = mn.getPoint();
            HexGameStatus newT = new HexGameStatus(t);
            newT.placeStone(p);

            int val = MAX(newT, depth - 1, alpha, beta, p);

            beta = Math.min(beta, val);
            // Poda
            if (alpha >= beta) {
                break;
            }
        }
        return beta;
    }

    /**
     * Retorna el coste mínimo de una jugada para un jugador determinado.
     * <p>
     * Internamente, hace una llamada al método {@link Dijkstra#dijkstra(HexGameStatus, PlayerType, Point)} 
     * para obtener el camino con menor distancia desde la fuente (la última pieza 
     * jugada) hasta el destino.
     * <p>
     * A partir de este camino, el puntaje de la heurística es calculada a partir 
     * de la cantidad de puntos del jugador puestos en el camino.
     * <p>
     * Es importante ejecutar este método solo después de haber simulado una jugada.
     * @param board      El tablero del juego.
     * @param player     El jugador que hace la consulta.
     * @param lastPlayed Es la última pieza jugada en una simulación.
     * @return           El coste de una jugada.
     * 
     * @see Dijkstra#dijkstra(HexGameStatus, PlayerType, Point)
     * @see Dijkstra#getCostOfPath(List<Point>)
     * @see Dijkstra#makePath(HexGameStatus, int[][], PlayerType)
     */
    public int heuristic(HexGameStatus board, PlayerType player, Point lastPlayed) {
        List<Point> path = this.dijkstra.dijkstra(board, player, lastPlayed);
        int score = 0;

        if (path == null) {
            // System.out.println("Path is null!");
            return (player == myType ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        }

        int playerColor = PlayerType.getColor(player);
        int enemyColor  = PlayerType.getColor(PlayerType.opposite(player));
        // int cost = dijkstra.getCostOfPath(path);

        for (Point p: path) {
            int color = board.getPos(p);
            if (color == playerColor)
                score += 350;
            if (color == enemyColor) {
                score -= 400;
            }
        }
        // score = score - cost;
        if (player == enemyType)
            score = score * (-1);
        // if (score == 0)
        //     System.out.printf("Score for player %s placed on [%d, %d]: %d\n",
        //                     player == PlayerType.PLAYER1 ? "PLAYER1" : "PLAYER2",
        //                     (int)lastPlayed.getX(), (int)lastPlayed.getY(), score);
        return score;
    }
}
