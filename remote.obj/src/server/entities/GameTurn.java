package server.entities;

/**
 * Клас, който описва ход в игра.
 */
public class GameTurn {

    /**
     * @param value    - стойност от 1 до 9.
     * @param rowIndex - индекс на реда, на който се намира въпросната стойност.
     * @param colIndex - индекс на колоната, в която се намира въпросната стойност.
     * @param score    - резултат към момента на хода.
     */
    public GameTurn(int value, int rowIndex, int colIndex, int score) {
        setValue(value);
        setRowIndex(rowIndex);
        setColIndex(colIndex);
        setScore(score);
    }

    /**
     * Гетър за стойността.
     *
     * @return стойността.
     */
    public int getValue() {
        return value;
    }

    /**
     * Сетър за стойостта.
     *
     * @param value - стойност.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Гетър за индекса на реда.
     *
     * @return индекса на реда.
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Сетър за индекса на реда.
     *
     * @param rowIndex - индекс на реда.
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * Гетър за индекса на колоната.
     *
     * @return индекса на колоната.
     */
    public int getColIndex() {
        return colIndex;
    }

    /**
     * Сетър за индекса на колоната.
     *
     * @param colIndex - индекса на колоната.
     */
    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    /**
     * Гетър за резултата.
     *
     * @return резултата.
     */
    public int getScore() {
        return score;
    }

    /**
     * Сетър за реезултата.
     *
     * @param score - текущия резултат.
     */
    public void setScore(int score) {
        this.score = score;
    }

    private int value;
    private int rowIndex;
    private int colIndex;
    private int score;
}