public class MarkupGrid {
    private int[][][] markupGrid;

    // Initializes with 1 everywhere
    public MarkupGrid(int length) {
        this.markupGrid = new int[9][9][9];
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                for (int k = 0; k < length; k++)
                    markupGrid[i][j][k] = 1;
    }

    // Creates a deep copy
    public MarkupGrid(MarkupGrid another){
        int length = another.getMarkupGrid().length;
        this.markupGrid = new int[9][9][9];
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                for (int k = 0; k < length; k++)
                    markupGrid[i][j][k] = another.getMarkupGrid()[i][j][k];
    }

    // Getter
    public int[][][] getMarkupGrid() {
        return markupGrid;
    }

    // Setter (You would clone one if you have to copy the entire markupGrid)
    public void modifyMarkupGrid(int row, int col, int index, int val) {
        markupGrid[row][col][index] = val;
    }

    // Update the markupGrid because a new entry("num" at position (row, col)) is now known
    public void update(int row, int col, int num){
        int length = markupGrid.length;
        int squareRoot = Math.round((long) Math.sqrt(length));
        int rowSquare = (row / squareRoot) * squareRoot;
        int colSquare = (col / squareRoot) * squareRoot;
        int index = num - 1;

        for (int k = 0; k < length; k++) {
            // 1) No other values can go into that cell, so markup for that cell is now an array of zeros
            markupGrid[row][col][k] = 0;
            // 2) num cannot appear again in the same row
            markupGrid[row][k][index] = 0;
            // 3) num cannot appear again in the same column
            markupGrid[k][col][index] = 0;
        }
        // 4) num cannot appear again in the same square
        for (int m = 0; m < squareRoot; ++m)
            for (int n = 0; n < squareRoot; ++n)
                markupGrid[rowSquare + m][colSquare + n][index] = 0;
    }

    public static void main(String[] args) {
        MarkupGrid thing = new MarkupGrid(9);
        thing.modifyMarkupGrid(0, 0, 0, 1);
        MarkupGrid another = new MarkupGrid(thing);
        System.out.println(another.getMarkupGrid()[0][0][0]);
        another.modifyMarkupGrid(0, 0, 0, 0);
        System.out.println(thing.getMarkupGrid()[0][0][0]);
        System.out.println(another.getMarkupGrid()[0][0][0]);
    }
}
