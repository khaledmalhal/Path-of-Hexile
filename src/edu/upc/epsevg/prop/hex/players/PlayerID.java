package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.*;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Adaptación del código ComputerPlayer al entorno HexGameStatus / IPlayer sin cambiar la
 * lógica del algoritmo ni la heurística, pero eliminando el forzado de turno.
 */
public class PlayerID implements IPlayer, IAuto {

    private int myColor = 1;
    private int oppNumber = 2;

    private final int MY_CHAIN_SCORE = 10;

    private String name = "AdaptedComputerPlayer";

    public PlayerID(int color) {
        setColor(color);
    }

    private void setColor(int color) {
        this.myColor = color;
        if (myColor == 1) {
            oppNumber = 2;
        } else {
            oppNumber = 1;
        }
    }

    @Override
    public PlayerMove move(HexGameStatus s) {
        // Profundidad fija = 5, igual que en el código original
        int[] index = minmax(s, 5, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Point bestMovePos = null;
        if (index[1] != -1 && index[2] != -1) {
            bestMovePos = new Point(index[1], index[2]);
        }
        return new PlayerMove(bestMovePos, 0, 0, SearchType.MINIMAX);
    }

    @Override
    public void timeout() {
        // No se implementa timeout en el original
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Minimax con poda alfa-beta, sin cambios en la lógica interna.
     */
    private int[] minmax(HexGameStatus board, int depth, boolean isMax, int alpha, int beta) {
        PlayerType winner = board.GetWinner();
        if (winner != null) {
            // Si el ganador soy yo
            if ((winner == PlayerType.PLAYER1 && myColor == 1) ||
                (winner == PlayerType.PLAYER2 && myColor == 2)) {
                int nodeValue = Integer.MAX_VALUE;
                return new int[]{nodeValue, -1, -1};
            } else {
                int nodeValue = Integer.MIN_VALUE;
                return new int[]{nodeValue, -1, -1};
            }
        }

        if (depth <= 0) {
            int a = heuristic(board);
            return new int[]{a, -1, -1};
        } else {
            ArrayList<Point> cells = getAvailableCells(board);
            int nodeValue;
            int bestRow = -1;
            int bestCol = -1;

            if (isMax) {
                nodeValue = Integer.MIN_VALUE;
                for (Point availableCell : cells) {
                    HexGameStatus newBoard = new HexGameStatus(board);

                    // Colocamos una ficha asumiendo que es el turno de myColor (MAX) o se alterna automáticamente.
                    newBoard.placeStone(availableCell);

                    int val = minmax(newBoard, depth - 1, !isMax, alpha, beta)[0];
                    if (val > nodeValue) {
                        nodeValue = val;
                        bestRow = availableCell.x;
                        bestCol = availableCell.y;
                    }
                    if (nodeValue > alpha) {
                        alpha = nodeValue;
                    }
                    if (beta <= alpha)
                        break;
                }
                return new int[]{alpha, bestRow, bestCol};
            } else {
                nodeValue = Integer.MAX_VALUE;
                for (Point availableCell : cells) {
                    HexGameStatus newBoard = new HexGameStatus(board);

                    // Ahora es turno del rival (MIN), pero confiamos en que `placeStone` y el estado del juego
                    // alternen adecuadamente el turno después de cada movimiento.
                    newBoard.placeStone(availableCell);

                    int val = minmax(newBoard, depth - 1, !isMax, alpha, beta)[0];
                    if (val < nodeValue) {
                        nodeValue = val;
                        bestRow = availableCell.x;
                        bestCol = availableCell.y;
                    }
                    if (nodeValue < beta) {
                        beta = nodeValue;
                    }
                    if (beta <= alpha)
                        break;
                }
                return new int[]{beta, bestRow, bestCol};
            }
        }
    }

    /**
     * Heurística sin cambios en la lógica.
     */
    private int heuristic(HexGameStatus board) {
        int score = 0;
        int size = board.getSize();
        boolean[][] visited = new boolean[size][size];
        score += findChain(board, myColor, visited);
        return score;
    }

    /**
     * Busca cadenas de mi color. Lógica sin cambios, se basa en adyacentes.
     */
    private int findChain(HexGameStatus board, int player, boolean[][] visited) {
        int chain = 0;
        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int tmpChain = 0;
                if (visited[i][j]) continue;
                // Obtener adyacentes hex
                ArrayList<Point> adj = getAdjacents(i, j, size);
                for (Point cell : adj) {
                    if (board.getPos(cell.x, cell.y) == player && !visited[i][j]) {
                        tmpChain++;
                        // Incentivar ejes según la lógica original
                        if (player == 1 && cell.x == i)
                            tmpChain += 2;
                        if (player == 2 && cell.y == j)
                            tmpChain += 2;
                        visited[i][j] = true;
                    }
                }
                chain = Math.max(chain, tmpChain);
                tmpChain = 0;
            }
        }

        if (player == myColor)
            chain *= MY_CHAIN_SCORE;
        return chain;
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

    /**
     * Obtener los adyacentes en un tablero hexagonal.
     */
    private ArrayList<Point> getAdjacents(int r, int c, int size) {
        ArrayList<Point> adj = new ArrayList<>();
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1},{1,-1},{-1,1}};
        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (nr >= 0 && nr < size && nc >=0 && nc < size) {
                adj.add(new Point(nr,nc));
            }
        }
        return adj;
    }
}
