public class ArrayOperations {
    /**
     * Delete the value at the given position in the argument array, shifting
     * all the subsequent elements down, and storing a 0 as the last element of
     * the array.
     */
    public static void delete(int[] values, int pos) {
        if (pos < 0 || pos >= values.length) {
            return;
        }
        // TODO: YOUR CODE HERE
        if (pos == values.length - 1) {
            values[values.length - 1] = 0;
        } else {
            int temp;
            while (pos <= values.length - 2){
                temp = values[pos + 1];
                values[pos] = temp;
                pos += 1;
            }
            values[values.length - 1] = 0;
        }
        
    }

    /**
     * Insert newInt at the given position in the argument array, shifting all
     * the subsequent elements up to make room for it. The last element in the
     * argument array is lost.
     */
    public static void insert(int[] values, int pos, int newInt) {
        if (pos < 0 || pos >= values.length) {
            return;
        }
        // TODO: YOUR CODE HERE
        int temp = newInt;
        int next = values[pos];
        while (pos <= values.length - 2){
            values[pos] = temp;
            temp = next; 
            next = values[pos + 1];
            pos += 1;
        }
        values[pos] = temp;
    }
}
