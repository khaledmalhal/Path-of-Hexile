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
    private int otherColor;
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
        createGoalArray(this.boardSize);
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

    private void printDist(int[][] dist) {
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

    public List<MoveNode> dijkstra(HexGameStatus board, PlayerType player) {
        // From a point (i, j), check the following points:
        /* (i, j-1)  ;           (i+1, j-1)
         * (i-1, j)  ; ((i, j)); (i+1, j)
         * (i-1, j+1);           (i, j+1)
         */
        PlayerType enemy = PlayerType.opposite(player);
        
        int playerColor = PlayerType.getColor(player);
        int enemyColor  = PlayerType.getColor(enemy);
        
        List<MoveNode> prev = new ArrayList<>();
        List<Point> source, goal;
        if (player == PlayerType.PLAYER1) {
            source = this.up;
            goal   = this.down;
        } else {
            source = this.left;
            goal   = this.right;
        }
        
        for (Point p: source) {
            int x = (int)p.getX();
            int y = (int)p.getY();
            
            List<MoveNode> temp  = new ArrayList<>();
            int     [][] dist    = new int[boardSize][boardSize];
            boolean [][] visited = new boolean[boardSize][boardSize];
            
            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {
                    dist[i][j]    = Integer.MAX_VALUE;
                    visited[i][j] = false;
                }
            }
            dist[x][y] = 0;
            temp.add(new MoveNode(p));
            
            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {
                    Point minPoint = minDistancePoint(board, enemyColor, dist, visited);
                    int xMin = (int)minPoint.getX();
                    int yMin = (int)minPoint.getY();

                    visited[xMin][yMin] = true;

                    List<Point> toVisit = new ArrayList<>(board.getNeigh(minPoint));
                    for (Point neigh: toVisit) {
                        // if (isSourcePoint(neigh, player))
                        //     continue;

                        int xNeigh = (int)neigh.getX();
                        int yNeigh = (int)neigh.getY();
                        int colorNeigh = board.getPos(neigh);
                        int cost = Integer.MAX_VALUE;

                        if (colorNeigh == playerColor)
                            cost = 1;
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
            up.add(   new Point(0, i));
            down.add( new Point(size - 1, 0));
            left.add( new Point(i, 0));
            right.add(new Point(i, size - 1));
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
        if (player == PlayerType.PLAYER1) {
            if (x == 0)
                return true;
        } else {
            if (y == 0)
                return true;
        }
        return false;
    }
    
    public boolean isGoalPoint(Point p, PlayerType player) {
        int x = (int)p.getX();
        int y = (int)p.getY();
        if (player == PlayerType.PLAYER1) {
            if (x == (boardSize - 1))
                return true;
        } else {
            if (y == (boardSize - 1))
                return true;
        }
        return false;
    }
}
