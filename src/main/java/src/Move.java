package src;

import org.json.simple.JSONObject;

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
        if(this.readState == readState && this.readChar == readChar) return true;
        else return false;
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

    public void print(){
        System.out.println("state:"+ readState + " char:" + readChar + " new state:" + newState + " new char:" + newChar + " dir:" + newDir);
    }

    public String stringValue(){
        return new String(readState + "," + readChar + "," + newState + "," + newChar + "," + newDir);
    }
}
