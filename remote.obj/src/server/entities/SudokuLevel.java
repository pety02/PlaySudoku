package server.entities;

/**
 * Изброим тип за нивата на трудност на играта - EASY, MEDIUM, HARD
 */
public enum SudokuLevel {
    EASY(0, 20),
    MEDIUM(0, 40),
    HARD(0, 60);

    /**
     * Гетър за минималния брой празни клетки според нивото на трудност.
     *
     * @return минималния брой празни клетки според нивото на трудност.
     */
    public int getMinEmptyCells() {
        return minEmptyCells;
    }

    /**
     * Гетър за максималния брой празни клетки според нивото на трудност.
     *
     * @return максималния брой празни клетки според нивото на трудност.
     */
    public int getMaxEmptyCells() {
        return maxEmptyCells;
    }

    /**
     * Конструктор с параметри.
     *
     * @param minEmptyCells - минимален брой празни клетки.
     * @param maxEmptyCells - максимален брой бразни клетки.
     */
    SudokuLevel(int minEmptyCells, int maxEmptyCells) {
        this.minEmptyCells = minEmptyCells;
        this.maxEmptyCells = maxEmptyCells;
    }

    private final int minEmptyCells;
    private final int maxEmptyCells;
}