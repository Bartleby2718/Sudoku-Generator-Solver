import java.util.ArrayList;

public class Utils {
    public static ArrayList<Integer> markupToArrayList(int[] markup) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < markup.length; i++)
            if (markup[i] == 1)
                list.add(i + 1); // i is an index; we want the entries
        return list;
    }

    public static int countOnes(int[] array) {
        int counter = 0;
        for (int i = 0; i < array.length; i++)
            if (array[i] == 1)
                counter++;
        return counter;
    }

    // Only checks if all elements are distinct
    // because solve() guarantees that only the positive number up through square is entered
    public static boolean isValid(int[][] grid) {
        ArrayList<Integer> list;
        int length = grid.length;
        // Check rows
        for (int i = 0; i < length; i++) {
            list = new ArrayList<Integer>();
            for (int j = 0; j < length; j++) {
                if (list.contains(grid[i][j]))
                    return false;
                list.add(grid[i][j]);
            }
        }
        // Check columns
        for (int j = 0; j < length; j++) {
            list = new ArrayList<Integer>();
            for (int i = 0; i < length; i++) {
                if (list.contains(grid[i][j]))
                    return false;
                list.add(grid[i][j]);
            }
        }
        // Check squares
        int root = Math.round((long) Math.sqrt(length));
        for (int i = 0; i < length; i += root) {
            for (int j = 0; j < length; j += root) {
                list = new ArrayList<Integer>();
                for (int m = 0; m < root; m++)
                    for (int n = 0; n < root; n++) {
                        if (list.contains(grid[i + m][j + n]))
                            return false;
                        list.add(grid[i][j]);
                    }
            }
        }
        return true;
    }

    public static int[][] copyGrid(int[][] grid, int row, int col, int square) {
        int[][] copy = new int[square][square];
        for (int i = 0; i < square; i++)
            System.arraycopy(grid[row + i], col, copy[i], 0, square);
        return copy;
    }
}