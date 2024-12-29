/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.*;
import java.awt.Point;
import java.util.ArrayList;
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

    private List<Point> up, down, left, right;
    int[][] distanceMap;
    private long numNodes;

    /**
     * Constructor de la clase {@link PathOfMinMax}.
     * @param name El nombre del bot.
     * @param depth La profunditat de cerca.

     */
    public PathOfMinMax(String name, int depth) {
        this.name = name;
        this.depth = depth;
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
     * @return Retorna el mejor movimiento posible a jugar.
     * @see HexGameStatus
     * @see PlayerMove
     */
    @Override
    public PlayerMove move(HexGameStatus hgs) {
        this.myType     = hgs.getCurrentPlayer();
        this.boardSize  = hgs.getSize();
        this.enemyType  = PlayerType.opposite(myType);
        System.out.printf("%s is player type %s\n", name, myType == PlayerType.PLAYER1 ? "PLAYER1" : "PLAYER2");
        createGoalArray(this.boardSize);
        System.out.println("Up list:");
        printListPoint(this.up);
        System.out.println("Down list:");
        printListPoint(this.down);
        System.out.println("Left list:");
        printListPoint(this.left);
        System.out.println("Right list:");
        printListPoint(this.right);

        // Testing out locations
        Point p = new Point(10, 10);
        int color = hgs.getPos(p);
        System.out.printf("Color at [%d, %d] = %d\n", (int)p.getX(), (int)p.getY(), color);

        // dijkstra(hgs, myType);
        return minimax(hgs, depth);
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
    public PlayerMove minimax(HexGameStatus t, int depth) {
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
            Point p = mn.getPoint();
            HexGameStatus newT = new HexGameStatus(t);
            newT.placeStone(p);  // Jugamos nuestra ficha BF en p

            int value = MIN(newT, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

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
    private int MAX(HexGameStatus t, int depth, int alpha, int beta) {
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
        if (depth == 0 || countEmptyCells(t) == 0) {
            numNodes++;
            return heuristic(t, myType);
        }

        // Generamos todos los movimientos posibles
        List<MoveNode> moves = t.getMoves();
        // Si no hay movimientos, devolvemos la heurística
        if (moves.isEmpty()) {
            return heuristic(t, myType);
        }

        // Recorremos cada movimiento y llamamos a MIN
        for (MoveNode mn : moves) {
            Point p = mn.getPoint();
            HexGameStatus newT = new HexGameStatus(t);
            newT.placeStone(p);

            int val = MIN(newT, depth - 1, alpha, beta);

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
    private int MIN(HexGameStatus t, int depth, int alpha, int beta) {
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
        if (depth == 0 || countEmptyCells(t) == 0) {
            numNodes++;
            return heuristic(t, enemyType);
        }

        // Generamos todos los movimientos posibles
        List<MoveNode> moves = t.getMoves();
        // Si no hay movimientos, devolvemos la heurística
        if (moves.isEmpty()) {
            return heuristic(t, enemyType);
        }

        // Recorremos cada movimiento y llamamos a MIN
        for (MoveNode mn : moves) {
            Point p = mn.getPoint();
            HexGameStatus newT = new HexGameStatus(t);
            newT.placeStone(p);

            int val = MAX(newT, depth - 1, alpha, beta);

            beta = Math.min(beta, val);
            // Poda
            if (alpha >= beta) {
                break;
            }
        }
        return beta;
    }
    
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////  MÉTODOS AUXILIARES  ///////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    /**
     * Función para calcular el número de casillas vacías en el tablero actual.
     *
     * @param t     Tablero actual.
     * @return      Devuelve el número de casillas vacías del tablero.
     */
    private int countEmptyCells(HexGameStatus t) {
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
     * Solo para própositos de debugging: Imprime una lista de puntos.
     * @param list Una lista de tipo {@link List<Point>}.
     */
    private void printListPoint(List<Point> list) {
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
     * @param dist Una matriz con las distancias desde la fuente. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @see dijkstra
     */
    private void printDist(int[][] dist) {
        System.out.println("\n############\n  Cost map\n############\n");
        for (int j = 0; j < boardSize; ++j) {
            for (int tab = 0; tab < j; ++tab) {
                System.out.printf(" ");
            }
            for (int i = 0; i < boardSize; ++i) {
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
     * Solo para própositos de debugging: Imprime una lista de puntos.
     * @param list Una lista de tipo {@link List<Point>} que contiene un camino a recorrer.
     */
    private void printPath(List<Point> list) {
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

    /**
     * Determina el punto con menor coste en todo el tablero.
     * @param board El tablero del juego.
     * @param enemyColor Color del enemigo.
     * @param dist Una matriz con las distancias desde la fuente. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @param visited Una matriz que determina si se ha visitado un cierto punto. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @return Un {@link Point} con el menor coste en todo el tablero.
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
     * Retorna el coste de una lista de {@link Point}.
     * <p>
     * Consulta por la matriz cuadrada de distancias el coste y va sumando los 
     * costes en las posiciones en la lista {@code list}
     * @param list Una {@link List<Point>} de todos los puntos a consultar.
     * @param dist Una matriz con las distancias desde la fuente. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @return Retorna el coste de una lista de {@link Point}.
     * @see dijkstra
     */
    public int getCostOfPath(List<Point> list, int dist[][]) {
        int ret = 0;
        for (Point p: list) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            ret += dist[x][y];
        }
        return ret;
    }

    /**
     * 
     * @param dist
     * @param player
     * @return
     */
    public Point getLowestGoal(int[][] dist, PlayerType player) {
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
     * 
     * @param board
     * @param prev
     * @param dist
     * @param player
     * @param pMin
     * @return
     */
    private boolean makePath2(HexGameStatus board, ArrayList<Point> prev, int[][] dist, PlayerType player, Point pMin) {
        if (pMin == null) {
            pMin = getLowestGoal(dist, player);
            prev.add((Point)pMin.clone());
        }

        if (isSourcePoint(pMin, player)) {
            prev.add((Point)pMin.clone());
            return true;
        }
        int min = Integer.MAX_VALUE;
        int x = (int)pMin.getX();
        int y = (int)pMin.getY();
        min = dist[x][y];

        List<Point> neighList = board.getNeigh(pMin);
        boolean found = false;
        for (Point neigh: neighList) {
            int xNeigh = (int)neigh.getX();
            int yNeigh = (int)neigh.getY();
            if (dist[xNeigh][yNeigh] < min) {
                found = true;
                // System.out.printf("Min point is [%d, %d]\n", xNeigh, yNeigh);
                if (makePath2(board, prev, dist, player, neigh) == true) {
                    if (!isSourcePoint(neigh, player))
                        prev.add((Point)neigh.clone());
                    min = dist[xNeigh][yNeigh];
                    // pMin.move(xNeigh, yNeigh);
                }
            }
        }
        return found;
    }

    /**
     * Construye una {@link ArrayList<Point>} con el camino de menor coste en el tablero.
     * @param board El tablero del juego.
     * @param dist Una matriz con las distancias desde la fuente. Las dimensiones de la matriz cuadrada es igual tamaño que el tablero.
     * @param player El juegador que hace la consulta del camino.
     * @return Una {@link ArrayList<Point>} con el camino con menor coste.
     * @see dijkstra
     */
    public ArrayList<Point> makePath(HexGameStatus board, int[][] dist, PlayerType player) {
        ArrayList<Point> prev = new ArrayList<Point>();
        if (makePath2(board, prev, dist, player, null) == true)
            return prev;
        return null;
    }

    /**
     * El método complementario de {@link heuristic}.
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
     * @param board El tablero del juego.
     * @param player El juegador que hace la consulta del camino.
     * @return Una {@link ArrayList<Point>} con el mejor camino posible.
     * @see makePath
     * @see getCostOfPath
     * @see <a href="https://github.com/orellabac/algoritmosS12015/blob/master/greedy/Dijkstra.java">https://github.com/orellabac/algoritmosS12015/blob/master/greedy/Dijkstra.java</a>
     * @see <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Pseudocode">https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Pseudocode</a>
     */
    public ArrayList<Point> dijkstra(HexGameStatus board, PlayerType player) {
        /* From a point (i, j), check the following points:
         * (i, j-1)  ;           (i+1, j-1)
         * (i-1, j)  ; ((i, j)); (i+1, j)
         * (i-1, j+1);           (i, j+1)
         */
        PlayerType enemy = PlayerType.opposite(player);

        int playerColor = PlayerType.getColor(player);
        int enemyColor  = PlayerType.getColor(enemy);
        int minCost = Integer.MAX_VALUE;

        ArrayList<Point> prev = null;
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

        for (Point p: source) {
            // Calculate best path for every source point.
            if (board.getPos(p) == enemyColor)
                continue;
            ArrayList<Point> temp;

            int x = (int)p.getX();
            int y = (int)p.getY();

            // System.out.println("\n###############################################");
            // System.out.printf("    Executing Dijkstra with source [%d, %d]", x, y);
            // System.out.println("\n###############################################\n");
            int     [][] dist    = new int[boardSize][boardSize];
            boolean [][] visited = new boolean[boardSize][boardSize];

            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {
                    dist[i][j]    = Integer.MAX_VALUE;
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
            temp = makePath(board, dist, player);
            if (temp == null)
                continue;
            int cost = getCostOfPath(temp, dist);
            if (cost < minCost) {
                minCost = cost;
                prev = temp;
                this.distanceMap = copy2DArray(dist);
            }
            /*************
             * DEBUGGING *
             *************/
            // System.out.printf("Shortest path: ");
            // for (Point prevPoint: temp) {
            //     int x2 = (int)prevPoint.getX();
            //     int y2 = (int)prevPoint.getY();
            //     System.out.printf("[%d, %d]; ", x2, y2);
            // }
            // System.out.println("");
            // printPath(temp);
            // printDist(dist);
        }
        return prev;
        // return 0;
    }
    
    /**
     * Retorna el coste mínimo de una jugada para un jugador determinado.
     * <p>
     * Internamente, hace una llamada al método {@link dijkstra} para obtener el 
     * camino con menor distancia desde la fuente hasta el destino. Y luego calcula 
     * el coste a partir del camino encontrado.
     * <p>
     * También, se usa de una matriz de tipo {@link int[][]} miembro 
     * ({@code this.distanceMap}) donde están establecidas las distancias desde 
     * un punto fuente del tablero hasta todos los demás puntos para el cálculo del 
     * coste del camino.
     * <p>
     * Es importante ejecutar este método solo después de haber simulado una jugada.
     * @param board El tablero del juego.
     * @param player El jugador que hace la consulta.
     * @return El coste de una jugada.
     * @see dijkstra
     * @see getCostOfPath
     * @see makePath
     */
    public int heuristic(HexGameStatus board, PlayerType player) {
        List<Point> path = dijkstra(board, player);
        if (path == null)
            return (player == myType ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        return getCostOfPath(path, this.distanceMap);
    }

    /**
     * Construye 4 {@link ArrayList<Point>} que determinan las fuentes y los destinos.
     * <p>
     * Las listas resultantes son la primera fila, la última fila,
     * la primera columna y la última columna del tablero.
     * @param size El tamaño del tablero.
     */
    public void createGoalArray(int size) {
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
     * Consulta cuales puntos en el tablero están libres para jugar.
     * @param board El tablero del juego.
     * @return Retorna una {@link ArrayList<Point>} con todos los puntos posibles a jugar en el tablero.
     */
    private ArrayList<Point> getAvailableCells(HexGameStatus board) {
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
     * Obtiene la menor distancia de todos los vecinos de un punto.
     * @param board El tablero del juego.
     * @param point El punto a consulta.
     * @param dist La matriz de distancias.
     * @return La menor distancia de un punto a sus vecinos.
     */
    private int getLowestCostFromNeighbors(HexGameStatus board, Point point, int[][] dist) {
        int ret = Integer.MAX_VALUE;
        for (Point neigh: board.getNeigh(point)) {
            int x = (int)neigh.getX();
            int y = (int)neigh.getY();
            if (dist[x][y] < ret)
                ret = dist[x][y];
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
     * @param p Punto a consultar.
     * @param player El jugador que hace la consulta.
     * @return {@code true} si el punto pertenece a los puntos fuente; {@code false} en caso contrario.
     * @see isGoalPoint
     * @see PlayerType
     */
    public boolean isSourcePoint(Point p, PlayerType player) {
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
     * @param p Punto a consultar.
     * @param player El jugador que hace la consulta.
     * @return {@code true} si el punto pertenece a los puntos destino; {@code false} en caso contrario.
     * @see isSourcePoint
     * @see PlayerType
     */
    public boolean isGoalPoint(Point p, PlayerType player) {
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
     * Retorna una copia de array.
     * @param array Array fuente
     * @return Retorna una copia de array de tipo {@link int[][]}.
     */
    public int[][] copy2DArray(int[][] array) {
        int len = array.length;
        int[][] ret = new int[len][len];
        for (int i = 0; i < len; ++i) {
            for (int j = 0; j < len; ++j) {
                ret[i][j] = array[i][j];
            }
        }
        return ret;
    }
}
