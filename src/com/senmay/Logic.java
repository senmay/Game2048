package com.senmay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Logic extends javafx.scene.canvas.Canvas {

    private Cell[] cells;
    boolean win = false;
    boolean lose = false;
    int score = 0;

    public Cell[] getCells() {
        return cells;
    }

    public Logic() {
        super(330, 390);
        resetGame();
    }

    public Logic(double width, double height) {
        super(width, height);
        resetGame();
    }

    void resetGame() {
        score = 0;
        win = false;
        lose = false;
        cells = new Cell[4 * 4];
        for (int cell = 0; cell < cells.length; cell++) {
            cells[cell] = new Cell();
        }
        addCell();
    }

    private void addCell() {
        List<Cell> list = availableSpace();
        if (!availableSpace().isEmpty()) {
            int index = (int) (Math.random() * list.size()) % list.size();
            Cell emptyCell = list.get(index);
            emptyCell.number = Math.random() < 0.8 ? 2 : 4;
        }
    }

    private List<Cell> availableSpace() {
        List<Cell> list = new ArrayList<>(16);
        for (Cell cell : cells)
            if (cell.isEmpty())
                list.add(cell);
        return list;
    }

    private boolean isFull() {
        return availableSpace().size() == 0;
    }

    private Cell cellAt(int x, int y) {
        return cells[x + y * 4];
    }

    boolean canMove() {
        if (!isFull()) return true;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Cell cell = cellAt(x, y);
                if ((x < 3 && cell.number == cellAt(x + 1, y).number) ||
                        (y < 3) && cell.number == cellAt(x, y + 1).number) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compareLines(Cell[] line1, Cell[] line2) {
        if (line1 == line2) {
            return true;
        }
        if (line1.length != line2.length) {
            return false;
        }

        for (int i = 0; i < line1.length; i++) {
            if (line1[i].number != line2[i].number) {
                return false;
            }
        }
        return true;
    }

    private Cell[] rotate(int angle) {
        Cell[] tiles = new Cell[4 * 4];
        int offsetX = 3;
        int offsetY = 3;
        if (angle == 90) {
            offsetY = 0;
        } else if (angle == 270) {
            offsetX = 0;
        }

        double rad = Math.toRadians(angle);
        int cos = (int) Math.cos(rad);
        int sin = (int) Math.sin(rad);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                int newX = (x * cos) - (y * sin) + offsetX;
                int newY = (x * sin) + (y * cos) + offsetY;
                tiles[(newX) + (newY) * 4] = cellAt(x, y);
            }
        }
        return tiles;
    }

    private Cell[] moveLine(Cell[] oldLine) {
        LinkedList<Cell> list = new LinkedList<Cell>();
        for (int i = 0; i < 4; i++) {
            if (!oldLine[i].isEmpty()) {
                list.addLast(oldLine[i]);
            }
        }

        if (list.size() == 0) {
            return oldLine;
        } else {
            Cell[] newLine = new Cell[4];
            while (list.size() != 4) {
                list.add(new Cell());
            }
            for (int j = 0; j < 4; j++) {
                newLine[j] = list.removeFirst();
            }
            return newLine;
        }
    }

    private Cell[] mergeLine(Cell[] oldLine) {
        LinkedList<Cell> list = new LinkedList<>();
        for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
            int num = oldLine[i].number;
            if (i < 3 && oldLine[i].number == oldLine[i + 1].number) {
                num *= 2;
                score += num;
                if (num == 2048) {
                    win = true;
                }
                i++;
            }
            list.add(new Cell(num));
        }

        if (list.size() == 0) {
            return oldLine;
        } else {
            while (list.size() != 4) {
                list.add(new Cell());
            }
            return list.toArray(new Cell[4]);
        }
    }

    private Cell[] getLine(int index) {
        Cell[] result = new Cell[4];
        for (int i = 0; i < 4; i++) {
            result[i] = cellAt(i, index);
        }
        return result;
    }

    private void setLine(int index, Cell[] re) {
        System.arraycopy(re, 0, cells, index * 4, 4);
    }

    public void left() {
        boolean needAddCell = false;
        for (int i = 0; i < 4; i++) {
            Cell[] line = getLine(i);
            Cell[] merged = mergeLine(moveLine(line));
            setLine(i, merged);
            if (!needAddCell && !compareLines(line, merged)) {
                needAddCell = true;
            }
        }
        if (needAddCell) {
            addCell();
        }
    }

    public void right() {
        cells = rotate(180);
        left();
        cells = rotate(180);
    }

    public void up() {
        cells = rotate(270);
        left();
        cells = rotate(90);
    }

    public void down() {
        cells = rotate(90);
        left();
        cells = rotate(270);
    }
}