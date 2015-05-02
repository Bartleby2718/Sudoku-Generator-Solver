import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

//TODO: Consider edge cases?
/*TODO: Minor speedups
1) Manual copy of array -> System.arraycopy()
2) Math.sqrt() -> for loop
*/
public class SudokuSolver {
    public static int[][] solve(int[][] grid) {
        // Declaration/Initialization of local variables
        int num, index, squareRoot, row, col, counter, min, rowSquare, colSquare;
        index = grid.length; // for debugging purposes
        int length = grid.length;
        squareRoot = Math.round((long) Math.sqrt(length));
        int[][][] markupGrid = new int[length][length][length];

        // Initialize markupGrid with 1 everywhere
        for (int i = 0; i < length; ++i)
            for (int j = 0; j < length; ++j)
                for (int k = 0; k < length; ++k)
                    markupGrid[i][j][k] = 1;

        // For every entry given, update markups accordingly
        for (int i = 0; i < length; ++i)
            for (int j = 0; j < length; ++j) {
                // Skip if the entry is not known
                if (grid[i][j] == 0) continue;
                // Now that the entry is known(nonzero)...
                // Get the number
                num = grid[i][j];
                // Get the corresponding index
                index = num - 1;
                // INVALID IF THIS NUMBER IS NOT IN THE MARKUP
                if (markupGrid[i][j][index] == 0) return new int[length][length];
                // UPDATE MARKUPS
                for (int k = 0; k < length; ++k) {
                    // 1) No other values can go into that cell, so markup for that cell is now an array of zeros
                    markupGrid[i][j][k] = 0;
                    // 2) num cannot appear again in the same row
                    markupGrid[i][k][index] = 0;
                    // 3) num cannot appear again in the same column
                    markupGrid[k][j][index] = 0;
                }
                // 4) num cannot appear again in the same square
                for (int m = 0; m < squareRoot; ++m)
                    for (int n = 0; n < squareRoot; ++n) {
                        rowSquare = (i / squareRoot) * squareRoot;
                        colSquare = (j / squareRoot) * squareRoot;
                        markupGrid[rowSquare + m][colSquare + n][index] = 0;
                    }
            }

        // Declare and initialize stacks
        Stack<int[][]> possibilities = new Stack<int[][]>();
        Stack<int[][][]> listOfMarkupGrids = new Stack<int[][][]>();
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
                for (int i = 0; i < length; ++i) {
                    for (int j = 0; j < length; ++j) {
                        // Pass if the entry is known
                        if (grid[i][j] != 0) continue;
                        full = false; // Some entry is not known yet!
                        // Count the number of candidates for that cell
                        counter = 0;
                        for (int k = 0; k < length; ++k)
                            if (markupGrid[i][j][k] == 1) {
                                counter++;
                                index = k;
                            }
                        // 0 candidate
                        if (counter == 0) { // Something must be wrong
                            valid = false;
                            break;
                            // 1 candidate
                        } else if (counter == 1) {
                            // Put in the only candidate
                            num = index + 1; // because num is the entry
                            grid[i][j] = num;
                            // UPDATE MARKUPS
                            // 1) The last 1 in the markup is now 0
                            markupGrid[i][j][index] = 0;
                            for (int k = 0; k < length; ++k) {
                                // 2) The number cannot appear again in the same row
                                markupGrid[i][k][index] = 0;
                                // 3) The number cannot appear again in the same column
                                markupGrid[k][j][index] = 0;
                            }
                            // 4) The number cannot appear again in the same square
                            for (int m = 0; m < squareRoot; ++m)
                                for (int n = 0; n < squareRoot; ++n) {
                                    rowSquare = (i / squareRoot) * squareRoot;
                                    colSquare = (j / squareRoot) * squareRoot;
                                    markupGrid[rowSquare + m][colSquare + n][index] = 0;
                                }
                            done = false;
                            break;
                        }
                    }
                }
            }
            if (full) return grid; // Done if full // TODO MAY NEED TO CHECK VALIDITY
            if (!valid) continue; // Move on to the next one if invalid
            // Done filling out trivial cells and valid so far!
            // Find the cell with the fewest candidates (linear search)
            min = length;
            row = -1; // for debugging purposes
            col = -1; // for debugging purposes
            outerLoop:
            for (int i = 0; i < length; ++i) {
                for (int j = 0; j < length; ++j) {
                    counter = 0;
                    // Skip blank cells
                    if (grid[i][j] != 0) continue;
                    for (int k = 0; k < length; ++k)
                        if (markupGrid[i][j][k] == 1) counter++;
                    if (counter > 0 && counter < min) {
                        min = counter;
                        row = i;
                        col = j;
                        // Exit when a cell with two candidates is found
                        if (counter == 2) break outerLoop;
                    }
                }
            }
            // Try all candidates
            ArrayList<Integer> listOfCandidates = SudokuSolver.markupToArrayList(markupGrid[row][col]);
            for (int k : listOfCandidates) {
                // Update grid and push it to the stack
                int[][] newGrid = new int[length][length];
                for (int i = 0; i < length; ++i)
                    for (int j = 0; j < length; ++j)
                        newGrid[i][j] = grid[i][j];
                newGrid[row][col] = k;
                possibilities.push(newGrid);
                // Update markupGrid and push it to the stack
                int[][][] newMarkupGrid = updateMarkupGrid(markupGrid, row, col, k);
                listOfMarkupGrids.push(newMarkupGrid);
            }
        }
        // The stack is empty if you get here
        return new int[length][length];
    }

    public static int[][][] updateMarkupGrid(int[][][] markupGrid, int row, int col, int num) {
        int length = markupGrid.length;
        int squareRoot = Math.round((long) Math.sqrt(length));
        int rowSquare = (row / squareRoot) * squareRoot;
        int colSquare = (col / squareRoot) * squareRoot;
        // Get the corresponding index
        int index = num - 1;
        // Make a copy
        int[][][] newMarkupGrid = new int[length][length][length];
        for (int i = 0; i < length; ++i)
            for (int j = 0; j < length; ++j)
                for (int k = 0; k < length; ++k)
                    newMarkupGrid[i][j][k] = markupGrid[i][j][k];
        // Start updating
        for (int k = 0; k < length; ++k) {
            // 1) No other values can go into that cell, so markup for that cell is now an array of zeros
            newMarkupGrid[row][col][k] = 0;
            // 2) num cannot appear again in the same row
            newMarkupGrid[row][k][index] = 0;
            // 3) num cannot appear again in the same column
            newMarkupGrid[k][col][index] = 0;
        }
        // 4) num cannot appear again in the same square
        for (int m = 0; m < squareRoot; ++m)
            for (int n = 0; n < squareRoot; ++n)
                newMarkupGrid[rowSquare + m][colSquare + n][index] = 0;
        return newMarkupGrid;
    }

    public static ArrayList<Integer> markupToArrayList(int[] markup) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < markup.length; i++)
            if (markup[i] == 1)
                list.add(i + 1); // i is an index; we want the entries
        return list;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner keyboard = new Scanner(System.in);
        int square, squareRoot;
        do {
            System.out.print("Enter a perfect square: ");
            square = keyboard.nextInt();
            squareRoot = Math.round((long) Math.sqrt(square));
            if (square == squareRoot * squareRoot)
                break;
        } while (true);
        String fileName = "";
        do {
            System.out.print("Enter a file name: ");
            fileName = keyboard.next();
        } while (!new File(fileName).exists());
        System.out.println("Now look at the pop-up!");
        keyboard.close();
        int[][] grid = new int[square][square];
        Scanner fileScanner = new Scanner(new File(fileName));
        int i = 0;
        while (fileScanner.hasNextLine()) {
            // Trim leading/trailing whitespace
            String line = fileScanner.nextLine().trim();
            if (line.length() == 0)
                continue;
            String[] data = line.split("   ");
            for (int j = 0; j < squareRoot; j++) {
                String[] data2 = data[j].split(" ");
                for (int k = 0; k < squareRoot; k++)
                    grid[i][squareRoot * j + k] = Integer.parseInt(data2[k]);
            }
            i++;
        }
        fileScanner.close();
        JFrame frame = new JFrame("Sudoku Solver");
        frame.setSize(500, 500);
        frame.setLocation(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new Panel(grid));
        frame.setVisible(true);
    }
}
                /* See if there is only one cell whose markup has a particular number
                row = -1;
                col = -1;
                for (num = 1; num <= length; num++) {
                    index = num - 1;
                    // Check horizontals
                    for (int i = 0; i < length; ++i) {
                        counter = 0;
                        for (int j = 0; j < length; ++j) {
                            if (markupGrid[i][j][index] == 1) {
                                counter++;
                                row = i;
                                col = j;
                            }
                        }
                        if (counter == 1) {
                            grid[row][col] = num;
                            markupGrid = updateMarkupGrid(markupGrid, row, col, num);
                        }
                        row = -1;
                        col = -1;
                    }
                    // Check verticals
                    for (int j = 0; j < length; ++j) {
                        counter = 0;
                        for (int i = 0; i < length; ++i) {
                            if (markupGrid[i][j][index] == 1) {
                                counter++;
                                row = i;
                                col = j;
                            }
                        }
                        if (counter == 1) {
                            grid[row][col] = num;
                            markupGrid = updateMarkupGrid(markupGrid, row, col, num);
                        }
                        row = -1;
                        col = -1;
                    }
                    // Check squares
                    for (int i = 0; i < length; i += squareRoot)
                        for (int j = 0; j < length; j += squareRoot) {
                            counter = 0;
                            for (int m = 0; m < squareRoot; ++m) {
                                for (int n = 0; n < squareRoot; ++n) {
                                    if (markupGrid[i][j][index] == 1) {
                                        counter++;
                                        row = i;
                                        col = j;
                                    }
                                }
                            }
                            if (counter == 1) {
                                grid[row][col] = num;
                                markupGrid = updateMarkupGrid(markupGrid, row, col, num);
                            }
                            row = -1;
                            col = -1;
                        }
                }*/
