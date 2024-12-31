/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kmalhal
 */
public class Utils {
    /**
     * Retorna una copia de array.
     * @param array Array fuente
     * @return      Retorna una copia de array de tipo {@link int[][]}.
     */
    static public int[][] copy2DArray(int[][] array) {
        int len = array.length;
        int[][] ret = new int[len][len];
        for (int i = 0; i < len; ++i) {
            for (int j = 0; j < len; ++j) {
                ret[i][j] = array[i][j];
            }
        }
        return ret;
    }
    /**
     * Consulta si el punto determinado pertenece a la primera fila o columna del tablero.
     * <p>
     * Dependiendo del jugador {@code player}, la fuente puede ser la primera fila 
     * o la primera columna.
     * <p>
     * Para un jugador de tipo {@code PlayerType.PLAYER1} los puntos fuente son 
     * aquellos que se encuentran en la primera columna.
     * <p>
     * Para un jugador de tipo {@code PlayerType.PLAYER2} los puntos fuente son 
     * aquellos que se encuentran en la primera fila.
     * @param p      Punto a consultar.
     * @param player El jugador que hace la consulta.
     * @return       {@code true} si el punto pertenece a los puntos fuente; {@code false} en caso contrario.
     * 
     * @see isGoalPoint
     * @see PlayerType
     */
    static public boolean isSourcePoint(Point p, PlayerType player) {
        int x = (int)p.getX();
        int y = (int)p.getY();
        if (player == PlayerType.PLAYER2) {
            if (y == 0)
                return true;
        } else {
            if (x == 0)
                return true;
        }
        return false;
    }

    /**
     * Consulta si el punto determinado pertenece a la última fila o columna del tablero.
     * <p>
     * Dependiendo del jugador {@code player}, el destino puede ser la última fila 
     * o la última columna.
     * <p>
     * Para un jugador de tipo {@code PlayerType.PLAYER1} los puntos destino son 
     * aquellos que se encuentran en la última columna.
     * <p>
     * Para un jugador de tipo {@code PlayerType.PLAYER2} los puntos destino son 
     * aquellos que se encuentran en la última fila.
     * @param p      Punto a consultar.
     * @param player El jugador que hace la consulta.
     * @return       {@code true} si el punto pertenece a los puntos destino; {@code false} en caso contrario.
     * 
     * @see isSourcePoint
     * @see PlayerType
     */
    static public boolean isGoalPoint(Point p, PlayerType player, int boardSize) {
        int x = (int)p.getX();
        int y = (int)p.getY();
        if (player == PlayerType.PLAYER2) {
            if (y == (boardSize - 1))
                return true;
        } else {
            if (x == (boardSize - 1))
                return true;
        }
        return false;
    }

    /**
     * Consulta cuales puntos en el tablero están libres para jugar.
     * @param board El tablero del juego.
     * @return      Retorna una {@link ArrayList<Point>} con todos los puntos posibles a jugar en el tablero.
     */
    static public ArrayList<Point> getAvailableCells(HexGameStatus board) {
        ArrayList<Point> availables = new ArrayList<>();
        int size = board.getSize();
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                if (board.getPos(i, j) == 0)
                    availables.add(new Point(i, j));
            }
        return availables;
    }
    
    /**
     * Método para calcular el número de casillas vacías en el tablero actual.
     *
     * @param t     Tablero actual.
     * @return      Devuelve el número de casillas vacías del tablero.
     */
    static public int countEmptyCells(HexGameStatus t) {
        int count = 0;
        int size = t.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (t.getPos(i, j) == 0) { 
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Obtiene la menor distancia de todos los vecinos de un punto.
     * @param board El tablero del juego.
     * @param point El punto a consulta.
     * @param dist  La matriz de distancias.
     * @return      La menor distancia de un punto a sus vecinos.
     */
    static public int getLowestCostFromNeighbors(HexGameStatus board, Point point, int[][] dist) {
        int ret = Integer.MAX_VALUE;
        for (Point neigh: board.getNeigh(point)) {
            int x = (int)neigh.getX();
            int y = (int)neigh.getY();
            if (dist[x][y] < ret)
                ret = dist[x][y];
        }
        return ret;
    }

    static public int countEnemyNeighbors(HexGameStatus board, Point point, PlayerType player) {
        PlayerType enemy = PlayerType.opposite(player);
        int enemyColor = PlayerType.getColor(enemy);
        ArrayList<Point> neighList = board.getNeigh(point);
        int ret = 0;
        for (Point neigh: neighList) {
            if (board.getPos(neigh) == enemyColor)
                ++ret;
        }
        return ret;
    }

    /**
     * Retorna el coste de una lista de {@link Point}.
     * <p>
     * Consulta por la matriz cuadrada de distancias el coste y va sumando los 
     * costes en las posiciones en la lista {@code list}
     * @param list Una {@link List<Point>} de todos los puntos a consultar.
     * @param dist Una matriz con las distancias desde la fuente. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @return     Retorna el coste de una lista de {@link Point}.
     * 
     * @see edu.upc.epsevg.prop.hex.heuristic.Dijkstra#dijkstra(HexGameStatus, PlayerType)
     */
    static public int getCostOfPath(List<Point> list, int[][] dist) {
        int ret = 0;
        for (Point p: list) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            ret += dist[x][y];
        }
        return ret;
    }

    /**
     * Solo para própositos de debugging: Imprime una lista de puntos.
     * @param list Una lista de tipo {@link List<Point>}.
     */
    static public void printListPoint(List<Point> list) {
        System.out.println("Debugging only purporses. Printing list:");
        for (Point p: list) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            System.out.printf("[%d, %d]", x, y);
        }
        System.out.println("");
    }

    /**
     * Solo para própositos de debugging: Imprime el mapa de coste de un juego.
     * <p>
     * En la matriz habrá un valor {@code dist[i][j] = 0} que determina la fuente 
     * de una instancia de ejecución Dijkstra.
     * @param dist      Una matriz con las distancias desde la fuente. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @param boardSize El tamaño del tablero.
     * 
     * @see edu.upc.epsevg.prop.hex.heuristic.Dijkstra#dijkstra(HexGameStatus, PlayerType)
     */
    static public void printDist(int[][] dist, int boardSize) {
        System.out.println("\n############\n  Cost map\n############\n");
        for (int j = 0; j < boardSize; ++j) {
            for (int tab = 0; tab < j; ++tab) {
                System.out.printf(" ");
            }
            for (int i = 0; i < boardSize; ++i) {
                if (dist[i][j] < 9)
                    System.out.printf(" ");
                if (dist[i][j] == Integer.MAX_VALUE)
                    System.out.printf("\033[0;31m##\033[0m, ");
                else
                    System.out.printf("\033[0m%d, ", dist[i][j]);
            }
            System.out.printf("\n");
        }
        System.out.println("");
    }

        /**
     * Solo para própositos de debugging: Imprime el mapa de fichas visitadas en la ejecución de Dijkstra.
     * <p>
     * @param visited   Una matriz con las fichas visitadas. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @param boardSize El tamaño del tablero.
     * 
     * @see edu.upc.epsevg.prop.hex.heuristic.Dijkstra#dijkstra(HexGameStatus, PlayerType)
     */
    static public void printVisited(boolean[][] visited, int boardSize) {
        System.out.println("\n###############\n  Visited map\n###############\n");
        for (int j = 0; j < boardSize; ++j) {
            for (int tab = 0; tab < j; ++tab) {
                System.out.printf(" ");
            }
            for (int i = 0; i < boardSize; ++i) {
                System.out.printf("\033[0m%d, ", visited[i][j] == true ? 1 : 0);
            }
            System.out.printf("\n");
        }
        System.out.println("");
    }


    /**
     * Solo para própositos de debugging: Imprime una lista de puntos.
     * @param list      Una lista de tipo {@link List<Point>} que contiene un camino a recorrer.
     * @param boardSize El tamaño del tablero.
     */
    static public void printPath(List<Point> list, int boardSize) {
        System.out.println("\n########################\n  Best possible path\n########################\n");
        for (int j = 0; j < boardSize; ++j) {
            for (int tab = 0; tab < j; ++tab) {
                System.out.printf(" ");
            }
            for (int i = 0; i < boardSize; ++i) {
                boolean found = false;
                for (Point p: list) {
                    int x = (int)p.getX();
                    int y = (int)p.getY();
                    if (x == i && y == j) {
                        found = true;
                        System.out.printf("X ");
                        break;
                    }
                }
                if (!found)
                    System.out.printf("· ");
            }
            System.out.printf("\n");
        }
        System.out.println("");
    }
}
