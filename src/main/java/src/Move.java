package src;

public class Move {

    private int readState;
    private char readChar;

    private int newState;
    private char newChar;
    private int newDir;

    public Move(int readState, char readChar, int newState, char newChar, int newDir) {
        this.readState = readState;
        this.readChar = readChar;
        this.newState = newState;
        this.newChar = newChar;
        this.newDir = newDir;

    }

    public boolean checkMatch(int readState, char readChar){
        return this.readState == readState && this.readChar == readChar;
    }

    public int getNewState() {
        return newState;
    }

    public char getNewChar() {
        return newChar;
    }

    public int getNewDir() {
        return newDir;
    }

}
