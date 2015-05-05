import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class SudokuSolver {
    public static int[][] solve(int[][] grid, boolean isSamurai, int square) {
        // Declaration/Initialization of local variables
        int num, row, col, counter, min;
        int index = grid.length; // for debugging purposes
        int length = grid.length;
        int root = Math.round((long) Math.sqrt(square));
        MarkupGrid markupGrid = new MarkupGrid(length, square);

        // Identify the top left positions
        int b = square - root;
        for (int i = 0; i < length - root; i += b)
            for (int j = 0; j < length - root; j += b)
                if ((i / b + j / b) % 2 == 0)
                    // For each sub-sudoku
                    for (int m = 0; m < square; m++) {
                        for (int n = 0; n < square; n++) {
                            row = i + m;
                            col = j + n;
                            // Update markups if an entry is given
                            // Skip if the cell is black or not blank
                            if (grid[row][col] <= 0) continue;
                            // Now that the entry is known(nonzero)...
                            num = grid[row][col];
                            index = num - 1;
                            // Update markupGrid
                            if (isSamurai) markupGrid.updateSamurai(row, col, num);
                            else markupGrid.updateSubsudoku(0, 0, row, col, num);
                        }
                    }
        // Declare and initialize stacks
        Stack<int[][]> possibilities = new Stack<int[][]>();
        Stack<MarkupGrid> listOfMarkupGrids = new Stack<MarkupGrid>();
        possibilities.push(grid);
        listOfMarkupGrids.push(markupGrid);

        // Repeat until the stack is empty
        while (!possibilities.isEmpty()) {
            grid = possibilities.pop();
            markupGrid = listOfMarkupGrids.pop();
            boolean full = false; // full being true means "There is no blank cell"
            boolean valid = true; // valid being true means "There is no blank cell with 0 candidate."
            boolean done = false; // done being true means "There is no blank cell with 1 candidate."
            while (valid && !done && !full) { // Repeat until the grid is full or there is no blank cell with 0/1 candidate
                full = true; // Full unless we find a blank cell
                valid = true; // Valid unless we find a blank cell with 0 candidate
                done = true; // Done unless we find a blank cell with 1 candidate
                // Iterate over all cells
                // TODO If Samurai, iterate over only non-black cells?
                loops:
                for (int i = 0; i < length; i++) {
                    for (int j = 0; j < length; j++) {
                        // Pass if the entry is known or the entry is black
                        if (grid[i][j] != 0) continue;
                        full = false; // Some entry is not known yet!
                        // Count the number of candidates for that cell
                        counter = 0;
                        for (int k = 0; k < square; k++)
                            if (markupGrid.getMarkupGrid()[i][j][k] == 1) {
                                counter++;
                                index = k;
                            }
                        // 0 candidate
                        if (counter == 0) { // Something must be wrong
                            valid = false;
                            break loops;
                            // 1 candidate
                        } else if (counter == 1) {
                            // Put in the only candidate
                            num = index + 1; // because num is the entry
                            grid[i][j] = num;
                            if (isSamurai) markupGrid.updateSamurai(i, j, num);
                            else markupGrid.updateSubsudoku(0, 0, i, j, num);
                            done = false;
                            break loops;
                        }
                    }
                }
            }
            // Move on to the next one if invalid
            if (!valid) continue;
            // Check the validity of the solution (Note: The line above guarantees that valid=true right now)
            if (full) {
                if (isSamurai) {
                    loop:
                    for (int i = 0; i < length - root; i += b)
                        for (int j = 0; j < length - root; j += b)
                            if ((i / b + j / b) % 2 == 0)
                                if (!Utils.isValid(Utils.copyGrid(grid, i, j, square))) {
                                    valid = false;
                                    break loop;
                                }
                } else valid = Utils.isValid(grid);
                if (valid) return grid;
                else continue;
            }
            // Done filling out trivial cells and valid so far!
            // Find the cell with the fewest candidates (linear search)
            min = length;
            row = -1; // for debugging purposes
            col = -1; // for debugging purposes
            outerLoop:
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    // Skip blank cells and black cells
                    if (grid[i][j] != 0) continue;
                    counter = Utils.countOnes(markupGrid.getMarkupGrid()[i][j]);
                    // For debugging purposes (Note: The previous while loop guarantees that counter>=2)
                    if (counter < 2)
                        System.out.println("Invalid at " + i + ", " + j);
                    else if (counter < min) {
                        row = i;
                        col = j;
                        // Exit when a cell with two candidates is found
                        if (counter == 2) break outerLoop;
                        min = counter;
                    }
                }
            }
            // Try all candidates
            ArrayList<Integer> listOfCandidates = Utils.markupToArrayList(markupGrid.getMarkupGrid()[row][col]);
            for (int k : listOfCandidates) {
                // Update grid and push it to the stack
                int[][] newGrid = new int[length][length];
                for (int i = 0; i < length; i++)
                    System.arraycopy(grid[i], 0, newGrid[i], 0, length);
                newGrid[row][col] = k;
                possibilities.push(newGrid);
                // Update markupGrid and push it to the stack
                MarkupGrid newMarkupGrid = new MarkupGrid(markupGrid);
                if (isSamurai) newMarkupGrid.updateSamurai(row, col, k);
                else newMarkupGrid.updateSubsudoku(0, 0, row, col, k);
                listOfMarkupGrids.push(newMarkupGrid);
            }
        }
        // The stack is empty if you get here
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner keyboard = new Scanner(System.in);
        String fileName;
        do {
            System.out.print("Enter a file name: ");
            fileName = keyboard.next();
        } while (!new File(fileName).exists());
        keyboard.close();

        System.out.println("Processing the input file...");
        int length = 0;

        // First run: get length
        Scanner fileScanner = new Scanner(new File(fileName));
        while (fileScanner.hasNextLine())
            if (fileScanner.nextLine().trim().length() != 0)
                length++;
        fileScanner.close();

        // Second run: get square and isSamurai
        boolean isSamurai = false;
        int square = length;
        int root, val;
        fileScanner = new Scanner(new File(fileName));
        int[][] grid = new int[length][length];
        int i = 0;
        while (fileScanner.hasNextLine()) {
            // Trim leading/trailing whitespace
            String line = fileScanner.nextLine().trim();
            // Skip blank lines
            if (line.length() == 0) continue;
            // Each square is delimited by three spaces
            String[] data = line.split("   ");
            for (int j = 0; j < data.length; j++) {
                // Each entry in a square is delimited by a single space
                String[] data2 = data[j].split(" ");
                // This is the square root of the number of cells in a square
                root = data2.length;
                for (int k = 0; k < root; k++) {
                    // "b" indicates a black cell
                    if (data2[k].equals("b")) {
                        // It must be a Samurai Sudoku
                        isSamurai = true;
                        // Fill in -1 if black
                        grid[i][root * j + k] = -1;
                    } else {
                        val = Integer.parseInt(data2[k]);
                        square = root * root;
                        // Guarantees that all numbers in the input file are between 1 and square inclusive (except for "b")
                        if (val >= 0 && val <= square)
                            grid[i][root * j + k] = val;
                        else
                            throw new IllegalArgumentException("Some entry is invalid in the input file.");
                    }
                }
            }
            i++;
        }
        fileScanner.close();

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
}