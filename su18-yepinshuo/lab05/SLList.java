import java.util.Objects;

/**
  * An SLList is a list of integers, which hides the terrible truth of the
  * nakedness within.
  */
public class SLList {
    private static class IntNode {
        private int item;
        private IntNode next;

        IntNode(int i, IntNode n) {
            item = i;
            next = n;
        }

        @Override
        public String toString() {
            return "IntNode{"
                    + "item=" + item
                    + ", next=" + next
                    + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            IntNode intNode = (IntNode) o;
            return item == intNode.item
                    && Objects.equals(next, intNode.next);
        }

    }

    /* The first item (if it exists) is at sentinel.next. */
    private IntNode sentinel;
    private int size;

    /** Creates an empty SLList. */
    public SLList() {
        sentinel = new IntNode(63, null);
        size = 0;
    }

    public SLList(int x) {
        sentinel = new IntNode(63, null);
        sentinel.next = new IntNode(x, null);
        size = 1;
    }

    /** Returns an SLList consisting of the given values. */
    public static SLList of(int... values) {
        SLList list = new SLList();
        for (int i = values.length - 1; i >= 0; i -= 1) {
            list.addFirst(values[i]);
        }
        return list;
    }

    /** Returns the size of the list. */
    public int size() {
        return size;
    }

    /** Adds x to the front of the list. */
    public void addFirst(int x) {
        sentinel.next = new IntNode(x, sentinel.next);
        size += 1;
    }

    /** Returns the first item in the list. */
    public int getFirst() {
        return sentinel.next.item;
    }

    /** Return the value at the given index. */
    public int get(int index) {
        IntNode p = sentinel.next;
        while (index > 0) {
            p = p.next;
            index -= 1;
        }
        return p.item;
    }

    /** Adds x to the end of the list. */
    public void addLast(int x) {
        size += 1;
        IntNode p = sentinel;
        while (p.next != null) {
            p = p.next;
        }
        p.next = new IntNode(x, null);
    }

    /** Adds x to the list at the specified index. */
    public void add(int index, int x) {
        if (index == 0) {
            sentinel.next = new IntNode(x, sentinel.next);
        } else if (index > this.size()) {
            if (this.size() == 0) {
                sentinel.next = new IntNode(x, null);
            } else {
                add(this.size(), x);
            }
        } else {
            IntNode p = sentinel.next;
            IntNode q = sentinel.next;
            while (index > 0) {
                if (index > 1) {
                    p = p.next;
                }
                q = q.next;
                index -= 1;
            }
            IntNode s = new IntNode(x, null);
            s.next = q;
            p.next = s;
        }
        size += 1;
    }

    /** Returns the reverse of this list. This method is destructive. */
    public void reverse() {
        if (this.size() >= 2) {
            int s = this.size();
            int index = this.size() - 1;
            while (index >= 0) {
                int item = this.get(index);
                this.addLast(item);
                index -= 1;
            }
            IntNode p = sentinel.next;
            while (s > 0) {
                p = p.next;
                s -= 1;
            }
            sentinel.next = p;
            size = size / 2;
        }
    }

    @Override
    public String toString() {
        return "SLList{"
                + "sentinel=" + sentinel
                + ", size=" + size
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SLList slList = (SLList) o;
        return size == slList.size
                && Objects.equals(sentinel, slList.sentinel);
    }
}
