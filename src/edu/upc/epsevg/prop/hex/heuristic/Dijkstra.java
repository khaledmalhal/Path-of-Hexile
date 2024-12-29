/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex.heuristic;

import edu.upc.epsevg.prop.hex.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que implementa los algoritmos de búsqueda del camino más corto Dijkstra.
 * 
 * <p>
 * Esta clase tiene la intención de ser utilizada por otros como utilitario en la búsqueda 
 * de las mejores jugadas posibles en el juego Hex.
 *
 * @author kmalhal
 * @author jmoreno
 */
public class Dijkstra {
    private int boardSize;

    private List<Point> up, down, left, right;
    int[][] distanceMap;

    /**
     * Constructor de la clase Dijkstra.
     * <p>
     * Es necesario conocer el tamaño del tablero para realizar la ejecución del 
     * algoritmo Dijkstra.
     * @param boardSize El tamaño del tablero.
     */
    public Dijkstra(int boardSize) {
        this.boardSize = boardSize;
        createGoalArray(this.boardSize);

        System.out.println("Up list:");
        Utils.printListPoint(this.up);
        System.out.println("Down list:");
        Utils.printListPoint(this.down);
        System.out.println("Left list:");
        Utils.printListPoint(this.left);
        System.out.println("Right list:");
        Utils.printListPoint(this.right);
    }

    /**
     * Construye 4 {@link ArrayList<Point>} que determinan las fuentes y los destinos.
     * <p>
     * Las listas resultantes son la primera fila, la última fila,
     * la primera columna y la última columna del tablero.
     * @param size El tamaño del tablero.
     */
    private void createGoalArray(int size) {
        if (up instanceof ArrayList<?>)
            return;
        up    = new ArrayList<>();
        down  = new ArrayList<>();
        left  = new ArrayList<>();
        right = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            up.add(   new Point(i, 0));
            down.add( new Point(i, size - 1));
            left.add( new Point(0, i));
            right.add(new Point(size - 1, i));
        }
    }

    /**
     * Retorna el coste de una lista de {@link Point}.
     * <p>
     * Consulta por la matriz cuadrada de distancias el coste y va sumando los 
     * costes en las posiciones en la lista {@code list}
     * @param list Una {@link List<Point>} de todos los puntos a consultar.
     * @return     Retorna el coste de una lista de {@link Point}.
     * 
     * @see Utils#getCostOfPath(List<Point>, int[][])
     */
    public int getCostOfPath(List<Point> list) {
        return Utils.getCostOfPath(list, this.distanceMap);
    }

    /**
     * Determina el punto con menor coste en todo el tablero.
     * @param board      El tablero del juego.
     * @param enemyColor Color del enemigo.
     * @param dist       Una matriz con las distancias desde la fuente. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @param visited    Una matriz que determina si se ha visitado un cierto punto. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @return           Un {@link Point} con el menor coste en todo el tablero.
     * 
     * @see dijkstra
     */
    private Point minDistancePoint(HexGameStatus board, int enemyColor, int[][] dist, boolean[][] visited) {
        int min = Integer.MAX_VALUE;
        Point minPoint = new Point(0, 0);
        for (int x = 0; x < boardSize; ++x) {
            for (int y = 0; y < boardSize; ++y) {
                if (visited[x][y] == true)
                    continue;

                int color = board.getPos(x, y);

                if (color == enemyColor) {
                    dist[x][y] = Integer.MAX_VALUE;
                    visited[x][y] = true;
                } else {
                    if (dist[x][y] <= min) {
                        min = dist[x][y];
                        minPoint.move(x, y);
                    }
                }
            }
        }
        return minPoint;
    }

    /**
     * Obtiene un {@link Point} con la distancia más corta de llegada.
     * @param dist   Una matriz con las distancias desde la fuente. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @param player El jugador que hace la consulta.
     * @return       {@link Point} de menor distancia.
     * 
     * @see makePath
     * @see makePath2
     */
    private Point getLowestSource(int[][] dist, PlayerType player) {
        ArrayList<Point> goal;
        int min = Integer.MAX_VALUE;
        Point pMin = null;
        if (player == PlayerType.PLAYER2) {
            goal = new ArrayList<>(this.up.size());
            for (Point p: this.up) {
                goal.add((Point)p.clone());
            }
        } else {
            goal = new ArrayList<>(this.left.size());
            for (Point p: this.left) {
                goal.add((Point)p.clone());
            }
        }

        // Get the goal point with the lowest cost.
        for (Point p: goal) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            if (dist[x][y] < min) {
                min  = dist[x][y];
                pMin = (Point)p.clone();
            }
        }
        // System.out.printf("Lowest goal point: [%d, %d]\n", (int)pMin.getX(), (int)pMin.getY());
        return (Point)pMin.clone();
    }

    /**
     * Obtiene un {@link Point} con la distancia más corta de llegada.
     * @param dist   Una matriz con las distancias desde la fuente. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @param player El jugador que hace la consulta.
     * @return       {@link Point} de menor distancia.
     * 
     * @see makePath
     * @see makePath2
     */
    private Point getLowestGoal(int[][] dist, PlayerType player) {
        ArrayList<Point> goal;
        int min = Integer.MAX_VALUE;
        Point pMin = null;
        if (player == PlayerType.PLAYER2) {
            goal = new ArrayList<>(this.down.size());
            for (Point p: this.down) {
                goal.add((Point)p.clone());
            }
        } else {
            goal = new ArrayList<>(this.right.size());
            for (Point p: this.right) {
                goal.add((Point)p.clone());
            }
        }

        // Get the goal point with the lowest cost.
        for (Point p: goal) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            if (dist[x][y] < min) {
                min  = dist[x][y];
                pMin = (Point)p.clone();
            }
        }
        // System.out.printf("Lowest goal point: [%d, %d]\n", (int)pMin.getX(), (int)pMin.getY());
        return (Point)pMin.clone();
    }

    /**
     * Un método privado que realiza el camino con menor coste desde la fuente hasta el destino.
     * 
     * <p>
     * A diferencia de {@link makePath} este método es recursivo. No es recomendable llamar a este 
     * método si lo que se quiere obtiener es el camino. Utiliza {@link makePath}.
     * <p>
     * El motivo de este método es debido a que existen casos donde hay puntos del tablero en que
     * estará rodeado por puntos con coste mayor que el propio punto. Al revisar todos los 
     * vecinos, el algoritmo no encontrará un vecino mayor y esto genera al final un softblock.
     * <p>
     * Para evitar esto, cuando se consulte los vecinos de un punto sin salida y no haya un vecino 
     * con menor distancia, el método retornará falso. En caso contrario, retornará true.
     *
     * @param board  El tablero del juego.
     * @param prev   Una {@link ArrayList<Point>} con el camino de menor distancia.
     * @param dist   Una matriz con las distancias desde la fuente.
     * @param player El jugador actual.
     * @param pMin   Punto mínimo. Si se va a llamar a este método, use {@code null} como valor en el argumento.
     * @return       True si un punto es válido para el camino. False en caso contrario.
     */
    private boolean makePath2(HexGameStatus board, ArrayList<Point> prev, int[][] dist, PlayerType player, Point pMin, boolean sourceOrGoal) {
        /* if (sourceOrGoal == false) {
            if (Utils.isSourcePoint(pMin, player)) {
                prev.add((Point)pMin.clone());
                return true;
            }
        } else {
            if (Utils.isGoalPoint(pMin, player, board.getSize())) {
                prev.add((Point)pMin.clone());
                return true;
            }
        }*/
        int min = Integer.MAX_VALUE;
        int x = (int)pMin.getX();
        int y = (int)pMin.getY();
        min = dist[x][y];

        if (min == 0) {
            return true;
        }

        List<Point> neighList = board.getNeigh(pMin);
        boolean found = false;
        for (Point neigh: neighList) {
            int xNeigh = (int)neigh.getX();
            int yNeigh = (int)neigh.getY();
            if (dist[xNeigh][yNeigh] < min) {
                found = true;
                System.out.printf("Min point is [%d, %d]\n", xNeigh, yNeigh);
                if (makePath2(board, prev, dist, player, neigh, sourceOrGoal) == true) {
                    if (sourceOrGoal == false) {
                        if (!Utils.isSourcePoint(neigh, player))
                            prev.add((Point)neigh.clone());
                    } else if (sourceOrGoal == true) {
                        if (!Utils.isGoalPoint(neigh, player, board.getSize()))
                            prev.add((Point)neigh.clone());
                    }
                    min = dist[xNeigh][yNeigh];
                    // pMin.move(xNeigh, yNeigh);
                }
            }
        }
        return found;
    }

    /**
     * Construye una {@link ArrayList<Point>} con el camino de menor coste en el tablero.
     * @param board  El tablero del juego.
     * @param dist   Una matriz con las distancias desde la fuente. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @param player El juegador que hace la consulta del camino.
     * @return       Una {@link ArrayList<Point>} con el camino con menor coste.
     * 
     * @see dijkstra
     * @see makePath2
     */
    private ArrayList<Point> makePath(HexGameStatus board, int[][] dist, PlayerType player, Point source) {
        ArrayList<Point> prev = new ArrayList<Point>();
        Point pGoal   = getLowestGoal(dist, player);
        Point pSource = getLowestSource(dist, player);
        Point clone1 = (Point)pGoal.clone();
        Point clone2 = (Point)pSource.clone();
        boolean toSource = makePath2(board, prev, dist, player, pSource, false);
        boolean toGoal   = makePath2(board, prev, dist, player, pGoal,   true);
        if (toSource == true && toGoal == true) {
            prev.add(clone1);
            prev.add(clone2);
            return prev;
        }
        return null;
    }

    /**
     * Realiza una ejecución del algoritmo Dijkstra y devuelve el mejor camino.
     * <p>
     * Es una implementación de Dijkstra que consulta de todos los puntos de la primera 
     * fila (o la primera columna) como fuentes e introduce la distancia hasta 
     * todos los puntos del tablero en una matriz cuadrada {@link int[][]} (con 
     * tamaño igual al del tablero).
     * <p>
     * El resultado final es una {@link ArrayList<Point>} con el camino de menor 
     * coste desde la fuente hasta el final del tablero.
     * <p>
     * Puede usar el método {@link getCostOfPath} para obtener el coste de dicho camino.
     * @param board  El tablero del juego.
     * @param player El juegador que hace la consulta del camino.
     * @return       Una {@link ArrayList<Point>} con el mejor camino posible.
     * 
     * @see makePath
     * @see getCostOfPath
     * @see <a href="https://github.com/orellabac/algoritmosS12015/blob/master/greedy/Dijkstra.java">https://github.com/orellabac/algoritmosS12015/blob/master/greedy/Dijkstra.java</a>
     * @see <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Pseudocode">https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Pseudocode</a>
     */
    public ArrayList<Point> dijkstra(HexGameStatus board, PlayerType player, Point sourcePoint) {
        /* From a point (i, j), check the following points:
         * (i  , j-1);           (i+1, j-1)
         * (i-1, j)  ; ((i, j)); (i+1, j)
         * (i-1, j+1);           (i  , j+1)
         */
        PlayerType enemy = PlayerType.opposite(player);

        int playerColor = PlayerType.getColor(player);
        int enemyColor  = PlayerType.getColor(enemy);

        List<Point> source;
        if (player == PlayerType.PLAYER2) {
            source = new ArrayList<>(this.up.size());
            for (Point p: this.up) {
                source.add((Point)p.clone());
            }
        } else {
            source = new ArrayList<>(this.left.size());
            for (Point p: this.left) {
                source.add((Point)p.clone());
            }
        }

        /* List<MoveNode> moves = board.getMoves();
        for (MoveNode move: moves) {
            Point p = (Point)move.getPoint().clone();
            int x = (int)p.getX();
            int y = (int)p.getY();
            System.out.printf("Possible move: [%d, %d]\n", x, y);

            MoveNode parent = move.getParent();
            if (parent == null)
                continue;

            p = parent.getPoint();
            x = (int)p.getX();
            y = (int)p.getY();
            System.out.printf("Parent: [%d, %d]\n", x, y);
        }
        MoveNode move = moves.get(0);
        Point p = (Point)move.getPoint().clone();*/


        int x = (int)sourcePoint.getX();
        int y = (int)sourcePoint.getY();

        int     [][] dist    = new int    [boardSize][boardSize];
        boolean [][] visited = new boolean[boardSize][boardSize];

        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                dist   [i][j] = Integer.MAX_VALUE;
                visited[i][j] = false;
            }
        }
        dist[x][y] = 0;

        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                Point minPoint = minDistancePoint(board, enemyColor, dist, visited);
                int xMin = (int)minPoint.getX();
                int yMin = (int)minPoint.getY();

                visited[xMin][yMin] = true;

                List<Point> toVisit = new ArrayList<>(board.getNeigh(minPoint));
                for (Point neigh: toVisit) {
                    int xNeigh = (int)neigh.getX();
                    int yNeigh = (int)neigh.getY();
                    int colorNeigh = board.getPos(neigh);
                    int cost = Integer.MAX_VALUE;

                    // Any cost tinkering can be done here.
                    if (colorNeigh == playerColor)
                        cost = -5;
                    else if (colorNeigh != playerColor && colorNeigh != enemyColor)
                        cost = 5;

                    if (visited[xNeigh][yNeigh] == false) {
                        if (dist[xMin][yMin] + cost < dist[xNeigh][yNeigh]) {
                            dist[xNeigh][yNeigh] = dist[xMin][yMin] + cost;
                        }
                    }
                }
            }
        }
        Utils.printDist(dist, boardSize);
        ArrayList<Point> prev = makePath(board, dist, player, sourcePoint);
        Utils.printPath(prev, boardSize);
        return prev;
    }
}
