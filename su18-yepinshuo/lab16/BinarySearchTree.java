public class BinarySearchTree<T extends Comparable<T>> extends BinaryTree<T> {

    /* Creates an empty BST. */
    public BinarySearchTree() {
        super();
    }

    /* Creates a BST with root as ROOT. */
    public BinarySearchTree(TreeNode root) {
        super(root);
    }

    /* Returns true if the BST contains the given KEY. */
    public boolean contains(T key) {
        if (root == null) {
            return false;
        } else {
            return containsHelper(key, root);
        }
    }

    public boolean containsHelper(T key, TreeNode node) {
        if (key.compareTo(node.item) == 0) {
            return true;
        } else if (key.compareTo(node.item) > 0) {
            if (node.right != null) {
                return containsHelper(key, node.right);
            } else {
                return false;
            }
        } else {
            if (node.left != null) {
                return containsHelper(key, node.left);
            } else {
                return false;
            }
        }
    }

    /* Adds a node for KEY iff KEY isn't in the BST already. */
    public void add(T key) {
        if (root == null) {
            root = new TreeNode(key);
        } else if (!contains(key)) {
            addHelper(key, root);
        }
    }

    public void addHelper(T key, TreeNode node) {
        if (key.compareTo(node.item) > 0) {
            if (node.right == null) {
                node.right = new TreeNode(key);
            } else {
                addHelper(key, node.right);
            }
        } else {
            if (node.left == null) {
                node.left = new TreeNode(key);
            } else {
                addHelper(key, node.left);
            }
        }
    }

    /* Deletes a node from the BST. */
    public T delete(T key) {
        TreeNode parent = null;
        TreeNode curr = root;
        TreeNode delNode = null;
        TreeNode replacement = null;
        boolean rightSide = false;

        while (curr != null && !curr.item.equals(key)) {
            if (((Comparable<T>) curr.item).compareTo(key) > 0) {
                parent = curr;
                curr = curr.left;
                rightSide = false;
            } else {
                parent = curr;
                curr = curr.right;
                rightSide = true;
            }
        }
        delNode = curr;
        if (curr == null) {
            return null;
        }

        if (delNode.right == null) {
            if (root == delNode) {
                root = root.left;
            } else {
                if (rightSide) {
                    parent.right = delNode.left;
                } else {
                    parent.left = delNode.left;
                }
            }
        } else {
            curr = delNode.right;
            replacement = curr.left;
            if (replacement == null) {
                replacement = curr;
            } else {
                while (replacement.left != null) {
                    curr = replacement;
                    replacement = replacement.left;
                }
                curr.left = replacement.right;
                replacement.right = delNode.right;
            }
            replacement.left = delNode.left;
            if (root == delNode) {
                root = replacement;
            } else {
                if (rightSide) {
                    parent.right = replacement;
                } else {
                    parent.left = replacement;
                }
            }
        }
        return delNode.item;
    }
}
