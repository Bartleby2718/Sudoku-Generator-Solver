import java.util.ArrayList;
import java.util.Collections;

public class test {
    public static void main(String[] args) {
        int length = 9;
        int squareRoot = Math.round((long) Math.sqrt(length));
        int[][] matrix = new int[length][length];

        // 1. Fill with numbers from 1 to length
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                matrix[i][j] = ((i / squareRoot) + squareRoot * (i % squareRoot) + j) % length + 1;
        //print2DArray(matrix);


        // 2. Fill with numbers from 0 to length-1
        // Use the following ArrayList to make the sudoku look random
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) numbers.add(i);
        Collections.shuffle(numbers);
        System.out.println(numbers + "\n");
        // Update the matrix
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                matrix[i][j] = numbers.get(((i / squareRoot) + squareRoot * (i % squareRoot) + j) % length);
        // Print
        //print2DArray(matrix);


        // 3. Leave only numClues of them (Change all other cells to 0)
        int k = 0;
        int numCells = length * length;
        int numClues = (int) (numCells * 0.2);
        while (k < numCells - numClues) {
            int x = (int) Math.round((length - 1) * Math.random());
            int y = (int) Math.round((length - 1) * Math.random());
            if (matrix[x][y] != 0) {
                matrix[x][y] = 0;
                k++;
            }
        }
        print2DArray(matrix);
    }

    public static void print2DArray(int[][] matrix) {
        int length = matrix.length;
        int squareRoot = Math.round((long) Math.sqrt(length));
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                System.out.print(matrix[i][j] + " ");
                if ((j + 1) % squareRoot == 0)
                    System.out.print("  ");
            }
            System.out.println();
            if ((i + 1) % squareRoot == 0)
                System.out.println();
        }
    }
}
