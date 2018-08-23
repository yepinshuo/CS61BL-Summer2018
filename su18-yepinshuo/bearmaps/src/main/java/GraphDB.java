import org.xml.sax.SAXException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Kevin Lowe, Antares Chen, Kevin Lin
 */
public class GraphDB {
    /**
     * This constructor creates and starts an XML parser, cleans the nodes, and prepares the
     * data structures for processing. Modify this constructor to initialize your data structures.
     * @param dbPath Path to the XML file to be parsed.
     */

    private HashMap<Long, Vertex> vMap = new HashMap<>();
    private HashMap<Long, Edge> eMap = new HashMap<>();
    KdNode rootNode;
    KdTree tree;

    public GraphDB(String dbPath) {
        File inputFile = new File(dbPath);
        try (FileInputStream inputStream = new FileInputStream(inputFile)) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputStream, new GraphBuildingHandler(this));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();

        List<KdNode> finalList = new ArrayList<>();
        finalList.add(0, null);
        finalList.add(null);
        List<KdNode> lst = new ArrayList<>();

        for (Vertex v : vMap.values()) {
            finalList.add(null);
            finalList.add(null);
            lst.add(new KdNode(v, true));
        }
        sortHelper(lst, finalList, 1);

        rootNode = finalList.get(1);
        rootHelper(finalList, rootNode, 1);
        tree = new KdTree(rootNode);
    }

    private static void rootHelper(List<KdNode> finalList, KdNode node, int index) {
        if (index * 2 <= finalList.size() - 1) {
            if (finalList.get(index * 2) != null) {
                node.setLeft(finalList.get(index * 2));
                rootHelper(finalList, node.getLeft(), index * 2);
            }
            if (finalList.get(index * 2 + 1) != null) {
                node.setRight(finalList.get(index * 2 + 1));
                rootHelper(finalList, node.getRight(), index * 2 + 1);
            }
        }
    }

    private static void sortHelper(List<KdNode> list, List<KdNode> finalList, int index) {
        if (list.isEmpty()) {
            return;
        } else {
            Collections.sort(list);
            finalList.set(index, list.get(list.size() / 2));
            List<KdNode> leftList = new ArrayList<>();
            List<KdNode> rightList = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                if (i != list.size() / 2) {
                    list.get(i).verticalChange();
                }

                if (i < list.size() / 2) {
                    leftList.add(list.get(i));
                } else if (i > list.size() / 2) {
                    rightList.add(list.get(i));
                }
            }
            sortHelper(leftList, finalList, index * 2);
            sortHelper(rightList, finalList, index * 2 + 1);
        }
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     * Remove nodes with no connections from the graph.
     * While this does not guarantee that any two nodes in the remaining graph are connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        // TODO
        HashSet<Long> removeItemIDs = new HashSet<>();
        for (Vertex v : vMap.values()) {
            if (v.neighbors.size() == 0) {
                removeItemIDs.add(v.id);
            }
        }
        vMap.keySet().removeAll(removeItemIDs);
    }

    /**
     * Returns the longitude of vertex <code>v</code>.
     * @param v The ID of a vertex in the graph.
     * @return The longitude of that vertex, or 0.0 if the vertex is not in the graph.
     */
    double lon(long v) {
        // TODO
        if (vMap.containsKey(v)) {
            return vMap.get(v).lon;
        } else {
            return 0.0;
        }
    }

    /**
     * Returns the latitude of vertex <code>v</code>.
     * @param v The ID of a vertex in the graph.
     * @return The latitude of that vertex, or 0.0 if the vertex is not in the graph.
     */
    double lat(long v) {
        // TODO
        if (vMap.containsKey(v)) {
            return vMap.get(v).lat;
        } else {
            return 0.0;
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of all vertex IDs in the graph.
     */
    Iterable<Long> vertices() {
        // TODO
        return vMap.keySet();
    }

    /**
     * Returns an iterable over the IDs of all vertices adjacent to <code>v</code>.
     * @param v The ID for any vertex in the graph.
     * @return An iterable over the IDs of all vertices adjacent to <code>v</code>, or an empty
     * iterable if the vertex is not in the graph.
     */
    Iterable<Long> adjacent(long v) {
        // TODO
        if (vMap.containsKey(v)) {
            return vMap.get(v).neighbors;
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Returns the great-circle distance between two vertices, v and w, in miles.
     * Assumes the lon/lat methods are implemented properly.
     * @param v The ID for the first vertex.
     * @param w The ID for the second vertex.
     * @return The great-circle distance between vertices and w.
     * @source https://www.movable-type.co.uk/scripts/latlong.html
     */
    public double distance(long v, long w) {
        double phi1 = Math.toRadians(lat(v));
        double phi2 = Math.toRadians(lat(w));
        double dphi = Math.toRadians(lat(w) - lat(v));
        double dlambda = Math.toRadians(lon(w) - lon(v));

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Returns the ID of the vertex closest to the given longitude and latitude.
     * @param lon The given longitude.
     * @param lat The given latitude.
     * @return The ID for the vertex closest to the <code>lon</code> and <code>lat</code>.
     */
    public long closest(double lon, double lat) {
        // TODO
/*        List<Vertex> vertices = new ArrayList<>(vMap.values());
        List<Long> ids = new ArrayList<>(vMap.keySet());
        vMap.put((long) 0, new Vertex(0, lon, lat));
        long result = vertices.get(0).id;
        double resultDis = distance(0, result);
        for (long id : ids) {
            double dis = distance(0, id);
            if (dis < resultDis) {
                resultDis = dis;
                result = id;
            }
        }
        vMap.remove((long) 0);
        return result; */
        long result = rootNode.getVertex().id;
        double resultDistance =
                distance2D(lon, lat, rootNode.getVertex().lon, rootNode.getVertex().lat);
        return closestHelper(lon, lat, rootNode, result, resultDistance);
    }

    public long closestHelper(double lon,
                              double lat, KdNode node, long result, double resultDistance) {
        if (distance2D(lon, lat, node.getVertex().lon, node.getVertex().lat) < resultDistance) {
            result = node.getVertex().id;
            resultDistance = distance2D(lon, lat, node.getVertex().lon, node.getVertex().lat);
        }
        if (node.isVertical()) {
            if (lon < node.getVertex().lon) {
                if (node.getLeft() != null) {
                    result = closestHelper(lon, lat, node.getLeft(), result, resultDistance);
                    // not prune
                    if (notPrune(lon, lat, resultDistance, node) && node.getRight() != null) {
                        resultDistance =
                                distance2D(lon, lat, vMap.get(result).lon, vMap.get(result).lat);
                        return closestHelper(lon, lat, node.getRight(), result, resultDistance);
                    }
                    return result;
                } else {
                    if (notPrune(lon, lat, resultDistance, node) && node.getRight() != null) {
                        return closestHelper(lon, lat, node.getRight(), result, resultDistance);
                    }
                    return result;
                }
            } else {
                if (node.getRight() != null) {
                    result = closestHelper(lon, lat, node.getRight(), result, resultDistance);
                    // not prune
                    if (notPrune(lon, lat, resultDistance, node) && node.getLeft() != null) {
                        resultDistance =
                                distance2D(lon, lat, vMap.get(result).lon, vMap.get(result).lat);
                        return closestHelper(lon, lat, node.getLeft(), result, resultDistance);
                    }
                    return result;
                } else {
                    if (notPrune(lon, lat, resultDistance, node) && node.getLeft() != null) {
                        return closestHelper(lon, lat, node.getLeft(), result, resultDistance);
                    }
                    return result;
                }
            }
        } else {
            if (lat < node.getVertex().lat) {
                if (node.getLeft() != null) {
                    result = closestHelper(lon, lat, node.getLeft(), result, resultDistance);
                    // not prune
                    if (notPrune(lon, lat, resultDistance, node) && node.getRight() != null) {
                        resultDistance =
                                distance2D(lon, lat, vMap.get(result).lon, vMap.get(result).lat);
                        return closestHelper(lon, lat, node.getRight(), result, resultDistance);
                    }
                    return result;
                } else {
                    if (notPrune(lon, lat, resultDistance, node) && node.getRight() != null) {
                        return closestHelper(lon, lat, node.getRight(), result, resultDistance);
                    }
                    return result;
                }
            } else {
                if (node.getRight() != null) {
                    result = closestHelper(lon, lat, node.getRight(), result, resultDistance);
                    // not prune
                    if (notPrune(lon, lat, resultDistance, node) && node.getLeft() != null) {
                        resultDistance =
                                distance2D(lon, lat, vMap.get(result).lon, vMap.get(result).lat);
                        return closestHelper(lon, lat, node.getLeft(), result, resultDistance);
                    }
                    return result;
                } else {
                    if (notPrune(lon, lat, resultDistance, node) && node.getLeft() != null) {
                        return closestHelper(lon, lat, node.getLeft(), result, resultDistance);
                    }
                    return result;
                }
            }
        }
    }

    public boolean notPrune(double lon, double lat, double resultDistance, KdNode node) {
        if (node.isVertical()) {
            return  !(resultDistance
                    <= Math.abs(projectToX(lon, lat)
                            - projectToX(node.getVertex().lon, node.getVertex().lat)));
        } else {
            return  !(resultDistance
                    <= Math.abs(projectToY(lon, lat)
                            - projectToY(node.getVertex().lon, node.getVertex().lat)));
        }
    }

    static double distance2D(double lon1, double lat1, double lon2, double lat2) {
        double x1 = projectToX(lon1, lat1);
        double x2 = projectToX(lon2, lat2);
        double y1 = projectToY(lon1, lat1);
        double y2 = projectToY(lon2, lat2);
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Return the Euclidean x-value for some point, p, in Berkeley. Found by computing the
     * Transverse Mercator projection centered at Berkeley.
     * @param lon The longitude for p.
     * @param lat The latitude for p.
     * @return The flattened, Euclidean x-value for p.
     * @source https://en.wikipedia.org/wiki/Transverse_Mercator_projection
     */
    static double projectToX(double lon, double lat) {
        double dlon = Math.toRadians(lon - ROOT_LON);
        double phi = Math.toRadians(lat);
        double b = Math.sin(dlon) * Math.cos(phi);
        return (K0 / 2) * Math.log((1 + b) / (1 - b));
    }

    /**
     * Return the Euclidean y-value for some point, p, in Berkeley. Found by computing the
     * Transverse Mercator projection centered at Berkeley.
     * @param lon The longitude for p.
     * @param lat The latitude for p.
     * @return The flattened, Euclidean y-value for p.
     * @source https://en.wikipedia.org/wiki/Transverse_Mercator_projection
     */
    static double projectToY(double lon, double lat) {
        double dlon = Math.toRadians(lon - ROOT_LON);
        double phi = Math.toRadians(lat);
        double con = Math.atan(Math.tan(phi) / Math.cos(dlon));
        return K0 * (con - Math.toRadians(ROOT_LAT));
    }

    /**
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        return Collections.emptyList();
    }

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A <code>List</code> of <code>LocationParams</code> whose cleaned name matches the
     * cleaned <code>locationName</code>
     */
    public List<LocationParams> getLocations(String locationName) {
        return Collections.emptyList();
    }

    /**
     * Returns the initial bearing between vertices <code>v</code> and <code>w</code> in degrees.
     * The initial bearing is the angle that, if followed in a straight line along a great-circle
     * arc from the starting point, would take you to the end point.
     * Assumes the lon/lat methods are implemented properly.
     * @param v The ID for the first vertex.
     * @param w The ID for the second vertex.
     * @return The bearing between <code>v</code> and <code>w</code> in degrees.
     * @source https://www.movable-type.co.uk/scripts/latlong.html
     */
    double bearing(long v, long w) {
        double phi1 = Math.toRadians(lat(v));
        double phi2 = Math.toRadians(lat(w));
        double lambda1 = Math.toRadians(lon(v));
        double lambda2 = Math.toRadians(lon(w));

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    public HashMap<Long, Vertex> getvMap() {
        return vMap;
    }

    public HashMap<Long, Edge> geteMap() {
        return eMap;
    }

    public static class Vertex {
        double lon;
        double lat;
        long id;
        List<Long> neighbors;
        String name;
        double distance;
        Vertex predecessor;

        public Vertex(long id, double lon, double lat) {
            this.lon = lon;
            this.lat = lat;
            this.id = id;
            this.neighbors = new ArrayList<>();
            this.distance = 0;
            this.predecessor = null;
        }

        public void addName(String n) {
            this.name = n;
        }

        public void addNeighbor(long newId) {
            neighbors.add(newId);
        }
    }

    public static class Edge {
        long id;
        List<Long> vertexList;
        String name;
        String highway;
        String maxSpeed;

        public Edge(long id, List<Long> vertexList) {
            this.id = id;
            this.vertexList = vertexList;
        }

        void addVertex(long i) {
            vertexList.add(i);
        }

        void setName(String name) {
            this.name = name;
        }

        void setHighway(String highway) {
            this.highway = highway;
        }

        void setMaxSpeed(String maxSpeed) {
            this.maxSpeed = maxSpeed;
        }
    }


    /** Radius of the Earth in miles. */
    private static final int R = 3963;
    /** Latitude centered on Berkeley. */
    private static final double ROOT_LAT = (MapServer.ROOT_ULLAT + MapServer.ROOT_LRLAT) / 2;
    /** Longitude centered on Berkeley. */
    private static final double ROOT_LON = (MapServer.ROOT_ULLON + MapServer.ROOT_LRLON) / 2;
    /**
     * Scale factor at the natural origin, Berkeley. Prefer to use 1 instead of 0.9996 as in UTM.
     * @source https://gis.stackexchange.com/a/7298
     */
    private static final double K0 = 1.0;
}
