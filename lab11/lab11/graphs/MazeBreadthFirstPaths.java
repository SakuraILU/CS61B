package lab11.graphs;

import java.util.*;

/**
 * @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /*
     * Inherits public fields:
     * public int[] distTo;
     * public int[] edgeTo;
     * public boolean[] marked;
     */

    private int s;
    private int t;
    private Maze maze;

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        // Add more variables here!
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        // TODO: Your code here. Don't forget to update distTo, edgeTo, and marked, as
        // well as call announce()
        Queue<Integer> nodeToVisit = new LinkedList<>();
        nodeToVisit.add(s);

        while (!nodeToVisit.isEmpty()) {
            int node = nodeToVisit.remove();
            marked[node] = true;
            announce();
            if (node == t) {
                return;
            }

            for (Integer neighbor : maze.adj(node)) {
                if (marked[neighbor]) {
                    continue;
                }
                edgeTo[neighbor] = node;
                distTo[neighbor] = distTo[node] + 1;
                announce();
                nodeToVisit.add(neighbor);
            }
        }
    }

    @Override
    public void solve() {
        bfs();
    }
}
