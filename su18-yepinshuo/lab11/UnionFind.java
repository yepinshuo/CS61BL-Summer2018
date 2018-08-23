public class UnionFind {

    public int[] A;

    /* Creates a UnionFind data structure holding N vertices. Initially, all
       vertices are in disjoint sets. */
    public UnionFind(int N) {
        A = new int[N];
        for (int i = 0; i <= N - 1; i++) {
            A[i] = -1;
        }
    }

    /* Returns the size of the set V belongs to. */
    public int sizeOf(int v) {
        int num = A[v];
        while (num >= 0) {
            num = A[num];
        }
        return -num;
    }

    /* Returns the parent of V. If V is the root of a tree, returns the
       negative size of the tree for which V is the root. */
    public int parent(int v) {
        return A[v];
    }

    /* Returns true if nodes V1 and V2 are connected. */
    public boolean connected(int v1, int v2) {
        while (A[v1] >= 0) {
            v1 = A[v1];
        }

        while (A[v2] >= 0) {
            v2 = A[v2];
        }
        return v1 == v2;
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. If invalid vertices are passed into this
       function, throw an IllegalArgumentException. */
    public int find(int v) {
        if (v <= A.length && v >= 0) {
            int u = v;
            while (A[v] >= 0) {
                v = A[v];
            }
            while (A[u] != v && A[u] >= 0) {
                int w = A[u];
                A[u] = v;
                u = w;
            }
            return v;
        }
        throw new IllegalArgumentException();
    }

    /* Connects two elements V1 and V2 together. V1 and V2 can be any element,
       and a union-by-size heuristic is used. If the sizes of the sets are
       equal, tie break by connecting V1's root to V2's root. Union-ing a vertex
       with itself or vertices that are already connected should not change the
       structure. */
    public void union(int v1, int v2) {
        if (!connected(v1, v2)) {
            int a = find(v1);
            int b = find(v2);
            if (A[a] <= A[b]) {
                A[b] = A[a] + A[b];
                A[a] = b;
            } else {
                A[a] = A[a] + A[b];
                A[b] = a;
            }
        }
    }
}
