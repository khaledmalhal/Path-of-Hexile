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
 *
 * @author kmalhal
 */
public class PathOfMinMax implements IPlayer, IAuto
{
    private String name = "PathOfMinMax";
    private SearchType search = SearchType.MINIMAX;
    private PlayerType myType;
    private int myColor, otherColor;
    private int boardSize;
    private List<Point> up, down, left, right;

    public PathOfMinMax(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public PlayerMove move(HexGameStatus hgs) {
        this.myType     = hgs.getCurrentPlayer();
        this.boardSize  = hgs.getSize();
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

        // Player colors
        this.myColor = PlayerType.getColor(myType);
        this.otherColor = PlayerType.getColor(PlayerType.opposite(myType));

        // Testing out locations
        Point p = new Point(10, 10);
        int color = hgs.getPos(p);
        System.out.printf("Color at [%d, %d] = %d\n", (int)p.getX(), (int)p.getY(), color);

        dijkstra(hgs, myType);
        minmax(hgs, 0, true, 0, 0);
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void timeout() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int[] minmax(HexGameStatus board, int depth, boolean isMax, int alpha, int beta) {
        if (depth == 5) return new int[]{0};
        ArrayList<Point> availables = getAvailableCells(board);
        board.placeStone(availables.get(0));
        System.out.printf("Current player is: %s\n", board.getCurrentPlayer() == PlayerType.PLAYER1 ? "PLAYER1" : "PLAYER2");

        minmax(board, depth+1, true, 0, 0);
        return new int[]{0};
    }

    private void printListPoint(List<Point> list) {
        System.out.println("Debugging only purporses. Printing list:");
        for (Point p: list) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            System.out.printf("[%d, %d]", x, y);
        }
        System.out.println("");
    }

    private void printDist(int[][] dist) {
        System.out.println("\n############\n  Cost map\n############\n");
        for (int j = 0; j < boardSize; ++j) {
            for (int tab = 0; tab < j; ++tab) {
                System.out.printf(" ");
            }
            for (int i = 0; i < boardSize; ++i) {
                System.out.printf("%d, ", dist[i][j]);
            }
            System.out.printf("\n");
        }
        System.out.println("");
    }

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

    public int getCostOfPath(List<Point> list, int dist[][]) {
        int ret = 0;
        for (Point p: list) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            ret += dist[x][y];
        }
        return ret;
    }

    public ArrayList<Point> makePath(HexGameStatus board, int[][] dist, PlayerType player) {
        ArrayList<Point> prev = new ArrayList<Point>();

        ArrayList<Point> goal;
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

        Point pMin = null;
        int min = Integer.MAX_VALUE;

        // Get the goal point with the lowest cost.
        for (Point p: goal) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            if (dist[x][y] < min) {
                min  = dist[x][y];
                pMin = p;
            }
        }
        System.out.printf("Lowest goal point: [%d, %d]\n", (int)pMin.getX(), (int)pMin.getY());
        prev.add(new Point(pMin));

        // Make a path until you reach a source point.
        while (!isSourcePoint(pMin, player)) {
            int x = (int)pMin.getX();
            int y = (int)pMin.getY();
            System.out.printf("Not source point, looking for more: [%d, %d]\n", x, y);
            min = dist[x][y];
            List<Point> neighList = board.getNeigh(pMin);
            for (Point neigh: neighList) {
                int xNeigh = (int)neigh.getX();
                int yNeigh = (int)neigh.getY();
                if (dist[xNeigh][yNeigh] < min) {
                    System.out.printf("Min point is [%d, %d]\n", xNeigh, yNeigh);
                    min = dist[xNeigh][yNeigh];
                    pMin.move(xNeigh, yNeigh);
                }
            }
            prev.add(new Point(pMin));
        }
        /* ################
         *    DEBUGGING
         * ################ */
        System.out.printf("Goal:\n");
        for (Point p: goal) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            System.out.printf("[%d, %d] → \t%d\n", x, y, dist[x][y]);
        }
        System.out.println("");
        return prev;
    }

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

            System.out.println("\n###############################################");
            System.out.printf("    Executing Dijkstra with source [%d, %d]", x, y);
            System.out.println("\n###############################################\n");
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
                            cost = 0;
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
            int cost = getCostOfPath(temp, dist);
            if (cost < minCost) {
                minCost = cost;
                prev = temp;
            }
            System.out.printf("Shortest path: ");
            for (Point prevPoint: temp) {
                int x2 = (int)prevPoint.getX();
                int y2 = (int)prevPoint.getY();
                System.out.printf("[%d, %d]; ", x2, y2);
            }
            System.out.println("");
            printPath(temp);
            printDist(dist);
        }
        return prev;
        // return 0;
    }
    
    public int heuristic(HexGameStatus board) {
        return 0;
    }
    
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
}
