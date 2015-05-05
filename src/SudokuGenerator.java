import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SudokuGenerator {
    public static void main(String[] args) throws IOException {
        Main.processUserInput();
    }

    public static int[][] generateRegular(int square, double clueProportion) throws IOException {
        int[][] matrix = new int[square][square];
        int root = Math.round((long) Math.sqrt(square));
        // 1. Fill with numbers from 1 to length
        for (int i = 0; i < square; i++)
            for (int j = 0; j < square; j++)
                matrix[i][j] = ((i / root) + root * (i % root) + j) % square + 1;
        // 2. Fill with numbers from 0 to length-1
        // Use the following ArrayList to make the sudoku look random
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < square; i++) numbers.add(i);
        Collections.shuffle(numbers);
        // Update the matrix
        // numbers: 0 ~ length-1
        // matrix: 1 ~ length
        // So +1 at the end
        for (int i = 0; i < square; i++)
            for (int j = 0; j < square; j++)
                matrix[i][j] = numbers.get(((i / root) + root * (i % root) + j) % square) + 1;
        // 3. Leave only numClues of them (Change all other cells to 0)
        int k = 0;
        int numCells = square * square;
        int numClues = (int) (square * square * clueProportion);
        while (k < numCells - numClues) {
            int x = (int) Math.round((square - 1) * Math.random());
            int y = (int) Math.round((square - 1) * Math.random());
            if (matrix[x][y] != 0) {
                matrix[x][y] = 0;
                k++;
            }
        }
        int percentage = (int) (clueProportion * 100);
        writeToTextFile(matrix, square, percentage, false);
        System.out.println("Check out \"" + square + "X" + square + "Regular" + percentage + "%.txt\"!");
        return matrix;
    }

    public static int[][] generateSamurai(int square, int length, double clueProportion) throws IOException {
        int root = Math.round((long) Math.sqrt(square));
        int[][] matrix = new int[length][length];
        // 1. Fill with numbers from 1 to length
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                matrix[i][j] = ((i / root) + root * (i % root) + j) % square + 1;
        // 2. Fill with numbers from 0 to length-1
        // Use the following ArrayList to make the sudoku look random
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < square; i++) numbers.add(i);
        Collections.shuffle(numbers);
        // 3. Update the matrix
        // numbers: 0 ~ length-1,   matrix: 1 ~ length
        // So +1 at the end
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                matrix[i][j] = numbers.get(((i / root) + root * (i % root) + j) % square) + 1;
        // 3. Use a helper array to identify black cells
        int[][] array = new int[length / root][length / root];
        int row, col;
        int a = (length - root) / root;
        int b = (square - root) / root;
        for (int i = 0; i < a; i += b)
            for (int j = 0; j < a; j += b)
                if ((i / b + j / b) % 2 == 0)
                    for (int m = 0; m < root; m++)
                        for (int n = 0; n < root; n++) {
                            row = i + m;
                            col = j + n;
                            // This is not black
                            array[row][col] = 1;
                            // 0 means black
                        }
        for (int i = 0; i < length; i += root)
            for (int j = 0; j < length; j += root)
                for (int m = 0; m < root; m++)
                    for (int n = 0; n < root; n++) {
                        row = i + m;
                        col = j + n;
                        if (array[row / root][col / root] == 0)
                            matrix[row][col] = -1;
                    }
        // 4. Leave only numClues of them (Change all other cells to 0)
        int numCells = 0;
        for (int i = 0; i < array.length; i++)
            numCells += Utils.countOnes(array[i]);
        numCells *= square;
        int numClues = (int) (numCells * clueProportion);
        int k = 0;
        while (k < numCells - numClues) {
            int x = (int) Math.round((length - 1) * Math.random());
            int y = (int) Math.round((length - 1) * Math.random());
            // Change to 0 only if it's not black and not 0
            if (matrix[x][y] > 0) {
                matrix[x][y] = 0;
                k++;
            }
        }
        int percentage = (int) (100 * clueProportion);
        writeToTextFile(matrix, square, percentage, true);
        System.out.println("Check out \"" + length + "X" + length + "Samurai" + percentage + "%.txt\"!");
        return matrix;
    }

    private static void print2DArray(int[][] matrix, int square) {
        int length = matrix.length;
        int root = Math.round((long) Math.sqrt(square));
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                System.out.print(matrix[i][j] + " ");
                if ((j + 1) % root == 0)
                    System.out.print("  ");
            }
            System.out.println();
            if ((i + 1) % root == 0)
                System.out.println();
        }
    }

    private static void writeToTextFile(int[][] matrix, int square, int clueProportion, boolean isSamurai) throws IOException {
        int length = matrix.length;
        int root = Math.round((long) Math.sqrt(square));
        File file = new File(length + "X" + length + Utils.determineType(isSamurai) + clueProportion + "%.txt");
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < length; i++) {
            String content = "";
            for (int j = 0; j < length; j++) {
                content += (matrix[i][j] + " ");
                if ((j + 1) % root == 0)
                    content += "  ";
            }
            bw.write(content + "\n");
            if ((i + 1) % root == 0)
                bw.write("\n");
        }
        bw.close();
    }
}