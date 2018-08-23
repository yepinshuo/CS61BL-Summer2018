/**
 * A data structure to represent a Linked List of Integers.
 * Each IntList represents one node in the overall Linked List.
 *
 * This is a dummy implementation to allow IntListTest to compile. Replace this
 * file with your own IntList class.
 */
public class IntList {
    int first;
    IntList rest;

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
        for (int i = 1; i < values.length; i += 1) {
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
        if (position == 0) {
            return first;
        } else {
            return rest.get(position - 1);
        }
    }

    public void add(int value) {
        if (this.rest == null) {
            this.rest = new IntList(value, null);
        } else {
            this.rest.add(value);
        }
    }

    public int smallest() {
        if (this.rest == null) {
            return this.first;
        } else {
            return Math.min(this.first, this.rest.smallest());
        }
    }

    public int squaredSum() {
        if (this.rest == null) {
            return (this.first * this.first);
        } else {
            return (this.first * this.first) + this.rest.squaredSum();
        }
    }

    public static void dSquareList(IntList L) {
        while (L != null) {
            L.first = L.first * L.first;
            L = L.rest;
        }
    }

    public static IntList catenate(IntList A, IntList B) {
        if (A == null) {
            return B;
        } else if (B == null) {
            return A;
        } else {
            IntList C = new IntList(0, null);
            C.first = A.first;
            C.rest = catenate(A.rest, B);
            return C;
        }
    }

    public static IntList dcatenate(IntList A, IntList B) {
        if (A == null) {
            return B;
        } else if (B == null) {
            return A;
        } else if (B.rest == null) {
            A.add(B.first);
            return A;
        } else {
            A.add(B.first);
            return dcatenate(A, B.rest);
        }
    }


    /** Returns the string representation of the list. */
    public String toString() {
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
        IntList p = this;
        if (other.size() != p.size()) {
            return false;
        } else {
            while (other != null) {
                if (other.first != p.first) {
                    return false;
                }
                other = other.rest;
                p = p.rest;
            }
            return true;
        }

    }
}
