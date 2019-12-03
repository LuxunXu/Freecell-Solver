package edu.ucr.lxu051;

enum SUIT {
    SPADE, HEART, DIAMOND, CLUB;
}

public class Card {
    private int value; // 1-13
    private String face; // 1-9, T-K
    private SUIT suit;
    private String suitString;

    public Card(String suit, int value) {
        this.suitString = suit;
        switch (suit) {
            case "S" :
                this.suit = SUIT.SPADE;
                break;
            case "H" :
                this.suit = SUIT.HEART;
                break;
            case "D" :
                this.suit = SUIT.DIAMOND;
                break;
            case "C" :
                this.suit = SUIT.CLUB;
                break;
            default:
                throw new IllegalArgumentException("Suit can only be S, H, D or C.");
        }
        if (value < 1 || value > 13) {
            throw new IllegalArgumentException("Value can only be within 1 to 13.");
        } else {
            this.value = value;
            if (value == 1) {
                this.face = "A";
            } else if (value == 10) {
                this.face = "T";
            } else if (value == 11) {
                this.face = "J";
            } else if (value == 12) {
                this.face = "Q";
            } else if (value == 13) {
                this.face = "K";
            } else {
                this.face = String.valueOf(value);
            }
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public SUIT getSuit() {
        return suit;
    }

    public void setSuit(SUIT suit) {
        this.suit = suit;
    }

    @Override
    public String toString() {
        String s = "";
        switch (suit) {
            case SPADE :
                s = "\u001B[0m♠";
                break;
            case HEART :
                s = "\u001B[31m♥";
                break;
            case DIAMOND :
                s = "\u001B[31m♦";
                break;
            case CLUB :
                s = "\u001B[0m♣";
                break;
        }
        return s + face;
    }

    public boolean isSameColor(Card other) {
        if (this.suit == SUIT.SPADE || this.suit == SUIT.CLUB) {
            if (other.getSuit() == SUIT.SPADE || other.getSuit() == SUIT.CLUB) {
                return true;
            } else {
                return false;
            }
        } else {
            if (other.getSuit() == SUIT.HEART || other.getSuit() == SUIT.DIAMOND) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (value != card.value) return false;
        if (!face.equals(card.face)) return false;
        return suit == card.suit;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + face.hashCode();
        result = 31 * result + suit.hashCode();
        return result;
    }

    public String getSuitString() {
        return suitString;
    }

    public void setSuitString(String suitString) {
        this.suitString = suitString;
    }
}
