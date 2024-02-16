package lab11.graphs;

import java.util.LinkedList;
import java.util.List;

import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.Stack;

/**
 * @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /*
     * Inherits public fields:
     * public int[] distTo;
     * public int[] edgeTo;
     * public boolean[] marked;
     */
    private Maze maze;
    private LinkedList<Integer> onPath;

    public MazeCycles(Maze m) {
        super(m);
        maze = m;
        onPath = new LinkedList<>();
    }

    @Override
    public void solve() {
        // TODO: Your code here!
        for (int v = 0; v < maze.V(); v++) {
            if (marked[v]) {
                continue;
            }
            distTo[v] = 0;
            edgeTo[v] = v;
            if (hasCycle(v)) {
                return;
            }
        }
    }

    // Helper methods go here
    private boolean hasCycle(int v) {
        marked[v] = true;
        onPath.addFirst(v);
        announce();

        for (Integer neighbor : maze.adj(v)) {
            if (onPath.contains(neighbor) && neighbor != onPath.get(1)) {
                return true;
            }
            if (marked[neighbor]) {
                continue;
            }

            edgeTo[neighbor] = v;
            announce();
            distTo[neighbor] = distTo[v] + 1;
            if (hasCycle(neighbor)) {
                return true;
            }
        }

        onPath.removeFirst();
        return false;
    }
}
