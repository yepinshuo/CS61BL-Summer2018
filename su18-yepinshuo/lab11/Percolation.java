// import edu.princeton.cs.algs4.QuickFindUF;
// import edu.princeton.cs.algs4.QuickUnionUF;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    public WeightedQuickUnionUF P;
    public boolean[][] blocks;
    public int size;
    public int opensites;

    /* Creates an N-by-N grid with all sites initially blocked. */
    public Percolation(int N) {
        if (N > 0) {
            P = new WeightedQuickUnionUF(N * N + 2);
            blocks = new boolean[N][N];
            for (int i = 0; i <= N - 1; i++) {
                for (int j = 0; j <= N - 1; j++) {
                    blocks[i][j] = true;
                }
            }
            size = N;
            opensites = 0;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /* Opens the site (row, col) if it is not open already. */
    public void open(int row, int col) {
        if (valid(row, col)) {
            if (!isOpen(row, col)) {
                blocks[row][col] = false;
                opensites += 1;
                if (row != 0) {
                    if (isOpen(row - 1, col)) {
                        P.union(xyTo1D(row, col), xyTo1D(row - 1, col));
                    }
                } else {
                    P.union(xyTo1D(row, col), size * size);
                }

                if (row != size - 1) {
                    if (isOpen(row + 1, col)) {
                        P.union(xyTo1D(row, col), xyTo1D(row + 1, col));
                    }
                } else {
                    P.union(xyTo1D(row, col), size * size + 1);
                }

                if (col != 0) {
                    if (isOpen(row, col - 1)) {
                        P.union(xyTo1D(row, col), xyTo1D(row, col - 1));
                    }
                }

                if (col != size - 1) {
                    if (isOpen(row, col + 1)) {
                        P.union(xyTo1D(row, col), xyTo1D(row, col + 1));
                    }
                }
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /* Returns true if the site at (row, col) is open. */
    public boolean isOpen(int row, int col) {
        if (valid(row, col)) {
            return !blocks[row][col];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /* Returns true if the site (row, col) is full. */
    public boolean isFull(int row, int col) {
        if (valid(row, col)) {
            return P.connected(xyTo1D(row, col), size * size);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /* Returns the number of open sites. */
    public int numberOfOpenSites() {
        return opensites;
    }

    /* Returns true if the system percolates. */
    public boolean percolates() {
        return P.connected(size * size, size * size + 1);
    }

    /* Converts row and column coordinates into a number. This will be helpful
       when trying to tie in the disjoint sets into our NxN grid of sites. */
    private int xyTo1D(int row, int col) {
        return row * size + col;
    }
    /* Returns true if (row, col) site exists in the NxN grid of sites.
       Otherwise, return false. */
    private boolean valid(int row, int col) {
        return row >= 0 && row <= size - 1 && col >= 0 && col <= size - 1;
    }

}
