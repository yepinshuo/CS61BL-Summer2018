/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    /** The max image depth level. */
    public static final int MAX_DEPTH = 7;

    /**
     * Takes a user query and finds the grid of images that best matches the query. These images
     * will be combined into one big image (rastered) by the front end. The grid of images must obey
     * the following properties, where image in the grid is referred to as a "tile".
     * <ul>
     *     <li>The tiles collected must cover the most longitudinal distance per pixel (LonDPP)
     *     possible, while still covering less than or equal to the amount of longitudinal distance
     *     per pixel in the query box for the user viewport size.</li>
     *     <li>Contains all tiles that intersect the query bounding box that fulfill the above
     *     condition.</li>
     *     <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     * @param params The RasterRequestParams containing coordinates of the query box and the browser
     *               viewport width and height.
     * @return A valid RasterResultParams containing the computed results.
     */
    public RasterResultParams getMapRaster(RasterRequestParams params) {
        /* Hint: Define additional classes to make it easier to pass around multiple values, and
         * define additional methods to make it easier to test and reason about code. */
        double actualLonDPP = MapServer.ROOT_LONDPP;
        double requestLonDPP = lonDPP(params.lrlon, params.ullon, params.w);
        int depth = 0;

        // Failed cases.
        if (requestLonDPP <= 0) {
            return RasterResultParams.queryFailed();
        }
        if (params.ullat > MapServer.ROOT_ULLAT || params.ullon < MapServer.ROOT_ULLON
                || params.lrlat < MapServer.ROOT_LRLAT || params.lrlon > MapServer.ROOT_LRLON
                || params.ullat < MapServer.ROOT_LRLAT || params.ullon > MapServer.ROOT_LRLON
                || params.lrlat > MapServer.ROOT_ULLAT || params.lrlon < MapServer.ROOT_ULLON) {
            return RasterResultParams.queryFailed();
        }

        // Successful cases.
        while (actualLonDPP > requestLonDPP && depth < MAX_DEPTH) {
            actualLonDPP /= 2;
            depth += 1;
        }
        double widthPerSquareLon = MapServer.ROOT_LON_DELTA / Math.pow(2, depth);
        double widthPerSquareLat = MapServer.ROOT_LAT_DELTA / Math.pow(2, depth);
        int ulImageX = (int) Math.abs((params.ullon - MapServer.ROOT_ULLON) / widthPerSquareLon);
        int ulImageY = (int) Math.abs((params.ullat - MapServer.ROOT_ULLAT) / widthPerSquareLat);
        int lrImageX = (int) Math.abs((params.lrlon - MapServer.ROOT_ULLON) / widthPerSquareLon);
        int lrImageY = (int) Math.abs((params.lrlat - MapServer.ROOT_ULLAT) / widthPerSquareLat);

        String[][] renderGrid = new String[lrImageY - ulImageY + 1][lrImageX - ulImageX + 1];
        for (int i = 0; i <= lrImageY - ulImageY; i++) {
            for (int j = 0; j <= lrImageX - ulImageX; j++) {
                renderGrid[i][j] =
                        "d" + depth + "_x" + (j + ulImageX) + "_y" + (i + ulImageY) + ".png";
            }
        }
        double rasterUllon = MapServer.ROOT_ULLON + ulImageX * widthPerSquareLon;
        double rasterUllat = MapServer.ROOT_ULLAT - ulImageY * widthPerSquareLat;
        double rasterLrlon = MapServer.ROOT_ULLON + (lrImageX + 1) * widthPerSquareLon;
        double rasterLrlat = MapServer.ROOT_ULLAT - (lrImageY + 1) * widthPerSquareLat;
        boolean querySuccess = true;

        RasterResultParams.Builder result = new RasterResultParams.Builder();
        result.setRenderGrid(renderGrid);
        result.setRasterUlLon(rasterUllon);
        result.setRasterUlLat(rasterUllat);
        result.setRasterLrLon(rasterLrlon);
        result.setRasterLrLat(rasterLrlat);
        result.setDepth(depth);
        result.setQuerySuccess(querySuccess);

        return result.create();
    }

    /**
     * Calculates the lonDPP of an image or query box
     * @param lrlon Lower right longitudinal value of the image or query box
     * @param ullon Upper left longitudinal value of the image or query box
     * @param width Width of the query box or image
     * @return lonDPP
     */
    private double lonDPP(double lrlon, double ullon, double width) {
        return (lrlon - ullon) / width;
    }
}
