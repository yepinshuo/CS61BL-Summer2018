/**
 * A data structure to represent a Linked List of Integers.
 * Each IntList represents one node in the overall Linked List.
 */
public class IntList {
    public int first;
    public IntList rest;

    public IntList(int f, IntList r) {
        first = f;
        rest = r;
    }

    /** Returns an IntList consisting of the given values. */
    public static IntList of(int... values) {
        if (values.length == 0) {
            return null;
        }
        IntList p = new IntList(values[0], null);
        IntList front = p;
        for (int i = 1; i < values.length; i++) {
            p.rest = new IntList(values[i], null);
            p = p.rest;
        }
        return front;
    }

    /** Returns the size of the list. */
    public int size() {
        if (rest == null) {
            return 1;
        }
        return 1 + rest.size();
    }

    /** Returns [position]th value in this list. */
    public int get(int position) {
        // TODO: YOUR CODE HERE
        IntList p = this;
        if (position == 0){
            return p.first;
        } else if (p.rest != null) {
            return p.rest.get(position - 1);
        } else {
            return 0;
        }
        
    }

    /** Returns the string representation of the list. */
    public String toString() {
        // TODO: YOUR CODE HERE
        IntList p = this;
        if (p.rest == null) {
            return Integer.toString(p.first);
        } else {
            return p.first + " " + p.rest.toString();
        }
    }

    /** Returns whether this and the given list or object are equal. */
    public boolean equals(Object o) {
        IntList other = (IntList) o;
        // TODO: YOUR CODE HERE
        IntList p = this;
        if (other.size() != p.size()){
            return false;
        } else {
            while(other != null){
                if (other.first != p.first){
                    return false;
                }
                other = other.rest;
                p = p.rest;
            }
            return true;
        }
        
    }
}
