package hw4.puzzle;

import java.util.LinkedList;
import java.util.Queue;

public class Board implements WorldState {
    private final int[][] tiles;
    private final int BLANK = 0;

    public Board(int[][] tiles) {
        this.tiles = new int[tiles.length][];
        for (int i = 0; i < this.tiles.length; i++) {
            this.tiles[i] = tiles[i].clone();
        }
    }

    public int tileAt(int i, int j) throws IndexOutOfBoundsException {
        if (i < 0 || i >= tiles.length || j < 0 || j >= tiles[0].length) {
            throw new IndexOutOfBoundsException();
        }

        return tiles[i][j];
    }

    public int size() {
        return tiles.length;
    }

    @Override
    /**
     * Returns neighbors of this board.
     * SPOILERZ: This is the answer.
     */
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new LinkedList<>();
        int hug = size();
        int bug = -1;
        int zug = -1;
        for (int rug = 0; rug < hug; rug++) {
            for (int tug = 0; tug < hug; tug++) {
                if (tileAt(rug, tug) == BLANK) {
                    bug = rug;
                    zug = tug;
                }
            }
        }
        int[][] ili1li1 = new int[hug][hug];
        for (int pug = 0; pug < hug; pug++) {
            for (int yug = 0; yug < hug; yug++) {
                ili1li1[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int l11il = 0; l11il < hug; l11il++) {
            for (int lil1il1 = 0; lil1il1 < hug; lil1il1++) {
                if (Math.abs(-bug + l11il) + Math.abs(lil1il1 - zug) - 1 == 0) {
                    ili1li1[bug][zug] = ili1li1[l11il][lil1il1];
                    ili1li1[l11il][lil1il1] = BLANK;
                    Board neighbor = new Board(ili1li1);
                    neighbors.add(neighbor);
                    ili1li1[l11il][lil1il1] = ili1li1[bug][zug];
                    ili1li1[bug][zug] = BLANK;
                }
            }
        }
        return neighbors;
    }

    public int hamming() {
        int dist = 0;

        for (int i = 1; i < tiles.length * tiles.length; i++) {
            int row = toRow(i);
            int col = toCol(i);
            if (tiles[row][col] != i) {
                dist++;
            }
        }

        return dist;
    }

    public int manhattan() {
        int dist = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                int elem = tiles[i][j];
                if (elem == BLANK) {
                    continue;
                }
                int targetRow = toRow(elem);
                int targetCol = toCol(elem);
                dist += Math.abs(i - targetRow) + Math.abs(j - targetCol);
            }
        }

        return dist;
    }

    private int toRow(int v) {
        return (v - 1) / tiles.length;
    }

    private int toCol(int v) {
        return Math.floorMod(v - 1, tiles[0].length);
    }

    @Override
    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    @Override
    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }

        if (y == null) {
            return false;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }

        Board other = (Board) y;

        if (other.size() != size()) {
            return false;
        }

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j] != other.tiles[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    /**
     * Returns the string representation of the board.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    @Override
    public int hashCode() {
        int hash = 1;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                hash = 31 * hash + tiles[i][j];
            }
        }
        return hash;
    }

}
