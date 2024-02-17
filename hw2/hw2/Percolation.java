package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private enum State {
        BLOCKED, OPEND
    }

    private State[][] sites;
    private int size;
    private int source; // if a site connect to the source, it's full
    private WeightedQuickUnionUF graph;

    // create N-by-N grid, with all sites initially blocked
    public Percolation(int N) throws IllegalArgumentException {
        if (N <= 0) {
            throw new IllegalArgumentException();
        }

        sites = new State[N][];
        for (int i = 0; i < N; i++) {
            sites[i] = new State[N];
        }
        size = N;

        source = 0;
        graph = new WeightedQuickUnionUF(N * N + 1);
    }

    // open the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (sites[row][col] == State.OPEND) {
            return;
        }

        sites[row][col] = State.OPEND;

        int curNode = toNodeId(row, col);
        if (row > 0 && sites[row - 1][col] == State.OPEND) {
            graph.union(curNode, toNodeId(row - 1, col));
        }
        if (row < size - 1 && sites[row + 1][col] == State.OPEND) {
            graph.union(curNode, toNodeId(row + 1, col));
        }
        if (col > 0 && sites[row][col - 1] == State.OPEND) {
            graph.union(curNode, toNodeId(row, col - 1));
        }
        if (col < size - 1 && sites[row][col + 1] == State.OPEND) {
            graph.union(curNode, toNodeId(row, col + 1));
        }

        // if open a site in the first row, connect to the source node.
        if (row == 0) {
            graph.union(curNode, source);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        return sites[row][col] == State.OPEND;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        return graph.connected(toNodeId(row, col), source);
    }

    // number of open sites
    public int numberOfOpenSites() {
        int count = 0;
        for (State[] row : sites) {
            for (State state : row) {
                if (state == State.OPEND) {
                    count++;
                }
            }
        }
        return count;
    }

    // does the system percolate?
    public boolean percolates() {
        for (int col = 0; col < size; col++) {
            if (isFull(size - 1, col)) {
                return true;
            }
        }
        return false;
    }

    // map the site to int ID
    private int toNodeId(int col, int row) {
        return row * size + col + 1;
    }

    public static void main(String[] args) {
    }
}
