package server.entities;

public class GameTurn {
    private int value;
    private int rowIndex;
    private int colIndex;

    public GameTurn(int value, int rowIndex, int colIndex) {
        setValue(value);
        setRowIndex(rowIndex);
        setColIndex(colIndex);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }
}