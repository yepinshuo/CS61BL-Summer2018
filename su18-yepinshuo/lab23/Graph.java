import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Stack;
import java.util.HashSet;
import java.util.Collections;
import java.util.ArrayList;

public class Graph implements Iterable<Integer> {

    private LinkedList<Edge>[] adjLists;
    private int vertexCount;

    /* Initializes a graph with NUMVERTICES vertices and no Edges. */
    public Graph(int numVertices) {
        adjLists = (LinkedList<Edge>[]) new LinkedList[numVertices];
        for (int k = 0; k < numVertices; k++) {
            adjLists[k] = new LinkedList<Edge>();
        }
        vertexCount = numVertices;
    }

    /* Adds a directed Edge (V1, V2) to the graph. */
    public void addEdge(int v1, int v2) {
        addEdge(v1, v2, 0);
    }

    /* Adds an undirected Edge (V1, V2) to the graph. */
    public void addUndirectedEdge(int v1, int v2) {
        addUndirectedEdge(v1, v2, 0);
    }

    /* Adds a directed Edge (V1, V2) to the graph with weight WEIGHT. If the
       Edge already exists, replaces the current Edge with a new Edge with
       weight WEIGHT. */
    public void addEdge(int v1, int v2, int weight) {
        for (Edge edge : adjLists[v1]) {
            if (v2 == edge.to) {
                edge.weight = weight;
                return;
            }
        }
        adjLists[v1].add(new Edge(v1, v2, weight));
    }

    /* Adds an undirected Edge (V1, V2) to the graph with weight WEIGHT. If the
       Edge already exists, replaces the current Edge with a new Edge with
       weight WEIGHT. */
    public void addUndirectedEdge(int v1, int v2, int weight) {
        boolean v1Tov2Exists = false;
        boolean v2Tov1Exists = false;

        for (Edge edge : adjLists[v1]) {
            if (v2 == edge.to) {
                edge.weight = weight;
                v1Tov2Exists = true;
            }
        }
        for (Edge edge : adjLists[v2]) {
            if (v1 == edge.to) {
                edge.weight = weight;
                v2Tov1Exists = true;
            }
        }

        if (!v1Tov2Exists) {
            adjLists[v1].add(new Edge(v1, v2, weight));
        }
        if (!v2Tov1Exists) {
            adjLists[v2].add(new Edge(v2, v1, weight));
        }
    }

    /* Returns true if there exists an Edge from vertex FROM to vertex TO.
       Returns false otherwise. */
    public boolean isAdjacent(int from, int to) {
        for (Edge edge : adjLists[from]) {
            if (to == edge.to) {
                return true;
            }
        }
        return false;
    }

    /* Returns a list of all the vertices u such that the Edge (V, u)
       exists in the graph. */
    public List<Integer> neighbors(int v) {
        List<Integer> result = new LinkedList<>();
        for (Edge edge : adjLists[v]) {
            result.add(edge.to);
        }
        return result;
    }

    /* Returns the number of incoming Edges for vertex V. */
    public int inDegree(int v) {
        int count = 0;
        for (LinkedList<Edge> lst : adjLists) {
            for (Edge edge : lst) {
                if (v == edge.to) {
                    count += 1;
                }
            }
        }
        return count;
    }

    /* Returns an Iterator that outputs the vertices of the graph in topological
       sorted order. */
    public Iterator<Integer> iterator() {
        return new TopologicalIterator();
    }

    /* A class that iterates through the vertices of this graph, starting with
       vertex START. If the iteration from START has no path from START to some
       vertex v, then the iteration will not include v. */
    private class DFSIterator implements Iterator<Integer> {

        private Stack<Integer> fringe;
        private HashSet<Integer> visited;

        DFSIterator(int start) {
            fringe = new Stack<>();
            fringe.push(start);
            visited = new HashSet<>();
        }

        public boolean hasNext() {
            for (int i :fringe) {
                if (!visited.contains(i)) {
                    return true;
                }
            }
            return false;
        }

        public Integer next() {
            while (!fringe.isEmpty()) {
                int result = fringe.pop();
                if (!visited.contains(result)) {
                    List<Integer> list = neighbors(result);
                    list.sort(Integer::compareTo);
                    Collections.reverse(list);

                    for (int i : list) {
                        if (!visited.contains(i)) {
                            fringe.push(i);
                        }
                    }
                    visited.add(result);
                    return result;
                }
            }
            return -1;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    /* Returns the collected result of performing a depth-first search on this
       graph's vertices starting from V. */
    public List<Integer> dfs(int v) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new DFSIterator(v);

        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    /* Returns true iff there exists a path from START to STOP. Assumes both
       START and STOP are in this graph. If START == STOP, returns true. */
    public boolean pathExists(int start, int stop) {
        return dfs(start).contains(stop);
    }


    /* Returns the path from START to STOP. If no path exists, returns an empty
       List. If START == STOP, returns a List with START. */
    public List<Integer> path(int start, int stop) {
        List<Integer> lst = new LinkedList<>();
        List<Integer> result = new LinkedList<>();
        Iterator<Integer> iter = new DFSIterator(start);
        if (pathExists(start, stop)) {
            while (iter.hasNext()) {
                int i = iter.next();
                lst.add(i);
                if (i == stop) {
                    break;
                }
            }

            int curr = stop;
            ((LinkedList<Integer>) result).addFirst(stop);
            while (curr != start) {
                for (int i : lst) {
                    if (isAdjacent(i, curr)) {
                        ((LinkedList<Integer>) result).addFirst(i);
                        curr = i;
                        break;
                    }
                }
            }
            return result;
        } else {
            return result;
        }
    }

    public List<Integer> topologicalSort() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new TopologicalIterator();
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    private class TopologicalIterator implements Iterator<Integer> {

        private Stack<Integer> fringe;


        TopologicalIterator() {
            fringe = new Stack<Integer>();
        }

        public boolean hasNext() {
            return false;
        }

        public Integer next() {
            return 0;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private class Edge {

        private int from;
        private int to;
        private int weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public String toString() {
            return "(" + from + ", " + to + ", weight = " + weight + ")";
        }

    }

    private void generateG1() {
        addEdge(0, 1);
        addEdge(0, 2);
        addEdge(0, 4);
        addEdge(1, 2);
        addEdge(2, 0);
        addEdge(2, 3);
        addEdge(4, 3);
    }

    private void generateG2() {
        addEdge(0, 1);
        addEdge(0, 2);
        addEdge(0, 4);
        addEdge(1, 2);
        addEdge(2, 3);
        addEdge(4, 3);
    }

    private void generateG3() {
        addUndirectedEdge(0, 2);
        addUndirectedEdge(0, 3);
        addUndirectedEdge(1, 4);
        addUndirectedEdge(1, 5);
        addUndirectedEdge(2, 3);
        addUndirectedEdge(2, 6);
        addUndirectedEdge(4, 5);
    }

    private void generateG4() {
        addEdge(0, 1);
        addEdge(1, 2);
        addEdge(2, 0);
        addEdge(2, 3);
        addEdge(4, 2);
    }

    private void generateG5() {
        addUndirectedEdge(0, 1);
        addUndirectedEdge(0, 2);
        addUndirectedEdge(1, 3);
        addUndirectedEdge(1, 4);
        addUndirectedEdge(2, 5);
        addUndirectedEdge(2, 6);
        addUndirectedEdge(3, 7);
    }

    private void printDFS(int start) {
        System.out.println("DFS traversal starting at " + start);
        List<Integer> result = dfs(start);
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
        System.out.println();
        System.out.println();
    }

    private void printPath(int start, int end) {
        System.out.println("Path from " + start + " to " + end);
        List<Integer> result = path(start, end);
        if (result.size() == 0) {
            System.out.println("No path from " + start + " to " + end);
            return;
        }
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
        System.out.println();
        System.out.println();
    }

    private void printTopologicalSort() {
        System.out.println("Topological sort");
        List<Integer> result = topologicalSort();
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
    }

    public static void main(String[] args) {
        Graph g1 = new Graph(5);
        g1.generateG1();
        g1.printDFS(0);
        g1.printDFS(2);
        g1.printDFS(3);
        g1.printDFS(4);

        g1.printPath(0, 3);
        g1.printPath(0, 4);
        g1.printPath(1, 3);
        g1.printPath(1, 4);
        g1.printPath(4, 0);

        Graph g2 = new Graph(5);
        g2.generateG2();
        g2.printTopologicalSort();

        Graph g3 = new Graph(7);
        g3.generateG3();
        g3.printDFS(0);
        g3.printDFS(2);
        g3.printDFS(3);
        g3.printDFS(4);

        g3.printPath(0, 3);
        g3.printPath(0, 6);
        g3.printPath(1, 3);
        g3.printPath(4, 1);
        g3.printPath(4, 0);

        Graph g5 = new Graph(8);
        g5.generateG5();
        g5.printDFS(0);
        g5.printPath(0, 6);
    }
}
