import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SudokuGenerator {
    public static void main(String[] args) throws IOException {
        int type, square, length;
        int[][] grid;
        double clueProportion;
        boolean isSamurai;
        type = Utils.answer0or1("Enter 0 if you want a regular sudoku.\nEnter 1 if you want a samurai sudoku.");
        clueProportion = Utils.answerWithDouble("What proportion of clues do you want revealed?");
        if (type == 0) { // regular sudoku
            isSamurai = false;
            square = Utils.getSquare("What is the length of the regular sudoku?\n(It must be a perfect square.)");
            grid = SudokuGenerator.generateSudoku(square, clueProportion);
        } else {// Samurai Sudoku
            isSamurai = true;
            square = Utils.getSquare("A samurai sudoku is composed of several squares.\nHow many cells are there in each square?");
            length = Utils.getLength("What is the length of this samurai sudoku?", square);
            grid = SudokuGenerator.samuraiSudokuGenerator(square, length, clueProportion);
        }
        JFrame frame;
        if (isSamurai) frame = new JFrame("Samurai Sudoku Solver");
        else frame = new JFrame("Regular Sudoku Solver");
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new Panel(grid, isSamurai, square));
        frame.setVisible(true);
        System.out.println("Look at the pop-up!");
    }

    private static void writeToTextFile(int[][] matrix, int square, int ratio) throws IOException {
        int length = matrix.length;
        int root = Math.round((long) Math.sqrt(square));
        File file = new File(length + "X" + length + "_Samurai_" + ratio + "%.txt");
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

    public static int[][] subSudoku(int i, int j, int square, int[][] grid) {
        int[][] subSudoku = new int[square][square];
        for (int x = 0; x < square; x++)
            for (int y = 0; y < square; y++)
                subSudoku[x][y] = grid[x + i][y + j];
        return subSudoku;
    }

    public static int[][] generateSudoku(int square, double clueProportion) throws IOException { // For regular Sudoku
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
        int ratioInInt = (int) (clueProportion * 100);
        writeToTextFile(matrix, square, ratioInInt);
        System.out.println("Check out \"" + square + "X" + square + "_Samurai_" + ratioInInt + "%.txt\"!");
        return matrix;
    }

    public static int[][] generateSamurai(int square, double clueProportion) throws IOException {
        int root = Math.round((long) Math.sqrt(square));
        int len = 2 * square + root;
        int[][] matrix = new int[len][len];
        for (int i = 0; i < len; i++)
            for (int j = 0; j < len; j++)
                if ((i >= square && i < square + root && (j < square - root || j >= 2 * square - root)) ||
                        (j >= square && j < square + root && (i < square - root || i >= 2 * square - root)))
                    matrix[i][j] = -1;
        int[][] grid1 = generateSudoku(square, 1);
        for (int i = 0; i < square; i++)
            for (int j = 0; j < square; j++)
                matrix[i][j] = grid1[i][j];
        int[][] grid3 = subSudoku(square - root, square - root, square, matrix);
        grid3 = SudokuSolver.solve(grid3, false, square);
        for (int i = 0; i < square; i++)
            for (int j = 0; j < square; j++)
                matrix[i + square - root][j + square - root] = grid3[i][j];
        int[][] grid2 = subSudoku(0, square + root, square, matrix);
        grid2 = SudokuSolver.solve(grid2, false, square);
        for (int i = 0; i < square; i++)
            for (int j = 0; j < square; j++)
                matrix[i][j + square + root] = grid2[i][j];
        int[][] grid4 = subSudoku(square + root, 0, square, matrix);
        grid4 = SudokuSolver.solve(grid4, false, square);
        for (int i = 0; i < square; i++)
            for (int j = 0; j < square; j++)
                matrix[i + square + root][j] = grid4[i][j];
        int[][] grid5 = subSudoku(square + root, square + root, square, matrix);
        grid5 = SudokuSolver.solve(grid5, false, square);
        for (int i = 0; i < square; i++)
            for (int j = 0; j < square; j++)
                matrix[i + square + root][j + square + root] = grid5[i][j];
        int k = 0;
        int numCells = (int) Math.pow((2 * square + root), 2);
        int numClues = (int) (numCells * clueProportion);
        while (k < numCells - numClues) {
            int x, y;
            do {
                x = (int) Math.round((2 * square + root - 1) * Math.random());
                y = (int) Math.round((2 * square + root - 1) * Math.random());
            } while ((x >= square && x < square + root && (y < square - root || y >= 2 * square - root)) ||
                    (y >= square && y < square + root && (x < square - root || x >= 2 * square - root)));
            if (matrix[x][y] > 0) {
                matrix[x][y] = 0;
                k++;
            }
        }
        return matrix;
    }

    public static int[][] samuraiSudokuGenerator(int square, int length, double ratio) throws IOException {
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
        int numClues = (int) (numCells * ratio);
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
        int ratioInInt = (int) (100 * ratio);
        writeToTextFile(matrix, square, ratioInInt);
        System.out.println("Check out \"" + length + "X" + length + "_Samurai_" + ratioInInt + "%.txt\"!");
        return matrix;
    }
}