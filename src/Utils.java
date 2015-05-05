import javax.swing.*;
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
        for (int i : array)
            if (i == 1)
                counter++;
        return counter;
    }

    // Only checks if all elements are distinct
    // because solve() guarantees that only the positive number up through square is entered
    public static boolean isValid(int[][] grid) {
        ArrayList<Integer> list;
        int length = grid.length;
        // Check rows
        for (int[] i : grid) {
            list = new ArrayList<Integer>();
            for (int j = 0; j < length; j++) {
                if (list.contains(i[j]))
                    return false;
                list.add(i[j]);
            }
        }
        // Check columns
        for (int j = 0; j < length; j++) {
            list = new ArrayList<Integer>();
            for (int[] aGrid : grid) {
                if (list.contains(aGrid[j]))
                    return false;
                list.add(aGrid[j]);
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

    public static int askDimSamurai(int square) {
        String message = "What is the dimension of this samurai sudoku?";
        int length, root;
        root = Math.round((long) Math.sqrt(square));
        do {
            length = Integer.parseInt(JOptionPane.showInputDialog(null, message));
        } while (((length - root) / (square - root)) % 2 != 1);
        return length;
    }

    public static int askSquare(String message) {
        int square, root;
        do {
            square = Integer.parseInt(JOptionPane.showInputDialog(null, message));
            root = Math.round((long) Math.sqrt(square));
        } while (square != root * root);
        return square;
    }

    public static int answer0or1(String message) {
        int a;
        do {
            a = Integer.parseInt(JOptionPane.showInputDialog(null, message));
        } while (a != 0 && a != 1);
        return a;
    }

    public static double askProportion() {
        double a;
        String message = "What proportion of clues do you want revealed?";
        message += "\n(Enter a value between 0 and 1.)";
        do {
            a = Double.parseDouble(JOptionPane.showInputDialog(null, message));
        } while (a < 0 || a > 1);
        return a;
    }

    public static String determineType(boolean isSamurai) {
        if (isSamurai) return "Samurai Sudoku";
        else return "Regular Sudoku";
    }
}