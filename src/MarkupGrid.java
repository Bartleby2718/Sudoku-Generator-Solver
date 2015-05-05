public class MarkupGrid {
    private int[][][] markupGrid;
    private final int length;
    private final int square;
    private final int root;

    // Initializes with 1 everywhere
    public MarkupGrid(int length, int square) {
        markupGrid = new int[length][length][square];
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                for (int k = 0; k < square; k++)
                    markupGrid[i][j][k] = 1;
        this.length = length;
        this.square = square;
        root = Math.round((long) Math.sqrt(square));
    }

    // Creates a deep copy
    public MarkupGrid(MarkupGrid another) {
        length = another.getMarkupGrid().length;
        square = another.getSquare();
        markupGrid = new int[length][length][square];
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                System.arraycopy(another.getMarkupGrid()[i][j], 0, markupGrid[i][j], 0, square);
        root = Math.round((long) Math.sqrt(square));
    }

    // Getter
    public int[][][] getMarkupGrid() {
        return markupGrid;
    }

    private int getSquare() {
        return square;
    }

    public void updateSamurai(int row, int col, int num) {
        int b = square - root;
        // TODO Identify i and j instead of iterating over all cells
        for (int i = 0; i < length - root; i += square - root)
            for (int j = 0; j < length - root; j += square - root)
                if ((i / b + j / b) % 2 == 0 && row >= i && row < i + square && col >= j && col < j + square)
                    updateSubsudoku(i, j, row - i, col - j, num);
    }

    // Update the markupGrid because a new entry("num" at position (row, col)) is now known
    public void updateSubsudoku(int rowStart, int colStart, int subRow, int subCol, int num) {
        int index = num - 1;
        for (int k = 0; k < square; k++) {
            // 1) No other values can go into that cell, so markup for that cell is now an array of zeros
            markupGrid[rowStart + subRow][colStart + subCol][k] = 0;
            // 2) num cannot appear again in the same row
            markupGrid[rowStart + subRow][colStart + k][index] = 0;
            // 3) num cannot appear again in the same column
            markupGrid[rowStart + k][colStart + subCol][index] = 0;
        }
        // 4) num cannot appear again in the same square
        int rowSquare = (subRow / root) * root;
        int colSquare = (subCol / root) * root;
        for (int m = 0; m < root; m++)
            for (int n = 0; n < root; n++)
                markupGrid[rowStart + rowSquare + m][colStart + colSquare + n][index] = 0;
    }
}