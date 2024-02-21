import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

public class TestKDTree {

    private class Point {
        public long id;
        public double lon;
        public double lat;

        Point(long id, double lon, double lat, boolean isLon) {
            this.id = id;
            this.lon = lon;
            this.lat = lat;
        }
    }

    @Test
    /** Test add, size and contains */
    public void testAdd() {
        int n = 10;
        KDTree t = new KDTree();

        for (int i = 0; i < n; i++) {
            t.insert(i, i, i);
        }

        assertEquals("KDTree should have size " + n, n, t.size());
        for (int i = 0; i < n; i++) {
            assertTrue("KDTree should contain point " + i, t.contains(i, i));
        }
    }

    @Test
    /** Test add and nearest */
    public void testNearest() {
        int n = 10;
        KDTree t = new KDTree();

        for (int i = 0; i < n; i++) {
            t.insert(i, i, i);
        }

        assertEquals("Nearest point to 0, 0 should be 0", 0, t.nearest(0.1, 0.2));
        assertEquals("Nearest point to 1, 1 should be 1", 1, t.nearest(1.3, 0.9));
        assertEquals("Nearest point to 2, 2 should be 2", 2, t.nearest(2.05, 2.3));
    }

    @Test
    /** Difference Test with other Set */
    public void testDifference() {
        KDTree t = new KDTree();
        Set<Point> set = new HashSet<>();

        // test add, size, contains and nearest
        int round = 10000;
        int range = 10;
        for (int i = 0; i < round; i++) {
            int type = (int) (Math.random() * 4);
            int idx = (int) (Math.random() * range);
            double lon = Math.random() * range;
            double lat = Math.random() * range;
            switch (type) {
                case 0:
                    // handle insert
                    t.insert(idx, lon, lat);
                    set.add(new Point(idx, lon, lat, true));
                    break;
                case 1:
                    // handle contains
                    boolean expected = set.contains(new Point(idx, lon, lat, true));
                    boolean actual = t.contains(lon, lat);
                    assertEquals("KDTree should contain point " + idx, expected, actual);
                    break;
                case 2:
                    // handle size
                    assertEquals("KDTree should have size " + set.size(), set.size(), t.size());
                    break;
                case 3:
                    // handle nearest
                    long expected2 = nearest(set, lon, lat);
                    long actual2 = t.nearest(lon, lat);
                    assertEquals("Nearest point to " + lon + ", " + lat + " should be " + expected2,
                            expected2, actual2);
                    break;
            }
        }

    }

    private long nearest(Set<Point> set, double lon, double lat) {
        long nearestId = -1;
        double minDistance = Double.MAX_VALUE;

        for (Point point : set) {
            double distance = GraphDB.distance(point.lon, point.lat, lon, lat);
            if (distance < minDistance) {
                minDistance = distance;
                nearestId = point.id;
            }
        }

        return nearestId;
    }
}
