public class KDTree {
    private class Point implements Comparable<Point> {
        private long id;
        private double lon;
        private double lat;
        private boolean isLon;

        Point(long id, double lon, double lat, boolean isLon) {
            this.id = id;
            this.lon = lon;
            this.lat = lat;
            this.isLon = isLon;
        }

        public double distance(Point p) {
            return GraphDB.distance(lon, lat, p.lon, p.lat);
        }

        /**
         * distance to the axis.
         * Lower approximation! Here, we treat the earth as a plane
         * Lower approximation of Axis distance is ok for kdtree,
         * because we are likely to expore more bad side than acurate approximation, no
         * space needed to explored is missed.
         * However, on the other hand, if upper approximation of Axis distance functions
         * right, we may ignore some good side!
         */
        public double distanceAxis(Point p) {
            if (isLon) {
                return this.lon - p.lon;
            } else {
                return this.lat - p.lat;
            }
        }

        public int compareTo(Point p) {
            if (isLon) {
                return Double.compare(lon, p.lon);
            } else {
                return Double.compare(lat, p.lat);
            }
        }
    }

    private class Node {
        public Point point;
        public Node left;
        public Node right;

        Node(Point point) {
            this.point = point;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;
    int size;

    private double minDistance;
    private Point bestPoint;

    KDTree() {
        root = null;
        size = 0;
    }

    public void insert(long id, double lon, double lat) {
        Point point = new Point(id, lon, lat, true);
        root = insert(root, point, true);
    }

    private Node insert(Node node, Point point, boolean isLon) {
        if (node == null) {
            size++;

            point.isLon = isLon;
            return new Node(point);
        }

        int cmp = node.point.compareTo(point);
        if (node.point.distance(point) == 0) {
            return node;
        } else if (cmp <= 0) {
            node.left = insert(node.left, point, !isLon);
        } else {
            node.right = insert(node.right, point, !isLon);
        }

        return node;
    }

    public boolean contains(double lon, double lat) {
        Point point = new Point(-1, lon, lat, true);
        return contains(root, point, true);
    }

    private boolean contains(Node node, Point point, boolean isLon) {
        if (node == null) {
            return false;
        }

        int cmp = node.point.compareTo(point);
        if (node.point.distance(point) == 0) {
            return true;
        } else if (cmp <= 0) {
            return contains(node.left, point, !isLon);
        } else {
            return contains(node.right, point, !isLon);
        }
    }

    public long nearest(double lon, double lat) {
        if (isEmpty()) {
            return -1;
        }

        Point goal = new Point(-1, lon, lat, true);
        minDistance = Double.MAX_VALUE;
        bestPoint = null;

        nearest(root, goal);

        return bestPoint.id;
    }

    private void nearest(Node node, Point goal) {
        if (node == null) {
            return;
        }

        double distance = node.point.distance(goal);
        if (distance < minDistance) {
            minDistance = distance;
            bestPoint = node.point;
        }

        Node goodSide = null;
        Node badSide = null;
        int cmp = node.point.compareTo(goal);
        if (cmp <= 0) {
            goodSide = node.left;
            badSide = node.right;
        } else {
            goodSide = node.right;
            badSide = node.left;
        }

        nearest(goodSide, goal);

        double distanceAxis = node.point.distanceAxis(goal);
        if (distanceAxis < minDistance) {
            nearest(badSide, goal);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
