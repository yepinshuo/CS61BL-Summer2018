public class RedBlackTree<T extends Comparable<T>> {

    /* Root of the tree. */
    RBTreeNode<T> root;

    /* Creates an empty RedBlackTree. */
    public RedBlackTree() {
        root = null;
    }

    /* Creates a RedBlackTree from a given BTree (2-3-4) TREE. */
    public RedBlackTree(BTree<T> tree) {
        Node<T> btreeRoot = tree.root;
        root = buildRedBlackTree(btreeRoot);
    }

    /* Builds a RedBlackTree that has isometry with given 2-3-4 tree rooted at
       given node R, and returns the root node. */
    RBTreeNode<T> buildRedBlackTree(Node<T> r) {
        if (r == null) {
            return null;
        } else {
            if (r.getItemCount() == 1) {
                return new RBTreeNode<T>(true, r.getItemAt(0),
                        buildRedBlackTree(r.getChildAt(0)), buildRedBlackTree(r.getChildAt(1)));
            } else if (r.getItemCount() == 2) {
                return new RBTreeNode<T>(true, r.getItemAt(0),
                        buildRedBlackTree(r.getChildAt(0)),
                        new RBTreeNode<T>(false, r.getItemAt(1),
                                buildRedBlackTree(r.getChildAt(1)),
                                buildRedBlackTree(r.getChildAt(2))));
            } else {
                return new RBTreeNode<T>(true, r.getItemAt(1),
                        new RBTreeNode<T>(false, r.getItemAt(0),
                                buildRedBlackTree(r.getChildAt(0)),
                                buildRedBlackTree(r.getChildAt(1))),
                        new RBTreeNode<T>(false, r.getItemAt(2),
                                buildRedBlackTree(r.getChildAt(2)),
                                buildRedBlackTree(r.getChildAt(3))));
            }
        }
    }

    /* Flips the color of NODE and its children. Assume that NODE has both left
       and right children. */
    void flipColors(RBTreeNode<T> node) {
        node.isBlack = !node.isBlack;
        node.left.isBlack = !node.left.isBlack;
        node.right.isBlack = !node.right.isBlack;
    }

    /* Rotates the given node NODE to the right. Returns the new root node of
       this subtree. */
    RBTreeNode<T> rotateRight(RBTreeNode<T> node) {
        if (node == null) {
            return null;
        } else {
            return new RBTreeNode<T>(node.isBlack, node.left.item, node.left.left,
                    new RBTreeNode<T>(false, node.item, node.left.right, node.right));
        }
    }

    /* Rotates the given node NODE to the left. Returns the new root node of
       this subtree. */
    RBTreeNode<T> rotateLeft(RBTreeNode<T> node) {
        if (node == null) {
            return null;
        } else {
            return new RBTreeNode<T>(node.isBlack, node.right.item,
                    new RBTreeNode<T>(false, node.item, node.left, node.right.left),
                    node.right.right);
        }
    }

    /* Insert ITEM into the red black tree, rotating
       it accordingly afterwards. */
    void insert(T item) {
        if (root == null) {
            root = new RBTreeNode(true, item);
        } else {
            insertHelper(item, root, null);
        }
    }

    void insertHelper(T item, RBTreeNode<T> node, RBTreeNode<T> parentNode) {
        if (item.compareTo(node.item) != 0) {
            if (item.compareTo(node.item) > 0) {
                if (node.right == null) {
                    if (node.isBlack) {
                        if (node.left == null) {
                            node.right = new RBTreeNode<T>(false, item);
                            node = rotateLeft(node);
                        } else {
                            node.right = new RBTreeNode<T>(true, item);
                            flipColors(node);
                        }
                    } else {
                        node.right = new RBTreeNode<T>(false, item);
                        node = rotateLeft(node);
                        parentNode = rotateRight(parentNode);
                        flipColors(parentNode);
                    }
                } else {
                    insertHelper(item, node.right, node);
                }
            } else {
                if (node.left == null) {
                    if (node.isBlack) {
                        node.left = new RBTreeNode<T>(false, item);
                    } else {
                        node.left = new RBTreeNode<T>(false, item);
                        parentNode = rotateRight(parentNode);
                        flipColors(parentNode);
                    }
                } else {
                    insertHelper(item, node.left, node);
                }
            }
        }
    }

    /* Returns whether the given node NODE is red. Null nodes (children of leaf
       nodes are automatically considered black. */
    private boolean isRed(RBTreeNode<T> node) {
        return node != null && !node.isBlack;
    }

    static class RBTreeNode<T> {

        final T item;
        boolean isBlack;
        RBTreeNode<T> left;
        RBTreeNode<T> right;

        /* Creates a RBTreeNode with item ITEM and color depending on ISBLACK
           value. */
        RBTreeNode(boolean isBlack, T item) {
            this(isBlack, item, null, null);
        }

        /* Creates a RBTreeNode with item ITEM, color depending on ISBLACK
           value, left child LEFT, and right child RIGHT. */
        RBTreeNode(boolean isBlack, T item, RBTreeNode<T> left,
                   RBTreeNode<T> right) {
            this.isBlack = isBlack;
            this.item = item;
            this.left = left;
            this.right = right;
        }
    }

}
