package server.entities;

import server.services.SudokuService;
import server.servicesImpls.SudokuServiceImpl;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

public class Game implements Serializable {
    private SudokuLevel level;
    private int emptyCells;
    private SudokuBoard board;
    private SudokuBoard solution;
    private int currentScore = 0;
    private boolean isWon;
    private Player player;
    private final Stack<GameTurn> undoStack = new Stack<>();
    private final Stack<GameTurn> redoStack = new Stack<>();

    private SudokuService solver;

    private class SudokuCellsValidator implements Serializable {
        private final int numberOfRowsCols = 9;
        private final int sqrtOfNumberOfRowsCols = (int) Math.sqrt(numberOfRowsCols);

        public int getNumberOfRowsCols() {
            return numberOfRowsCols;
        }

        public int getSqrtOfNumberOfRowsCols() {
            return sqrtOfNumberOfRowsCols;
        }

        public boolean unUsedInBox(SudokuBoard.SudokuGrid[][] grids, int cellValue) {
            int rowIndex = 0, colIndex = 0;
            int gridColIndex = 0;
            for (SudokuBoard.SudokuGrid[] rowOfGrids : grids) {
                for (SudokuBoard.SudokuGrid currentGrid : rowOfGrids) {
                    while (gridColIndex < currentGrid.getGrid()[gridColIndex].length) {
                        int currentValue = currentGrid.getGrid()[rowIndex][colIndex++];
                        if (currentValue == cellValue) {
                            return false;
                        }
                        gridColIndex++;
                    }
                    rowIndex++;
                }
            }
            return true;
        }

        public boolean unUsedInRow(int row, int cellValue) {
            for (int colIndex = 0; colIndex < numberOfRowsCols; colIndex++) {
                if (board.getCellValue(row, colIndex) == cellValue) {
                    return false;
                }
            }
            return true;
        }

        public boolean unUsedInCol(int col, int cellValue) {
            for (int rowIndex = 0; rowIndex < numberOfRowsCols; rowIndex++) {
                if (board.getCellValue(rowIndex, col) == cellValue) {
                    return false;
                }
            }
            return true;
        }

        public boolean CheckIfSafe(SudokuBoard.SudokuGrid[][] box, int rowI, int colI, int cellValue) {
            return validator.CheckIfSafe(box, rowI, colI, cellValue);
        }

        public boolean isValidRowCol(int[] row) {
            boolean isValid = false;
            if (row.length != numberOfRowsCols) {
                return false;
            }
            int[] possibleValues = new int[numberOfRowsCols];
            {
                int index = 1;
                while (index <= numberOfRowsCols) {
                    possibleValues[index - 1] = index;
                    index++;
                }
            }
            for (int rowIndex : row) {
                int possibleValueIndex = 0;
                while (possibleValueIndex < numberOfRowsCols) {
                    if (possibleValues[possibleValueIndex] != 0 && rowIndex
                            == possibleValues[possibleValueIndex]) {
                        possibleValues[possibleValueIndex] = 0;
                        isValid = true;
                    } else {
                        isValid = false;
                    }
                    possibleValueIndex++;
                }
            }
            return isValid;
        }

        public boolean isValidBox(int[][] box) {
            boolean isValid = false;
            if (box.length != numberOfRowsCols || box[0].length != numberOfRowsCols) {
                return false;
            }
            int[] possibleValues = new int[numberOfRowsCols];
            for (int index = 1; index <= numberOfRowsCols; index++) {
                possibleValues[index - 1] = index;
            }
            int possibleValuesIndex = 0;
            for (int[] row : box) {
                for (int cell : row) {
                    if (possibleValues[possibleValuesIndex] != 0 && cell == possibleValues[possibleValuesIndex]) {
                        possibleValues[possibleValuesIndex] = 0;
                        isValid = true;
                    } else {
                        isValid = false;
                    }
                }
            }
            return isValid;
        }
    }

    private final SudokuCellsValidator validator = new SudokuCellsValidator();

    private int[][] getCellsValues(SudokuBoard board) {
        int boardSize = board.getBoardSize() * board.getBoardSize();
        int[][] cellsValues = new int[boardSize][boardSize];

        for (int rowIndex = 0; rowIndex < boardSize; rowIndex++) {
            for (int colIndex = 0; colIndex < boardSize; colIndex++) {
                cellsValues[rowIndex][colIndex] = board.getCellValue(rowIndex, colIndex);
            }
        }

        return cellsValues;
    }

    private boolean validateSudokuSolution(int[][] solutionValues) {
        boolean isValidSolution = true;

        for (int[] currentRow : solutionValues) {
            if (isValidRowCol(currentRow)) {
                isValidSolution = false;
                break;
            }
            int[] currentColumn = new int[solutionValues.length];
            System.arraycopy(currentRow, 0, currentColumn, 0, currentRow.length);
            if (isValidRowCol(currentColumn)) {
                isValidSolution = false;
                break;
            }
        }

        int boxSize = (int) Math.sqrt(solutionValues.length);
        int boxesCount = solutionValues.length;
        int[][][] currentBox = new int[boxesCount][boxSize][boxSize];
        int boxIndex = 0;
        while (boxIndex < boxesCount) {
            for (int rowIndex = 0; rowIndex < solutionValues.length; rowIndex++) {
                for (int colIndex = 0; colIndex < solutionValues[rowIndex].length; colIndex++) {
                    currentBox[boxIndex][rowIndex / boxSize][colIndex / boxSize] =
                            board.getCellValue(rowIndex, colIndex);
                }
            }
            boxIndex++;
        }

        for (int[][] box : currentBox) {
            isValidSolution = isValidBox(box);
            if (!isValidSolution) {
                break;
            }
        }

        return isValidSolution;
    }

    public Game(SudokuLevel level, int emptyCells, SudokuBoard gameBoard, SudokuBoard solution,
                int currentScore, Player player) {
        setLevel(level);
        setEmptyCells(emptyCells);
        setGameBoard(gameBoard);
        setSolution(solution);

        setCurrentScore(currentScore);
        setWon();
        setPlayer(player);
    }

    public Game(SudokuLevel level, int emptyCells, SudokuBoard gameBoard, SudokuService solver, SudokuBoard solution,
                int currentScore, Player player) {
        this(level, emptyCells, gameBoard, solution, currentScore, player);
        setSolver(solver);
    }

    public Game() {
        /*SudokuService s;
        try {
            s = new SudokuServiceImpl();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }*/
        setLevel(SudokuLevel.EASY);
        setEmptyCells(SudokuLevel.EASY.getMaxEmptyCells());
        setGameBoard(new SudokuBoard());
        //setSolver(s);
        setSolution(new SudokuBoard());
        setCurrentScore(0);
        setPlayer(new Player());
        setWon();
    }

    public Game(Game game) {
        this(game.level, game.emptyCells, game.board, game.solver, game.solution, game.currentScore, game.player);
    }

    public SudokuLevel getLevel() {
        return level;
    }

    public int getEmptyCells() {
        return emptyCells;
    }

    public SudokuBoard getGameBoard() {
        return new SudokuBoard(board);
    }

    public SudokuBoard getSolution() {
        return new SudokuBoard(solution);
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public boolean isWon() {
        return isWon;
    }

    public Player getPlayer() {
        return new Player(player);
    }

    public server.entities.GameTurn[] getLastUndo() {
        GameTurn[] result = new GameTurn[2];
        result[0] = null;
        result[1] = null;
        if (undoStack.isEmpty()) {
            return result;
        }
        GameTurn currentTurn = undoStack.pop();
        if (undoStack.isEmpty()) {
            undoStack.push(currentTurn);
            return result;
        }
        GameTurn prevUndoTurn = undoStack.peek();
        redoStack.push(currentTurn);

        result[0] = currentTurn;
        result[1] = prevUndoTurn;

        return result;
    }

    public GameTurn getFirstRedo() {
        if (redoStack.isEmpty()) {
            return null;
        }

        GameTurn prevRedoTurn = redoStack.pop();
        undoStack.push(prevRedoTurn);

        return prevRedoTurn;
    }

    public Stack<GameTurn> getUndoStack() {
        return undoStack;
    }

    public int getRowsCols() {
        return validator.getNumberOfRowsCols();
    }

    public boolean isUnusedInRow(int row, int cellValue) {
        return validator.unUsedInRow(row, cellValue);
    }

    public boolean isUnusedInCol(int col, int cellValue) {
        return validator.unUsedInCol(col, cellValue);
    }

    public boolean isUnusedInBox(SudokuBoard.SudokuGrid[][] box, int value) {
        return validator.unUsedInBox(box, value);
    }

    public boolean isValidRowCol(int[] values) {
        return !validator.isValidRowCol(values);
    }

    public boolean isValidBox(int[][] values) {
        return validator.isValidBox(values);
    }

    public boolean isSafePosition(SudokuBoard.SudokuGrid[][] box, int rowI, int colI, int cellValue) {
        return validator.CheckIfSafe(box, rowI, colI, cellValue);
    }

    public int getSqrtOfRowsCols() {
        return validator.getSqrtOfNumberOfRowsCols();
    }

    public void clearRedoStack() {
        redoStack.clear();
    }

    public void setLevel(SudokuLevel level) {
        this.level = (level == SudokuLevel.EASY || level == SudokuLevel.MEDIUM || level == SudokuLevel.HARD)
                ? level : SudokuLevel.EASY;
    }

    public void setEmptyCells(int emptyCells) {
        this.emptyCells = (level.getMinEmptyCells() <= emptyCells && emptyCells <= level.getMaxEmptyCells())
                ? emptyCells : level.getMinEmptyCells();
    }

    public void setGameBoard(SudokuBoard gameBoard) {
        this.board = Objects.requireNonNullElseGet(board, SudokuBoard::new);
    }

    public void setSolution(SudokuBoard solution) {
        int[][] cellsValues = getCellsValues(solution);
        this.solution = (validateSudokuSolution(cellsValues)) ? solution : new SudokuBoard();
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore += currentScore;
    }

    public void setWon() {
        this.isWon = (board == solution);
    }

    public void setPlayer(Player player) {
        this.player = Objects.requireNonNullElseGet(player, Player::new);
    }

    public SudokuService getSolver() {
        return solver;
    }

    public void setSolver(SudokuService solver) {
        this.solver = solver;
    }

    public void addLastTurn(GameTurn lastTurn) {
        undoStack.push(lastTurn);
    }

    public void removeLastTurn() {
        GameTurn lastTurn = undoStack.pop();
        redoStack.push(lastTurn);
    }
}