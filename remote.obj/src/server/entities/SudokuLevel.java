package server.entities;

public enum SudokuLevel {
    EASY(0, 20),
    MEDIUM(0, 40),
    HARD(0, 60);

    private final int minEmptyCells;
    private final int maxEmptyCells;

    public int getMinEmptyCells() {
        return minEmptyCells;
    }

    public int getMaxEmptyCells() {
        return maxEmptyCells;
    }

    SudokuLevel(int minEmptyCells, int maxEmptyCells) {
        this.minEmptyCells = minEmptyCells;
        this.maxEmptyCells = maxEmptyCells;
    }
}