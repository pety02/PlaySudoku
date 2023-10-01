package server.entities;

public class GameTurn {
    private int value;
    private int rowIndex;
    private int colIndex;
    private int score;

    public GameTurn(int value, int rowIndex, int colIndex, int score) {
        setValue(value);
        setRowIndex(rowIndex);
        setColIndex(colIndex);
        setScore(score);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = Math.max(0, value);
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = Math.max(0, rowIndex);
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = Math.max(0, colIndex);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }
}