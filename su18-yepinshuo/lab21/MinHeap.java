import java.util.ArrayList;

/* A MinHeap class of Comparable elements backed by an ArrayList. */
public class MinHeap<E extends Comparable<E>> {

    /* An ArrayList that stores the elements in this MinHeap. */
    private ArrayList<E> contents;
    private int size;

    /* Initializes an empty MinHeap. */
    public MinHeap() {
        contents = new ArrayList<>();
        contents.add(null);
        size = 0;
    }

    /* Returns the element at index INDEX, and null if it is out of bounds. */
    private E getElement(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.get(index);
        }
    }

    /* Sets the element at index INDEX to ELEMENT. If the ArrayList is not big
       enough, add elements until it is the right size. */
    private void setElement(int index, E element) {
        while (index >= contents.size()) {
            contents.add(null);
        }
        contents.set(index, element);
    }

    /* Swaps the elements at the two indices. */
    private void swap(int index1, int index2) {
        E element1 = getElement(index1);
        E element2 = getElement(index2);
        setElement(index2, element1);
        setElement(index1, element2);
    }

    /* Prints out the underlying heap sideways. Use for debugging. */
    @Override
    public String toString() {
        return toStringHelper(1, "");
    }

    /* Recursive helper method for toString. */
    private String toStringHelper(int index, String soFar) {
        if (getElement(index) == null) {
            return "";
        } else {
            String toReturn = "";
            int rightChild = getRightOf(index);
            toReturn += toStringHelper(rightChild, "        " + soFar);
            if (getElement(rightChild) != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + getElement(index) + "\n";
            int leftChild = getLeftOf(index);
            if (getElement(leftChild) != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += toStringHelper(leftChild, "        " + soFar);
            return toReturn;
        }
    }

    /* Returns the index of the left child of the element at index INDEX. */
    private int getLeftOf(int index) {
        return index * 2;
    }

    /* Returns the index of the right child of the element at index INDEX. */
    private int getRightOf(int index) {
        return index * 2 + 1;
    }

    /* Returns the index of the parent of the element at index INDEX. */
    private int getParentOf(int index) {
        return index / 2;
    }

    /* Returns the index of the smaller element. At least one index has a
       non-null element. */
    private int min(int index1, int index2) {
        if (getElement(index1) == null && getElement(index2) != null) {
            return index2;
        } else if (getElement(index2) == null && getElement(index1) != null) {
            return index1;
        } else {
            if (getElement(index1).compareTo(getElement(index2)) > 0) {
                return index2;
            } else {
                return index1;
            }
        }
    }

    /* Returns but does not remove the smallest element in the MinHeap. */
    public E peek() {
        return getElement(1);
    }

    /* Bubbles up the element currently at index INDEX. */
    private void bubbleUp(int index) {
        while (min(index, getParentOf(index)) == index && index != 1) {
            swap(index, getParentOf(index));
            index = getParentOf(index);
        }
    }

    /* Bubbles down the element currently at index INDEX. */
    private void bubbleDown(int index) {
        while (min(index, getLeftOf(index)) == getLeftOf(index)
                || min(index, getRightOf(index)) == getRightOf(index)) {
            int minimum = min(getLeftOf(index), getRightOf(index));
            swap(index, min(getLeftOf(index), getRightOf(index)));
            index = minimum;
        }
    }

    /* Inserts element into the MinHeap. */
    public void insert(E element) {
        setElement(size() + 1, element);
        bubbleUp(size() + 1);
        size += 1;
    }

    /* Returns the number of elements in the MinHeap. */
    public int size() {
        return size;
    }

    /* Returns the smallest element. */
    public E removeMin() {
        if (size != 1) {
            swap(1, size());
            E result = contents.remove(size());
            bubbleDown(1);
            size -= 1;
            return result;
        } else {
            size -= 1;
            return contents.remove(1);
        }
    }

    /* Updates the position of ELEMENT inside the MinHeap, which may have been
       mutated since the inital insert. If a copy of ELEMENT does not exist in
       the MinHeap, do nothing.*/
    public void update(E element) {
        int i;
        for (i = 1; i <= size(); i++) {
            if (element.equals(getElement(i))) {
                setElement(i, element);
                break;
            }
        }
        bubbleUp(i);
        bubbleDown(i);
    }
}
