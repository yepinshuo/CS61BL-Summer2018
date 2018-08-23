import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Objects;

/**
 * This class provides a <code>shortestPath</code> method and <code>routeDirections</code> for
 * finding routes between two points on the map.
 */
public class Router {
    /**
     * Return a <code>List</code> of vertex IDs corresponding to the shortest path from a given
     * starting coordinate and destination coordinate.
     * @param g <code>GraphDB</code> data source.
     * @param stlon The longitude of the starting coordinate.
     * @param stlat The latitude of the starting coordinate.
     * @param destlon The longitude of the destination coordinate.
     * @param destlat The latitude of the destination coordinate.
     * @return The <code>List</code> of vertex IDs corresponding to the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g,
                                          double stlon, double stlat,
                                          double destlon, double destlat) {
        // TODO
        long startVertex = g.closest(stlon, stlat);
        long destVertex = g.closest(destlon, destlat);
        PriorityQueue<GraphDB.Vertex> fringe =
                new PriorityQueue<GraphDB.Vertex>((x, y) ->
                        comparatorHelper(destVertex, x, y, g));
        fringe.add(g.getvMap().get(startVertex));
        HashSet<GraphDB.Vertex> visited = new HashSet<>();

        while (!fringe.isEmpty()) {
            GraphDB.Vertex v = fringe.poll();
            if (v.id == destVertex) {
                List<Long> lst = new LinkedList<>();
                ((LinkedList<Long>) lst).addFirst(v.id);
                GraphDB.Vertex x = v.predecessor;
                while (x != null) {
                    ((LinkedList<Long>) lst).addFirst(x.id);
                    x = x.predecessor;
                }
                for (GraphDB.Vertex vertex : g.getvMap().values()) {
                    vertex.distance = 0;
                    vertex.predecessor = null;
                }
                return lst;
            }

            for (Long i : v.neighbors) {
                GraphDB.Vertex w = g.getvMap().get(i);

                if (!visited.contains(w)) {
                    if (!fringe.contains(w)) {
                        w.distance = v.distance + g.distance(i, v.id);
                        w.predecessor = v;
                        fringe.add(w);
                    } else {
                        if (v.distance + g.distance(i, v.id) < w.distance) {
                            w.distance = v.distance + g.distance(v.id, i);
                            w.predecessor = v;
                            fringe.add(w);
                        }
                    }
                }
            }
            visited.add(v);
        }
        return Collections.emptyList();
    }

    public static int comparatorHelper(long dest, GraphDB.Vertex x, GraphDB.Vertex y, GraphDB g) {
        if (g.distance(dest, x.id) + x.distance > g.distance(dest, y.id) + y.distance) {
            return 1;
        } else if (g.distance(dest, x.id) + x.distance > g.distance(dest, y.id) + y.distance) {
            return 0;
        } else {
            return -1;
        }
    }
    /**
     * Given a <code>route</code> of vertex IDs, return a <code>List</code> of
     * <code>NavigationDirection</code> objects representing the travel directions in order.
     * @param g <code>GraphDB</code> data source.
     * @param route The shortest-path route of vertex IDs.
     * @return A new <code>List</code> of <code>NavigationDirection</code> objects.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        // TODO
        return Collections.emptyList();
    }

    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0, STRAIGHT = 1, SLIGHT_LEFT = 2, SLIGHT_RIGHT = 3,
                RIGHT = 4, LEFT = 5, SHARP_LEFT = 6, SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction represented.*/
        int direction;
        /** The name of this way. */
        String way;
        /** The distance along this way. */
        double distance = 0.0;

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Returns a new <code>NavigationDirection</code> from a string representation.
         * @param dirAsString <code>String</code> instructions for a navigation direction.
         * @return A new <code>NavigationDirection</code> based on the string, or <code>null</code>
         * if unable to parse.
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
                // Not a valid nd
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
