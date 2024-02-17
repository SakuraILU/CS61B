package lab11.graphs;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author Josh Hug
 */
public class MazeAStarPath extends MazeExplorer {
    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        for (int i = 0; i < distTo.length; i++) {
            distTo[i] = Integer.MAX_VALUE;
        }
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Estimate of the distance from v to the target. */
    private int h(int v) {
        int vx = maze.toX(v);
        int vy = maze.toY(v);
        int tx = maze.toX(t);
        int ty = maze.toY(t);

        return Math.abs(vx - tx) + Math.abs(vy - ty);
    }

    /** Finds vertex estimated to be closest to target. */
    private int findMinimumUnmarked() {
        return -1;
        /* You do not have to use this method. */
    }

    /** Performs an A star search from vertex s. */
    private void astar(int s) {
        // TODO
        Comparator<Integer> cmp = new Comparator<Integer>() {
            public int compare(Integer node1, Integer node2) {
                // A start, priority is distToSource + estimateDistToTarget
                return distTo[node1] + h(node1) - distTo[node2] - h(node2);
            }
        };

        PriorityQueue<Integer> nodeToVisit = new PriorityQueue<>(cmp);
        nodeToVisit.add(s);

        while (!nodeToVisit.isEmpty()) {
            // visit the smallest node
            int v = nodeToVisit.remove();
            marked[v] = true;
            announce();
            if (v == t) {
                return;
            }

            // relax all unMarked neigbhors
            for (int neighbor : maze.adj(v)) {
                if (marked[neighbor]) {
                    continue;
                }

                if (distTo[v] + 1 < distTo[neighbor]) {
                    edgeTo[neighbor] = v;
                    distTo[neighbor] = distTo[v] + 1;
                    announce();

                    // change priority of this node
                    if (nodeToVisit.contains(neighbor)) {
                        nodeToVisit.remove(neighbor);
                    }
                    nodeToVisit.add(neighbor);
                }
            }
        }
    }

    @Override
    public void solve() {
        astar(s);
    }

}
