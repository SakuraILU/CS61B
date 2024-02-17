package hw4.puzzle;

import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class Solver {
    private WorldState start;
    private WorldState goal;
    private Set<WorldState> marked;
    private Map<WorldState, Integer> distTo;
    private Map<WorldState, WorldState> edgeTo;
    private LinkedList<WorldState> solution;

    public Solver(WorldState initial) {
        start = initial;
        marked = new HashSet<>();
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();

        distTo.put(initial, 0);
        edgeTo.put(initial, initial);

        PriorityQueue<WorldState> nodeToVisit = new PriorityQueue<>(new Comparator<WorldState>() {
            public int compare(WorldState node1, WorldState node2) {
                return (distTo.get(node1) + node1.estimatedDistanceToGoal())
                        - (distTo.get(node2) + node2.estimatedDistanceToGoal());
            }
        });

        nodeToVisit.add(initial);

        while (!nodeToVisit.isEmpty()) {
            WorldState node = nodeToVisit.remove();
            marked.add(node);
            if (node.isGoal()) {
                goal = node;
                break;
            }

            for (WorldState neighbor : node.neighbors()) {
                if (neighbor.equals(edgeTo.get(node))) {
                    continue;
                }

                if (!distTo.containsKey(neighbor) || distTo.get(node) + 1 < distTo.get(neighbor)) {
                    edgeTo.put(neighbor, node);
                    distTo.put(neighbor, distTo.get(node) + 1);

                    if (nodeToVisit.contains(neighbor)) {
                        nodeToVisit.remove(neighbor);
                    }
                    nodeToVisit.add(neighbor);
                }
            }
        }

        solution = new LinkedList<>();
        for (WorldState node = goal; !node.equals(start); node = edgeTo.get(node)) {
            solution.addFirst(node);
        }
        solution.addFirst(start);
    }

    public int moves() {
        return solution.size() - 1;
    }

    public Iterable<WorldState> solution() {
        return solution;
    }
}
