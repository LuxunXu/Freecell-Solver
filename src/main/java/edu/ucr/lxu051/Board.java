package edu.ucr.lxu051;

import java.util.*;

public class Board implements Comparable<Board> {
    private HashSet<Card> freecell;
    private Card[] foundation; // H, C, D, S
    private LinkedList<Card>[] column; // 8 columns
    private int numOfFreeSpaces; // number of cards can be moved = numOfFreeSpaces + 1
    private LinkedList<Board> previousStates;

    public Board() {
        this.freecell = new HashSet<>();
        this.foundation = new Card[4];
        this.column = new LinkedList[8];
        for (int i = 0; i < 8; i++) {
            this.column[i] = new LinkedList<>();
        }
        this.numOfFreeSpaces = 4;
        this.previousStates = new LinkedList<>();
    }

    public Board(Board other) {
        this.freecell = new HashSet<>(other.getFreecell());
        this.foundation = new Card[4];
        for (int i = 0; i < 4; i++) {
            this.foundation[i] = other.getFoundation()[i];
        }
        this.column = new LinkedList[8];
        for (int i = 0; i < 8; i++) {
            this.column[i] = new LinkedList<>(other.getColumn()[i]);
        }
        this.numOfFreeSpaces = other.getNumOfFreeSpaces();
        this.previousStates = new LinkedList<>(other.getPreviousStates());
    }

    public void initBoard(long seed) {
        ArrayList<Card> deck = getDeck();
        Random rnd = new Random();
        rnd.setSeed(seed);
        Collections.shuffle(deck, rnd);
        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) { // 7 cards for first four columns
                this.column[i].add(deck.get(k));
                k++;
            }
        }
        for (int i = 4; i < 8; i++) {
            for (int j = 0; j < 6; j++) { // 6 cards for last four columns
                this.column[i].add(deck.get(k));
                k++;
            }
        }
        if (k != 52) {
            throw new IllegalStateException("Invalid board.");
        }
    }

    private ArrayList<Card> getDeck() {
        ArrayList<Card> deck = new ArrayList<>();
        String[] suits = new String[]{"H", "S", "D", "C"};
        for (int i = 1; i <= 13; i++) {
            for (String suit : suits) {
                deck.add(new Card(suit, i));
            }
        }
        return deck;
    }

    public boolean canMoveToCell(int columnNum) {
        LinkedList<Card> column = this.column[columnNum];
        if (column.isEmpty()) {
            return false;
        }
        if (this.freecell.size() < 4) {
            return true;
        }
        return false;
    }

    public void moveToCell(int columnNum) {
        this.previousStates.add(new Board(this));
        LinkedList<Card> column = this.column[columnNum];
        this.freecell.add(column.pollLast());
        this.numOfFreeSpaces--;
        if (column.isEmpty()) {
            this.numOfFreeSpaces++;
        }
    }

    public Card canMoveFromCellToColumn(int columnNum) {
        if (this.freecell.isEmpty()) {
            return null;
        }
        LinkedList<Card> column = this.column[columnNum];
        if (column.isEmpty()) {
            List<Card> cellList = new ArrayList<>(this.freecell);
            Random rnd = new Random();
            return cellList.get(rnd.nextInt(cellList.size()));
        } else {
            Card lastCard = column.peekLast();
            for (Card cardAtCell : this.freecell) {
                if (!cardAtCell.isSameColor(lastCard) && cardAtCell.getValue() + 1 == lastCard.getValue()) {
                    return cardAtCell;
                }
            }
        }
        return null;
    }

    public void moveFromCellToColumn(int columnNum, Card card) {
        this.previousStates.add(new Board(this));
        LinkedList<Card> column = this.column[columnNum];
        this.freecell.remove(card);
        this.numOfFreeSpaces++;
        column.addLast(card);
    }

    public boolean canMoveToColumn(int fromColNum, int toColNum, int numOfCards) {
        LinkedList<Card> fromColumn = this.column[fromColNum];
        LinkedList<Card> toColumn = this.column[toColNum];

        if (numOfCards < 1) {
            throw new IllegalStateException("At least move one card.");
        }
        if (fromColumn.isEmpty()) {
            throw new IllegalStateException("From column is empty.");
        }
        if (numOfCards > fromColumn.size()) {
            throw new IllegalStateException("Not enough cards to move.");
        }
        if (numOfCards > this.numOfFreeSpaces + 1) {
            return false;
        }
        if (toColumn.isEmpty()) {
            return true;
        }
        int i = 0;
        Card prev = null;
        while (i < numOfCards) {
            Card peek = fromColumn.get(fromColumn.size() - 1 - i);
            if (prev == null) {
                prev = peek;
                i++;
            } else if (!peek.isSameColor(prev) && peek.getValue() - 1 == prev.getValue()) {
                prev = peek;
                i++;
            } else {
                return false;
            }
        }
        if (!toColumn.getLast().isSameColor(prev) && toColumn.getLast().getValue() - 1 == prev.getValue()) {
            return true;
        }
        return false;
    }

    public void moveToColumn(int fromColNum, int toColNum, int numOfCards) {
        this.previousStates.add(new Board(this));
        LinkedList<Card> fromColumn = this.column[fromColNum];
        LinkedList<Card> toColumn = this.column[toColNum];

        int i = 0;
        LinkedList<Card> seq = new LinkedList<>();
        while (i < numOfCards) {
            Card poll = fromColumn.pollLast();
            seq.addLast(poll);
            i++;
        }
        for (int j = 0; j < seq.size(); j++) {
            toColumn.addLast(seq.get(j));
        }
        if (fromColumn.isEmpty()) {
            this.numOfFreeSpaces++;
        }
    }

    public int canMoveToFoundation(int columnNum) {
        if (this.column[columnNum].isEmpty()) {
            return -1;
        }
        Card peek = this.column[columnNum].peekLast();
        int fColNum = -1;
        switch (peek.getSuit()) {
            case HEART :
                fColNum = 0;
                break;
            case CLUB :
                fColNum = 1;
                break;
            case DIAMOND :
                fColNum = 2;
                break;
            case SPADE :
                fColNum = 3;
                break;
        }
        Card curAtFoundation = this.foundation[fColNum];
        if (curAtFoundation == null) {
            if (peek.getValue() == 1) {
                return fColNum;
            } else {
                return -1;
            }
        } else {
            if (curAtFoundation.getValue() + 1 == peek.getValue()) {
                return fColNum;
            } else {
                return -1;
            }
        }
    }

    public void moveToFoundation(int columnNum, int foundationColNum) {
        this.previousStates.add(new Board(this));
        LinkedList<Card> column = this.column[columnNum];
        this.foundation[foundationColNum] = column.pollLast();
        if (column.isEmpty()) {
            this.numOfFreeSpaces++;
        }
    }

    public Card canMoveFromCellToFoundation() {
        if (this.freecell.isEmpty()) {
            return null;
        }
        int fColNum = -1;
        for (Card c : this.freecell) {
            switch (c.getSuit()) {
                case HEART:
                fColNum = 0;
                break;
            case CLUB :
                fColNum = 1;
                break;
            case DIAMOND :
                fColNum = 2;
                break;
            case SPADE:
                fColNum = 3;
                break;
            }
            if (this.foundation[fColNum] == null) {
                if (c.getValue() == 1) {
                    return c;
                }
            } else {
                if (this.foundation[fColNum].getValue() + 1 == c.getValue()) {
                    return c;
                }
            }
        }
        return null;
    }

    public void moveFromCellToFoundation(Card card) {
        this.previousStates.add(new Board(this));
        int fColNum = -1;
        switch (card.getSuit()) {
            case HEART:
                fColNum = 0;
                break;
            case CLUB :
                fColNum = 1;
                break;
            case DIAMOND :
                fColNum = 2;
                break;
            case SPADE:
                fColNum = 3;
                break;
        }
        this.freecell.remove(card);
        this.numOfFreeSpaces++;
        this.foundation[fColNum] = card;
    }

    public boolean isEnd() {
        try {
            if (foundation[0].equals(new Card("H", 13)) && foundation[1].equals(new Card("C", 13)) &&
                    foundation[2].equals(new Card("D", 13)) && foundation[3].equals(new Card("S", 13))) { // H, C, D, S
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Card c : this.freecell) {
            result.append(c + " ");
        }
        for (int i = 0; i < 4 - this.freecell.size(); i++) {
            result.append("\u001B[0m[] ");
        }
        for (int i = 0; i < 4; i++) {
            if (foundation[i] == null) {
                result.append("\u001B[0m[] ");
            } else {
                result.append(foundation[i] + " ");
            }
        }
        result.deleteCharAt(result.length() - 1);
        result.append("\n");
        result.append("\u001B[0m------------------------\n");
        for (int i = 0; i < maxColumnSize(); i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    Card c = this.column[j].get(i);
                    result.append(c + " ");
                } catch (IndexOutOfBoundsException e) {
                    result.append("   ");
                }
            }
            result.deleteCharAt(result.length() - 1);
            result.append("\n");
        }
        return result.toString();
    }

    private int maxColumnSize() {
        int max = 0;
        for (LinkedList<Card> list : this.column) {
            if (list.size() > max) {
                max = list.size();
            }
        }
        return max;
    }

    @Override
    public int hashCode() {
        int result = freecell.hashCode();
        result = 31 * result + Arrays.hashCode(foundation);
        result = 31 * result + Arrays.hashCode(column);
        result = 31 * result + numOfFreeSpaces;
        return result;
    }

    public HashSet<Card> getFreecell() {
        return freecell;
    }

    public void setFreecell(HashSet<Card> freecell) {
        this.freecell = freecell;
    }

    public Card[] getFoundation() {
        return foundation;
    }

    public void setFoundation(Card[] foundation) {
        this.foundation = foundation;
    }

    public LinkedList<Card>[] getColumn() {
        return column;
    }

    public void setColumn(LinkedList<Card>[] column) {
        this.column = column;
    }

    public int getNumOfFreeSpaces() {
        return numOfFreeSpaces;
    }

    public void setNumOfFreeSpaces(int numOfFreeSpaces) {
        this.numOfFreeSpaces = numOfFreeSpaces;
    }

    public LinkedList<Board> getPreviousStates() {
        return previousStates;
    }

    public void setPreviousStates(LinkedList<Board> previousStates) {
        this.previousStates = previousStates;
    }

    public int getScore() {
        int score = 0;
        score += getNumOfFreeSpaces() * 2;
        for (int i = 0; i < 4; i++) {
            Card c = this.foundation[i];
            if (c != null) {
                score += c.getValue();
            }
        }
        int hasEmptyFoundationPiles = 0;
        for (int i = 0; i < 4; i++) { // H, C, D, S
            Card foundation = this.foundation[i];
            Card toBeAddedToFoundation = null;
            int value;
            if (foundation == null) {
                value = 1;
                hasEmptyFoundationPiles += 5;
            } else {
                value = foundation.getValue() + 1;
            }
            if (value >= 1 && value <= 13) {
                switch (i) {
                    case 0:
                        toBeAddedToFoundation = new Card("H", value);
                        break;
                    case 1:
                        toBeAddedToFoundation = new Card("C", value);
                        break;
                    case 2:
                        toBeAddedToFoundation = new Card("D", value);
                        break;
                    case 3:
                        toBeAddedToFoundation = new Card("S", value);
                        break;
                }
            }
            if (toBeAddedToFoundation != null) {
                for (LinkedList<Card> column : this.column) {
                    if (column.contains(toBeAddedToFoundation)) {
                        score -= (column.size() - column.indexOf(toBeAddedToFoundation) - 1);
                    }
                }
            }
        }
        score -= hasEmptyFoundationPiles;
        return score;
    }

    @Override
    public int compareTo(Board other) {
        return other.getScore() - getScore();
    }

    public String toBoardString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < maxColumnSize(); i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    Card c = this.column[j].get(i);
                    result.append(c.getFace() + c.getSuitString() + " ");
                } catch (IndexOutOfBoundsException e) {
                    result.append("   ");
                }
            }
            result.deleteCharAt(result.length() - 1);
            result.append("\n");
        }
        return result.toString();
    }
}
