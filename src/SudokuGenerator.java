import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class SudokuGenerator {
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

	private static void print2DArray(int[][] matrix) {
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

    public static int[][] subSudoku(int i, int j, int square, int[][] grid) {
    	int[][] subSudoku = new int[square][square];
    	for (int x = 0; x < square; x++)
    		for (int y = 0; y < square; y++)
    			subSudoku[x][y] = grid[x + i][y + j];
    	return subSudoku;
    }
	
	public static int[][] generateSudoku(int square, double clueProportion) { // For regular Sudoku
		// TODO Auto-generated method stub
		int[][] matrix = new int[square][square];
		int squareRoot = (int)Math.sqrt(square);
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
		int numClues = (int)(square * square * clueProportion);
		while (k < numCells - numClues) {
			int x = (int) Math.round((square - 1) * Math.random());
			int y = (int) Math.round((square - 1) * Math.random());
			if (matrix[x][y] != 0) {
				matrix[x][y] = 0;
				k++;
			}
		}
		return matrix;
	}

	public static int[][] generateSamurai(int square, double clueProportion) {
		// TODO Auto-generated method stub
		int root = (int)Math.sqrt(square);
		int len = (int)(2 * square + root);
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
		int numCells = (int)Math.pow((2 * square + root), 2);
		int numClues = (int)(numCells * clueProportion);
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
}