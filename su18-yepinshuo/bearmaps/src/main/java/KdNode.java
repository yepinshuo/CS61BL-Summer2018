
public class KdNode implements Comparable<KdNode> {
    private GraphDB.Vertex vertex;
    private KdNode left;
    private KdNode right;
    private boolean vertical;

    public KdNode(GraphDB.Vertex vertex, boolean vertical) {
        this.vertex = vertex;
        this.left = null;
        this.right = null;
        this.vertical = vertical;
    }

    public KdNode(GraphDB.Vertex vertex, KdNode left, KdNode right, boolean vertical) {
        this.vertex = vertex;
        this.left = left;
        this.right = right;
        this.vertical = vertical;
    }

    public KdNode getLeft() {
        return left;
    }

    public KdNode getRight() {
        return right;
    }

    public GraphDB.Vertex getVertex() {
        return vertex;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void verticalChange() {
        if (vertical) {
            vertical = false;
        } else {
            vertical = true;
        }
    }

    public void setLeft(KdNode left) {
        this.left = left;
    }

    public void setRight(KdNode right) {
        this.right = right;
    }

    public int compareX(KdNode node) {
        if (GraphDB.projectToX(this.vertex.lon, this.vertex.lat)
                > GraphDB.projectToX(node.vertex.lon, node.vertex.lat)) {
            return 1;
        } else if (GraphDB.projectToX(this.vertex.lon, this.vertex.lat)
                == GraphDB.projectToX(node.vertex.lon, node.vertex.lat)) {
            return 0;
        } else {
            return -1;
        }
    }

    public int compareY(KdNode node) {
        if (GraphDB.projectToY(this.vertex.lon, this.vertex.lat)
                > GraphDB.projectToY(node.vertex.lon, node.vertex.lat)) {
            return 1;
        } else if (GraphDB.projectToY(this.vertex.lon, this.vertex.lat)
                == GraphDB.projectToY(node.vertex.lon, node.vertex.lat)) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public int compareTo(KdNode node) {
        if (vertical) {
            return this.compareX(node);
        } else {
            return this.compareY(node);
        }
    }
}
