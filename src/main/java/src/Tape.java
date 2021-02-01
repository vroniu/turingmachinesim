package src;

public class Tape {

    private int tapeLen;
    private char[] tapeData;

    public Tape(String data){
        tapeLen = data.length();
        tapeData = data.toCharArray();
    }

    public char getSymbol(int index){
        if((index<0) || (index>=tapeLen)) return 'B';
        else return tapeData[index];
    }

    public void setSymbol(int index, char symbol){
        if(!((index<0) || (index>=tapeLen)))tapeData[index] = symbol;
    }

    public int getTapeLen() {
        return tapeLen;
    }
}
