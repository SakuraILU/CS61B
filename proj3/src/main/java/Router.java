import java.util.Objects;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two
 * points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for
 * your
 * satisfaction (or the autograder), upgrade your implementation by switching it
 * to A*.
 * Your code will probably not be fast enough to pass the autograder unless you
 * use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code,
 * and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * 
     * @param g       The graph to use.
     * @param stlon   The longitude of the start location.
     * @param stlat   The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
            double destlon, double destlat) {
        final long s = g.closest(stlon, stlat);
        final long t = g.closest(destlon, destlat);

        Map<Long, Double> distTo = new HashMap<>();
        Map<Long, Long> preNode = new HashMap<>();
        distTo.put(s, 0.0);
        preNode.put(s, null);

        ArrayHeap<Long> toVisit = new ArrayHeap<>();

        toVisit.insert(s, distTo.get(s) + g.distance(s, t));
        while (!toVisit.isEmpty()) {
            long node = toVisit.removeMin();
            if (node == t) {
                break;
            }

            for (GraphDB.Edge e : g.adjEdges(node)) {
                long neighbor = e.to();
                double weight = e.weight();
                if (!distTo.containsKey(neighbor)
                        || distTo.get(node) + weight < distTo.get(neighbor)) {
                    distTo.put(neighbor, distTo.get(node) + weight);
                    preNode.put(neighbor, node);

                    toVisit.changePriority(
                            neighbor, distTo.get(neighbor) + g.distance(neighbor, t));
                }
            }
        }

        LinkedList<Long> path = new LinkedList<>();
        for (Long node = t; node != null; node = preNode.get(node)) {
            path.addFirst(node);
        }

        return path;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * 
     * @param g     The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     *         route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        List<NavigationDirection> navs = new LinkedList<>();

        int direction = NavigationDirection.START;
        double distance = 0;
        for (int i = 1; i < route.size(); i++) {
            long preV = route.get(i - 1);
            long curV = route.get(i);
            GraphDB.Edge preE = g.edge(preV, curV);
            distance += preE.weight();

            if (i == route.size() - 1) {
                NavigationDirection nav = new NavigationDirection();
                nav.way = preE.name();
                nav.direction = direction;
                nav.distance = distance;
                navs.add(nav);
                break;
            }

            long nextV = route.get(i + 1);
            GraphDB.Edge curE = g.edge(curV, nextV);
            if (!preE.name().equals(curE.name())) {
                NavigationDirection nav = new NavigationDirection();
                nav.way = preE.name();
                nav.direction = direction;
                nav.distance = distance;
                navs.add(nav);

                double preBearing = g.bearing(curV, preV);
                double curBearing = g.bearing(nextV, curV);
                double diffBearing = normalizeDiffBearing(curBearing - preBearing);
                double absDiffBearing = Math.abs(diffBearing);
                if (absDiffBearing <= 15) {
                    direction = NavigationDirection.STRAIGHT;
                } else if (absDiffBearing <= 30) {
                    direction = diffBearing < 0 ? NavigationDirection.SLIGHT_LEFT
                            : NavigationDirection.SLIGHT_RIGHT;
                } else if (absDiffBearing <= 100) {
                    direction = diffBearing < 0 ? NavigationDirection.LEFT
                            : NavigationDirection.RIGHT;
                } else {
                    direction = diffBearing < 0 ? NavigationDirection.SHARP_LEFT
                            : NavigationDirection.SHARP_RIGHT;
                }

                distance = 0;
            }
        }

        return navs;
    }

    private static double normalizeDiffBearing(double diffBearing) {
        if (diffBearing > 180) {
            diffBearing = diffBearing - 360;
        } else if (diffBearing < -180) {
            diffBearing = diffBearing + 360;
        }

        return diffBearing;
    }

    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions. */
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";

        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents. */
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it
         * into
         * a Navigation Direction object.
         * 
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                        && way.equals(((NavigationDirection) o).way)
                        && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
