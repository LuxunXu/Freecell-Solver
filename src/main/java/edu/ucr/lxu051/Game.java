package edu.ucr.lxu051;

import java.util.*;

public class Game {
    private int seed;
    private Board initState;
    private Set<Integer> visitedState;
    private final int DEPTH = 3;
    private PriorityQueue<Board> nextState;
    private Board solution;
    private int curBestScore;
    private boolean debug = true;
    private int trimSize = 1000;

    public static void main(String[] args) {
//        for (int i = 1; i <= 100000; i++) {
//            System.out.println("Case " + i + ": ");
//            Game game = new Game(i);
//            game.newGame();
//            boolean solved = game.solve();
//            if (!solved) {
//                System.out.println("Not solved.");
//            }
//        }

        Game game = new Game(2);
        game.newGame();
        boolean solved = game.solve();
    }

    public Game(int seed) {
        this.seed = seed;
        this.initState = new Board();
        visitedState = new HashSet<>();
        nextState = new PriorityQueue<>();
    }

    public void newGame() {
        this.initState.initBoard(this.seed);
        System.out.println(this.initState.toBoardString());
        System.out.println(this.initState.hashCode());
        visitedState.add(initState.hashCode());
        nextState.add(initState);
        curBestScore = -Integer.MAX_VALUE;
    }

    public boolean solve() {
        long startTime = System.currentTimeMillis();
        while (!genStateAfterSixDepth()) {
            Board b = this.nextState.peek();
            this.curBestScore = b.getScore();
            if (debug) {
                System.out.println(b);
                System.out.print(this.nextState.size() + " ");
                System.out.println(b.getScore());
            }
            if (trimSize > 0) {
                trimState();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Solution - " + this.solution.getPreviousStates().size() + " steps.");
        System.out.println("Time taken: " + (endTime - startTime) / 1000 + "s.");
        for (Board solutionSeq : this.solution.getPreviousStates()) {
            System.out.println(solutionSeq);
        }
        return true;
    }

    public List<Board> getState(Board curBoard) {
        List<Board> stateList = new ArrayList<>();
        Board boardCopy;
        // MOVE FROM COLUMN TO FOUNDATION
        boolean movedToFoundation = false;
        for (int i = 0; i < 8; i++) {
            boardCopy = new Board(curBoard);
            int foundationColNum = boardCopy.canMoveToFoundation(i);
            if (foundationColNum != -1) {
                movedToFoundation = true;
                boardCopy.moveToFoundation(i, foundationColNum);
                stateList.add(boardCopy);
            }
        }
        if (movedToFoundation) {
            return stateList;
        }
        // MOVE FROM CELL TO FOUNDATION
        boardCopy = new Board(curBoard);
        Card card = boardCopy.canMoveFromCellToFoundation();
        if (card != null) {
            boardCopy.moveFromCellToFoundation(card);
            stateList.add(boardCopy);
            return stateList;
        }
        // MOVE FROM COLUMN TO CELL
        for (int i = 0; i < 8; i++) {
            boardCopy = new Board(curBoard);
            if (boardCopy.canMoveToCell(i)) {
                boardCopy.moveToCell(i);
                stateList.add(boardCopy);
            }
        }
        // MOVE FROM CELL TO COLUMN
        for (int i = 0; i < 8; i++) {
            boardCopy = new Board(curBoard);
            Card toMove = boardCopy.canMoveFromCellToColumn(i);
            if (toMove != null) {
                boardCopy.moveFromCellToColumn(i, toMove);
                stateList.add(boardCopy);
            }
        }
        // MOVE FROM COLUMN TO COLUMN
        for (int i = 0; i < 8; i++) {
            boardCopy = new Board(curBoard);
            for (int j = 0; j < 8; j++) {
                if (i != j) {
                    for (int k = 1; k <= boardCopy.getColumn()[i].size(); k++) {
                        if (boardCopy.canMoveToColumn(i, j, k)) {
                            boardCopy.moveToColumn(i, j, k);
                            stateList.add(boardCopy);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return stateList;
    }

    public boolean addState(Board board) {
        int hashCode = board.hashCode();
        if (visitedState.add(hashCode)) {
            if (trimSize > 0) {
                this.nextState.add(board);
                return true;
            } else {
                if (board.getScore() >= this.curBestScore) {
                    this.nextState.add(board);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean genStateAfterSixDepth() {
        for (int i = 0; i < DEPTH; i++) {
            List<Board> boardListCopy = new LinkedList<>(this.nextState);
            this.nextState.clear();
            for (Board b : boardListCopy) {
                List<Board> stateList = getState(b);
                for (Board board : stateList) {
                    if (board.isEnd()) {
                        this.solution = board;
                        return true;
                    }
                    addState(board);
                }
            }
        }
        return false;
    }

    public void trimState() {
        List<Board> boardListCopy = new LinkedList<>(this.nextState);
        this.nextState.clear();
        int max = boardListCopy.size() < trimSize ? boardListCopy.size() : trimSize;
        for (int i = 0; i < max; i++) {
            this.nextState.add(boardListCopy.get(i));
        }
    }
}
