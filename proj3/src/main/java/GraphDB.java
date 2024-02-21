import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;;

/**
 * Graph for storing all of the intersection (vertex) and road (edge)
 * information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /**
     * Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc.
     */
    public class Node {
        private final String name;
        private final long id;
        private final double lon;
        private final double lat;

        Node(long id, String nodeName, double longitude, double latitude) {
            this.id = id;
            this.name = nodeName;
            this.lon = longitude;
            this.lat = latitude;
        }

        public String name() {
            return name;
        }

        public long id() {
            return id;
        }

        public double lon() {
            return lon;
        }

        public double lat() {
            return lat;
        }
    }

    public class Edge {
        private String name;
        private long from;
        private long to;
        private double weight;

        Edge(String edgeName, long from, long to) {
            this.name = edgeName;
            this.from = from;
            this.to = to;
            this.weight = distance(from, to);
        }

        public String name() {
            return name;
        }

        public long from() {
            return from;
        }

        public long to() {
            return to;
        }

        public double weight() {
            return weight;
        }
    }

    private Map<Long, Set<Edge>> graph = new HashMap<Long, Set<Edge>>();
    private Map<Long, Node> idToNode = new HashMap<Long, Node>();
    private TrieMap<Set<Node>> locations = new TrieMap<Set<Node>>();
    private KDTree nodeKDTree = new KDTree();

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * 
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();

        buildKDTree();
    }

    public void addNode(long id, String name, double lon, double lat) {
        if (graph.containsKey(id)) {
            return;
        }

        graph.put(id, new HashSet<Edge>());

        Node node = createNode(id, name, lon, lat);
        idToNode.put(id, node);
    }

    public void addLocation(String name, long id) {
        String cleanName = cleanString(name);
        if (!locations.containsKey(cleanName)) {
            locations.put(cleanName, new HashSet<>());
        }
        Node node = idToNode.get(id);
        locations.get(cleanName).add(node);
    }

    public void addEdge(String name, long v, long w) {
        if (!graph.containsKey(v) || !graph.containsKey(w)) {
            throw new IllegalArgumentException(
                    "nodes are not added into graph yet, but try to add their edge");
        }

        graph.get(v).add(createEdge(name, v, w));
        graph.get(w).add(createEdge(name, w, v));
    }

    public void addWay(String name, long[] vs) {
        for (int i = 0; i < vs.length - 1; i++) {
            addEdge(name, vs[i], vs[i + 1]);
        }
    }

    public Set<Node> getLocations(String locationName) {
        locationName = cleanString(locationName);
        return locations.get(locationName);
    }

    public List<String> getLocationNamesByPrefix(String prefix) {
        List<String> locationNames = new LinkedList<>();

        prefix = cleanString(prefix);
        Set<String> keys = locations.keySetWithPrefix(prefix);
        if (keys == null) {
            return locationNames;
        }

        for (String key : keys) {
            for (Node node : locations.get(key)) {
                locationNames.add(node.name());
            }
        }

        return locationNames;
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and
     * capitalization.
     * 
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     * Remove nodes with no connections from the graph.
     * While this does not guarantee that any two nodes in the remaining graph are
     * connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Iterator<Map.Entry<Long, Set<Edge>>> itr = graph.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<Long, Set<Edge>> entry = itr.next();
            if (entry.getValue().size() <= 0) {
                itr.remove();
            }
        }
    }

    private void buildKDTree() {
        for (long id : graph.keySet()) {
            Node node = idToNode.get(id);
            nodeKDTree.insert(node.id, node.lon, node.lat);
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * 
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        // YOUR CODE HERE, this currently returns only an empty list.
        return graph.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * 
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        Set<Long> adjVertex = new HashSet<>();
        for (Edge e : graph.get(v)) {
            adjVertex.add(e.to());
        }

        return adjVertex;
    }

    public Iterable<Edge> adjEdges(long v) {
        return graph.get(v);
    }

    public Edge edge(long v, long w) {
        Iterable<Edge> edges = graph.get(v);
        for (Edge edge : edges) {
            if (edge.to() == w) {
                return edge;
            }
        }

        return null;
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * 
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * 
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * 
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        return nodeKDTree.nearest(lon, lat);
    }

    /**
     * Gets the longitude of a vertex.
     * 
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        Node node = idToNode.get(v);
        return node.lon;
    }

    /**
     * Gets the latitude of a vertex.
     * 
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        Node node = idToNode.get(v);
        return node.lat;
    }

    private Node createNode(long id, String name, double lon, double lat) {
        return new Node(id, name, lon, lat);
    }

    private Edge createEdge(String name, long from, long to) {
        return new Edge(name, from, to);
    }
}
