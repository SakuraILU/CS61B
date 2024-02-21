import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    private final double ROOT_ULLON = MapServer.ROOT_ULLON;
    private final double ROOT_LRLON = MapServer.ROOT_LRLON;
    private final double ROOT_ULLAT = MapServer.ROOT_ULLAT;
    private final double ROOT_LRLAT = MapServer.ROOT_LRLAT;
    private final int TILE_SIZE = MapServer.TILE_SIZE;
    private final int MAX_DEPTH = 7;

    public Rasterer() {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query.
     * These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size.
     * </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box
     *               and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     *         "render_grid" : String[][], the files to display. <br>
     *         "raster_ul_lon" : Number, the bounding upper left longitude of the
     *         rastered image. <br>
     *         "raster_ul_lat" : Number, the bounding upper left latitude of the
     *         rastered image. <br>
     *         "raster_lr_lon" : Number, the bounding lower right longitude of the
     *         rastered image. <br>
     *         "raster_lr_lat" : Number, the bounding lower right latitude of the
     *         rastered image. <br>
     *         "depth" : Number, the depth of the nodes of the rastered image <br>
     *         "query_success" : Boolean, whether the query was able to successfully
     *         complete; don't
     *         forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        Map<String, Object> results = new HashMap<>();

        double ullon = params.get("ullon");
        double lrlon = params.get("lrlon");
        double ullat = params.get("ullat");
        double lrlat = params.get("lrlat");
        double w = params.get("w");

        boolean querySuccess = true;
        if (lrlon <= ullon || lrlon <= ROOT_ULLON || ullon >= ROOT_LRLON
                || lrlat >= ullat || lrlat >= ROOT_ULLAT || ullat <= ROOT_LRLAT) {
            querySuccess = false;
            results.put("query_success", querySuccess);
            return results;
        }

        int depth = getDepth(ullon, lrlon, w);

        double tileXWidth = (ROOT_LRLON - ROOT_ULLON) / Math.pow(2, depth);
        double tileYWidth = (ROOT_ULLAT - ROOT_LRLAT) / Math.pow(2, depth);

        int lTileN = (int) ((ullon - ROOT_ULLON) / tileXWidth);
        int rTileN = (int) ((lrlon - ROOT_ULLON) / tileXWidth);
        int tTileN = (int) ((ROOT_ULLAT - ullat) / tileYWidth);
        int bTileN = (int) ((ROOT_ULLAT - lrlat) / tileYWidth);

        String[][] renderGrid = new String[bTileN - tTileN + 1][];
        for (int row = 0; row < renderGrid.length; row++) {
            renderGrid[row] = new String[rTileN - lTileN + 1];
            for (int col = 0; col < renderGrid[0].length; col++) {
                int x = col + lTileN;
                int y = row + tTileN;
                renderGrid[row][col] = String.format("d%d_x%d_y%d.png", depth, x, y);
            }
        }

        double rasterUllon = ROOT_ULLON + lTileN * tileXWidth;
        double rasterLrlon = ROOT_ULLON + (rTileN + 1) * tileXWidth;
        double rasterUllat = ROOT_ULLAT - tTileN * tileYWidth;
        double rasterLrlat = ROOT_ULLAT - (bTileN + 1) * tileYWidth;

        results.put("render_grid", renderGrid);
        results.put("raster_ul_lon", rasterUllon);
        results.put("raster_ul_lat", rasterUllat);
        results.put("raster_lr_lon", rasterLrlon);
        results.put("raster_lr_lat", rasterLrlat);
        results.put("depth", depth);
        results.put("query_success", querySuccess);
        return results;
    }

    private int getDepth(double ullon, double lrlon, double w) {
        double desireLonDPP = (lrlon - ullon) / w;
        int depth = (int) Math.ceil(
                Math.log((ROOT_LRLON - ROOT_ULLON) / (TILE_SIZE * desireLonDPP)) / Math.log(2));
        return depth > MAX_DEPTH ? MAX_DEPTH : depth;
    }
}
