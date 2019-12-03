package edu.ucr.lxu051;

public class Test {
    public static void main(String[] args) {
        Board b = new Board();
        b.initBoard(7686);
        System.out.println(b);
        b.moveToCell(0);
        b.moveToCell(4);
        b.moveToFoundation(4, 0);
        b.moveToFoundation(3, 2);
        System.out.println(b);
    }
}
