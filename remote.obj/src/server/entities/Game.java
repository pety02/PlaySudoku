package server.entities;

import java.io.Serializable;
import java.util.Map;
import java.util.Stack;

/**
 * Клас, описващ играта като обект.
 */
public class Game implements Serializable {
    private SudokuLevel level;
    private int emptyCells;
    private int[][] board;
    private int[][] solution;
    private int currentScore;
    private boolean isWon;
    private Player player;

    private Stack<GameTurn> undoStack;

    private GameTurn lastTurn;

    /**
     * Вътрешен клас, който да валидира клетките на судокуто.
     */
    public class SudokuCellsValidator implements Serializable{
        private final int numberOfRowsCols = 9;
        private final int sqrtOfNumberOfRowsCols = (int) Math.sqrt(numberOfRowsCols);

        /**
         *
         * @return броят редове/колони на матрицата.
         */
        public int getNumberOfRowsCols() {
            return numberOfRowsCols;
        }

        /**
         *
         * @return корен квадратен от броя редове/колони на матрицата.
         */
        public int getSqrtOfNumberOfRowsCols() {
            return sqrtOfNumberOfRowsCols;
        }

        /**
         * Проверява дали дадена стойност не е използвана вече в определна кутя (част) от судоко пъзела.
         * @param rowsStart - стартов индекс за редовете.
         * @param colsStart - стартов индекс за колоните.
         * @param cellValue - стойност, която трябва да бъде проверена.
         * @return true - при вече използвана стойнсот, false - при вссе още не използвана стойнсот.
         */
        public boolean unUsedInBox(int rowsStart, int colsStart, int cellValue) {
            for (int rowsIndex = 0; rowsIndex < sqrtOfNumberOfRowsCols; ++rowsIndex) {
                for (int colsIndex = 0; colsIndex < sqrtOfNumberOfRowsCols; colsIndex++) {
                    if (board[rowsStart + rowsIndex][colsStart + colsIndex] == cellValue) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Проверява дали дадена стойност не е използвана вече в определен ред от судоко пъзела.
         * @param rowI - индекс на реда.
         * @param cellValue - стойност, която трябва да бъде проверена.
         * @return true - при вече използвана стойнсот, false - при вссе още не използвана стойнсот.
         */
        public boolean unUsedInRow(int rowI,int cellValue) {
            for (int j = 0; j< numberOfRowsCols; j++) {
                if (board[rowI][j] == cellValue) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Проверява дали дадена стойност не е използвана вече в определена колона от судоко пъзела.
         * @param colI - индек на колона.
         * @param cellValue - стойност, която трябва да бъде проверена.
         * @return true - при вече използвана стойнсот, false - при вссе още не използвана стойнсот.
         */
        public boolean unUsedInCol(int colI,int cellValue) {
            for (int i = 0; i< numberOfRowsCols; i++) {
                if (board[i][colI] == cellValue) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Запълва незапъленената част на судоко пъзела.
         * @param rowI - индекс на ред.
         * @param colI - индекс на колона.
         * @return true - при успешно запълване, false - при неуспешно запълване.
         */
        public boolean fillRemaining(int rowI, int colI) {
            if (colI >= numberOfRowsCols && rowI< numberOfRowsCols - 1) {
                rowI += 1;
                colI = 0;
            }
            if (rowI >= numberOfRowsCols && colI >= numberOfRowsCols) {
                return true;
            }
            if (rowI < sqrtOfNumberOfRowsCols) {
                if (colI < sqrtOfNumberOfRowsCols) {
                    colI = sqrtOfNumberOfRowsCols;
                }
            }
            else if (rowI < numberOfRowsCols - sqrtOfNumberOfRowsCols) {
                if (colI == (rowI / sqrtOfNumberOfRowsCols) * sqrtOfNumberOfRowsCols) {
                    colI += sqrtOfNumberOfRowsCols;
                }
            }
            else {
                if (colI == numberOfRowsCols - sqrtOfNumberOfRowsCols) {
                    rowI += 1;
                    colI = 0;
                    if (rowI >= numberOfRowsCols) {
                        return true;
                    }
                }
            }
            for (int cellValue = 1; cellValue <= numberOfRowsCols; ++cellValue) {
                if (CheckIfSafe(rowI, colI, cellValue)) {
                    board[rowI][colI] = cellValue;
                    if (fillRemaining(rowI, colI + 1)) {
                        return true;
                    }
                    board[rowI][colI] = 0;
                }
            }
            return false;
        }

        /**
         * Проверява дали е възможно поставянето на дадена стойнсот в клетка с определени координати.
         * @param rowI - индекс на ред.
         * @param colI - индекс на колона.
         * @param cellValue - стойнност.
         * @return true - при възможност, false - при невъзможност.
         */
        public boolean CheckIfSafe(int rowI,int colI,int cellValue) {
            return (validator.unUsedInRow(rowI, cellValue) &&
                    validator.unUsedInCol(colI, cellValue) &&
                    validator.unUsedInBox(rowI - rowI % sqrtOfNumberOfRowsCols,
                            colI - colI % sqrtOfNumberOfRowsCols, cellValue));
        }
    }

    private final SudokuCellsValidator validator = new SudokuCellsValidator();

    /**
     * Конструктор с параметри.
     * @param level - ниво на трудност.
     * @param emptyCells - брой празни клетки.
     * @param board - дъска.
     * @param solution - решение.
     * @param currentScore - текущ резултат.
     * @param player - играч.
     */
    public Game( SudokuLevel level, int emptyCells, int[][] board, int[][] solution,
                 int currentScore, Player player) {
        setLevel(level);
        setEmptyCells(emptyCells);
        setBoard(board);
        setSolution(solution);

        setCurrentScore(currentScore);
        setWon();
        setPlayer(player);
        undoStack = new Stack<>();
    }

    /**
     * Конструктор без параметри.
     */
    public Game() {
        setLevel(SudokuLevel.EASY);
        setEmptyCells(level.getMaxEmptyCells());
        setBoard(new int[9][9]);
        setSolution(new int[9][9]);

        setCurrentScore(0);
        setWon();
        setPlayer(new Player());
        undoStack = new Stack<>();
    }

    /**
     * Гетър за ниво на трудност.
     * @return ниво на трудност.
     */
    public SudokuLevel getLevel() {
        return level;
    }

    /**
     * Гетър за брой празни клетки.
     * @return брой празни клетки.
     */
    public int getEmptyCells() { return emptyCells; }

    /**
     * Гетър за игрална дъска.
     * @return игрална дъска.
     */
    public int[][] getBoard() {
        return board;
    }

    /**
     * Гетър за решение на судоку пъзела.
     * @return решение на судоку пъзела.
     */
    public int[][] getSolution() {
        return solution;
    }

    /**
     * Гетър за текущ резултат.
     * @return текущ резултат.
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Гетър за състояние (победа/загуба).
     * @return състояние.
     */
    public boolean isWon() {
        return isWon;
    }

    /**
     * Гетър за играч.
     * @return играч.
     */
    public Player getPlayer() {
        return player;
    }

    public Stack<GameTurn> getUndoStack() {
        return undoStack;
    }

    public GameTurn getLastTurn() {
        return lastTurn;
    }

    /**
     * Гетър за валидатор на клетките на судокуто.
     * @return валидатор.
     */
    public SudokuCellsValidator getValidator() {
        return validator;
    }

    /**
     * Сетър за ниво на трудност.
     * @param level - ниво на трудност.
     */
    public void setLevel(SudokuLevel level) {
        this.level = (level == SudokuLevel.EASY || level == SudokuLevel.MEDIUM || level == SudokuLevel.HARD)
                ? level : SudokuLevel.EASY;
    }

    /**
     * Сетър за брой празни клетки.
     * @param emptyCells - брой ппразни клетки.
     */
    public void setEmptyCells(int emptyCells) {
        this.emptyCells = (level.getMinEmptyCells() <= emptyCells && emptyCells <= level.getMaxEmptyCells())
                ? emptyCells : level.getMinEmptyCells();
    }

    /**
     * Сетър за дъска.
     * @param board - игрална дъска.
     */
    public void setBoard(int[][] board) {
        this.board = (board != null) ? board : new int [9][9];
    }

    /**
     * Сетър за решение.
     * @param solution - решение на судоку пъзела.
     */
    public void setSolution(int[][] solution) { this.solution = (solution != null) ? solution : new int[9][9]; }

    /**
     * Сетър за текущ резултат.
     * @param currentScore - текущ резултат.
     */
    public void setCurrentScore(int currentScore) {
        this.currentScore = Math.max(0, currentScore); }

    /**
     * Сетър за състояние (победа/загуба).
     */
    public void setWon() {
        this.isWon = (board == solution);
    }

    /**
     * Сетър за играч.
     * @param player - играч.
     */
    public void setPlayer(Player player) {
        this.player = (player != null ) ? player : new Player();
    }

    public void setLastTurn(GameTurn lastTurn) {
        this.lastTurn = lastTurn;
    }

    /**
     * Принтира дъската на судокуто.
     */
    public void printSudokuBoard() {
        for (int i = 0; i<validator.numberOfRowsCols; i++) {
            for (int j = 0; j<validator.numberOfRowsCols; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}