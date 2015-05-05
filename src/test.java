import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        int square, squareRoot, numClues;
        double ratio;
        do {
            System.out.print("Enter a perfect square: ");
            square = keyboard.nextInt();
            squareRoot = Math.round((long) Math.sqrt(square));
            if (square == squareRoot * squareRoot)
                break;
        } while (true);
        do {
            System.out.print("How many clues do you want?\nEnter a ratio between 0 and 1: ");
            ratio = keyboard.nextDouble();
            if (ratio >= 0 && ratio <= 1)
                break;
        } while (true);
        numClues = (int) (square * square * ratio);
        System.out.println("Created a random " + square + " by " + square + " matrix with " + numClues + " clues.");

        int[][] matrix = new int[square][square];

        // 1. Fill with numbers from 1 to length
        for (int i = 0; i < square; i++)
            for (int j = 0; j < square; j++)
                matrix[i][j] = ((i / squareRoot) + squareRoot * (i % squareRoot) + j) % square + 1;
        //print2DArray(matrix);


        // 2. Fill with numbers from 0 to length-1
        // Use the following ArrayList to make the sudoku look random
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < square; i++) numbers.add(i);
        Collections.shuffle(numbers);
        //System.out.println(numbers + "\n");

        // Update the matrix
        // numbers: 0 ~ length-1
        // matrix: 1 ~ length
        // So +1 at the end
        for (int i = 0; i < square; i++)
            for (int j = 0; j < square; j++)
                matrix[i][j] = numbers.get(((i / squareRoot) + squareRoot * (i % squareRoot) + j) % square) + 1;
        // Print
        //print2DArray(matrix);


        // 3. Leave only numClues of them (Change all other cells to 0)
        int k = 0;
        int numCells = square * square;
        while (k < numCells - numClues) {
            int x = (int) Math.round((square - 1) * Math.random());
            int y = (int) Math.round((square - 1) * Math.random());
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